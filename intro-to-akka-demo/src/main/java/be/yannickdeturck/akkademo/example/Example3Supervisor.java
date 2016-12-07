package be.yannickdeturck.akkademo.example;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.Duration;

import java.util.Random;

/**
 * Example of a Supervisor Strategy
 */
public class Example3Supervisor {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create();
        final ActorRef supervisor = system.actorOf(Supervisor.props(), "supervisor");
        for (int i=0; i < 10; i++) {
            supervisor.tell(new UnstableActor.Command(), ActorRef.noSender());
        }
        system.awaitTermination();
    }
}

class Supervisor extends AbstractLoggingActor {
    public static final OneForOneStrategy STRATEGY = new OneForOneStrategy( // each child is treated separately
            10, // max 10 restart per 10 seconds
            Duration.create("10 seconds"),
            DeciderBuilder
                    .match(RuntimeException.class, ex -> SupervisorStrategy.restart())
                    .build()
    );


    public Supervisor() {
        final ActorRef unstableActor = getContext().actorOf(UnstableActor.props(), "unstableActor");
        receive(ReceiveBuilder
                .matchAny(any -> unstableActor.forward(any, getContext()))
                .build()
        );
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return STRATEGY;
    }

    public static Props props() {
        return Props.create(Supervisor.class);
    }
}

class UnstableActor extends AbstractLoggingActor {

    public static class Command {}

    public UnstableActor() {
        log().info("Creating UnstableActor...");
        receive(ReceiveBuilder
                .match(Command.class, this::onCommand)
                .build()
        );
    }

    private void onCommand(Command c) {
        int rng = new Random().nextInt(10);
        if (rng < 9) {
            throw new RuntimeException("Woops, something went wrong");
        }
        log().info("Handling the command...");
    }

    public static Props props() {
        return Props.create(UnstableActor.class);
    }

    @Override
    public void postStop() throws Exception {
        log().info("Stopping UnstableActor...");
    }
}


