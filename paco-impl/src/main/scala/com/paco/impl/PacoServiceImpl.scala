package com.paco.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.paco.api.PacoService

import scala.concurrent.Future

class PacoServiceImpl extends PacoService {
  override def sayHello(): ServiceCall[NotUsed, String] = ServiceCall{
    _ => Future.successful("Hola, como estas!!!")
  }
}
