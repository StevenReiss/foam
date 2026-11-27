/********************************************************************************/
/*                                                                              */
/*              FideProjectManager.java                                         */
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import edu.brown.cs.foam.foam.FoamException;
import edu.brown.cs.foam.foam.FoamFileHierarchyManager;
import edu.brown.cs.foam.foam.FoamProjectManager;
import edu.brown.cs.ivy.file.IvyLog;

class FideProjectManager implements FoamProjectManager,
      FoamFileHierarchyManager, FideConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private FideSystem      foam_main;
private File            project_dir;
private String          project_name;
private Properties      project_props;


/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

FideProjectManager(FideSystem sys)
{
   foam_main = sys;
   project_dir = null;
   project_name = null;
   project_props = null;
}


/********************************************************************************/
/*                                                                              */
/*      Access methods                                                          */
/*                                                                              */
/********************************************************************************/

@Override public String getCurrentProjectDirectory() throws FoamException
{
   if (project_dir == null) {
      throw new FoamException("No project set");
    }
   return project_dir.getAbsolutePath();
}

File getProjectDirectory()
{
   return project_dir;
}


@Override public String getProjectName() throws FoamException
{
   if (project_dir == null) {
      throw new FoamException("No project set");
    }
   return project_name;
}


public String getProperty(String name) 
{
   return getProperty(name,null);
}


public String getProperty(String name,String dflt)
{
   if (project_props == null) return dflt;
   
   String rslt = project_props.getProperty(name);
   if (rslt == null) rslt = dflt;
   return rslt;
}


boolean haveProject()
{
   return project_dir != null;
}

/********************************************************************************/
/*                                                                              */
/*      Set current project                                                     */
/*                                                                              */
/********************************************************************************/

@Override public void setProject(String dir) throws FoamException
{
   if (dir == null || dir.isBlank()) {
      throw new FoamException("Project directory can't be empty");
    }
   File dirf = new File(dir);
   if (!isProjectDirectory(dirf)) {
      throw new FoamException("Bad project directory");
    }
   setupDirectory(null,dirf,false);
   
   project_dir = dirf;
   project_props = new Properties();
   File f1 = new File(project_dir,"metadata");
   File f2 = new File(f1,"Foam.props");
   if (f2.exists()) {
      try (InputStream ins = new FileInputStream(f2)) {
         project_props.loadFromXML(ins);
       }
      catch (IOException e) {
         throw new FoamException("Problem reading property file",e);
       }  
    }
   project_name = project_props.getProperty("NAME");
   if (project_name == null) {
      project_name = dirf.getName();
      project_props.put("NAME",project_name);
      saveProperties();
    }
   
   foam_main.noteProjectSet();
}



/********************************************************************************/
/*                                                                              */
/*      Setup a project                                                         */
/*                                                                              */
/********************************************************************************/

@Override public void createProject(String name,String dir)
   throws FoamException
{
   if (dir == null || dir.isBlank()) {
      throw new FoamException("Project directory can't be empty");
    }
   File dirf = new File(dir);
   
   setupDirectory(name,dirf,true);
   
   setProject(dir);
}


private void setupDirectory(String name,File dirf,boolean init)
   throws FoamException
{
  
   if (dirf.exists()) {
      init = false;
    }
    else if (!dirf.mkdirs()) {
      throw new FoamException("Can't create project directory " + dirf);
    }

   for (String s : FOAM_PROJECT_DIRS) {
      File f1 = dirf;
      for (String f : s.split("/")) {
         File f2 = new File(f1,f);
         if (!f2.exists() || !f2.isDirectory()) {
            if (!f2.mkdirs()) {
               throw new FoamException("Can't create project subdirectory " + f2);
             }
          }
         f1 = f2;
       }
    }
   
   File f3 = new File(dirf,"metadata");
   File f4 = new File(f3,PROPERTY_FILE);
   Properties p = new Properties();
   if (f4.exists()) {
      try (InputStream ins = new FileInputStream(f4)) {
         p.loadFromXML(ins);
       }
      catch (IOException e) {
         throw new FoamException("Problem reading property file",e);
       }  
    }
   if (!p.containsKey("NAME")) {
      if (name == null || name.isBlank()) {
         throw new FoamException("Name must not be empty for new project");
       }
      p.put("NAME",name);
      try (OutputStream ots = new FileOutputStream(f4)) {
         p.storeToXML(ots,"Created by FOAM");
       }
      catch (IOException e) {
         throw new FoamException("Problem writing property file",e);
       }
    }
}



private void saveProperties()
{
   File f1 = new File(project_dir,"metadata");
   File f2 = new File(f1,PROPERTY_FILE);
   try (OutputStream ots = new FileOutputStream(f2)) {
      project_props.storeToXML(ots,"Created by FOAM");
    }
   catch (IOException e) {
      IvyLog.logE("FIDE","Problem writing property file",e);
    } 
}


private boolean isProjectDirectory(File dirf)
{
   String [] check = { "metadata", "specification" };
   for (String s : check) {
      File f1 = dirf;
      for (String f : s.split("/")) {
         File f2 = new File(f1,f);
         if (!f2.exists() || !f2.isDirectory()) return false;
         f1 = f2;
       } 
    }
   return true;
}



/********************************************************************************/
/*                                                                              */
/*      File Hierarhcy management                                               */
/*                                                                              */
/********************************************************************************/

@Override public void createStandardHierarchy(String dir) throws FoamException
{
   if (dir == null || dir.isBlank()) {
      throw new FoamException("Project directory can't be empty");
    }
   File dirf = new File(dir);
   if (!isProjectDirectory(dirf)) {
      throw new FoamException("Bad project directory");
    }
   setupDirectory(null,dirf,false);
}


@Override public File getMetadataDirectory() throws FoamException
{
   return getDirectory("metadata");
}

@Override public File getResourcesDirectory() throws FoamException
{
   return getDirectory("resources");
}

@Override public File getSpecificationDirectory() throws FoamException
{
   return getDirectory("specification");
}

@Override public File getDesignDirectory() throws FoamException
{
   return getDirectory("design");
}


@Override public File getTestsDirectory() throws FoamException
{
   return getDirectory("tests");
}



@Override public File getLibDirectory() throws FoamException
{
   return getDirectory("lib");
}


@Override public File getScenariosDirectory() throws FoamException
{
   return getDirectory("scenarios");
}


@Override public File getUIDirectory() throws FoamException
{
   return getDirectory("userinterfaces");
}



private File getDirectory(String which) throws FoamException
{
   if (project_dir == null) {
      throw new FoamException("No current project");
    }
   
   File rslt = new File(project_dir,which);
   return rslt;
}


}       // end of class FideProjectManager




/* end of FideProjectManager.java */

