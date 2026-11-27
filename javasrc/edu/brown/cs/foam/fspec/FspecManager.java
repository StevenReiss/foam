/********************************************************************************/
/*                                                                              */
/*              FspecManager.java                                               */
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



package edu.brown.cs.foam.fspec;

import edu.brown.cs.foam.foam.FoamSyncManager;
import edu.brown.cs.ivy.file.IvyLog;
import edu.brown.cs.ivy.xml.IvyXml;
import edu.brown.cs.ivy.xml.IvyXmlWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import edu.brown.cs.foam.fide.FideSystem;
import edu.brown.cs.foam.foam.FoamChangeRequest;
import edu.brown.cs.foam.foam.FoamChecker;
import edu.brown.cs.foam.foam.FoamDocumentManager;
import edu.brown.cs.foam.foam.FoamElement;
import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamFileHierarchyManager;

public class FspecManager implements FoamSyncManager, FoamChecker, FspecConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private FideSystem      foam_main;
private File            status_file;
private Map<File,DocumentStatus> document_map;



/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

public FspecManager(FideSystem sys)
{
   foam_main = sys;
   document_map = null;
   
   setProject();
}



public void setProject()
{
   if (document_map != null) return;
   
   boolean save = false;
   FoamFileHierarchyManager hm = foam_main.getHierarchyManager();
   try {
      File mdir = hm.getMetadataDirectory();
      status_file = new File(mdir,"SyncStatus.xml"); 
      if (loadKnownStatus()) save = true;
    }
   catch (FoamException e) {
      // no project
    }
   
   FoamDocumentManager dm = foam_main.getDocumentManager();
   for (File f : dm.getAllDocuments()) {
      if (document_map.get(f) == null) {
         try {
            noteEdited(f.getPath());
          }
         catch (FoamException e) {
            IvyLog.logE("FSPEC","Problem handling new file",e);
          }
       }
    }
   
   if (save) saveKnownStatus();
}



/********************************************************************************/
/*                                                                              */
/*      Save.restore project status                                             */
/*                                                                              */
/********************************************************************************/

private boolean loadKnownStatus()
{
   if (status_file == null) return false;
   
   document_map = new HashMap<>();
   List<DocumentStatus> changed = new ArrayList<>();
   Element xml = IvyXml.loadXmlFromFile(status_file);
   for (Element xdoc : IvyXml.children(xml,"DOCUMENT")) {
      try {
         DocumentStatus ds = new DocumentStatus(xdoc);
         document_map.put(ds.getDocument(),ds);
         if (ds.checkChanged()) changed.add(ds);
       }
      catch (FoamException fe) {
         // file went away
       }
    }
   
   boolean rslt = false;
   for (DocumentStatus ds : changed) {
      updateStatus(ds);
      rslt = true;
    }
  
   return rslt;
}


private void saveKnownStatus()
{
   if (status_file == null) return;
   
   try (IvyXmlWriter xw = new IvyXmlWriter(status_file)) {
      xw.begin("DOCUMENTS");
      for (DocumentStatus ds : document_map.values()) {
         ds.outputXml(xw);
       }
      xw.end("DOCUMENTS");
    }
   catch (IOException e) {
      IvyLog.logE("FSPEC","Problem saving document status",e);
    }
}



/********************************************************************************/
/*                                                                              */
/*      Return document status information                                      */
/*                                                                              */
/********************************************************************************/

@Override public String getStatus(String filename) throws FoamException
{
   IvyXmlWriter xw = new IvyXmlWriter();
   FoamDocumentManager dm = foam_main.getDocumentManager();
   
   if (filename == null) {
      List<FoamElement> elts = dm.identifyElements(null,
            FoamElementType.DOCUMENT);
      for (FoamElement fe : elts) {
         try {
            File filnm = dm.getDocumentPath(fe.getName());
            outputStatus(filnm,xw);
          }
         catch (FoamException e) {
            IvyLog.logE("FSPEC",
                  "Problem getting document path for " + fe.getName(),e);
          }
       }
    }
   else {
      File filnm = dm.getDocumentPath(filename);
      outputStatus(filnm,xw);
    }
   
   return xw.toString();
}


private void outputStatus(File file,IvyXmlWriter xw) throws FoamException
{
   FoamDocumentManager dm = foam_main.getDocumentManager();
   FoamDocumentType typ = dm.getDocumentType(file.getPath());
   
   xw.begin("DOCUMENT");
   xw.field("FILE",file.getAbsolutePath());
   xw.field("TYPE",typ);
   xw.field("STATUS","UNKNOWN");
   xw.end("DOCUMENT");
}


/********************************************************************************/
/*                                                                              */
/*      Handle file edited (or created)                                         */
/*                                                                              */
/********************************************************************************/

