/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.jcontroller.session;


import java.util.HashMap;
import java.util.Map;

import org.flowforwarding.warp.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageError.OFMessageErrorRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;

import akka.actor.ActorRef;

/**
 * @author Infoblox Inc.
 * @doc.desc OpenFlow protocol Session handler base class
 *
 */
public abstract class OFSessionHandler extends OFActor{
   
   Map<SwitchRef, ActorRef> switches = new HashMap<> ();
   
   @Override
   public void onReceive(Object msg) throws Exception {
      
      // TODO Improvs: replace if-else with HashMap
      if (msg instanceof OFEventHandshaked) {
         
         SwitchRef swR = ((OFEventHandshaked) msg).getSwitchRef();
         switches.put(swR, getSender());
         
         handshaked(swR);         
      } else if (msg instanceof OFEventIncoming) {
         
      } else if (msg instanceof OFEventPacketIn) {
         SwitchRef swR = ((OFEventPacketIn) msg).getSwitchRef();
         OFMessagePacketInRef pIn = ((OFEventPacketIn) msg).getPacketIn();
         packetIn(swR, pIn);
         
      } else if (msg instanceof org.flowforwarding.warp.jcontroller.session.OFEventSwitchConfig) {
         SwitchRef swR = ((org.flowforwarding.warp.jcontroller.session.OFEventSwitchConfig) msg).getSwitchRef();
         OFMessageSwitchConfigRef configH = ((org.flowforwarding.warp.jcontroller.session.OFEventSwitchConfig) msg).getConfigRef();
         switchConfig(swR, configH);
         
      } else if (msg instanceof EventGetSwitches) {
         System.out.println("[OF-INFO]: Get Switches");         
      } else if (msg instanceof OFEventError) {
         error(((OFEventError)msg).getSwitchRef(), ((OFEventError)msg).getError());
      }
   }
   
   /*
    * User-defined Switch event handlers
    */
   protected void handshaked(SwitchRef swR) {}
   protected void connected(SwitchRef swR) {}
   protected void packetIn(SwitchRef swR, OFMessagePacketInRef pIn) {}
   protected void switchConfig(SwitchRef swR, OFMessageSwitchConfigRef configH) {}
   protected void error(SwitchRef swR, OFMessageErrorRef error) {}
   
   
   /**
    * 
    * @param swR
    * Reference to Switch
    */
   protected void sendSwitchConfigRequest (SwitchRef swR) {
      switches.get(swR).tell(new org.flowforwarding.warp.jcontroller.session.OFCommandSendSwConfigRequest(), getSelf());
   }
   
   /**
    * 
    * @param swR
    * Reference to switch
    * @param flowMod
    * Reference to OF Flow Mod 
    */
   protected void sendFlowModMessage (SwitchRef swR, OFMessageFlowModRef flowMod) {
      switches.get(swR).tell(new org.flowforwarding.warp.jcontroller.session.OFCommandSendSwConfigRequest(), getSelf());
   }
}
