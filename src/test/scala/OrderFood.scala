package nl.uva.cci

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.Scheduler
import asl.test
import bb.expstyla.exp.{StringTerm, StructTerm, VarTerm}
import infrastructure._
import org.scalatest.wordspec.AnyWordSpecLike
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
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
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class OrderFood extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  import org.apache.log4j.BasicConfigurator
  BasicConfigurator.configure()



  val mas = MAS()

  override def beforeAll(): Unit = {
    import org.apache.log4j.BasicConfigurator
    BasicConfigurator.configure()


    // Create System

    val system: ActorSystem[IMessage] = typed.ActorSystem(mas(), "MAS")


    //    ClusterBootstrap(system).start()
    implicit val timeout: Timeout = 5000.milliseconds
    implicit val ec: ExecutionContextExecutor = system.executionContext
    implicit val scheduler: Scheduler = system.scheduler

    // Ask the system to create agents
    val result: Future[IMessage] = system.ask(ref => AgentRequestMessage(
      Seq(
        AgentRequest(new test().agentBuilder, "assistant", 1)
      ),ref))(timeout,scheduler)

    //wait for response
    val system_ready : Boolean = try {
      val response = Await.result(result, timeout.duration).asInstanceOf[ReadyMessage]

      true
    }
    catch {
      case x : Throwable =>
        x.printStackTrace()
        false
    }

    if(system_ready)
      println("agent created")
  }

  "the agent" should {
    "exist in yellow pages if it was created before" in {

        assert(mas.yellowPages.getAgent("assistant").isDefined)
    }
  }



  "A food ordering agent" should {

    "stay in french restaurant and order meal(veg,fish,red) if it is already there" in {
      val prob = testKit.createTestProbe[IMessage]()
      mas.yellowPages.getAgent("assistant").get.asInstanceOf[AkkaMessageSource].address()  ! GoalMessage(StructTerm("go_order",Seq(
        VarTerm("L"),StructTerm("meal",Seq(VarTerm("A"),VarTerm("B"),VarTerm("C")))
      )),AkkaMessageSource(prob.ref))
      val msg = prob.receiveMessage()
      assert(msg.isInstanceOf[GoalMessage])
      assert(msg.asInstanceOf[GoalMessage].p_content.asInstanceOf[StructTerm].toString() equals "meal_from(loc(french),meal(veg,fish,red))")
    }

    "go to italian and order meal(fish,meat,white) if asked" in {
      val prob = testKit.createTestProbe[IMessage]()
      mas.yellowPages.getAgent("assistant").get.asInstanceOf[AkkaMessageSource].address()  ! GoalMessage(StructTerm("go_order",Seq(
        StringTerm("italian"),StructTerm("meal",Seq(VarTerm("A"),VarTerm("B"),VarTerm("C")))
      )),AkkaMessageSource(prob.ref))
      val msg = prob.receiveMessage()
      assert(msg.isInstanceOf[GoalMessage])
      assert(msg.asInstanceOf[GoalMessage].p_content.asInstanceOf[StructTerm].toString() equals "meal_from(loc(italian),meal(fish,meat,white))")
    }
  }

  override def afterAll(): Unit = testKit.shutdownTestKit()
}