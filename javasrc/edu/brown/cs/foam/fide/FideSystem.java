/********************************************************************************/
/*                                                                              */
/*              FideSystem.java                                                 */
/*                                                                              */
/*      description of class                                                    */
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import java.util.Random;

import org.w3c.dom.Element;

import edu.brown.cs.foam.felt.FeltManager;
import edu.brown.cs.foam.film.FilmLLMManager;
import edu.brown.cs.foam.foam.FoamDocumentManager;
import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamLLMManager;
import edu.brown.cs.foam.foam.FoamProjectManager;
import edu.brown.cs.foam.fspec.FspecManager;
import edu.brown.cs.ivy.file.IvyIncludeReader;
import edu.brown.cs.ivy.file.IvyLog;
import edu.brown.cs.ivy.mint.MintConstants;
import edu.brown.cs.ivy.mint.MintHandler;
import edu.brown.cs.ivy.mint.MintReply;
import edu.brown.cs.ivy.xml.IvyXml;
import edu.brown.cs.ivy.xml.IvyXmlReader;
import edu.brown.cs.ivy.xml.IvyXmlWriter;

public class FideSystem implements FideConstants
{


/********************************************************************************/
/*                                                                              */
/*      Main program                                                            */
/*                                                                              */
/********************************************************************************/

public static void main(String [] args)
{
   FideSystem fs = new FideSystem(args);
   fs.process();
}


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private String          mint_id;
private Properties      foam_props;
private File            input_file;
private File            log_file;
private IvyLog.LogLevel log_level;
private boolean         log_stderr;
private String          project_name;
private String          project_dir;

private FideMonitor     msg_server;
private FideProjectManager project_manager;
private FideVersionManager version_manager;
private FilmLLMManager  llm_manager;
private FspecManager    spec_manager;
private FideCommandFactory command_factory;
private FeltManager     document_manager;


private static Random   random_gen = new Random();


/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

FideSystem(String [] args)
{
   mint_id = null;
   input_file = null;
   log_level = IvyLog.LogLevel.DEBUG;
   String home = System.getProperty("user.home");
   File f1 = new File(home);
   log_file = new File(f1,"foam.log");
   log_stderr = false;
   File f2 = new File(f1,".bubbles");
   File f3 = new File(f2,"Foam.props");
   foam_props = new Properties();
   try {
      InputStream ins = getClass().getClassLoader().getResourceAsStream("Foam.props");
      if (ins != null) foam_props.loadFromXML(ins);
    }
   catch (IOException e) {
      IvyLog.logE("FOAM","Problem loading system properties");
    }
   try (InputStream ins1 = new FileInputStream(f3)) {
      foam_props.loadFromXML(ins1);
    }
   catch (FileNotFoundException e) { }
   catch (IOException e) {
      IvyLog.logE("FOAM","Problem loading user properties");
    }
   project_name = null;
   project_dir = null;
   
   scanArgs(args);
}



/********************************************************************************/
/*                                                                              */
/*      Access methods                                                          */
/*                                                                              */
/********************************************************************************/

public FoamProjectManager getProjectManager()
{
   return project_manager;
}


public FideVersionManager getVersionManager()
{
   return version_manager;
}





FideProjectManager getOurProjectManager()
{
   return project_manager;
}

public FoamDocumentManager getDocumentManager()
{
   return document_manager;
}


public FoamLLMManager getLLMManager()
{
   return llm_manager;
}


public FspecManager getSpecManager()
{
   return spec_manager;
}


public String getProperty(String id,String dflt)
{
   String p = project_manager.getProperty(id);
   if (p == null) {
      p = foam_props.getProperty(id,dflt);
    }
   return p;
}

void noteProjectSet()
{
   version_manager.setProject();
   document_manager.setProject();
   spec_manager.setProject();
   if (msg_server == null) {
      try {
         String wsname = project_manager.getCurrentProjectDirectory();
         if (wsname.endsWith(File.separator)) {
            wsname = wsname.substring(0,wsname.length()-1);
          }
         int idx = wsname.lastIndexOf(File.separator);
         if (idx > 0) {
            wsname = wsname.substring(idx+1);
          }
         if (wsname == null) wsname = "";
         else wsname = wsname.replace(" ","_");
         String mintid =  "BUBBLES_" + System.getProperty("user.name").replace(" ","_");
         mintid += "_" + wsname;
         setupMessageServer(mintid);
       }
      catch (FoamException e) {
         IvyLog.logE("FIDE","Problem setting up message server",e);
       }
    }
   llm_manager.setProject();
}



