package com.akmatov.restful_api_example.dao

import com.akmatov.restful_api_example.models.Account
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

class AccountDAOSpec(implicit ee: ExecutionEnv) extends Specification {

  sequential

  private val service = new AccountDAO

  "create" should {
    "return new Account object" in {
      service.create() must beSome[Account].await
    }
  }

  "get" should {
    "return existing account" in {
      service.get(1L) must beSome[Account].await
    }

    "return None if no account is found with given id" in {
      service.get(-1L) must beNone.await
    }
  }

  "deposit" should {
    "return modified account" in {
      service.deposit(1L, BigDecimal(100.0)) must beSuccessfulTry.withValue(
        Account(1L, BigDecimal(100.0))
      ).await

      service.deposit(1L, BigDecimal(100.0)) must beSuccessfulTry.withValue(
        Account(1L, BigDecimal(200.0))
      ).await
    }

    "persist new account balance" in {
      service.get(1L) must beSome(
        Account(1L, BigDecimal(200.0))
      ).await
    }

    "throw exception when input amount is negative" in {
      service.deposit(1L, BigDecimal(-100.0)) must beFailedTry.withThrowable[IllegalArgumentException].await
    }

    "throw exception when account not found" in {
      service.deposit(-1L, BigDecimal(100.0)) must beFailedTry.withThrowable[IllegalArgumentException].await
    }
  }

  "withdraw" should {
    "return modified account" in {
      service.withdraw(1L, BigDecimal(50.0)) must beSuccessfulTry.withValue(
        Account(1L, BigDecimal(150.0))
      ).await

      service.withdraw(1L, BigDecimal(50.0)) must beSuccessfulTry.withValue(
        Account(1L, BigDecimal(100.0))
      ).await
    }

    "persist new account balance" in {
      service.get(1L) must beSome(
        Account(1L, BigDecimal(100.0))
      ).await
    }

    "throw exception when balance after withdraw operation is negative" in {
      service.withdraw(1L, BigDecimal(-101.0)) must beFailedTry.withThrowable[IllegalArgumentException].await
    }

    "throw exception when input amount is negative" in {
      service.withdraw(1L, BigDecimal(-100.0)) must beFailedTry.withThrowable[IllegalArgumentException].await
    }

    "throw exception when account not found" in {
      service.withdraw(-1L, BigDecimal(100.0)) must beFailedTry.withThrowable[IllegalArgumentException].await
    }
  }

}
