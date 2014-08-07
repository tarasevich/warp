package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.util.MacAddress
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.util.MacAddress
import spire.math.UShort
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

case class PortModInput(portNumber:
                        PortNumber,
                        hwAddress: MacAddress,
                        config: PortConfig,
                        mask: PortConfig,
                        advertise: PortFeatures) extends Ofp13MessageInput

private[fixed] trait Ofp13PortModDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]] with Ofp13HeaderDescription =>

  private class PortModBuilder extends OfpMessageBuilder[PortModInput]{
    // Fills the underlying builder with the specified input.
    protected override def applyInput(input: PortModInput): Unit = {
      super.applyInput(input)
      setMember("port_no", input.portNumber.number)
      setMember("hw_addr", input.hwAddress.toLong)
      setMember("config", input.config.bitmap)
      setMember("mask", input.mask.bitmap)
      setMember("advertise", input.advertise.bitmap)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): PortModInput =
      PortModInput(
        PortNumber("port_no"),
        "hw_addr",
        bitmap[PortConfig]("config"),
        bitmap[PortConfig]("mask"),
        bitmap[PortFeatures]("advertise"))
    }

  protected abstract override def builderClasses = classOf[PortModBuilder] :: super.builderClasses
}