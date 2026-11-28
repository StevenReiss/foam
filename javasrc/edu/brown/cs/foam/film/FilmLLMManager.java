/********************************************************************************/
/*                                                                              */
/*              FilmManager.java                                                */
/*                                                                              */
/*      Manager for LLM access for FOAM                                         */
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



package edu.brown.cs.foam.film;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import edu.brown.cs.foam.fide.FideSystem;
import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamLLMManager;
import edu.brown.cs.ivy.exec.IvyExec;
import edu.brown.cs.ivy.exec.IvyExecQuery;
import edu.brown.cs.ivy.file.IvyLog;
import edu.brown.cs.ivy.mint.MintArguments;
import edu.brown.cs.ivy.mint.MintDefaultReply;
import edu.brown.cs.ivy.mint.MintHandler;
import edu.brown.cs.ivy.mint.MintMessage;
import edu.brown.cs.ivy.mint.MintConstants.CommandArgs;
import edu.brown.cs.ivy.xml.IvyXml;
import edu.brown.cs.ivy.xml.IvyXmlWriter;

public class FilmLLMManager implements FoamLLMManager, FilmConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private FideSystem      foam_main;
private boolean         limba_starting;
private Map<String,Element> limba_replies;
private boolean         limba_ready;
private String          limba_id;


private static boolean  debug_limba = true;
private static boolean  trace_limba = true;



/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

public FilmLLMManager(FideSystem sys)
{
   foam_main = sys;
   limba_starting = false;
   limba_replies = new HashMap<>();
   limba_id = "FOAM_" + (int) (Math.random()*1000000);
   
   setProject();
}


/********************************************************************************/
/*                                                                              */
/*      Setup methods                                                           */
/*                                                                              */
/********************************************************************************/

public void setProject()
{
   if (foam_main.haveMessageServer() && !limba_ready) {
      startLimba();
      if (limba_ready) {
         foam_main.registerMessage("<LIMBAREPLY />",new LimbaHandler());
       }
    }
}



/********************************************************************************/
/*                                                                              */
/*      Command handling methods                                                */
/*                                                                              */
/********************************************************************************/

@Override public void clearHistory()
{
   if (limba_ready) {
      sendLimbaMessage("CLEAR",null,null);
    }
}


@Override public void setModel(String mdl)
{
   if (limba_ready) {
      CommandArgs args = new CommandArgs("MODEL",mdl);
      sendLimbaMessage("SETMODEL",args,null);
    }
}



/********************************************************************************/
/*                                                                              */
/*      Handle queries                                                          */
/*                                                                              */
/********************************************************************************/

@Override public Element sendQuery(String query) throws FoamException
{
   Element rslt = sendLimbaMessage("ASKLIMBA",null,query);

   return rslt;
}






/********************************************************************************/
/*                                                                              */
/*      Get prompt from resource file                                           */
/*                                                                              */
/********************************************************************************/

@Override public String getPrompt(String cmd)
{
   InputStream ins = getClass().getClassLoader().getResourceAsStream("resources/prompts.xml");
   if (ins == null) {
      ins = getClass().getClassLoader().getResourceAsStream("prompts.xml");
    }
   if (ins == null) return null;
   Element xml = IvyXml.loadXmlFromStream(ins);
   if (xml == null) return null;
   String base = null;
   String ptxt = null;
   boolean useboth = true;
   for (Element pmpt : IvyXml.children(xml,"PROMPT")) {
      String what = IvyXml.getAttrString(pmpt,"COMMAND");
      if (what == null) base = IvyXml.getText(pmpt);
      else {
         StringTokenizer tok = new StringTokenizer(what,",; ");
         while (tok.hasMoreTokens()) {
            String w = tok.nextToken();
            if (cmd.equals(w)) {
               ptxt = IvyXml.getText(pmpt);
               if (IvyXml.getAttrBool(pmpt,"COMPLETE")) {
                  useboth = false;
                }
             }
          }
       }
    }
   
   if (base == null) return ptxt;
   if (ptxt == null) return null;                 // command must be in prompts file
   if (!useboth) return ptxt;
   
   return base + "\n" + ptxt;
}



/********************************************************************************/
/*                                                                              */
/*      Send message to limba                                                   */
/*                                                                              */
/********************************************************************************/

