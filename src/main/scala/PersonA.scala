package demo
import akka.actor.{Actor, ActorIdentity, ActorRef, Identify, PoisonPill, Props}
import scala.Option
import scala.Some

object PersonA{
  final case class targetChild(cName: String)
  final case class findLostActor(aName: String)
}

class PersonA extends Actor {

  import PersonA._
  var ChildList: Map[String, ActorRef] = Map()
  val myLoc = s"${self.path.parent}/${self.path.name}"
  val myName = s"${self.path.name}"

  override def preStart(): Unit = {
    super.preStart()
    println(s"--preStart-- for $myLoc ")
    // Create 2 child actor under me. Using context
    ChildList += ("Child1" -> context.actorOf(Props[ChildofPersonA], "Child1"))
    ChildList += ("Child2" -> context.actorOf(Props[ChildofPersonA], "Child2"))
    ChildList += ("Child3" -> context.actorOf(Props[ChildofPersonA], "Child3"))
    ChildList += ("Child4" -> context.actorOf(Props[ChildofPersonA], "Child4"))
  }

  override def postStop(): Unit = {
    super.postStop()
    println(s"--postStop-- for $myName")
  }

  override def receive: Receive = {

    case "ShowMeYourPath" =>
      println(s"$myName: Path=$self")

    case (sName :String, sMsg :String) =>
      println(s"$myName: Received tuple($sName, $sMsg) ")

      ChildList.get(sName).foreach(_ ! sMsg)

    case "ok" =>
      println(s"$myName: Got 'ok' from $sender()")

    case targetChild(sName) =>
      println(s"$myName: Received command to terminate $sName, passing a Poison Pill")
      ChildList.get(sName).foreach(_ ! PoisonPill)
  }
}

class ChildofPersonA extends Actor{

  val myLoc = s"${self.path.parent}/${self.path.name}"
  val myName = s"${self.path.name}"
  override def preStart(): Unit = {
    super.preStart()
    println(s"$myLoc is alive")
  }

  override def postStop(): Unit = {
    super.postStop()
    println(s"$myName: is terminated")
  }

  override def receive: Receive = {
    case "stop" =>
      println(s"$myName: Received 'stop' message from ${sender.path.name}, replying with 'ok' and terminate myself")
      // Reply to original sender
      sender() ! "ok"
      // End of life
      context.stop(self)
  }
}
