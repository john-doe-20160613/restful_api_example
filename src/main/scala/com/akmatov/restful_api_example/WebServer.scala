package com.akmatov.restful_api_example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.akmatov.restful_api_example.actors.AccountHandler
import com.akmatov.restful_api_example.actors.AccountHandler.{CreateNewAccount, Deposit, GetAccount}
import com.akmatov.restful_api_example.dao.{AccountDAO, TransferDAO}
import com.akmatov.restful_api_example.models.{Account, JsonSupport, Transfer}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object WebServer extends JsonSupport {

  implicit private val system: ActorSystem = ActorSystem("my-system")
  implicit private val executionContext: ExecutionContext = system.dispatcher
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

  // dao
  private val accountDAO = new AccountDAO
  private val transferDAO = new TransferDAO(accountDAO)

  //actors
  private val accountHandler = system.actorOf(AccountHandler.props(accountDAO, transferDAO))
  implicit private val timeout: Timeout = Timeout(5.seconds)

  val route: Route = pathPrefix("account") {
    path("create") {
      post {
        val maybeAccount = (accountHandler ? CreateNewAccount).mapTo[Option[Account]]
        onSuccess(maybeAccount) {
          case Some(account) =>
            complete(account)
          case None =>
            complete(StatusCodes.InternalServerError)
        }
      }
    } ~ path(LongNumber) { id =>
      get {
        val maybeAccount = (accountHandler ? GetAccount(id)).mapTo[Option[Account]]
        onSuccess(maybeAccount) {
          case Some(account) =>
            complete(account)
          case None =>
            complete(StatusCodes.NotFound)
        }
      }
    } ~ path(LongNumber / "deposit") { id =>
      post {
        parameters('amount.as[Double]) { amount =>
          val tryAccount = (accountHandler ? Deposit(id, BigDecimal(amount))).mapTo[Try[Account]]
          onSuccess(tryAccount) {
            case Success(account) =>
              complete(account)
            case Failure(_) =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  } ~ path("transfer") {
    post {
      parameters('sender_account_id.as[Long], 'receiver_account_id.as[Long], 'amount.as[Double]) {
        (senderAccountId, receiverAccountId, amount) =>
          val tryTransfer = (accountHandler ? AccountHandler.Transfer(senderAccountId, receiverAccountId, amount)).mapTo[Try[Transfer]]
          onSuccess(tryTransfer) {
            case Success(transfer) =>
              complete(transfer)
            case Failure(_) =>
              complete(StatusCodes.InternalServerError)
          }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
