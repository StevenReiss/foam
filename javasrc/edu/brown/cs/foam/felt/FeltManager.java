/********************************************************************************/
/*                                                                              */
/*              FeltManager.java                                                */
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



package edu.brown.cs.foam.felt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.brown.cs.foam.fide.FideSystem;
import edu.brown.cs.foam.foam.FoamDocumentManager;
import edu.brown.cs.foam.foam.FoamElement;
import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamProjectManager;
import edu.brown.cs.ivy.file.IvyFile;
import edu.brown.cs.ivy.file.IvyLog;


public class FeltManager implements FoamDocumentManager,
      FeltConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private FideSystem      foam_main;
private FoamProjectManager project_manager;

private Map<File,DocumentInfo>  document_map;



/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

public FeltManager(FideSystem sys)
{
   foam_main = sys;
   project_manager = foam_main.getProjectManager();
   document_map = new HashMap<>();
   
   loadProject();
}



/********************************************************************************/
/*                                                                              */
/*      Methods to update project                                               */
/*                                                                              */
/********************************************************************************/

public void setProject()
{
   document_map.clear();
   loadProject();
}

private void loadProject() 
{
   try {
      File f1 = project_manager.getResourcesDirectory();
      if (f1.exists() && f1.isDirectory()) {
         for (File fx : f1.listFiles()) {
            if (fx.getName().startsWith(".")) continue;
            DocumentInfo di = new DocumentInfo(fx,FoamDocumentType.RESOURCE);
            document_map.put(fx,di);
          }
       }
      File f2 = project_manager.getSpecificationDirectory();
      if (f2.exists() && f2.isDirectory()) {
         for (File fx : f2.listFiles()) {
            FoamDocumentType fdt = null;
            String nm = fx.getName();
            int idx1 = nm.lastIndexOf(".");
            if (idx1 > 0) nm = nm.substring(0,idx1);
            nm = nm.toUpperCase();
            if (nm.equals("DESCRIPTION")) fdt = FoamDocumentType.DESCRIPTION;
            else if (nm.equals("REQUIREMENTS")) fdt = FoamDocumentType.REQUIREMENTS;
            else if (nm.equals("FEATURES")) fdt = FoamDocumentType.FEATURES;
            else if (nm.equals("OVERVIEW")) fdt = FoamDocumentType.OVERVIEW;
            else if (nm.equals("ARCHITECTURE")) fdt = FoamDocumentType.ARCHITECTURE;
            if (fdt != null) {
               DocumentInfo di = new DocumentInfo(fx,fdt);
               document_map.put(fx,di);
             }
          }
       }
      File f3 = project_manager.getLibDirectory();
      if (f3.exists() && f3.isDirectory()) {
         for (File fx : f3.listFiles()) {
            if (fx.getName().startsWith(".")) continue;
            DocumentInfo di = new DocumentInfo(fx,FoamDocumentType.LIBRARY);
            document_map.put(fx,di);
          }
       }
      File f4 = project_manager.getUIDirectory();
      if (f4.exists() && f4.isDirectory()) {
         DocumentInfo di = new DocumentInfo(f4,FoamDocumentType.USER_INTERFACE);
         document_map.put(f4,di);
       }
      File f5 = project_manager.getDesignDirectory();
      if (f5.exists() && f5.isDirectory()) {
         DocumentInfo di = new DocumentInfo(f5,FoamDocumentType.DESIGN);
         document_map.put(f5,di);
       }
      File f6 = project_manager.getTestsDirectory();
      if (f6.exists() && f6.isDirectory()) {
         DocumentInfo di = new DocumentInfo(f6,FoamDocumentType.TEST_CASE);
         document_map.put(f6,di);
       }
    }
   catch (FoamException e) {
      return;           // no project yet
    }
   
}



/********************************************************************************/
/*                                                                              */
/*      Access methods                                                          */
/*                                                                              */
/********************************************************************************/

