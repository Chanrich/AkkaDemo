import akka.actor.{ActorIdentity, Identify, Actor}
import demo.PersonA.{findLostActor, targetChild}

class PersonB extends Actor {

  val myLoc = s"${self.path.parent}/${self.path.name}"
  val myName = s"${self.path.name}"
  val idid = 1
  override def receive: Receive = {

    case findLostActor(aName) =>
      println(s"$myName: Received command to find $aName and say hello to it")
      // context.actorSelection("akka.tcp://app@otherhost:1234/user/serviceB")
      context.actorSelection(aName) ! Identify(idid)

    case ActorIdentity(`idid`, Some(lostR)) =>
      val actorRef = lostR
      actorRef ! "Hello"

    case "Hello" =>
      println(s"$myName: Received Hello from ${sender().path.name}")
  }
}

