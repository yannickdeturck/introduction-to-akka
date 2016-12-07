package be.yannickdeturck.akkademo.pizzaorderer.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Office extends AbstractLoggingActor {

    public static class StartWorkshop {
    }

    public static class KnockDoor {
        private final List<String> orderedPizzas;

        public KnockDoor(List<String> orderedPizzas) {
            this.orderedPizzas = Collections.unmodifiableList(new ArrayList<>(orderedPizzas));
        }
    }

    public static class ReceiveOrder {
        private final List<String> pizzas;

        public ReceiveOrder(List<String> pizzas) {
            this.pizzas = Collections.unmodifiableList(new ArrayList<>(pizzas));
        }
    }


    public static Props props() {
        return Props.create(Office.class);
    }

    private ActorRef developer;

    public Office() {
        developer = getContext().actorOf(Developer.props("Yannick"), "actor-yannick");

        receive(ReceiveBuilder
                .match(StartWorkshop.class, this::onStartWorkshop)
                .match(KnockDoor.class, this::onKnockDoor)
                .match(ReceiveOrder.class, this::onReceiveOrder)
                .build()
        );
    }

    private void onStartWorkshop(StartWorkshop message) {
        log().info("Starting workshop...");
        developer.tell(new Developer.StartOrdering(), self());
    }

    private void onReceiveOrder(ReceiveOrder message) {
        log().info("Received order of {} from {}", message.pizzas, sender());
        ActorRef pizzaGuy = getContext().actorOf(PizzaGuy.props(), "mario");
        pizzaGuy.tell(new PizzaGuy.TakeOrder("Blarenberglaan 3b, Mechelen", message.pizzas), self());
        log().info("Made the order");
    }

    private void onKnockDoor(KnockDoor message) {
        log().info("Paying the pizzaguy {} and accepting the pizzas {}", sender(), message.orderedPizzas);
        getContext().stop(sender());
        log().info("Informing the developer {}...", developer);
        developer.tell(new Developer.PizzaHasArrived(message.orderedPizzas), sender());
    }
}
