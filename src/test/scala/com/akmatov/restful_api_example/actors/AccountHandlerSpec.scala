package com.akmatov.restful_api_example.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.akmatov.restful_api_example._
import com.akmatov.restful_api_example.actors.AccountHandler._
import com.akmatov.restful_api_example.dao.{AccountDAO, TransferDAO}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class AccountHandlerSpec extends TestKit(ActorSystem("MySpec"))
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  private val accountDAO = new AccountDAO()
  private val transferDAO = new TransferDAO(accountDAO)
  private val actor = system.actorOf(AccountHandler.props(accountDAO, transferDAO))

  "AccountHandler actor" should {
    "CreateNewAccount" in {
      actor ! CreateNewAccount

      expectMsg(Some(models.Account(1L, BigDecimal(0.0))))
    }

    "GetAccount" in {
      actor ! GetAccount(1L)

      expectMsg(Some(models.Account(1L, BigDecimal(0.0))))
    }

    "Deposit" in {
      actor ! Deposit(1L, 100.0)

      expectMsg(Success(models.Account(1L, BigDecimal(100.0))))
    }

    "Withdraw" in {
      actor ! Withdraw(1L, 50.0)

      expectMsg(Success(models.Account(1L, BigDecimal(50.0))))
    }

    "Transfer" in {
      actor ! CreateNewAccount

      expectMsg(Some(models.Account(2L, BigDecimal(0.0))))

      actor ! Transfer(1L, 2L, BigDecimal(50.0))

      expectMsg(Success(models.Transfer(1L, 1L, 2L, BigDecimal(50.0))))
    }
  }

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

}
