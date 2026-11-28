/********************************************************************************/
/*                                                                              */
/*              FoamProjectManager.java                                         */
/*                                                                              */
/*      Project Manager interface                                               */
/*                                                                              */
/********************************************************************************/
/*      Copyright 2025 Brown University -- Steven P. Reiss                    */
/*********************************************************************************
 *  Copyright 2025, Brown University, Providence, RI.                            *
 *                                                                               *
 *                        All Rights Reserved                                    *
 *                                                                               *
 *  Permission to use, copy, modify, and distribute this software and its        *
 *  documentation for any purpose other than its incorporation into a            *
 *  commercial product is hereby granted without fee, provided that the          *
 *  above copyright notice appear in all copies and that both that               *
 *  copyright notice and this permission notice appear in supporting             *
 *  documentation, and that the name of Brown University not be used in          *
 *  advertising or publicity pertaining to distribution of the software          *
 *  without specific, written prior permission.                                  *
 *                                                                               *
 *  BROWN UNIVERSITY DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS                *
 *  SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND            *
 *  FITNESS FOR ANY PARTICULAR PURPOSE.  IN NO EVENT SHALL BROWN UNIVERSITY      *
 *  BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY          *
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,              *
 *  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS               *
 *  ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE          *
 *  OF THIS SOFTWARE.                                                            *
 *                                                                               *
 ********************************************************************************/



package edu.brown.cs.foam.foam;

import java.io.File;

/**
 * Interface for managing FOAM projects and their file hierarchies.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.A.1 - System shall maintain knowledge of project name and directory where project should be saved
 * @see II.A.2 - System shall manage work within a project directory with defined file hierarchy structure
 * @see II.A.3 - System shall support single project operation at a time with global awareness of current project
 * @see II.A.4 - System shall provide ability to set project either from front end or via XML PROJECT command
 */
public interface FoamProjectManager extends FoamConstants
{


/**
 * Sets the current project directory.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @throws FoamException if project directory is invalid or inaccessible
 * @see II.A.4 - System shall provide ability to set project either from front end or via XML PROJECT command
 * @test Test that valid project directory sets successfully and invalid directory throws exception
 * @test Test that setting project updates global awareness of current project
 */
void setProject(String projectDirectory) throws FoamException;

/**
 * Creates a new project with specified name and directory.
 * 
 * @param projectName The name of the project to create
 * @param projectDirectory The directory where project should be created
 * @throws FoamException if project creation fails due to invalid parameters or file system issues
 * @see II.N.2 - System shall support PROJECT command for setting project workspace
 * @see II.N.3 - System shall support CREATE command for creating new (empty) workspace
 * @test Test that valid project creation succeeds and creates all required directories
 * @test Test that invalid parameters throw appropriate exceptions
 * @test Test that project directory is properly initialized with standard hierarchy
 */
void createProject(String projectName, String projectDirectory) throws FoamException;

/**
 * Gets the current project directory.
 * 
 * @return The absolute path to the current project directory
 * @throws FoamException if no project is currently set
 * @see II.A.3 - System shall support single project operation at a time with global awareness of current project
 * @test Test that valid project returns correct directory path
 * @test Test that no project throws appropriate exception
 */
String getCurrentProjectDirectory() throws FoamException;

/**
 * Gets the project name.
 * 
 * @return The name of the current project
 * @throws FoamException if no project is currently set
 * @see II.A.1 - System shall maintain knowledge of project name and directory where project should be saved
 * @test Test that valid project returns correct name
 * @test Test that no project throws appropriate exception
 */
String getProjectName() throws FoamException;


/**
 * Creates the standard file hierarchy for a FOAM project.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @throws FoamException if hierarchy creation fails due to invalid parameters or file system issues
 * @see II.B.1 - System shall maintain file hierarchy within project directory with following top-level directories
 * @test Test that standard hierarchy is created with all required directories
 * @test Test that invalid project directory throws appropriate exception
 * @test Test that existing hierarchy is not overwritten without explicit flag
 */
void createStandardHierarchy(String projectDirectory) throws FoamException;

/**
 * Gets the metadata directory path for a project.
 * 
 * @return The absolute path to the metadata directory
 * @throws FoamException if project directory is invalid or metadata directory cannot be accessed
 * @see II.B.1.a - metadata directory for properties and data maintained by software
 * @test Test that valid project returns correct metadata directory path
 * @test Test that invalid project directory throws appropriate exception
 */
File getMetadataDirectory() throws FoamException;

/**
 * Gets the resources directory path for a project.
 * 
 * @return The absolute path to the resources directory
 * @throws FoamException if project directory is invalid or resources directory cannot be accessed
 * @see II.B.1.b - resources directory for sample resource files
 * @test Test that valid project returns correct resources directory path
 * @test Test that invalid project directory throws appropriate exception
 */
File getResourcesDirectory() throws FoamException;

/**
 * Gets the specification directory path for a project.
 * 
 * @return The absolute path to the specification directory
 * @throws FoamException if project directory is invalid or specification directory cannot be accessed
 * @see II.B.1.c - specification directory containing various specification documents
 * @test Test that valid project returns correct specification directory path
 * @test Test that invalid project directory throws appropriate exception
 */
File getSpecificationDirectory() throws FoamException;

/**
 * Gets the design directory path for a project.
 * 
 * @return The absolute path to the design directory
 * @throws FoamException if project directory is invalid or design directory cannot be accessed
 * @see II.B.1.d - design directory for design specifications
 * @test Test that valid project returns correct design directory path
 * @test Test that invalid project directory throws appropriate exception
 */
File getDesignDirectory() throws FoamException;

/**
 * Gets the lib directory path for a project.
 * 
 * @return The absolute path to the lib directory
 * @throws FoamException if project directory is invalid or lib directory cannot be accessed
 * @see II.B.1.e - lib directory for external software references and documentation
 * @test Test that valid project returns correct lib directory path
 * @test Test that invalid project directory throws appropriate exception
 */
File getLibDirectory() throws FoamException;



/**
 * Gets the scenarios directory path for a project.
 * 
 * @return The absolute path to the lib directory
 * @throws FoamException if project directory is invalid or lib directory cannot be accessed
 * @see II.B.1.e - lib directory for external software references and documentation
 * @test Test that valid project returns correct lib directory path
 * @test Test that invalid project directory throws appropriate exception
 */
File getScenariosDirectory() throws FoamException;


/**
 * Gets the user interface directory path for a project.
 * 
 * @return The absolute path to the lib directory
 * @throws FoamException if project directory is invalid or lib directory cannot be accessed
 * @see II.B.1.e - lib directory for external software references and documentation
 * @test Test that valid project returns correct lib directory path
 * @test Test that invalid project directory throws appropriate exception
 */
File getUIDirectory() throws FoamException;


/**
 * Gets the test cases directory path for a project.
 * 
 * @return The absolute path to the lib directory
 * @throws FoamException if project directory is invalid or lib directory cannot be accessed
 * @see II.B.1.e - lib directory for external software references and documentation
 * @test Test that valid project returns correct lib directory path
 * @test Test that invalid project directory throws appropriate exception
 */
File getTestsDirectory() throws FoamException;


}       // end of interface FoamProjectManager




/* end of FoamProjectManager.java */

