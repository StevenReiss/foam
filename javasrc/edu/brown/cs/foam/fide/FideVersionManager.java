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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamVersionManager;
import edu.brown.cs.ivy.exec.IvyExec;
import edu.brown.cs.ivy.file.IvyLog;
import edu.brown.cs.ivy.xml.IvyXmlWriter;

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
   version_map = new HashMap<>();
   
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
   
   GitCommand cmd = new GitCommand("@init --bare " + git_root);
   if (cmd.getStatus() != 0) {
      throw new FoamException("Git init failed"); 
    }
   GitCommand cmd3 = new GitCommand(
         "config --local status.showUntrackedFiles no");
   if (cmd3.getStatus() != 0) {
      throw new FoamException("Git config failed");
    }
   GitCommand cmd1 = new GitCommand("add .");
   if (cmd1.getStatus() != 0) {
      throw new FoamException("Git add failed");
    }
   GitCommand cmd2 = new GitCommand("commit -m 'Initial commit'");
   if (cmd2.getStatus() != 0) {
      throw new FoamException("Git commit failed");
    }
   GitCommand cmd4 = new GitCommand("tag -a v1.0 -m 'Initial commit'");
   if (cmd4.getStatus() != 0) {
      throw new FoamException("Git tag failed");
    }
}



private void findVersions()
{
   if (git_root == null) return;
   
   String head = null;
   String cmd1 = "log --reverse '--pretty=format:" + GIT_PRIOR_FORMAT + "'";
   GitCommand cmd = new GitCommand(cmd1);
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
         version_map.put(srev,vi);
         if (tag != null) version_map.put(tag,vi);
         
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
/*      Version management                                                      */
/*                                                                              */
/********************************************************************************/

@Override public String setVersion(String version) throws FoamException
{
   if (version == null) {
      throw new FoamException("Version should not be null");
    }
   VersionInfo vi = version_map.get(version);
   if (vi == null) {
      throw new FoamException("Version " + version + " doesn't exist");
    }
   
   commit(null);
   
   GitCommand cmd1 = new GitCommand("revert " + vi.getHash());
   if (cmd1.getStatus() != 0) {
      throw new FoamException("Set version failed");
    }
   
   current_version = vi;
   
   return vi.getTag();
}


@Override public String commit(String tag0) throws FoamException
{
   String oldtag = current_version.getTag();
   String chngs = getChanges();
   if (chngs == null || chngs.isBlank()) {
      // nothing changed
      if (tag0 != null && !tag0.isBlank()) {
         GitCommand cmd4 = new GitCommand("tag -a " + tag0 + " -m 'Rename " + oldtag + "'"); 
         if (cmd4.getStatus() != 0) {
            throw new FoamException("Git tag failed");
          }
         current_version.setTag(tag0);
       }
      return current_version.getTag();
    }
   try (BufferedReader br = new BufferedReader(new StringReader(chngs))) {
      for ( ; ; ) {
         String ln = br.readLine();
         if (ln == null) break;
         ln = ln.trim();
         if (ln.startsWith("??")) {
            String file = ln.substring(2).trim();
            File f1 = project_manager.getProjectDirectory();
            File f2 = new File(f1,file);
            GitCommand cmd1 = new GitCommand("add " + f2.getAbsolutePath());
            if (cmd1.getStatus() != 0) {
               throw new FoamException("Git add failed for commit " + file);
             }
          }
       }
    }
   catch (IOException e) {
      IvyLog.logE("FIDE","Unexpected exception reading string",e);
    }
   
   GitCommand cmd2 = new GitCommand("commit -a");
   if (cmd2.getStatus() != 0) {
      throw new FoamException("git commit failed");
    }
   
   String tag = tag0;
   if (tag == null || tag0.isBlank()) {
      tag = computeNextVersion(oldtag);
    }
   GitCommand cmd3 = new GitCommand("tag -a " + tag + " -m 'Version after " + oldtag + "'"); 
   if (cmd3.getStatus() != 0) {
      throw new FoamException("Git tag failed");
    }
   current_version.setTag(tag);
      
   return null;
}

private String getChanges() throws FoamException
{
   GitCommand cmd = new GitCommand("status --porcelain");
   if (cmd.getStatus() != 0) {
      throw new FoamException("Git status failed");
    }
   
   String cnts = cmd.getContent();
   if (cnts.isBlank()) return null;
   
   return cnts;
}


private String computeNextVersion(String prev)
{
   // might want to handle duplicate version names
   
   int idx = prev.lastIndexOf(".");
   if (idx < 0) {
      return prev + ".1";
    }
   String last = prev.substring(idx+1);
   if (last.isBlank()) {
      return prev + "1";
    }
   try {
      int lno = Integer.parseInt(last);
      return prev.substring(0,idx+1) + (lno+1);
    } 
   catch (NumberFormatException e) {
      return prev + ".1";
    }
}



/********************************************************************************/
/*                                                                              */
/*      Abstract Method Implementations                                         */
/*                                                                              */
/********************************************************************************/

@Override public String getDifferences(String arg0,String arg1,String arg2)
{
   // method body goes here

   return null;
}


@Override public String undo() throws FoamException
{
   if (current_version == null) throw new FoamException("No current version");
 
   commit(null);
   VersionInfo nextv = current_version.getPriorVersion();
   if (nextv == null) throw new FoamException("No prior version");
   
   String next = nextv.getTag();
   return setVersion(next);
}


@Override public String redo() throws FoamException
{
   if (current_version == null) throw new FoamException("No current version");
  
   String chngs = getChanges();
   if (chngs != null && !chngs.isBlank()) {
      // might want to stash changes if there are any
    }
   
   VersionInfo nextv = current_version.getNextVersion();
   if (nextv == null) throw new FoamException("No next version");
   
   String next = nextv.getTag();
   return setVersion(next);
}


@Override public List<String> getAvailableVersions()
{
   List<VersionInfo> versions = new ArrayList<>();
   for (VersionInfo vi = current_version; vi != null; vi.getNextVersion()) {
      versions.add(0,vi);
    }
   if (current_version != null) {
      for (VersionInfo vi = current_version.getPriorVersion(); vi != null; vi = vi.getPriorVersion()) {
         versions.add(vi);
       }
    }
   
   List<String> rslt = new ArrayList<>();
   for (VersionInfo vi : versions) {
      IvyXmlWriter xw = new IvyXmlWriter();
      vi.outputXml(xw,vi == current_version);
      rslt.add(xw.toString());
    }

   return rslt;
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


protected class GitCommand implements CommandCallback {

   private StringBuffer msg_content;
   private int command_status;
   
   GitCommand(String cmd) {
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
   
   void setTag(String tag)              { tag_name = tag; }
   
   void outputXml(IvyXmlWriter xw,boolean cur) {
      xw.begin("VERSION");
      xw.field("HASH",long_hash);
      xw.field("NAME",tag_name);
      if (cur) xw.field("CURRENT",true);
      xw.end("VERSION");
    }
    
}       // end of inner class VersionInfo


}       // end of class FideVersionManager




/* end of FideVersionManager.java */

