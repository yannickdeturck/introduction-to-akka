package be.yannickdeturck.akkademo.example;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

/**
 * Example of an actor.
 */
public class Example1BasicActor {

    public static void main(String[] args) {
        // we don't instantiate the actor like a regular object but instead we use an actorsystem
        // actor's lifecycle is bound to the actorsystem, if the system terminates so will its actors
        ActorSystem system = ActorSystem.create("example1");

        // create an actor and bind it to the system, pass props and give it a name
        // you don't get the actor directly but instead you get a reference to the actor
        final ActorRef greeter = system.actorOf(Greeter.props(), "greeter");

        // It's completely safe from a concurrency point of view so we can create threads without having to expect any issues
        // The actor model assumes that each actor instance processes its own mailbox sequentially, making it threadsafe
        // Also one of the reasons, why Akka introduced the concept of ActorRef:
        //    being a handle, it lets you communicate with the actor through message passing
        //    but not by calling its methods directly, preventing your from directly calling methods of the actor and
        //    losing the thread-safety guarantees the model offers
        greeter.tell(new Greeter.Greet(), ActorRef.noSender());
        greeter.tell(new Greeter.Greet(), ActorRef.noSender());
        greeter.tell(new Greeter.Greet(), ActorRef.noSender());
        system.awaitTermination();
    }

    static class Greeter extends AbstractLoggingActor { // Logger for free compared to AbstractActor
        // After creating the Actor define the protocol of your actor
        static class Greet { // no data for now in this sample, it will just increment the counter
        }

        private int counter = 0; // state

        // Initializer block, similar to a default constructor
        // We define the partial function 'receive' (Scala construct) via a ReceiveBuilder
        // You define the behaviour via matches eg handle message x by calling function y
        // Use matchAny() to define the action for handling unknown messages
        public Greeter(){
            receive(ReceiveBuilder
                    .match(Greet.class, this::onGreet)
                    .build()
            );
        }

        private void onGreet(Greet greet) {
            counter++;
            log().info("Hello there! #{}", counter);
        }

        // descriptor of the actor, describes the class of the actor and other properties of the actor
        // A Props is a immutable configuration object of an actor
        // You can see it as a freely shareable recipe for creating an actor including associated deployment
        // information (e.g. which dispatcher to use)
        public static Props props() {
            return Props.create(Greeter.class);
        }
    }
}
