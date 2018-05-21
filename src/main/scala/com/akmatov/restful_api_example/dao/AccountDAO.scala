package com.akmatov.restful_api_example.dao

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import com.akmatov.restful_api_example.models.Account

import scala.collection.JavaConverters._
import scala.collection._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class AccountDAO(implicit ec: ExecutionContext) {

  private val db: concurrent.Map[Long, Account] = new ConcurrentHashMap[Long, Account]().asScala
  private val idSeq = new AtomicLong(0L)

  def create(): Future[Option[Account]] = Future {
    val newId = idSeq.incrementAndGet()
    val newAccount = Account(newId, BigDecimal(0.0))
    Try {
      db.put(newId, newAccount)
      newAccount
    }.toOption
  }

  def get(accountId: Long): Future[Option[Account]] = Future {
    db.get(accountId)
  }

  def deposit(accountId: Long, amount: BigDecimal): Future[Try[Account]] = Future {
    Try {
      if (amount < 0) {
        throw new IllegalArgumentException("negative amount")
      }

      db.get(accountId) match {
        case Some(account) =>
          val newBalance = account.balance + amount
          val newAccount = account.copy(balance = newBalance)
          db(accountId) = newAccount
          newAccount

        case None =>
          throw new IllegalArgumentException(s"Account $accountId does not exist.")
      }
    }
  }

  def withdraw(accountId: Long, amount: BigDecimal): Future[Try[Account]] = Future {
    Try {
      if (amount < 0) {
        throw new IllegalArgumentException("negative amount")
      }

      db.get(accountId) match {
        case Some(account) =>
          val newBalance = account.balance - amount

          if (newBalance >= 0) {
            val newAccount = account.copy(balance = newBalance)
            db(accountId) = newAccount
            newAccount
          } else {
            throw new IllegalArgumentException(s"Account $accountId balance is ${account.balance}. Cannot withdraw $amount.")
          }
        case None =>
          throw new IllegalArgumentException(s"Account $accountId does not exist.")
      }
    }
  }

}
