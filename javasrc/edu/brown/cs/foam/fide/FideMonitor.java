/********************************************************************************/
/*                                                                              */
/*              FideMonitor.java                                                */
/*                                                                              */
/*      Handle interactions with message server                                 */
/*                                                                              */
/********************************************************************************/
/*      Copyright 2025 Brown University -- Steven P. Reiss                    */
/*********************************************************************************
 *  Copyright 2025, Brown University, Providence, RI.                            *
 *                                                                               *
 *                        All Rights Reserved                                    *
 *                                                                               *
 * This program and the accompanying materials are made available under the      *
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, *
 * and is available at                                                           *
 *      http://www.eclipse.org/legal/epl-v10.html                                *
 *                                                                               *
 ********************************************************************************/



package edu.brown.cs.foam.fide;

import org.w3c.dom.Element;

import edu.brown.cs.ivy.file.IvyLog;
import edu.brown.cs.ivy.mint.MintArguments;
import edu.brown.cs.ivy.mint.MintConstants;
import edu.brown.cs.ivy.mint.MintControl;
import edu.brown.cs.ivy.mint.MintDefaultReply;
import edu.brown.cs.ivy.mint.MintHandler;
import edu.brown.cs.ivy.mint.MintMessage;
import edu.brown.cs.ivy.mint.MintConstants.MintSyncMode;
import edu.brown.cs.ivy.xml.IvyXmlWriter;

class FideMonitor implements FideConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private MintControl     mint_control;
private FideSystem      foam_main;



/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

FideMonitor(FideSystem sys,String mintid)
{ 
   foam_main = sys;
   mint_control = MintControl.create(mintid,MintSyncMode.ONLY_REPLIES);
   mint_control.register("<FOAM DO='_VAR_0' />",
         new CommandHandler());
   mint_control.register("<BUBBLES DO='EXIT' />",new ExitHandler());
   
   IvyLog.logD("FIDE","Listening for messages on " + mintid);
}



/********************************************************************************/
/*                                                                              */
/*      Command methods                                                         */
/*                                                                              */
/********************************************************************************/

boolean sendPing()
{
   try (IvyXmlWriter xw = new IvyXmlWriter()) {
      xw.begin("FOAMREPLY");
      xw.field("DO","PING");
      xw.end("FOAMREPLY");
      MintDefaultReply mdr = new MintDefaultReply();
      mint_control.send(xw.toString(),mdr,
            MintConstants.MINT_MSG_FIRST_NON_NULL);
      String s = mdr.waitForString();
      if (s != null) return true;
    }
   return false;
}


void send(String msg) 
{
   mint_control.send(msg);
}


void pongEclipse()
{
   mint_control.register("<BEDROCK TYPE='PING' />",
         new BubblesPingHandler());
}


/********************************************************************************/
/*                                                                              */
/*      Handler classes                                                         */
/*                                                                              */
/********************************************************************************/

private final class CommandHandler implements MintHandler {
   
   @Override public void receive(MintMessage msg,MintArguments args) {
      IvyLog.logD("LIMBA","PROCESS MSG COMMAND " + msg.getText());
      String cmd = args.getArgument(0);
      Element xml = msg.getXml();
      String rslt = foam_main.processXMLCommand(cmd,xml);
      IvyLog.logD("FOAM","Reply for " + cmd + ": " + rslt);
      msg.replyTo(rslt);
    }
   
}       // end of inner class CommandHandler



private final class ExitHandler implements MintHandler {
   
   @Override public void receive(MintMessage msg,MintArguments args) {
      IvyLog.logD("FOAM","EXIT from Code Bubbles");
      System.exit(0);
    }
   
}       // end of inner class ExitHandler


private final class BubblesPingHandler implements MintHandler {
   
   @Override public void receive(MintMessage msg,MintArguments args) {
      msg.replyTo("<PONG/>");
    }
   
}       // end of inner class ExitHandler



}       // end of class FideMonitor




/* end of FideMonitor.java */

