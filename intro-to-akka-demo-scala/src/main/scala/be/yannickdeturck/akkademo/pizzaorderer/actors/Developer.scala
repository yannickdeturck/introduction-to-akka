package be.yannickdeturck.akkademo.pizzaorderer.actors

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, Props}
import be.yannickdeturck.akkademo.pizzaorderer.actors.Developer.EatPizza

class Developer(name: String) extends Actor with ActorLogging {
  override def receive: Receive = {
    case Developer.StartOrdering =>
      val pizzas = Seq("Hawai", "Meat lover", "Prosciutto")
      log.info(s"$name is about to make his order of $pizzas")
      sender ! Office.ReceiveOrder(pizzas)
    case Developer.PizzaHasArrived(orderedPizzas) =>
      log.info(s"$name runs to collect the pizzas...")
      for (orderedPizza <- orderedPizzas){
        self ! EatPizza(orderedPizza)
      }
    case Developer.EatPizza(pizza) =>
      log.info(s"Mmm, pizza $pizza... Omnomnom...")
  }
}

object Developer {
  def props(name: String) = Props(new Developer(name))
  case class StartOrdering()
  case class PizzaHasArrived(orderedPizzas: Seq[String])
  case class EatPizza(pizza: String)
}
