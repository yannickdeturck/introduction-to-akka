package be.yannickdeturck.akkademo.example

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object Example2Behaviour extends App {
  val system = ActorSystem("system")
  val door = system.actorOf(Door.props, "door")
  door ! Door.PassThrough
  door ! Door.Open
  door ! Door.PassThrough
  door ! Door.Close
  system.awaitTermination()
}

class Door extends Actor with ActorLogging {
  override def receive: Receive = closed

  def open: Receive = {
    case Door.Close =>
      log.info("Closing door")
      context.become(closed)
    case Door.PassThrough =>
      log.warning("Passing through the open door")
  }

  def closed: Receive = {
    case Door.Open =>
      log.info("Opening door")
      context.become(open)
    case Door.PassThrough =>
      log.warning("*BUMP*")
  }
}

object Door {
  val props: Props = Props[Door]
  case class Open()
  case class Close()
  case class PassThrough()
}