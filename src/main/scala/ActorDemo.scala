
import demo._
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import scala.io.StdIn._

// Akka Actor model
object ActorDemo{
  import PersonA._
  def main(args: Array[String]) : Unit = {
    // Create our actor system
    val system = ActorSystem("DemoSystem")

    // Props is used to configure actor
    // Using ActorSystem to create top level actor (Under /user/)
    val actor1 = system.actorOf(Props[PersonA], "FirstActor")

    // Now, actor1 has a type ActorRef, which points to the actor we just created

    // 1. Passing string type
    // 2. Fire up asynchronous message using !
    // 3. Receive function defined in actor
    WaitForMe("Send actor 1 'ShowMeYourPath'")
    actor1 ! "ShowMeYourPath"

    // 1. Passing case class type
    // 2. Then actor1 will pass a poison pill to the child
    // 3. Actor will end upon receiving poison
    WaitForMe("Send actor 1 case class of targetChild")
    actor1 ! targetChild("Child1")

    // 1. Passing a tuple type
    // 2. actor1 will then send another message to target child
    // 3. Target child replies with an 'ok' and end itself
    WaitForMe("Send actor 1 a tuple")
    actor1 ! ("Child2", "stop")

    // Terminating parent actor also will terminate its children
    WaitForMe("Send actor 1 a PoisonPill")
    actor1 ! PoisonPill


    // For remote system, which we used in our beacon project, how do we get hold of actors?
    // Now all actors should be terminated
    // Let us assume that we do not have actorRef now and will have to search for one
    // Given that we know the location of our actor
    WaitForMe("Ask SecondActor to find LostActor and say hello")
    val SecondActor = system.actorOf(Props[PersonB], "SecondActor")
    val LostActor = system.actorOf(Props[PersonB], "LostActor")

    SecondActor ! findLostActor("/user/LostActor")


    WaitForMe("End?")
  }

  def WaitForMe(txt: String) = {
    // Let other asynchronous processes start before this execute
    Thread.sleep(1000)
    println()
    readLine(s"\t$txt? ")
    println()
  }
}
