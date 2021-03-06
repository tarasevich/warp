/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller

import spire.syntax.literals._

import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

trait Ofp13AsyncTest extends MessageTestsSet[Ofp13DriverApi] {
  val setAsyncInput = SetAsyncInput(ui"1", ui"2", ui"3", ui"4", ui"5", ui"6")

  abstract override def tests = super.tests +
    (setAsyncInput ->  TestNoError("Set Async")) +
    (GetAsyncRequestInput() -> TestResponse({ case reply: GetAsyncReply =>
      reply.packetInMask1     == setAsyncInput.packetInMask1    &&
      reply.packetInMask2     == setAsyncInput.packetInMask2    &&
      reply.portStatusMask1   == setAsyncInput.portStatusMask1  &&
      reply.portStatusMask2   == setAsyncInput.portStatusMask2  &&
      reply.flowRemovedMask1  == setAsyncInput.flowRemovedMask1 &&
      reply.flowRemovedMask2  == setAsyncInput.flowRemovedMask2
    }, "Get Async"))
}