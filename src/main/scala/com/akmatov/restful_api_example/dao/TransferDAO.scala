package com.akmatov.restful_api_example.dao

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import com.akmatov.restful_api_example.models.Transfer

import scala.collection.JavaConverters._
import scala.collection.concurrent
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class TransferDAO(accountDAO: AccountDAO)(implicit ec: ExecutionContext) {

  private val db: concurrent.Map[Long, Transfer] = new ConcurrentHashMap[Long, Transfer]().asScala
  private val idSeq = new AtomicLong(0L)

  def transfer(senderAccountId: Long, receiverAccountId: Long, amount: BigDecimal): Future[Try[Transfer]] = {
    accountDAO.withdraw(senderAccountId, amount).flatMap {
      case Success(_) =>
        accountDAO.deposit(receiverAccountId, amount).flatMap {
          case Success(_) => Future {
            Try {
              val newTransferId = idSeq.incrementAndGet()
              val newTransfer = Transfer(newTransferId, senderAccountId, receiverAccountId, amount)
              db(newTransferId) = newTransfer
              newTransfer
            }
          }
          case Failure(e) =>
            accountDAO.deposit(senderAccountId, amount).map {
              case Success(_) =>
                Failure(e)
              case Failure(_) =>
                Failure(e)
            }
        }

      case Failure(e) => Future(Failure(e))
    }
  }

}