Element sendLimbaMessage(String cmd,CommandArgs args,String cnts)
{
   MintDefaultReply rply = new MintDefaultReply();
   
   IvyXmlWriter xw = new IvyXmlWriter();
   xw.begin("LIMBA");
   xw.field("DO",cmd);
   if (args == null) args = new CommandArgs("ID",limba_id);
   else args.put("ID",limba_id);
   String rid = "LIMBA_" + (int) (Math.random()*1000000);
   args.put("RID",rid);
   for (Map.Entry<String,Object> ent : args.entrySet()) {
      xw.field(ent.getKey(),ent.getValue());
    }
   if (cnts != null) {
      xw.xmlText(cnts);
    }
   xw.end("LIMBA");
   String msg = xw.toString();
   xw.close();
   
   IvyLog.logD("DICONTROL","Send to LIMBA: " + msg);
   
   foam_main.sendMessage(msg,rply);
   
   Element rslt = rply.waitForXml(0);
   String nrid = IvyXml.getAttrString(rslt,"RID");
   if (nrid != null) {
      if (!rid.equals(nrid)) {
         IvyLog.logE("DICCONTROL","Reply ids don't match " + rid + " " + nrid);
       }
      IvyLog.logD("DICONTROL","Waiting for limba reply " + nrid);
      synchronized (limba_replies) {
         for ( ; ; ) {
            if (limba_replies.containsKey(nrid)) {
               rslt = limba_replies.remove(nrid);
               break;
             }
            try {
               limba_replies.wait(3000);
             }
            catch (InterruptedException e) { }
          }
       }
    }
   
   IvyLog.logD("DICONTROL","Reply from LIMBA: " + IvyXml.convertXmlToString(rslt));
   
   return rslt;
}



/********************************************************************************/
/*                                                                              */
/*      Start limba server                                                      */
/*                                                                              */
/********************************************************************************/

//CHECKSTYLE:OFF
private boolean startLimba()
// CHECKSTYLE:ON
{
   if (limba_starting) return false;
   limba_starting = true;
   
   IvyLog.logD("DITEST","Starting Limba server");
   
   IvyExec exec = null;
   File wd = null;
   String dirnm = null;
   try {
      dirnm = foam_main.getProjectManager().getCurrentProjectDirectory();
      wd = new File(dirnm);
      wd = new File(dirnm,"metadata");
    }
   catch (FoamException e) { }
   if (wd == null) wd = new File(System.getProperty("user.home"));
   File logf = new File(wd,"limba.log");
   File trans = new File(wd,"limbatrans.html");
   
   List<String> args = new ArrayList<>();
   args.add(IvyExecQuery.getJavaPath());
   
   File jarfile = getLimbaBinary();
   
   String xcp = foam_main.getProperty("Foam.limba.class.path",null);
   if (xcp == null) {
      xcp = System.getProperty("java.class.path");
      String ycp = foam_main.getProperty("Foam.limba.add.path",null);
      if (ycp != null) xcp = ycp + File.pathSeparator + xcp;
    }
   else {
      StringBuffer buf = new StringBuffer();
      StringTokenizer tok = new StringTokenizer(xcp,":;");
      while (tok.hasMoreTokens()) {
         String elt = tok.nextToken();
         if (!elt.startsWith("/") &&  !elt.startsWith("\\")) {
            if (elt.equals("eclipsejar")) {
               elt = getEclipsePath();
             }
            else if (elt.equals("limba.jar") && jarfile != null) {
               elt = jarfile.getPath();
             }
            else {
               elt = getLibraryPath(elt);
             }
          }
         if (buf.length() > 0) buf.append(File.pathSeparator);
         buf.append(elt);
       }
      xcp = buf.toString();
    }
   xcp = xcp.trim();
   args.add("-cp");
   args.add(xcp);
   
   args.add("edu.brown.cs.limba.limba.LimbaMain");
   args.add("-m");
   args.add(foam_main.getMintId());
   args.add("-L");
   args.add(logf.getPath());
   if (debug_limba) {
      args.add("-D");
    }
   if (trace_limba) {
      args.add("-T");
      args.add(trans.getPath());
    }
   String oh = foam_main.getProperty("Foam.ollama.host",null);
   if (oh != null && !oh.isEmpty()) {
      args.add("-host");
      args.add(oh);
    }
   String op = foam_main.getProperty("Foam.ollama.port",null);
   if (op != null) {
      args.add("-port");
      args.add(op);
    }
   String mdl = foam_main.getProperty("Foam.ollama.model",null);
   if (mdl != null && !mdl.isEmpty()) {
      args.add("-llama");
      args.add(mdl);
    }
   
   boolean isnew = false;
   for (int i = 0; i < 500; ++i) {
      Element rslt = sendLimbaMessage("PING",null,null);
      if (rslt != null) {
         break;
       }
      if (i == 0) {
         try {
            // make IGNORE_OUTPUT to clean up otutput
            exec = new IvyExec(args,null,IvyExec.ERROR_OUTPUT);    
            isnew = true;
            IvyLog.logD("DITEST","Run " + exec.getCommand());
          }
         catch (IOException e) {
            break;
          }
       }
      else {
         try {
            if (exec != null) {
               int sts = exec.exitValue();
               IvyLog.logD("DITEST",
                     "Limba server disappeared with status " + sts);
               break;
             }
          }
         catch (IllegalThreadStateException e) { }
       }
      
      try {
         Thread.sleep(2000);
       }
      catch (InterruptedException e) { }
    }
   
   
   String sty = foam_main.getProperty("Foam.limba.style",null);
   if (sty != null) {
      IvyXmlWriter xw = new IvyXmlWriter();
      xw.cdataElement("STYLE",sty);
      String cnts = xw.toString();
      xw.close();
      sendLimbaMessage("STYLE",null,cnts);
    }
   
   sendLimbaMessage("PROJECT",null,null);
   
   limba_starting = false;
   limba_ready = true;
   
   return isnew;
}