@Override public void noteEdited(String file) throws FoamException
{
   FoamDocumentManager dm = foam_main.getDocumentManager();
   File df = dm.getDocumentPath(file);
   DocumentStatus ds = document_map.get(df);
   if (ds == null) {
      // file added:
      ds = new DocumentStatus(df);
      document_map.put(df,ds);
    }
   ds.noteEdited();
   // now update status of all other documents
   saveKnownStatus();
}


/********************************************************************************/
/*                                                                              */
/*      Handle automatic transformations using LLM                              */
/*                                                                              */
/********************************************************************************/

@Override public void mapSpecification(FoamDocumentType source,FoamDocumentType target,
      boolean preserve) 
   throws FoamException
{
   // method body goes here
}






@Override public void synchronizeSpecifications(String arg0,String arg1,String arg2)
{
   // method body goes here
}






@Override public boolean isDocumentUpToDate(String arg0,String arg1)
{
   // method body goes here

   return false;
}






@Override public boolean isSufficientForTestGeneration(String arg0,String arg1)
{
   // method body goes here

   return false;
}






@Override public boolean isSufficientForVerificationGeneration(String arg0,String arg1)
{
   // method body goes here

   return false;
}






@Override public boolean isSufficientForCodeGeneration(String arg0,String arg1)
{
   // method body goes here

   return false;
}






@Override public boolean performExtensibleCheck(String arg0,String arg1,String arg2)
{
   // method body goes here

   return false;
}






@Override public List<FoamChangeRequest> checkCompletenessAndConsistency(String arg0,String arg1)
{
   // method body goes here

   return null;
}



/********************************************************************************/
/*                                                                              */
/*      Propogate status changes                                                */
/*                                                                              */
/********************************************************************************/

private void updateStatus(DocumentStatus changed)
{ 
   for (DocumentStatus ds : document_map.values()) {
      if (ds == changed) continue;
    }
}



/********************************************************************************/
/*                                                                              */
/*      Document Status information                                             */
/*                                                                              */
/********************************************************************************/

private class DocumentStatus {

   private File for_document;
   private FoamDocumentStatus cur_status;
   private File updated_from;                   // source document
   private long modified_time;                  // time stamp of source document 
   private long updated_time;                   // time stamp after update
   
   DocumentStatus(File f) {
      for_document = f;
      cur_status = FoamDocumentStatus.EDITED;
      updated_from = null;
      modified_time = 0;
      updated_time = System.currentTimeMillis();
    }
   
   DocumentStatus(Element xml) throws FoamException {
      String fnm = IvyXml.getAttrString(xml,"FILE");
      FoamDocumentManager dm = foam_main.getDocumentManager();
      for_document = dm.getDocumentPath(fnm);
      cur_status = IvyXml.getAttrEnum(xml,"STATUS",FoamDocumentStatus.UNKNOWN);
      updated_time = IvyXml.getAttrLong(xml,"TIME");
      updated_from = null;
      modified_time = 0;
      String unm = IvyXml.getAttrString(xml,"FROM");
      if (unm != null) {
         try {
            updated_from = dm.getDocumentPath(unm);
            modified_time = IvyXml.getAttrLong(xml,"MODIFIED");
          }
         catch (FoamException fe) { 
            // from file disappeared
          }
       }
      if (for_document.lastModified() > updated_time) {
         noteEdited();
       }
    }
   
   boolean checkChanged() {
      if (for_document.lastModified() <= updated_time) return false;
      noteEdited();
      return true;
    }
   
   File getDocument()                                   { return for_document; }
   long getUpdatedTime() {
      if (updated_from == null) return modified_time;
      DocumentStatus ds = document_map.get(updated_from);
      if (ds == null) return modified_time;
      return ds.getUpdatedTime();
    }
   
   DocumentStatus outOfDate(DocumentStatus ds) {
      return null;
}
   
   void outputXml(IvyXmlWriter xw) {
      xw.begin("DOCUMENT");
      xw.field("FILE",for_document);
      xw.field("STATUS",cur_status);
      xw.field("TIME",updated_time);
      if (updated_from != null) {
         xw.field("FROM",updated_from);
         xw.field("MODIFIED",modified_time);
       }
      xw.end("DOCUMENT");
    }
   
   void noteFrom(DocumentStatus ds) {
      cur_status = FoamDocumentStatus.UPDATED;
      updated_from = ds.getDocument();
      modified_time = ds.getUpdatedTime();
    }
   
   void noteEdited() {
      cur_status = FoamDocumentStatus.EDITED;
      updated_time = for_document.lastModified();
      updated_from = null;
      modified_time = 0;
    }
   
}       // end of inner class DocumentStatus





}       // end of class FspecManager




/* end of FspecManager.java */

