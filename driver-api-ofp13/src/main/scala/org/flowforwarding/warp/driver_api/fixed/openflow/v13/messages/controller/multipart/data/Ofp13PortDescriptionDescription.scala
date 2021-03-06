/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.{Ofp13PortDescription, Port}
import spire.math.ULong

case class PortDescriptionRequestBodyInput() extends EmptyMultipartRequestBodyInput

trait PortDescriptionReplyBody extends MultipartReplyBody[Array[Port]]

trait PortDescriptionReplyHandler{
  def onPortDescriptionReply(dpid: ULong, desc: Array[Port]): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13PortDescriptionDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with PortDescriptionReplyHandler] with Ofp13PortDescription =>

  protected class PortDescriptionRequestBodyInputBuilder extends OfpStructureBuilder[PortDescriptionRequestBodyInput] {
    protected def applyInput(input: PortDescriptionRequestBodyInput): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): PortDescriptionRequestBodyInput = PortDescriptionRequestBodyInput()
  }

  protected abstract override def builderClasses = classOf[PortDescriptionRequestBodyInputBuilder] :: super.builderClasses
}