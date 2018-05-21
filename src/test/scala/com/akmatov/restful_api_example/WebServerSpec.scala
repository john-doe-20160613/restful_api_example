package com.akmatov.restful_api_example

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.akmatov.restful_api_example.models.{Account, JsonSupport, Transfer}
import org.scalatest.{Matchers, WordSpec}

class WebServerSpec extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport {

  "WebServer" should {

    "return new account for POST requests to /account/create" in {
      Post("/account/create") ~> WebServer.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Account] shouldEqual Account(1L, BigDecimal(0.0))
      }
    }

    "return existing account for GET requests to /account/id" in {
      Get("/account/1") ~> WebServer.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Account] shouldEqual Account(1L, BigDecimal(0.0))
      }
    }

    "return NotFound response for GET requests to /account/id when id does not exist" in {
      Get("/account/2") ~> WebServer.route ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "return modified account for POST requests to /account/deposit" in {
      Post("/account/1/deposit?amount=100") ~> WebServer.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Account] shouldEqual Account(1L, BigDecimal(100.0))
      }
    }

    "return modified account for second POST requests to /account/deposit" in {
      Post("/account/1/deposit?amount=100") ~> WebServer.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Account] shouldEqual Account(1L, BigDecimal(200.0))
      }
    }

    "return transfer for POST requests to /transfer" in {
      Post("/account/create") ~> WebServer.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Account] shouldEqual Account(2L, BigDecimal(0.0))
      }

      Post("/transfer?sender_account_id=1&receiver_account_id=2&amount=100") ~> WebServer.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Transfer] shouldEqual Transfer(1L, 1L, 2L, BigDecimal(100.0))
      }

      Get("/account/1") ~> WebServer.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Account] shouldEqual Account(1L, BigDecimal(100.0))
      }

      Get("/account/2") ~> WebServer.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Account] shouldEqual Account(2L, BigDecimal(100.0))
      }
    }

  }

}
