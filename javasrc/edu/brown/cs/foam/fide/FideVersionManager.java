/********************************************************************************/
/*                                                                              */
/*              FideVersionManager.java                                         */
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamVersionManager;
import edu.brown.cs.ivy.exec.IvyExec;
import edu.brown.cs.ivy.file.IvyLog;

class FideVersionManager implements FoamVersionManager, FideConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private FideSystem             foam_main;
private FideProjectManager     project_manager;
private File                    git_root;
private VersionInfo             current_version;
private String                  git_command;
private Map<String,VersionInfo> version_map;
      

private static final String GIT_PRIOR_FORMAT = "%H%x09%h%x09%P%x09%d%n";




/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

FideVersionManager(FideSystem fs)
{
   foam_main = fs;
   project_manager = null;
   git_root = null;
   current_version = null;
   version_map = new LinkedHashMap<>();
   
   git_command = foam_main.getProperty("foam.git.command","git");
   
   setProject();
}


/********************************************************************************/
/*                                                                              */
/*      Access methods                                                          */
/*                                                                              */
/********************************************************************************/

String getCurrentVersion()                
{
   return current_version.getHash();
}


/********************************************************************************/
/*                                                                              */
/*      Setup methods                                                           */
/*                                                                              */
/********************************************************************************/

void setProject()
{
   project_manager = foam_main.getOurProjectManager();
   if (project_manager == null ||
         project_manager.getProjectDirectory() == null) {
      return;
    }
   
   try {
      File metadata = project_manager.getMetadataDirectory();
      git_root = new File(metadata,GIT_ROOT);
      if (!git_root.exists()) {
         startGitRepo();
       }
      findVersions();
    }
   catch (FoamException e) {
      IvyLog.logE("FIDE","Problem setting up project",e);
      git_root = null;
      current_version = null;
    }
}



private void startGitRepo() throws FoamException
{
   File dir = project_manager.getProjectDirectory();
   File f1 = new File(dir,".gitignore");
   if (!f1.exists()) {
      try (FileWriter fw = new FileWriter(f1)) {
         fw.write("# files to ignore\n");
         for (String s : GIT_IGNORES) {
            fw.write(s + "\n");
          }
       }
      catch (IOException e) {
         IvyLog.logE("FIDE","Problem writing .gitignore",e);
       }
    }
   File f2 = project_manager.getMetadataDirectory();
   File f3 = new File(f2,GIT_IGNORE);
   if (!f3.exists()) {
      try (FileWriter fw = new FileWriter(f3)) {
         fw.write("# files to ignore\n");
         for (String s : GIT_IGNORES) {
            fw.write(s + "\n");
          }
         fw.write("Foam.git\n");
       }
      catch (IOException e) {
         IvyLog.logE("FIDE","Problem writing .gitignore",e);
       }
    }
   
   StringCommand cmd = new StringCommand("@init --bare " + git_root);
   if (cmd.getStatus() != 0) {
      throw new FoamException("Git init failed"); 
    }
   StringCommand cmd3 = new StringCommand(
         "config --local status.showUntrackedFiles no");
   if (cmd3.getStatus() != 0) {
      throw new FoamException("Git config failed");
    }
   StringCommand cmd1 = new StringCommand("add .");
   if (cmd1.getStatus() != 0) {
      throw new FoamException("Git add failed");
    }
   StringCommand cmd2 = new StringCommand("commit -m 'Initial commit'");
   if (cmd2.getStatus() != 0) {
      throw new FoamException("Git commit failed");
    }
   StringCommand cmd4 = new StringCommand("tag -a v1.0.0 -m 'Initial commit'");
   if (cmd4.getStatus() != 0) {
      throw new FoamException("Git tagfailed");
    }
}



private void findVersions()
{
   if (git_root == null) return;
   
   String head = null;
   String cmd1 = "log --reverse '--pretty=format:" + GIT_PRIOR_FORMAT + "'";
   StringCommand cmd = new StringCommand(cmd1);
   StringTokenizer tok = new StringTokenizer(cmd.getContent(),"\n\r");
   VersionInfo last = null;
   Map<String,String> priormap = new HashMap<>();
   while (tok.hasMoreTokens()) {
      String ent = tok.nextToken();
      try {
         String [] ldata = ent.split("\t",4);
         int idx = 0;
         String rev = ldata[idx++];
         String srev = ldata[idx++];
         String prev = ldata[idx++];
         String alts = ldata[idx++];
         String prior = null;
         for (StringTokenizer ltok = new StringTokenizer(prev,"(, )"); ltok.hasMoreTokens(); ) {
            String pid = ltok.nextToken();
            if (prior == null) prior = pid;
          }
         String tag = null;
         boolean nexttag = false;
         if (alts != null) {
            for (StringTokenizer ltok = new StringTokenizer(alts,"(, )"); ltok.hasMoreTokens(); ) {
               String nm = ltok.nextToken();
               if (head == null && nm.equals("HEAD")) head = rev;
               else if (nm.equals("tag:") && tag == null) nexttag = true;
               else if (nexttag) {
                  tag = nm;
                  nexttag = false;
                }
             }
          }
         VersionInfo vi = new VersionInfo(rev,srev,null,last,tag);
         version_map.put(rev,vi);
         if (prior != null) priormap.put(rev,prior);
         last = vi;
       }
      catch (Throwable t) {
         IvyLog.logE("BVCR","Problem parsing priors log entry",t);
       }
    }
   for (Map.Entry<String,String> ent : priormap.entrySet()) {
      String fv = ent.getKey();
      String tv = ent.getValue();
      VersionInfo fver = version_map.get(fv);
      VersionInfo tver = version_map.get(tv);
      fver.setPriorVersion(tver);
    }
}


