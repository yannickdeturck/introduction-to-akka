package be.yannickdeturck.akkademo.example

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}

import scala.concurrent.duration._
import scala.util.Random

object Example3Supervisor extends App {
  val system = ActorSystem("system")
  val supervisor = system.actorOf(Supervisor.props, "supervisor")
  for (i <- 0 until 10){
    supervisor ! UnstableActor.Command
  }
}

class Supervisor extends Actor with ActorLogging {
  val unstableActor: ActorRef = context.actorOf(UnstableActor.props, "unstableActor")

  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 second) {
      case _: RuntimeException         => SupervisorStrategy.Restart
    }
  }

  override def receive: Receive = {
    case msg => unstableActor forward msg
  }
}

object Supervisor {
  val props: Props = Props[Supervisor]
}

class UnstableActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case UnstableActor.Command =>
      val rng = Random.nextInt(10)
      if (rng < 4) throw new RuntimeException("Woops, something went wrong")
      log.info("Handling the command...")
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = log.info("Stopping UnstableActor...")

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = log.info("Creating UnstableActor...")
}

object UnstableActor {
  case class Command()
  val props: Props = Props[UnstableActor]
}