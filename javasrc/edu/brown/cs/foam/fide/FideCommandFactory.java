/********************************************************************************/
/*                                                                              */
/*              FideCommandFactory.java                                         */
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
import java.io.IOException;

import org.w3c.dom.Element;

import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamVersionManager;
import edu.brown.cs.ivy.exec.IvyExec;
import edu.brown.cs.ivy.file.IvyFile;
import edu.brown.cs.ivy.file.IvyLog;
import edu.brown.cs.ivy.xml.IvyXml;
import edu.brown.cs.ivy.xml.IvyXmlWriter;

class FideCommandFactory implements FideConstants
{

/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private FideSystem      foam_main;



/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

FideCommandFactory(FideSystem sys)
{ 
   foam_main = sys;
}



/********************************************************************************/
/*                                                                              */
/*      Command creation methods                                                */
/*                                                                              */
/********************************************************************************/

FoamCommand createCommand(Element xml)
{
   String cmd = IvyXml.getAttrString(xml,"DO");
   if (cmd == null) {
      IvyLog.logI("FIDE","Null command given");
      return null;
    }
   cmd = cmd.toUpperCase();
   FoamCommandType fct = null;
   try {
      fct = FoamCommandType.valueOf(cmd);
    }
   catch (Throwable t) {
      IvyLog.logE("FIDE","Illegal command " + cmd);
      return null;
    }
   
   
   FoamCommand rslt = null;
   switch (fct) {
      case AGENT :
      case CLEAR :
      case COMMIT :
      case DIFFS :
      case EDIT :
      case EDITED :
      case FIND :
      case HIERARCHY :
      case QUERY :
      case STATUS :
      case SYNC :
      case UPDATE :
        break;
      default :
         IvyLog.logE("FIDE","Illegal command " + cmd);
         return null;
      case CREATE :
         rslt = new CommandCreate(xml);
         break;
      case EXIT :
         System.exit(0);
         break;
      case PING :
         rslt = new CommandPing(xml);
         break;
      case PROJECT :
         rslt = new CommandProject(xml);
         break;
      case SETVERSION :
         rslt = new CommandSetVersion(xml);
         break;
      case SHELL :
         rslt = new CommandShell(xml);
         break;
    }
   
   return rslt;
}


/********************************************************************************/
/*                                                                              */
/*      Base for commands                                                       */
/*                                                                              */
/********************************************************************************/

private abstract class CommandBase implements FoamCommand {
   
   private String command_name;
   
   CommandBase(Element xml) {
      command_name = IvyXml.getAttrString(xml,"DO");
    }
   
   @Override public void process(IvyXmlWriter rslt) {
      boolean retry = true;
      for (int i = 0; retry && i < 10; ++i) {
         retry = false;
         try {
            localProcess(rslt);
          }
         catch (FoamException e) {
            IvyLog.logE("FIDE","Problem processinq " + getCommandName(),e);
          }
         catch (IOException e) {
            IvyLog.logE("FIDE","I/O Problem processinq " +
                  getCommandName(),e);
          }
         catch (InterruptedException e) {
            IvyLog.logE("FIDE","Command " + getCommandName() + 
                  " interrputed",e);
            retry = true;
          }
         catch (Throwable t) {
            IvyLog.logE("FIDE","Problem with " + getCommandName(),t);
          }
         if (retry) {
            rslt.clear();
          }
       }
    }
   
   @Override public String getCommandName()             { return command_name; }
   
   @Override public boolean isImmediate()               { return true; }
   
   protected abstract void localProcess(IvyXmlWriter rslt) throws Exception;
   
}       // end of inner class CommandBase



/********************************************************************************/
/*                                                                              */
/*      Ping command                                                            */
/*                                                                              */
/********************************************************************************/

private class CommandPing extends CommandBase {  
   
   CommandPing(Element xml) { 
      super(xml);
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws Exception {
      xw.text("PONG");
    }
   
}       // end of inner class CommandPing


/********************************************************************************/
/*                                                                              */
/*      SHELL command                                                           */
/*                                                                              */
/********************************************************************************/

private class CommandShell extends CommandBase {
   
   private String shell_command;
   
   CommandShell(Element xml) {
      super(xml);
      shell_command = IvyXml.getTextElement(xml,"COMMAND");
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws FoamException {
      try {
         IvyExec ex = new IvyExec(shell_command,
               IvyExec.READ_OUTPUT|IvyExec.ERROR_OUTPUT);
         String cnts = IvyFile.loadFile(ex.getInputStream());
         xw.cdataElement("OUTPUT",cnts);
       }
      catch (Throwable t) {
         throw new FoamException("Problem running command",t);
       }
    }
   
}       // end of inner class CommandShell



/********************************************************************************/
/*                                                                              */
/*      Project command                                                         */
/*                                                                              */
/********************************************************************************/

private class CommandProject extends CommandBase {
   
   private String project_dir;
   
   CommandProject(Element xml) {
      super(xml);
      project_dir = IvyXml.getAttrString(xml,"PROJECT");
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws Exception {
      foam_main.getProjectManager().setProject(project_dir);
    }

}       // end of inner class CommandProject



/********************************************************************************/
/*                                                                              */
/*      CREATE command                                                         */
/*                                                                              */
/********************************************************************************/

private class CommandCreate extends CommandBase {

   private String project_dir;
   private String project_name;
   private boolean remove_old;
   
   CommandCreate(Element xml) {
      super(xml);
      project_dir = IvyXml.getAttrString(xml,"PROJECT");
      project_name = IvyXml.getAttrString(xml,"NAME");
      remove_old = IvyXml.getAttrBool(xml,"REMOVE"); 
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws FoamException {
      File dirf = new File(project_dir);
      if (dirf.exists() && remove_old) {
         try {
            IvyFile.remove(dirf);
          }
         catch (IOException e) {
            throw new FoamException("Can't remove previous project",e);
          }
       }
      foam_main.getProjectManager().createProject(project_name,project_dir);
    }

}       // end of inner class CommandProject



/********************************************************************************/
/*                                                                              */
/*      Command SETVERSION                                                      */
/*                                                                              */
/********************************************************************************/

private class CommandSetVersion extends CommandBase {

   private String version_name;
   
   CommandSetVersion(Element xml) {
      super(xml);
      version_name = IvyXml.getAttrString(xml,"VERSION");
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws FoamException {
      if (version_name == null || version_name.isBlank()) {
         throw new FoamException("No version name specified");
       }
      FoamVersionManager vm = foam_main.getVersionManager();
      if (version_name.equals("NEXT")) {
         vm.redo();
       }
      else if (version_name.equals("PREVIOUS")) {
         vm.undo();
       }
      else {
         vm.setVersion(version_name);
       }
    }
   
}       // end of inner class CommandSetVersion



}       // end of class FideCommandFactory




/* end of FideCommandFactory.java */

