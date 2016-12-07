package be.yannickdeturck.akkademo.example

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object Example1BasicActor extends App {
  val system = ActorSystem("system")
  val greeter = system.actorOf(Greeter.props, "greeter")
  greeter ! Greeter.Greet
  greeter ! Greeter.Greet
  greeter ! Greeter.Greet
  system.awaitTermination()
}

class Greeter extends Actor with ActorLogging {
  var counter = 0

  override def receive: Receive = {
    case Greeter.Greet =>
      counter += 1
      log.info(s"Hello there! #$counter")
  }
}

object Greeter {
  val props: Props = Props[Greeter]
  case class Greet()
}