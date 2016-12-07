package be.yannickdeturck.akkademo.pizzaorderer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import be.yannickdeturck.akkademo.pizzaorderer.actors.Office;

public class WorkshopRunner {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("akka-workshop");
        final ActorRef office = system.actorOf(Office.props(), "office");
        office.tell(new Office.StartWorkshop(), ActorRef.noSender());
        system.awaitTermination();
    }
}
