package be.yannickdeturck.akkademo.example;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

/**
 * Example of an actor that changes behavior.
 */
public class Example2Behaviour {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("example2");

        final ActorRef door = system.actorOf(Door.props(), "door");

        door.tell(new Door.PassThrough(), ActorRef.noSender());
        door.tell(new Door.Open(), ActorRef.noSender());
        door.tell(new Door.PassThrough(), ActorRef.noSender());
        door.tell(new Door.Close(), ActorRef.noSender());

        system.awaitTermination();
    }

    static class Door extends AbstractLoggingActor {
        // the door's protocol
        static class Open {
        }

        static class Close {
        }

        static class PassThrough {
        }

        // two behaviours implemented as a PartialFunction (object to boxed unit)
        // unit is Scala's void of Java, thanks to unit you can pass in an arbitrary block of code to be executed
        // behaviours never change so make them final
        private final PartialFunction<Object, BoxedUnit> open;
        private final PartialFunction<Object, BoxedUnit> closed;

        public Door() {
            // We specify the two different behaviours using the ReceiveBuilder
            open = ReceiveBuilder
                    .match(Close.class, this::onClose)
                    .match(PassThrough.class, this::onPassThroughOpenDoor)
                    .build();

            closed = ReceiveBuilder
                    .match(Open.class, this::onOpen)
                    .match(PassThrough.class, this::onPassThroughClosedDoor)
                    .build();

            receive(closed); // disabled is the default behaviour of the door
        }

        private void onClose(Close message) {
            log().info("Closing door");
            getContext().become(closed);
        }

        private void onOpen(Open message) {
            log().info("Opening door");
            getContext().become(open);
        }

        private void onPassThroughOpenDoor(PassThrough message) {
            log().warning("Passing through the open door");
        }

        private void onPassThroughClosedDoor(PassThrough message) {
            log().warning("*BUMP*");
        }

        public static Props props() {
            return Props.create(Door.class);
        }
    }
}
