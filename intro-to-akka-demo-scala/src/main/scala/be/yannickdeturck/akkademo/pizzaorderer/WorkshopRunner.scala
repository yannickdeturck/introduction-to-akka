package be.yannickdeturck.akkademo.pizzaorderer

import akka.actor.ActorSystem
import be.yannickdeturck.akkademo.pizzaorderer.actors.Office

object WorkshopRunner extends App{
  val system = ActorSystem("akka-workshop")
  val office = system.actorOf(Office.props, "office")
  office ! Office.StartWorkshop
  system.awaitTermination()
}