private File getLimbaBinary()
{
   String cp = System.getProperty("java.class.path");
   StringTokenizer tok = new StringTokenizer(cp,File.pathSeparator);
   while (tok.hasMoreTokens()) {
      String pe = tok.nextToken();
      File pf = new File(pe);
      if (pf.getName().equals("foam.jar")) {
         File ppf = pf.getParentFile();
         return ppf;
       }
    }
   
   String pro = System.getenv("PRO");
   if (pro == null) pro = System.getenv("BROWN_IVY_PRO");
   if (pro == null) pro = "/pro";
   File f0 = new File(pro);
   File f1 = new File(f0,"bubbles");
   File f2 = new File(f1,"dropins");
   File f3 = new File(f2,"limba.jar");
   return f3;
}


private String getEclipsePath()
{
   String ourcp = System.getProperty("java.class.path");
   StringTokenizer tok = new StringTokenizer(ourcp,File.pathSeparator);
   StringBuffer rslt = new StringBuffer();
   while (tok.hasMoreTokens()) {
      String pe = tok.nextToken();
      if (pe.contains("org.eclipse.") || pe.contains("com.google.")) {
         if (!rslt.isEmpty()) rslt.append(File.pathSeparator);
         rslt.append(pe);
       }
    }
   
   return rslt.toString();
}


private String getLibraryPath(String elt)
{
   String ourcp = System.getProperty("java.class.path");
   StringTokenizer tok = new StringTokenizer(ourcp,File.pathSeparator);
   while (tok.hasMoreTokens()) {
      String pe = tok.nextToken();
      if (pe.contains(elt)) {
         return pe;
       }
      else if (elt.equals("ivy.jar") && pe.contains("ivy/java")) {
         return pe;
       }
    }
   
   return elt;
}


/********************************************************************************/
/*                                                                              */
/*      Handle Limba messages                                                   */
/*                                                                              */
/********************************************************************************/

private final class LimbaHandler implements MintHandler {
   
   @Override public void receive(MintMessage msg,MintArguments args) {
      Element xml = msg.getXml();
      String cmd = IvyXml.getAttrString(xml,"DO");
      if (cmd != null && cmd.equals("PING")) {
         msg.replyTo("<PONG/>");
         return;
       }
      String rid = IvyXml.getAttrString(xml,"RID");
      if (rid != null) {
         synchronized (limba_replies) {
            limba_replies.put(rid,IvyXml.getChild(xml,"RESULT"));
            limba_replies.notifyAll();
          }
       }
    }
   
}       // end of inner class LimbaHandler



}       // end of class FilmManager




/* end of FilmManager.java */

