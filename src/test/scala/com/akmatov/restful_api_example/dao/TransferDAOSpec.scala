package com.akmatov.restful_api_example.dao

import com.akmatov.restful_api_example.models.{Account, Transfer}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration._

class TransferDAOSpec(implicit ee: ExecutionEnv) extends Specification {

  "transfer" should {

    "return failure when sender account id is not found" in {
      val accountDAO = new AccountDAO()
      val dao = new TransferDAO(accountDAO)
      dao.transfer(1L, 2L, BigDecimal(100.0)) must beFailedTry
        .withThrowable[IllegalArgumentException].await
    }

    "return failure when receiver account id is not found" in {
      val accountDAO = new AccountDAO()
      val maybeAccount = Await.result(accountDAO.create(), 1.second)

      maybeAccount should beSome

      val senderAccount = maybeAccount.get
      val dao = new TransferDAO(accountDAO)
      dao.transfer(senderAccount.id, 2L, BigDecimal(100.0)) must beFailedTry
        .withThrowable[IllegalArgumentException].await
    }

    "return failure when sender does not have enough money" in {
      val accountDAO = new AccountDAO()
      val maybeAccount = Await.result(accountDAO.create(), 1.second)

      maybeAccount should beSome

      val senderAccount = maybeAccount.get

      val maybeAccount2 = Await.result(accountDAO.create(), 1.second)

      maybeAccount2 should beSome

      val receiverAccount = maybeAccount2.get

      val dao = new TransferDAO(accountDAO)
      dao.transfer(senderAccount.id, receiverAccount.id, BigDecimal(100.0)) must beFailedTry
        .withThrowable[IllegalArgumentException].await
    }


    "withdraw money from sender's account and deposit money to receiver's account" in {
      val accountDAO = new AccountDAO()
      val maybeAccount = Await.result(accountDAO.create(), 1.second)

      maybeAccount should beSome

      val senderAccount = maybeAccount.get

      accountDAO.deposit(senderAccount.id, BigDecimal(100.0)) must beSuccessfulTry.withValue(
        Account(1L, BigDecimal(100.0))
      ).await

      val maybeAccount2 = Await.result(accountDAO.create(), 1.second)

      maybeAccount2 should beSome

      val receiverAccount = maybeAccount2.get

      val dao = new TransferDAO(accountDAO)
      dao.transfer(senderAccount.id, receiverAccount.id, BigDecimal(100.0)) must beSuccessfulTry.withValue(
        Transfer(1L, 1L, 2L, BigDecimal(100.0))
      ).await

      accountDAO.get(2L) must beSome(Account(2L, BigDecimal(100.0))).await
    }

  }

}