@Override public FoamElement getRootElement(String filename) throws FoamException
{
   File file = getDocumentPath(filename);
   DocumentInfo di = document_map.get(file);
   if (di == null) {
      throw new FoamException("File " + filename + " not found");
    }
   FeltElement root = di.getRootElement();
   if (root != null) return root;
   
   // parse the document if opened the first time
   switch (di.getType()) {
      case ARCHITECTURE :
      case OVERVIEW :
      case SCENARIO :
         FeltSectionParser sectpar = new FeltSectionParser(di.getFile());
         root = sectpar.parse();  // outline document
         break;
      case LIBRARY :
      case RESOURCE :
         String enm = di.getFile().getName();
         int idx = enm.indexOf(".");
         if (idx > 0) enm = enm.substring(0,idx);
         root = new FeltElement(enm,FoamElementType.DOCUMENT,null);
         root.setFile(file);
         break;
      case DESCRIPTION :
      case FEATURES :
      case REQUIREMENTS :
         FeltOutlineParser outpar = new FeltOutlineParser(di.getFile());
         root = outpar.parse();  // outline document
         break;
      case TEST_CASE :
      case DESIGN :
         File dir = di.getFile();
         if (dir.exists() && dir.isDirectory()) {
            root = new FeltElement(dir.getName(),
                  FoamElementType.DOCUMENT,null);
            root.setFile(dir);
            for (File f : dir.listFiles()) {
               if (f.getName().endsWith(".java")) {
                  FeltJavaParser javapar = new FeltJavaParser(f);
                  javapar.parse(root);
                }
             }
          }
         break;
      case USER_INTERFACE :
         dir = di.getFile();
         if (dir.exists() && dir.isDirectory()) {
            root = new FeltElement(dir.getName(),
                  FoamElementType.DOCUMENT,null);
            root.setFile(dir);
            for (File f : dir.listFiles()) {
               if (f.getName().endsWith(".md")) {
                  FeltElement fe = new FeltElement(f.getName(),
                        FoamElementType.UI_DESCRIPTION,root);
                  fe.setFile(f);
                }
               else if (f.getName().endsWith(".png")) {
                  FeltElement fe = new FeltElement(f.getName(),
                        FoamElementType.UI_IMAGE,root);
                  fe.setFile(f);
                }
             }
          }
      default :
         throw new FoamException("Can't find root element for document type");
    }
   di.setRootElement(root);
   
   return root;
}

@Override public void addCommentsFromLLM(String file)
   throws FoamException
{
   throw new FoamException("Not implement yet");
}


/********************************************************************************/
/*                                                                              */
/*      Create a new document                                                   */
/*                                                                              */
/********************************************************************************/

@Override public void createDocument(String filename,FoamDocumentType typ)
   throws FoamException
{
   File dir = null;
   String ext = null;
   switch (typ) {
      case DESCRIPTION :
      case FEATURES :
      case REQUIREMENTS :
         dir = project_manager.getSpecificationDirectory();
         ext = "md";
         break;
      case SCENARIO :
         dir = project_manager.getScenariosDirectory();
         ext = "md";
         break;
      case USER_INTERFACE :
         dir = project_manager.getUIDirectory();
         break;
      case TEST_CASE :
         dir = project_manager.getTestsDirectory();
         break;
      case DESIGN :
         dir = project_manager.getDesignDirectory();
         break;
    }
   
   String fnm = filename;
   int idx1 = fnm.lastIndexOf(File.separator);
   if (idx1 > 0) {
      fnm = fnm.substring(idx1+1);
    }
   int idx2 = fnm.lastIndexOf(".");
   if (idx2 > 0) {
      String fext = fnm.substring(idx1+1);
      if (!ext.contains(fext)) {
         throw new FoamException("Bad file extension " + fext + " for this type of file");
       }
    }
   else {
      int idx3 = ext.indexOf(" ");
      if (idx3 > 0) ext = ext.substring(0,idx3);
      fnm += "." + ext;
    }
   
   File f1 = new File(dir,fnm);
   if (f1.exists()) {
      return;
    }
   
   f1 = IvyFile.getCanonical(f1);
   try (FileWriter fw = new FileWriter(f1)) {
      fw.write("Put content here\n");
    }
   catch (IOException e) {
      throw new FoamException("Can't create file",e);
    }
   
   document_map.put(f1,new DocumentInfo(f1,typ));
}



