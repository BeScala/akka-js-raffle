package eu.unicredit

import scala.scalajs.js
import js.Dynamic.{global => g, literal}

import akka.actor._

object RaffleTwitterActor extends js.JSApp {

  val credentials = loadCredentials()

  def loadCredentials() = {
    val fs = g.require("fs")
    js.JSON.parse(fs.readFileSync(".credentials", "utf8").toString)
  }

  def main() = {

    val system = ActorSystem("raffle-twitter")

    val request = g.require("ajax-request")

    system.actorOf(Props(new Actor {
      import TwitterMsgs._
      val twitterActor = context.actorOf(Props(new TwitterActor()))

      twitterActor ! Track("beescala")

      def receive = {
        case _msg =>
          val msg = _msg.asInstanceOf[js.Dynamic]

          val name = msg.user.screen_name.toString
          val txt = msg.text.toString

          println("user: "+name+" msg: "+txt)

          if (txt.contains("#raffle")) {

          request.post(
            literal(
              //url = "http://fe71875c.ngrok.io/raffle/BeeScala",
              url = "http://69fab5f0.ngrok.io/raffle/BeeScala",
              headers = literal(),
              data = literal(
                participantName = s"$name"
              )
            ),
            (err: js.Dynamic, res: js.Dynamic, body: js.Dynamic) => {
              println("renato answer "+body.toString)
            }
          )
        } else {
          println("sorry no #raffle in your message..")
        }
      }
    }))
  }
}
