package com.paco.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server._
import com.paco.api.PacoService
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

class PacoLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new PacoApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new PacoApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[PacoService])
}

abstract class PacoApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[PacoService](wire[PacoServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = PacoSerializerRegistry

  // Register the hello persistent entity
  persistentEntityRegistry.register(wire[PacoEntity])
}

