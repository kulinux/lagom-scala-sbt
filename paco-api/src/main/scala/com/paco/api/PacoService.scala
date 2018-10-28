package com.paco.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait PacoService extends Service {

  def sayHello() : ServiceCall[NotUsed, String]

  override def descriptor: Descriptor = {
    import Service._
    named("paco").withCalls(
     pathCall("/api/paco/sayHello", sayHello _)
    )
  }


}
