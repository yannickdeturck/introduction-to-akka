package be.yannickdeturck.akkademo.pizzaorderer.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PizzaGuy extends AbstractLoggingActor {
    private final List<String> preparedPizzas = new ArrayList<>();

    public static class TakeOrder {
        private final String address;
        private final List<String> pizzas;

        public TakeOrder(String address, List<String> pizzas) {
            this.address = address;
            this.pizzas = Collections.unmodifiableList(new ArrayList<>(pizzas));
        }
    }

    public PizzaGuy() {
        receive(ReceiveBuilder
            .match(TakeOrder.class, this::onTakeOrder)
            .match(PreparePizza.class, this::onPreparePizza)
            .match(DeliverPizzas.class, this::onDeliverPizzas)
            .build()
        );
    }

    @Override
    public void postStop() throws Exception {
        log().info("Going to bed ZzZzZz...");
    }

    public static class PreparePizza {
        private final String pizza;

        public PreparePizza(String pizza) {
            this.pizza = pizza;
        }
    }

    public static class DeliverPizzas {
        private final String address;

        public DeliverPizzas(String address) {
            this.address = address;
        }
    }

    public static Props props() {
        return Props.create(PizzaGuy.class);
    }

    private void onTakeOrder(TakeOrder message) {
        log().info("Received order {} to be sent at {}", message.pizzas, message.address);
        for (String pizza : message.pizzas) {
            self().tell(new PreparePizza(pizza), self());
        }
        self().tell(new DeliverPizzas(message.address), self());
    }

    private void onPreparePizza(PreparePizza message){
        log().info("Preparing a pizza {}...", message.pizza);
        preparedPizzas.add(message.pizza);
    }

    private void onDeliverPizzas(DeliverPizzas message){
        log().info("Delivering pizza order of {} to {}", preparedPizzas, message.address);
        getContext().parent().tell(new Office.KnockDoor(preparedPizzas), self());
    }
}
