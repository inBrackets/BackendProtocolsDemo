[![Integration Tests](https://github.com/inBrackets/BackendProtocolsDemo/actions/workflows/integration-tests.yml/badge.svg)](https://github.com/inBrackets/BackendProtocolsDemo/actions/workflows/integration-tests.yml)

# Backend Protocols Demo

Spring Boot application showcasing four backend protocols with integration tests:

| Protocol | Endpoint | Response |
|----------|----------|----------|
| REST | `GET http://localhost:8080/api/hello` | `{"message":"Hello World!"}` |
| gRPC | `localhost:9090 / hello.HelloService` | Unary + infinite server-streaming |
| WebSocket | `ws://localhost:8080/ws-stomp` | STOMP infinite streaming |
| SOAP | `http://localhost:8080/soap/hello.wsdl` | `<message>Hello World!</message>` |