/********************************************************************************/
/*                                                                              */
/*      Abstract Method Implementations                                         */
/*                                                                              */
/********************************************************************************/

@Override public String setVersion(String arg1)
{
   // method body goes here
   return null;
}

@Override public String getDifferences(String arg0,String arg1,String arg2)
{
   // method body goes here

   return null;
}


@Override public String undo()
{
   // method body goes here
   return null;
}

@Override public List<String> getAvailableVersions()
{
   // method body goes here

   return null;
}

@Override public String redo()
{
   // method body goes here
   return null;
}

@Override public String commit(String arg1)
{
   // method body goes here
   return null;
}


/********************************************************************************/
/*                                                                              */
/*      Methods to run commands and provide the output                          */
/*                                                                              */
/********************************************************************************/

protected interface CommandCallback {

   void handleLine(String line);
   void handleDone(int sts);

}       // end of inner interface CommandCallback


protected class StringCommand implements CommandCallback {

   private StringBuffer msg_content;
   private int command_status;
   
   StringCommand(String cmd) {
      msg_content = new StringBuffer();
      command_status = 0;
      runCommand(cmd,this);
    }
   
   @Override public void handleLine(String ln) {
      msg_content.append(ln);
      msg_content.append("\n");
    }
   
   @Override public void handleDone(int sts) { 
      command_status = sts; 
      IvyLog.logD("FIDE","GIT result : " + sts  + " :: " + getContent());
    }
   
   String getContent()                          { return msg_content.toString(); }
   int getStatus()                              { return command_status; }

}       // end of inner class StringCommand


private void runCommand(String cmd0,CommandCallback cb)
{
   File pdir = project_manager.getProjectDirectory();
   
   String cmd = null;
   if (cmd0.startsWith("@")) {
      cmd = git_command + " " + cmd0.substring(1).trim();
    }
   else {
      cmd = git_command + " --git-dir=" + git_root + 
         " --work-tree=" + pdir.getAbsolutePath();
      if (cmd0.startsWith("add") || cmd0.startsWith("status")) {
         File f1 = new File(pdir,"metadata");
         File f2 = new File(f1,GIT_IGNORE);
         cmd += " -c core.excludesFile=" + f2.getAbsolutePath();
       }
      cmd += " " + cmd0;
    }
   
   
   try {
      IvyExec ex = new IvyExec(cmd,pdir,
            IvyExec.READ_OUTPUT|IvyExec.READ_ERROR);
      IvyLog.logD("FIDE","Run " + ex.getCommand() + " @ " + pdir);
      InputStream ins = ex.getInputStream();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(ins))) {
         for ( ; ; ) {
            String ln = br.readLine();
            if (ln == null) break;
            cb.handleLine(ln);
          }
       }
      InputStream eins = ex.getErrorStream();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(eins))) {
         for ( ; ; ) {
            String ln = br.readLine();
            if (ln == null) break;
            cb.handleLine(ln);
          }
       }
      cb.handleDone(ex.waitFor());
    }
   catch (IOException e) {
      IvyLog.logE("FIDE","Problem running command: " + cmd,e);
    }
}



/********************************************************************************/
/*                                                                              */
/*      Version information                                                     */
/*                                                                              */
/********************************************************************************/

private static class VersionInfo {

   private String long_hash;
   private VersionInfo prior_version;
   private VersionInfo next_version;
   private String tag_name;
   
   VersionInfo(String hash,String shash,VersionInfo prior,VersionInfo next,String tag) {
      long_hash = hash;
      prior_version = prior;
      next_version = next;
      tag_name = tag;
    }
   
   void setPriorVersion(VersionInfo v)  { prior_version = v; }
   
   String getHash()                     { return long_hash; }
   VersionInfo getPriorVersion()        { return prior_version; }
   VersionInfo getNextVersion()         { return next_version; }
   String getTag()                      { return tag_name; }
    
}       // end of inner class VersionInfo


}       // end of class FideVersionManager




/* end of FideVersionManager.java */

