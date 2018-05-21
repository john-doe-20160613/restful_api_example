package com.akmatov.restful_api_example.actors

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.akmatov.restful_api_example.actors.AccountHandler._
import com.akmatov.restful_api_example.dao.{AccountDAO, TransferDAO}

import scala.concurrent.ExecutionContextExecutor

object AccountHandler {

  def props(accountDAO: AccountDAO, transferDAO: TransferDAO): Props = Props(
    new AccountHandler(accountDAO, transferDAO)
  )

  case class GetAccount(id: Long)

  case class Deposit(accountId: Long, amount: BigDecimal)

  case class Withdraw(accountId: Long, amount: BigDecimal)

  case class Transfer(senderAccountId: Long, receiverAccountId: Long, amount: BigDecimal)

  case object CreateNewAccount

}

class AccountHandler(
  accountDAO: AccountDAO,
  transferDAO: TransferDAO
) extends Actor {

  implicit val ec: ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = {
    case CreateNewAccount =>
      accountDAO.create().pipeTo(sender())
    case GetAccount(id) =>
      accountDAO.get(id).pipeTo(sender())
    case Deposit(accountId, amount) =>
      accountDAO.deposit(accountId, amount).pipeTo(sender())
    case Withdraw(accountId, amount) =>
      accountDAO.withdraw(accountId, amount).pipeTo(sender())
    case Transfer(senderAccountId, receiverAccountId, amount) =>
      transferDAO.transfer(senderAccountId, receiverAccountId, amount).pipeTo(sender())
  }

}