 /********************************************************************************/
/*                                                                              */
/*      Argument scanning                                                       */
/*                                                                              */
/********************************************************************************/

private void scanArgs(String[] args)
{
   for (int i = 0; i < args.length; ++i) {
      if (i+1 < args.length && args[i].startsWith("-")) {
         if (args[i].startsWith("-m")) {                     // -m <mint id>
            mint_id = args[++i];
            continue;
          }
         else if (args[i].startsWith("-f")) {                // -f <input file>
            input_file = new File(args[++i]);
            continue;
          }
         else if (args[i].startsWith("-L")) {                // -Log <logfile>
            setLog(args[++i]);
            continue;
          }
       }
      if (i+2 < args.length && args[i].startsWith("-")) {
         if (args[i].startsWith("-c")) {
            project_name = args[++i];
            project_dir = args[++i];
          }
       }
      if (args[i].startsWith("-")) {
         if (args[i].startsWith("-D")) {                     // -DEBUG
            log_level = IvyLog.LogLevel.DEBUG;
          }
         else if (args[i].startsWith("-O")) {                // -Output (to stderr)
            log_stderr = true;
          }
         else badArgs();
       }
      else {
         badArgs();
       }
    }
}



private void badArgs()
{
   System.err.println("FOAM: foam [-m <mint id>] [-f <xml input>]");
   System.err.println("          [-create project_name directory]");
   System.exit(1);
}


private void setLog(String file) 
{
   long now = System.currentTimeMillis();
   int idx = file.indexOf(".log");
   if (idx < 0) {
      log_file = new File(file);
      return;
    }
   for (int i = 0; i < 20; ++i) {
      String fnm = file;
      if (i > 0) {
         fnm = fnm.substring(0,idx) + "_" + i + ".log";
       }
      File f1 = new File(fnm);
      if (f1.exists() && now - f1.lastModified() < 10000) continue;
      log_file = f1;
      return;
    }
   log_file = new File(file);
}


/********************************************************************************/
/*                                                                              */
/*      Processing methods                                                      */
/*                                                                              */
/********************************************************************************/

private void process()
{
   msg_server = null;
   
   IvyLog.setupLogging("FOAM",true);
   IvyLog.setLogLevel(log_level);
   IvyLog.setLogFile(log_file);
   IvyLog.useStdErr(log_stderr);
   
   project_manager = new FideProjectManager(this);
   version_manager = new FideVersionManager(this);
   document_manager = new FeltManager(this);
   llm_manager = new FilmLLMManager(this);
   spec_manager = new FspecManager(this);
   command_factory = new FideCommandFactory(this);
   
   if (mint_id != null) {
      setupMessageServer(mint_id);
    }
   if (project_name != null && project_dir != null) {
      try {
         project_manager.createProject(project_name,project_dir);
       }
      catch (FoamException e) {
         System.err.println("Problem creating project: " + e);
         IvyLog.logE("FIDE","Problem creating project",e);
         System.exit(1);
       }
      return;
    }
   
   if (input_file != null) {
      try (Reader fr = new IvyIncludeReader(input_file.getPath())) {
         processXmlFile(fr);
       }
      catch (IOException e) {
         IvyLog.logE("FIDE","Problem reading input file " + input_file,e);
       }
      return;
    }
   
   boolean haveping = msg_server.sendPing();
   synchronized (this) {
      for ( ; ; ) {
         // wait for explicit exit command
         try {
            wait(10000);
          }
         catch (InterruptedException e) { }
         boolean chk = msg_server.sendPing();
         if (haveping && !chk) break;
         else if (!haveping && chk) haveping = true;
       }
    }
}


boolean setupMessageServer(String mintid)
{
   if (msg_server != null) return false;
   
   mint_id = mintid;
   msg_server = new FideMonitor(this,mint_id);
   
   return true;
}


private void processXmlFile(Reader fr)
{
   try (IvyXmlReader xr = new IvyXmlReader(fr)) {
      for ( ; ; ) {
         String xmlstr = xr.readXml();
         if (xmlstr == null) break;
         IvyLog.logD("FIDE","Process XML command: " + xmlstr);
         Element xml = IvyXml.convertStringToXml(xmlstr);
         try {
            FoamCommand cmd = setupFoamCommand(xml);
            try (IvyXmlWriter xw = new IvyXmlWriter()) {
               xw.begin("RESULT");
               cmd.process(xw);
               xw.end("RESULT");
               IvyLog.logD("FIDE","FIDE RESULT for " + 
                     cmd.getCommandName() + ": " + xw.toString());
               System.err.println("DONE command " + cmd.getCommandName());
             }
            catch (Throwable t) {
               IvyLog.logE("FIDE",
                     "Problem prcessing command " + cmd.getCommandName(),t);
             }
          }
         catch (FoamException e) {
            IvyLog.logE("FIDE","Bad command",e);
          }
       }
    }
   catch (IOException e) {}
}


FoamCommand setupFoamCommand(Element xml) throws FoamException
{
   String cmd = IvyXml.getAttrString(xml,"DO");
   FoamCommand lcmd = command_factory.createCommand(xml);
   if (lcmd == null) {
      throw new FoamException("Invalid command " + cmd);
    }
   
   return lcmd;
}



/********************************************************************************/
/*                                                                              */
/*      Command processing for message server                                   */
/*                                                                              */
/********************************************************************************/

String processXMLCommand(String cmd,Element xml)
{
   try (IvyXmlWriter xw = new IvyXmlWriter()) {
      xw.begin("RESULT");
      FoamCommand foamcmd = null;
      try {
          foamcmd = setupFoamCommand(xml);
       }
      catch (FoamException e) {
         IvyLog.logE("FIDE","Problem processing command " + cmd,e);
         xw.begin("ERROR");
         xw.textElement("MESSAGE",e);
         xw.end("ERROR");
       }
      if (foamcmd != null && foamcmd.isImmediate()) {
         try {
            foamcmd.process(xw);
          }
         catch (Throwable t) {
            IvyLog.logE("FIDE","Problem processing command " + cmd,t);
            xw.begin("ERROR");
            xw.textElement("MESSAGE",t);
            xw.end("ERROR");
          }
       }
      else if (foamcmd != null) {
         // background commands
         String rid = IvyXml.getAttrString(xml,"RID");
         if (rid == null) {
            rid = "FOAM" + random_gen.nextInt(1000000);
          }
         CommandProcessor cp = new CommandProcessor(foamcmd,rid);
         xw.field("RID",rid);
         cp.start();
       }
      
      xw.end("RESULT");
      
      return xw.toString();
    }
}



/********************************************************************************/
/*                                                                              */
/*      Messaging methods                                                       */
/*                                                                              */
/********************************************************************************/

public String getMintId()
{
   return mint_id; 
}


public boolean haveMessageServer()
{
   return msg_server !=  null;
}


public boolean registerMessage(String pat,MintHandler hdlr)
{
   if (msg_server == null) return false;
   
   msg_server.register(pat,hdlr);
   return true;
}


public boolean sendMessage(String msg)
{
   if (msg_server == null) return false;
   
   msg_server.send(msg);
   return true;
}

public boolean sendMessage(String msg,MintReply rply)
{
   if (msg_server == null) return false;
   
   msg_server.send(msg,rply,MintConstants.MINT_MSG_FIRST_NON_NULL);
   
   return true;
}


/********************************************************************************/
/*                                                                              */
/*      Background command processor                                            */
/*                                                                              */
/********************************************************************************/

private class CommandProcessor extends Thread {
   
