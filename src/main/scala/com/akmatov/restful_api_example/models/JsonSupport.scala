package com.akmatov.restful_api_example.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val accountFormat: RootJsonFormat[Account] = jsonFormat2(Account)
  implicit val transferFormat: RootJsonFormat[Transfer] = jsonFormat4(Transfer)
}
