package demo
import akka.actor.{ActorRef, Props, Actor, PoisonPill}

object PersonA{
  final case class targetChild(cName: String)
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
    case "printit" =>
      println(s"Self: $self")

    case (sName :String, sMsg :String) =>
      println(s"$myName: Received tuple($sName, $sMsg) ")
      // We can also forward to child, so the $sender in child will be the originator
      ChildList.get(sName).foreach(_ ! sMsg)

    case "ok" =>
      println(s"$myName: Got 'ok' from $sender()")

    case targetChild(sName) =>
      println(s"$myName: Received command to terminate $sName")
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
    println(s"$myName is gone")
  }

  override def receive: Receive = {
    case "stop" =>
      println(s"$myName: Received 'stop' message from ${sender.path.name}, replying with 'ok'")
      // Reply to original sender
      sender() ! "ok"
      // End of life
      context.stop(self)
  }
}
