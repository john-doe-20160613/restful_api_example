package com.akmatov.restful_api_example.models

final case class Transfer(
  id: Long,
  senderAccountId: Long,
  receiverAccountId: Long,
  amount: BigDecimal
)
