package be.yannickdeturck.akkademo.pizzaorderer.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Developer extends AbstractLoggingActor {
    private final String name;

    public Developer(String name) {
        this.name = name;

        receive(ReceiveBuilder
            .match(StartOrdering.class, this::onStartOrdering)
            .match(PizzaHasArrived.class, this::onPizzaHasArrived)
            .match(EatPizza.class, this::onEatPizza)
            .build()
        );
    }

    public static class StartOrdering {
    }

    public static class PizzaHasArrived {
        private final List<String> orderedPizzas;

        public PizzaHasArrived(List<String> orderedPizzas) {
            this.orderedPizzas = Collections.unmodifiableList(new ArrayList<>(orderedPizzas));
        }
    }

    public static class EatPizza {
        private String pizza;

        public EatPizza(String pizza) {
            this.pizza = pizza;
        }
    }

    public static Props props(String name) {
        return Props.create(Developer.class, name);
    }

    private void onStartOrdering(StartOrdering message) {
        List<String> pizzas = Arrays.asList("Hawai", "Meat Lover", "Prosciutto");
        log().info("{} is about to make his order of {}", this.name, pizzas);
        ActorRef office = sender();
        office.tell(new Office.ReceiveOrder(pizzas), self());
    }

    private void onPizzaHasArrived(PizzaHasArrived message) {
        log().info("{} runs to collect the pizzas...", this.name);
        for (String orderedPizza : message.orderedPizzas) {
            self().tell(new EatPizza(orderedPizza), self());
        }
    }

    private void onEatPizza(EatPizza message) {
        log().info("Mmmm, pizza {}... Omnomnom...", message.pizza);
    }
}
