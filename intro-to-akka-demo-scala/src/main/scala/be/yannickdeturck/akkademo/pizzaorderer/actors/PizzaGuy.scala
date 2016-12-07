package be.yannickdeturck.akkademo.pizzaorderer.actors

import akka.actor.{Actor, ActorLogging, Props}

class PizzaGuy extends Actor with ActorLogging{
  var preparedPizzas: Seq[String] = Seq()
  override def receive: Receive = {
    case PizzaGuy.TakeOrder(address, pizzas) =>
      log.info(s"Received order $pizzas to be sent at $address")
      for (pizza <- pizzas){
        self ! PizzaGuy.PreparePizza(pizza)
      }
      self ! PizzaGuy.DeliverPizzas(address)
    case PizzaGuy.PreparePizza(pizza) =>
      log.info(s"Preparing a pizza $pizza")
      preparedPizzas :+=  pizza
    case PizzaGuy.DeliverPizzas(address) =>
      log.info(s"Delivering pizza order of $preparedPizzas to $address")
      context.parent ! Office.KnockDoor(preparedPizzas)
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = log.info("Going to bed ZzZzZz...")
}

object PizzaGuy {
  def props(): Props = Props(new PizzaGuy())
  case class TakeOrder(address: String, pizzas: Seq[String])
  case class PreparePizza(pizza: String)
  case class DeliverPizzas(address: String)
}
