# Vertx and Jersey REST API implementation


## Overview

This project includes three implementations of simple get request 

```
http://localhost:8080/myapp/myresource
```

It will wait 100ms to simulate busy work, and then return "Got it!".


## To run the service

Vert.x implementation

```
cd vertx-rest
mvn clean install vertx:runMod
```

Jersey sync version

```
cd jersey-simple-sync
mvn clean test exec:java
```

Jersey async version

```
cd jersey-simple-async
mvn clean test exec:java
```

## Load Testing

Here is the gatling scripts (using gatling 1.5.6)


```
package org.tongqing

import java.util.Calendar
import java.text.SimpleDateFormat
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._
import org.apache.drill.synth.SchemaSampler
import com.google.common.io.Resources
import com.google.common.base.Charsets
import com.fasterxml.jackson.databind.JsonNode

class Simple extends Simulation {
  val hostIP = "localhost"
  var url = "http://" + hostIP + ":8080/myapp"
  val threads = Integer.getInteger("threads", 1000) // concurrent users
  val rampup = Integer.getInteger("rampup", 1).toLong 
  val duration = Integer.getInteger("duration", 120).toLong
  var repeat = Integer.getInteger("repeat", 10)

  val httpConf = httpConfig
    .baseURL(url)
    .acceptHeader("text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .connection("keep-alive")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:29.0) Gecko/20100101 Firefox/29.0")

  val scn = scenario("Simple Rest Test") 
    .repeat(repeat) {
      exec(
      http("get_request")
        .get("/myresource") // must be lowercase
        .check(status.in(200 to 410))
      )
    }
    


  setUp(
    scn.users(threads).ramp(rampup).protocolConfig(httpConf)
  
  )
}
```
This script simulats 1000 users coming in 10 seconds, and each users will send 100 requests repeatly.
Here is result


|                              | Jersey Sync | Jersey Async | Vert.x |
|------------------------------|-------------|--------------|--------|
| Throughput (requests/second) | ~ 70        | ~ 1600       | ~ 5000 |
| Average Response Time (ms)   | ~12000      | ~ 300        | ~ 105  |