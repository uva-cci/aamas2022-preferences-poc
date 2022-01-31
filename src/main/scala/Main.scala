package nl.uva.cci

import akka.actor.typed
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorSystem, Scheduler}
import akka.util.Timeout
import asl.test
import bb.expstyla.exp.StructTerm
import infrastructure._
import std.DefaultCommunications

import _root_.scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object Main {


  def main(args: Array[String]): Unit = {
    run_success
  }

  def run_success = {
    import org.apache.log4j.BasicConfigurator
    BasicConfigurator.configure()


    // Create System
    val mas = MAS()
    val system: ActorSystem[IMessage] = typed.ActorSystem(mas(), "MAS")


//    ClusterBootstrap(system).start()
    implicit val timeout: Timeout = 5000.milliseconds
    implicit val ec: ExecutionContextExecutor = system.executionContext
    implicit val scheduler: Scheduler = system.scheduler

    // Ask the system to create agents
    val result: Future[IMessage] = system.ask(ref => AgentRequestMessage(
      Seq(
        AgentRequest(new test().agentBuilder, "assistant", 1)
      ),ref))

    //wait for response
    val system_ready : Boolean = try {
      val response = Await.result(result, timeout.duration).asInstanceOf[ReadyMessage]
      true
    }
    catch {
      case _ =>
        false
    }



    if(system_ready) {

    }

//    system.terminate()
  }



}
