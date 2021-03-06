/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.demo;

import org.flowforwarding.warp.jcontroller.session.OFSessionHandler;
import org.flowforwarding.warp.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;
import org.flowforwarding.warp.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionRef;

public class SimpleHandler extends OFSessionHandler {

   @Override
   protected void switchConfig(SwitchRef swH, OFMessageSwitchConfigRef configH) {
      super.switchConfig(swH, configH);
      
      System.out.print("[OF-INFO] DPID: " + Long.toHexString(swH.getDpid()) + " Configuration: ");

      if (configH.isFragDrop()) {
         System.out.println("Drop fragments");
      }
      
      if (configH.isFragMask()) {
         System.out.println("Mask");
      }
      
      if (configH.isFragNormal()) {
         System.out.println("Normal");
      }

      if (configH.isFragReasm()) {
         System.out.println("Reassemble");
      }      
      
   }

   @Override
   protected void handshaked(SwitchRef swH) {
      super.handshaked(swH);
      System.out.println("[OF-INFO] HANDSHAKED " + Long.toHexString(swH.getDpid()));
      
      sendSwitchConfigRequest(swH);
   }
   
   @Override
   protected void packetIn(SwitchRef swRef, OFMessagePacketInRef packetIn) {
      super.packetIn(swRef, packetIn);
      IOFMessageProvider provider = swRef.getProvider();
      
      OFMessageFlowModRef flowMod = provider.buildFlowModMsg();
      
      if (packetIn.existMatchInPort()) {
         flowMod.addMatchInPort(packetIn.getMatchInPort().getMatch());
      } else if (packetIn.existMatchEthDst()) {
         flowMod.addMatchEthDst(packetIn.getMatchEthDst().getMatch());
      } else if (packetIn.existMatchEthSrc()) {
         flowMod.addMatchEthSrc(packetIn.getMatchEthSrc().getMatch());
      }
      
      OFStructureInstructionRef instruction = provider.buildInstructionApplyActions();
      instruction.addActionOutput("2");
      flowMod.addInstruction("apply_actions", instruction);

      sendFlowModMessage(swRef, flowMod);
   }
}