/********************************************************************************/
/*                                                                              */
/*      Update a document                                                       */
/*                                                                              */
/********************************************************************************/

@Override public void updateDocument(String filename,String content)
   throws FoamException
{
   if (filename == null) {
      throw new FoamException("File must be specified on updateDocument");
    }
   File file = getDocumentPath(filename);
   DocumentInfo di = document_map.get(file);
   if (di == null) {
      throw new FoamException("File is not known to the system");
    }
   
   if (!file.isDirectory()) {
      if (!file.isDirectory() && content == null) {
         di.getFile().delete();
         document_map.remove(file);
         return;
       }
      
      if (!content.endsWith("\n")) content += "\n";
      try (FileWriter fw = new FileWriter(filename)) {
         fw.write(content);
       }
      catch (IOException e) {
         IvyLog.logE("FELT","Problem updating file",e);
       }
    }
   
   di.setRootElement(null);
}



/********************************************************************************/
/*                                                                              */
/*      Normalize document path                                                 */
/*                                                                              */
/********************************************************************************/

@Override public File getDocumentPath(String file) throws FoamException
{
   file = file.replace("/",File.separator);
   File f1 = new File(file);
   File root = new File(project_manager.getCurrentProjectDirectory());
   
   if (f1.isAbsolute()) {
      // make relative to projet
      f1 = IvyFile.getCanonical(f1);
      Path p1 = f1.toPath();
      Path p2 = root.toPath();
      try {
         Path p3 = p2.relativize(p1);
         f1 = p3.toFile();
       }
      catch (IllegalArgumentException e) {
         throw new FoamException("Path " + file + " is not in project");
       }
    }
   
   File f3 = findDocumentFile(root,f1);
   if (f3 == null || !f3.exists()) {
      throw new FoamException("File " + file + " not found");
    }
   
   f3 = IvyFile.getCanonical(f3);
   
   return f3;
}


private File findDocumentFile(File root,File given)
{
   File f2 = new File(root,given.getPath());
   if (document_map.get(f2) != null) return f2;
   
   File par = given.getParentFile();
   if (par != null) {
      File f1 = findDocumentFile(root,par);
      if (f1 != null) return f1;
    }
  
   String top = null;
   for (File par1 = given; par1 != null; par1 = par1.getParentFile()) {
      if (root.equals(par1.getParentFile())) top = par1.getName();
    }
   String nm = given.getName();
   for (String s : FOAM_PROJECT_DIRS) {
      if (top == null || top.equals(s)) {
         File f4 = new File(root,s);
         File f5 = checkFile(f4,nm);
         if (f5 != null) return f5;
       }
    }
   
   return null;
}



private File checkFile(File dir,String name)
{
   File f1 = new File(dir,name);
   if (f1.exists()) return f1;
   
   // try ignoring the extension
   if (dir.exists() && dir.isDirectory()) {
      String nm = f1.getName();
      int idx = nm.indexOf(".");
      if (idx > 0) {
         // just get the main part of the name -- rest might be element id
         nm = nm.substring(0,idx);
       }
      String check = nm + ".";
      for (File f : dir.listFiles()) {
         if (f.getName().startsWith(check)) return f;
       }
    }
   
   return null;
}


/********************************************************************************/
/*                                                                              */
/*      Get document information                                                */
/*                                                                              */
/********************************************************************************/

@Override public FoamDocumentType getDocumentType(String filename)
   throws FoamException
{
   File file = getDocumentPath(filename);
   DocumentInfo di = document_map.get(file);
   if (di == null) {
      throw new FoamException("File " + filename + " not found");
    }
   
   return di.getType();
}


