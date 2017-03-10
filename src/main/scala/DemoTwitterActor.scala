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

      twitterActor ! Track("bescala")

      def receive = {
        case _msg =>
          val msg = _msg.asInstanceOf[js.Dynamic]

          val name = msg.user.screen_name.toString
          val txt = msg.text.toString.toLowerCase

          if (txt.contains("#raffle") 
               && !msg.retweeted.asInstanceOf[Boolean] 
               && !msg.favorited.asInstanceOf[Boolean]) {
          
          println("user: " + name )
          request.post(
            literal(
              url = "http://localhost:9000/raffle/BeScala",
              headers = literal(),
              data = literal(
                participantName = s"$name"
              )
            ),
            (err: js.Dynamic, res: js.Dynamic, body: js.Dynamic) => {
              println(body.toString)
            }
          )
        } else {
          println(s"sorry $name no #raffle in your message..")
        }
      }
    }))
  }
}
