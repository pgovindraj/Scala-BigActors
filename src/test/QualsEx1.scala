package test

import bigactors._
import bigactors.BigActorImplicits._

object QualsEx1 extends App{

  new BigActor("observer","camera0") {
    def act() {
      loop {
        observe("children.parent.host")
        react {
          case obs: Observation => println(obs)
        }
      }
    }
  }

  new BigActor("env","room0"){
    def act(){
      control("room0_Room.$0 -> room0_Room.(p0_Person|$0)")
      control("room0_Room.$0 -> room0_Room.(p1_Person|$0)")
      control("room0_Room.$0 -> room0_Room.(p2_Person|$0)")
    }
  }
}
