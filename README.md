# restful_api_example

This project is done to show how easy it is to develop rest web services with Akka HTTP.

run the project with

`sbt run`

This will launch web server with sample api. Press RETURN to stop web server and exit from the command.

## test

While server is running execute CURL commands:

1. `curl -X POST "localhost:8080/account/create"` - Creates first account.
2. `curl -X POST "localhost:8080/account/1/deposit?amount=100"` - Deposits 100 to the first account.
3. `curl -X POST "localhost:8080/account/create"` - Creates second account.
4. `curl -X POST "localhost:8080/transfer?sender_account_id=1&receiver_account_id=2&amount=100"` - Transfers 100 from first to second account.
5. `curl -X GET "localhost:8080/account/1"` - Shows details of first account.
6. `curl -X GET "localhost:8080/account/2"` - Shows details of second account.
