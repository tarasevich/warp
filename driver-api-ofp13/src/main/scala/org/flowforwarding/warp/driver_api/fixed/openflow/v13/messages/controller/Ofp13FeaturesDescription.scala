/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.util._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.Ofp13HeaderDescription
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

case class SwitchCapabilities(
  flowStats:   Boolean = false,
  tableStats:  Boolean = false,
  portStats:   Boolean = false,
  groupStats:  Boolean = false,
  ipReasm:     Boolean = false,
  queueStats:  Boolean = false,
  portBlocked: Boolean = false) extends Bitmap

trait FeaturesReply extends Ofp13Message{
  def datapathId: ULong
  def buffersCount: UInt
  def tablesCount: UByte
  def auxiliaryId: UByte
  def capabilities: SwitchCapabilities
}

case class FeaturesRequestInput() extends Ofp13MessageInput

private[fixed] trait FeaturesReplyHandler{
  def onFeaturesReply(dpid: ULong, msg: FeaturesReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13FeaturesDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with FeaturesReplyHandler] with Ofp13HeaderDescription =>

  private class FeaturesRequestBuilder extends OfpMessageBuilder[FeaturesRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): FeaturesRequestInput = FeaturesRequestInput()
  }

  private class FeaturesReplyStructure(s: DynamicStructure) extends OfpMessage[FeaturesReply](s) with FeaturesReply {
    val datapathId: ULong  = primitiveField[ULong]("datapath_id")
    val buffersCount: UInt = primitiveField[UInt]("n_buffers")
    val tablesCount: UByte = primitiveField[UByte]("n_tables")
    val auxiliaryId: UByte = primitiveField[UByte]("auxiliary_id")
    val capabilities: SwitchCapabilities = bitmapField[SwitchCapabilities]("capabilities")
  }

  protected abstract override def builderClasses = classOf[FeaturesRequestBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[FeaturesReplyStructure] :: super.messageClasses
}