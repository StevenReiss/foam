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
import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.foam.fide.FideSystem;
import edu.brown.cs.foam.foam.FoamDocumentManager;
import edu.brown.cs.foam.foam.FoamElement;
import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamFileHierarchyManager;
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
private FoamFileHierarchyManager file_manager;

private Map<String,DocumentInfo>  document_map;



/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

public FeltManager(FideSystem sys)
{
   foam_main = sys;
   project_manager = foam_main.getProjectManager();
   file_manager = foam_main.getHierarchyManager();
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
      File f1 = file_manager.getResourcesDirectory();
      if (f1.exists() && f1.isDirectory()) {
         for (File fx : f1.listFiles()) {
            if (fx.getName().startsWith(".")) continue;
            DocumentInfo di = new DocumentInfo(fx,FoamDocumentType.RESOURCE);
            document_map.put(fx.getAbsolutePath(),di);
          }
       }
      File f2 = file_manager.getSpecificationDirectory();
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
            else if (nm.startsWith("IMAGE")) fdt = FoamDocumentType.UI_IMAGE;
            if (fdt != null) {
               DocumentInfo di = new DocumentInfo(fx,fdt);
               document_map.put(fx.getAbsolutePath(),di);
             }
          }
       }
      File f3 = file_manager.getLibDirectory();
      if (f3.exists() && f3.isDirectory()) {
         for (File fx : f3.listFiles()) {
            if (fx.getName().startsWith(".")) continue;
            DocumentInfo di = new DocumentInfo(fx,FoamDocumentType.LIBRARY);
            document_map.put(fx.getAbsolutePath(),di);
          }
       }
      File f4 = file_manager.getDesignDirectory();
      File f5 = new File(f4,"main");
      if (f5.exists() && f5.isDirectory()) {
         for (File fx : f5.listFiles()) {
            if (fx.getName().endsWith(".java")) {
               DocumentInfo di = new DocumentInfo(fx,FoamDocumentType.DESIGN);
               document_map.put(fx.getAbsolutePath(),di);
             }
          }
       }
      File f6 = new File(f4,"test");
      if (f6.exists() && f6.isDirectory()) {
         for (File fx : f6.listFiles()) {
            if (fx.getName().endsWith(".java")) {
               DocumentInfo di = new DocumentInfo(fx,FoamDocumentType.TEST_CASE);
               document_map.put(fx.getAbsolutePath(),di);
             }
          }
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
   String fnm = getDocumentPath(filename);
   DocumentInfo di = document_map.get(fnm);
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
      case UI_DESIGN :
         FeltSectionParser sectpar = new FeltSectionParser(di.getFile());
         root = sectpar.parse();  // outline document
         break;
      case LIBRARY :
      case RESOURCE :
      case UI_IMAGE :
         root = new FeltElement(di.getFile().getName(),
               FoamElementType.DOCUMENT,null);
         break;
      case DESCRIPTION :
      case FEATURES :
      case REQUIREMENTS :
         FeltOutlineParser outpar = new FeltOutlineParser(di.getFile());
         root = outpar.parse();  // outline document
         break;
      case TEST_CASE :
      case DESIGN :
         FeltJavaParser javapar = new FeltJavaParser(di.getFile());
         root = javapar.parse();  // outline document
         break;
    }
   di.setRootElement(root);
   
   return root;
}


@Override public void parseLLMOutputForDesign(String file)
   throws FoamException
{
   getRootElement(file);
}


@Override public void addCommentsFromLLM(String file)
   throws FoamException
{
   throw new FoamException("Not implement yet");
}


@Override public void createStructuredOutline(String file)
   throws FoamException
{
   getRootElement(file);
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
         dir = file_manager.getSpecificationDirectory();
         ext = "md";
         break;
      case SCENARIO :
         dir = file_manager.getScenariosDirectory();
         ext = "md";
         break;
      case UI_IMAGE :
      case UI_DESIGN :
         dir = file_manager.getUIDirectory();
         ext = "md png";
         break;
      case TEST_CASE :
         dir = file_manager.getTestsDirectory();
         ext = "java";
         break;
      case DESIGN :
         dir = file_manager.getDesignDirectory();
         ext = "java";
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
   
   String nm0 = getDocumentPath(f1.getAbsolutePath());
   document_map.put(nm0,new DocumentInfo(f1,typ));
}


/********************************************************************************/
/*                                                                              */
/*      Update a document                                                       */
/*                                                                              */
/********************************************************************************/

@Override public void updateDocument(String file,String content)
   throws FoamException
{
   if (file == null) {
      throw new FoamException("File must be specified on updateDocument");
    }
   String fnm = getDocumentPath(file);
   DocumentInfo di = document_map.get(fnm);
   if (di == null) {
      throw new FoamException("File is not known to the system");
    }
   
   if (content == null) {
      di.getFile().delete();
      document_map.remove(fnm);
      return;
    }
   
   if (!content.endsWith("\n")) content += "\n";
   try (FileWriter fw = new FileWriter(file)) {
      fw.write(content);
    }
   catch (IOException e) {
      IvyLog.logE("FELT","Problem updating file",e);
    }
   di.setRootElement(null);
}



/********************************************************************************/
/*                                                                              */
/*      Normalize document path                                                 */
/*                                                                              */
/********************************************************************************/

@Override public String getDocumentPath(String file) throws FoamException
{
   file = file.replace("/",File.separator);
   File f1 = new File(file);
   if (!f1.isAbsolute()) {
      File f2 = new File(project_manager.getCurrentProjectDirectory());
      File f3 = new File(f2,file);
      if (f3.exists()) f1 = f3;
      else {
         String nm = f1.getName();
         for (String s : FOAM_PROJECT_DIRS) {
            String s1 = s.replace("/",File.separator);
            File f4 = new File(f2,s1);
            File f5 = new File(f4,nm);
            if (f5.exists()) {
               f1 = f5;
               break;
             }
          }
       }
    }
   if (!f1.exists()) {
      throw new FoamException("File " + file + " not found");
    }
   
   f1 = IvyFile.getCanonical(f1);
   
   return f1.getAbsolutePath();
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