   private FoamCommand for_command;
   private String reply_id;
   
   CommandProcessor(FoamCommand cmd,String rid) {
      super("FIDE_" + cmd.getCommandName() + "_" + rid);
      for_command = cmd;
      reply_id = rid;
    }
   
   @Override public void run() {
      try (IvyXmlWriter xw = new IvyXmlWriter()) {
         xw.begin("FIDEREPLY");
         xw.field("RID",reply_id);
         xw.begin("RESULT");
         for_command.process(xw);
         xw.end("RESULT");
         xw.end("FIDEREPLY");
         IvyLog.logD("FIDE","Send reply " + xw.toString());
         msg_server.send(xw.toString());
       }
      catch (Throwable t) {
         IvyLog.logE("FIDE","Problem processing command",t);
         IvyXmlWriter xw = new IvyXmlWriter();
         xw.begin("FIDEREPLY");
         xw.field("RID",reply_id);
         xw.begin("ERROR");
         xw.textElement("MESSAGE",t);
         xw.end("ERROR");
         xw.end("FIDEREPLY");
         IvyLog.logD("FIDE","Send error reply " + xw.toString());
         msg_server.send(xw.toString());
         xw.close();
       }
}

}       // end of inner class CommandProecessor



}       // end of class FideSystem




/* end of FideSystem.java */

