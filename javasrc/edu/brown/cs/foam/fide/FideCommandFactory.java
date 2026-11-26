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
import java.util.List;

import org.w3c.dom.Element;

import edu.brown.cs.foam.foam.FoamDocumentManager;
import edu.brown.cs.foam.foam.FoamElement;
import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamLLMManager;
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
      case DIFFS :
      case EDIT :
      case FIND :
      case QUERY :
      case STATUS :
      case SYNC :
      case UPDATE :
         break;
      default :
         IvyLog.logE("FIDE","Illegal command " + cmd);
         return null;
      case CLEAR :
         rslt = new CommandClear(xml);
         break;
      case COMMIT :
         rslt = new CommandCommit(xml);
         break;
      case CREATE :
         rslt = new CommandCreate(xml);
         break;
      case EDITED :
         rslt = new CommandEdited(xml);
         break;
      case EXIT :
         System.exit(0);
         break;
      case HIERARCHY :
         rslt = new CommandHierarchy(xml);
         break;
      case PING :
         rslt = new CommandPing(xml);
         break;
      case PROJECT :
         rslt = new CommandProject(xml);
         break;
      case SETMODEL :
         rslt = new CommandSetModel(xml);
         break;
      case SETVERSION :
         rslt = new CommandSetVersion(xml);
         break;
      case SHELL :
         rslt = new CommandShell(xml);
         break;
      case VERSIONS :
         rslt = new CommandVersions(xml);
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

}       // end of inner class CommandCreate




/********************************************************************************/
/*                                                                              */
/*      CREATE command                                                         */
/*                                                                              */
/********************************************************************************/

private class CommandEdited extends CommandBase {
   
   private String file_name;
   
   CommandEdited(Element xml) {
      super(xml);
      file_name = IvyXml.getAttrString(xml,"NAME");
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws FoamException {
      String cnts = null;
      String fnm = foam_main.getDocumentManager().getDocumentPath(file_name);
      try {
         cnts = IvyFile.loadFile(new File(fnm));
       }
      catch (IOException e) {
         throw new FoamException("Can't access contents of " + fnm);
       }
      foam_main.getDocumentManager().updateDocument(file_name,cnts);
    }

}       // end of inner class CommandEdited



/********************************************************************************/
/*                                                                              */
/*      CREATE command                                                         */
/*                                                                              */
/********************************************************************************/

private class CommandHierarchy extends CommandBase {
   
   private String file_name;
   private FoamElementType element_type;
   
   CommandHierarchy(Element xml) {
      super(xml);
      file_name = IvyXml.getAttrString(xml,"NAME");
      element_type = null;
      if (IvyXml.getAttrPresent(xml,"TYPE")) {
         element_type = IvyXml.getAttrEnum(xml,"TYPE",FoamElementType.DOCUMENT);
       }
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws FoamException {
      FoamDocumentManager docmgr = foam_main.getDocumentManager();
      List<FoamElement> elts = docmgr.identifyElements(file_name,
            element_type);
      for (FoamElement fe : elts) {
         fe.outputXml(xw);
       }
    }
   
}       // end of inner class CommandHierarchy




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
      
      switch (version_name) {
         case "UNDO" :
         case "PREVIOUS" :
         case "-" :
            vm.undo();
            break;
         case "REDO" :
         case "NEXT" :
         case "+" :
            vm.redo();
            break;
         default :
            vm.setVersion(version_name);
            break;
       }
    }
   
}       // end of inner class CommandSetVersion



/********************************************************************************/
/*                                                                              */
/*      Command COMMIT                                                          */
/*                                                                              */
/********************************************************************************/

private class CommandCommit extends CommandBase {

   private String tag_name;
   
   CommandCommit(Element xml) {
      super(xml);
      tag_name = IvyXml.getAttrString(xml,"VERSION");
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws FoamException {
      FoamVersionManager vm = foam_main.getVersionManager();
      vm.commit(tag_name);
    }
   
}       // end of inner class CommandCommit



/********************************************************************************/
/*                                                                              */
/*      Command VERSIONS to list version info                                   */
/*                                                                              */
/********************************************************************************/

private class CommandVersions extends CommandBase {

   CommandVersions(Element xml) {
      super(xml);
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws FoamException {
      FoamVersionManager vm = foam_main.getVersionManager();
      List<String> vers = vm.getAvailableVersions();
      for (String s :vers) {
         xw.xmlText(s);
       }
    }
   
}       // end of inner class CommandVersions


/********************************************************************************/
/*                                                                              */
/*      CLEAR history command                                                   */
/*                                                                              */
/********************************************************************************/

private class CommandClear extends CommandBase {
   
   CommandClear(Element xml) {
      super(xml);
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws FoamException {
      FoamLLMManager lm = foam_main.getLLMManager();
      lm.clearHistory();
    }

}       // end of inner class CommandClear



/********************************************************************************/
/*                                                                              */
/*      SETMODEL command to set LLM model                                       */
/*                                                                              */
/********************************************************************************/

private class CommandSetModel extends CommandBase {
   
   private String model_name; 
   
   CommandSetModel(Element xml) {
      super(xml);
      model_name = IvyXml.getAttrString(xml,"MODEL");
    }
   
   @Override public void localProcess(IvyXmlWriter xw) throws FoamException {
      FoamLLMManager lm = foam_main.getLLMManager();
      lm.setModel(model_name);
    }

}       // end of inner class CommandSetModel



}       // end of class FideCommandFactory




/* end of FideCommandFactory.java */

