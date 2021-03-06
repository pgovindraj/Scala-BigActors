/**
 * Created by eloi on 03-07-2014.
 */

package akkaBigActors.examples

import java.nio.file.Paths

import akka.actor.{ActorRef, ActorSystem, Props}
import akkaBigActors.{BigActor, BigActorSchdl, BigMCBigraphManager}
import edu.berkeley.eloi.bigraph.Place

object ExampleAkkaRendezvous4 extends App {
  implicit val system = ActorSystem("mySystem")

  val bigraphManager = system.actorOf(Props(classOf[BigMCBigraphManager], Paths.get(System.getProperty("user.dir")).resolve("src/main/resources/robots.bgm").toString, true))
  val bigraphScheduler = system.actorOf(Props(classOf[BigActorSchdl], bigraphManager))

  system.actorOf(Props(classOf[DiscoverRobots], 'r0)) ! "start"

  class DiscoverRobots(host: Symbol) extends BigActor(host,bigraphScheduler){
    def receive = {
      case "start" =>
        val robots = LINKED_TO_HOST ++ HOST
        robots.foreach{r =>
          system.actorOf(Props(classOf[AgreeAndPursue], Symbol(r.getId.toString)))
        }
    }
  }

  class AgreeAndPursue(host: Symbol) extends BigActor(host,bigraphScheduler){
    var leader: ActorRef = self
    var rvLoc: Place = PARENT_HOST.head
    broadcast
    def receive = {
      case RENDEZVOUS(leader_,rvLoc_)  => {
        if(leader.hashCode <  leader_.hashCode){
          leader = leader_
          rvLoc = rvLoc_
          MOVE_HOST_TO(rvLoc)
          broadcast
        }
      }
      case _ => println("unknown message")
    }
    def broadcast = {
      val bigactors = HOSTED_AT_LINKED_TO_HOST
      bigactors.foreach{b=>
        b ! RENDEZVOUS(leader,rvLoc)
      }
    }
  }
}