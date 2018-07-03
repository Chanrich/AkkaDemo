
import demo._
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}



object ActorDemo{
  import PersonA._


  def main(args: Array[String]) : Unit = {

    val system = ActorSystem("DemoSystem")
    // Props is used to configure actor
    // Using ActorSystem to create top level actor (Under /user/)
    val actor1 = system.actorOf(Props[PersonA], "FirstActor")

    // Fire up asynchronous message using !
    actor1 ! "printit"
    println()
    // To end or kill an actor, send them custom message or a PosionPill
//    actor1 ! "stop"
    //

    // case class
    actor1 ! targetChild("Child1")
    Thread.sleep(1000)
    println()
    // Tuples
    actor1 ! ("Child2", "stop")


    Thread.sleep(2000)
    println()
    // Terminating parent actor also will terminate its children
    println("Sending actor1 PoisonPill")
    actor1 ! PoisonPill
  }

}
