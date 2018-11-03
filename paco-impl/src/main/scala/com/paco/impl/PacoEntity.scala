package com.paco.impl


import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq



class PacoEntity extends PersistentEntity {

  override type Command = PacoCommand[_]
  override type Event = PacoEvent
  override type State = PacoState

  override def initialState: PacoState =
    PacoState("start state", LocalDateTime.now.toString )

  override def behavior: Behavior = {
    case PacoState(message, _) => Actions().onCommand[PacoCommandMessage, Done] {
      case (PacoCommandMessage(newMsg), ctx, state) =>
        ctx.thenPersist( PacoMessageChanged(newMsg, "mono!!!") ) {
          _ => ctx.reply(Done)
        }
    }.onReadOnlyCommand[Paco, String] {
      case (Paco(name), ctx, state) =>
        ctx.reply( s"$message, $name")
    }.onEvent{
      case (PacoMessageChanged(newMessage, mono), state) =>
        PacoState(newMessage, LocalDateTime.now.toString)
    }
  }
}


//command
sealed trait PacoCommand[R] extends ReplyType[R]
case class PacoCommandMessage(message: String) extends PacoCommand[Done]
object PacoCommandMessage {
  implicit val format: Format[PacoCommandMessage] = Json.format
}
case class Paco(name: String) extends PacoCommand[String]
object Paco {
  implicit val format: Format[Paco] = Json.format
}

//event
sealed trait PacoEvent extends AggregateEvent[PacoEvent] {
  override def aggregateTag: AggregateEventTagger[PacoEvent] = PacoEvent.Tag
}
object PacoEvent {
  val Tag = AggregateEventTag[PacoEvent]
}
case class PacoMessageChanged(message: String, mono: String) extends PacoEvent
object PacoMessageChanged {
  implicit val format: Format[PacoMessageChanged] = Json.format
}

//state
case class PacoState(message: String, timestamp: String)
object PacoState {
  implicit val format: Format[PacoState] = Json.format
}


object PacoSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[PacoCommandMessage],
    JsonSerializer[Paco],
    JsonSerializer[PacoMessageChanged],
    JsonSerializer[PacoState]
  )
}

