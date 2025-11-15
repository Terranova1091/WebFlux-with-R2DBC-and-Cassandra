package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicMessageSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling-Load-Test")
    .shareConnections

  val healthCheckScenario = scenario("Health Check")
    .exec(
      http("Get Message List")
        .get("/message/getList")
        .check(status.is(200))
        .check(jsonPath("$").exists)
    )
    .pause(1.second)

  setUp(
    healthCheckScenario.inject(
      nothingFor(5.seconds),
      rampUsersPerSec(1).to(500).during(30.seconds),
      constantUsersPerSec(10).during(1.minute)
    )
  ).protocols(httpProtocol)
}