@Override public Collection<File> getAllDocuments()
{
   return document_map.keySet();
}



/********************************************************************************/
/*                                                                              */
/*      Element methods                                                         */
/*                                                                              */
/********************************************************************************/

@Override public List<FoamElement> identifyElements(String file,FoamElementType typ)
         throws FoamException
{
   FoamElement root = null;
   if (file != null && !file.isBlank()) {
      root = getRootElement(file);
      String [] hier = file.split("\\.");
      for (int i = 1; i < hier.length; ++i) {
         String enm = hier[i].trim();
         boolean fnd = false;
         for (FoamElement ce : root.getChildren()) {
            if (ce.getName().equals(enm)) {
               root = ce;
               fnd = true;
               break;
             }
          }
         if (!fnd) break;
       }
    }
   
   List<FoamElement> rslt = new ArrayList<>();
   getAllElements(root,typ,rslt);
   return rslt;
}


private void getAllElements(FoamElement fe,FoamElementType typ,
      List<FoamElement> rslt) 
{
   if (fe == null) {
      Set<DocumentInfo> alldocs = new TreeSet<>();
      alldocs.addAll(document_map.values());
      for (DocumentInfo di : alldocs) {
         try {
            String fnm = di.getFile().getAbsolutePath();
            FoamElement root = getRootElement(fnm);
            getAllElements(root,typ,rslt);
          }
         catch (FoamException e) {
            IvyLog.logE("FELT","Problem getting document root");
          }
       }
      return;
    }
   
   if (typ == null || typ == fe.getType()) {
      if (fe.getParent() != null) rslt.add(fe);
      else if (!fe.getChildren().isEmpty()) rslt.add(fe);
      else if (typ == FoamElementType.DOCUMENT) rslt.add(fe);
    }
   for (FoamElement ce : fe.getChildren()) {
      getAllElements(ce,typ,rslt);
    }
}


/********************************************************************************/
/*                                                                              */
/*      Get the complete text of a specification                                */
/*                                                                              */
/********************************************************************************/

@Override public String getSpecificationText(FoamDocumentType type)
{
   StringBuffer buf = new StringBuffer();
   
   for (DocumentInfo di : document_map.values()) {
      if (di.getType() == type) {
         if (!buf.isEmpty()) buf.append("\n\n");
         appendText(di,buf);
       }
    }
   
   return buf.toString();
}


private void appendText(DocumentInfo di,StringBuffer buf)
{
   if (di.getFile().isDirectory()) {
      FeltElement fe = di.getRootElement();
      for (FoamElement fce : fe.getChildren()) {
         FeltElement ce = (FeltElement) fce;
         File f1 = ce.getFile();
         if (f1 != di.getFile()) {
            try {
               String cnts = IvyFile.loadFile(di.getFile());
               if (!buf.isEmpty()) buf.append("\n");
               buf.append(cnts);
             }
            catch (IOException e) {
               IvyLog.logE("FELT","Problem reading specificaiton",e);
             }
          }
       }
    }
   else if (di.getFile().exists()) {
      try {
         String cnts = IvyFile.loadFile(di.getFile());
         buf.append(cnts);
       }
      catch (IOException e) {
         IvyLog.logE("FELT","Problem reading specificaiton",e);
       }
    }
}



/********************************************************************************/
/*                                                                              */
/*      DocumentInfo class to hold information about a document                 */
/*                                                                              */
/********************************************************************************/

private static class DocumentInfo {

   private FeltElement document_root;
   private FoamDocumentType document_type;
   private File        document_file;
   
   DocumentInfo(File file,FoamDocumentType type) {
      document_root = null;
      document_type = type;
      document_file = file;
    }
   
   File getFile()                               { return document_file; }
   FoamDocumentType getType()                  { return document_type; }
   FeltElement getRootElement()                 { return document_root; }
   
   void setRootElement(FeltElement fe)          { document_root = fe; }
   
}       // end of inner class DocumentInfo






}       // end of class FeltManager




/* end of FeltManager.java */

