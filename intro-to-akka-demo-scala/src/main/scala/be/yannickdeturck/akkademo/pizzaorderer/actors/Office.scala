package be.yannickdeturck.akkademo.pizzaorderer.actors

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, Props}

class Office extends Actor with ActorLogging {
  val developer = context.actorOf(Developer.props("Yannick"), "actor-yannick")
  override def receive: Receive = {
    case Office.StartWorkshop =>
      log.info("Starting workshop...")
      developer ! Developer.StartOrdering
    case Office.KnockDoor(orderedPizzas) =>
      log.info(s"Paying the pizzaguy ${sender()} and accepting the pizzas $orderedPizzas")
      context.stop(sender())
      log.info(s"Informing the developer $developer")
      developer ! Developer.PizzaHasArrived(orderedPizzas)
    case Office.ReceiveOrder(pizzas) =>
      log.info(s"Received order of $pizzas from $sender()")
      val pizzaGuy = context.actorOf(PizzaGuy.props(), "mario")
      pizzaGuy ! PizzaGuy.TakeOrder("Blarenberglaan 3b, Mechelen", pizzas)
      log.info("Made the order")
  }
}

object Office {
  val props: Props = Props[Office]
  case class StartWorkshop()
  case class KnockDoor(orderedPizzas: Seq[String])
  case class ReceiveOrder(pizzas: Seq[String])
}