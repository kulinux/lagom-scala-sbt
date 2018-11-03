package com.paco.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.paco.api.PacoService

import scala.concurrent.Future

class PacoServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)
  extends PacoService {

  override def sayHello(): ServiceCall[NotUsed, String] = ServiceCall{
    _ => Future.successful("Hola, como estas!!!")
  }

  override def store(): ServiceCall[NotUsed, String] = ServiceCall{ _ =>
    // Look up the hello entity for the given ID.
    val ref = persistentEntityRegistry.refFor[PacoEntity]("YYidToAsk")
    ref.ask(PacoCommandMessage("Quiero guardar esto"))
    Future.successful("Stored!!")
  }
}
