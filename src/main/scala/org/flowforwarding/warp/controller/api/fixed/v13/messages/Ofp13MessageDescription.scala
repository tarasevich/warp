package org.flowforwarding.warp.controller.api.fixed.v13.messages

import scala.reflect.ClassTag

import spire.math._

import org.flowforwarding.warp.controller.XidGenerator
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructure
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{Ofp13HeaderDescription, HeaderInput, Header}
import org.flowforwarding.warp.controller.api.fixed.v13.structures.HeaderInput

trait Ofp13Message{
  def header: Header
}

trait Ofp13MessageInput extends BuilderInput{
  val header: HeaderInput = new HeaderInput(xid)
  private def xid: UInt = UInt(XidGenerator.nextXid())
}

private[fixed] trait Ofp13MessageDescription extends MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]] with Ofp13HeaderDescription =>

  type MessageInput = Ofp13MessageInput

  abstract class OfpMessage[T: ClassTag](s: DynamicStructure) extends OfpStructure(s) {
    apiToImplement: T =>  // Forces to implement necessary methods. I'm not sure is it really good idea.
    def header: Header = structureField[Header]("header")
  }

  protected trait OfpMessageBuilder[Input <: Ofp13MessageInput] extends OfpStructureBuilder[Input]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: Input): Unit = {
      // builder already must contain message type and version !!!
      setMember("header", input.header)
    }
  }
}