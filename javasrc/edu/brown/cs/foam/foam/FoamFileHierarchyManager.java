/********************************************************************************/
/*                                                                              */
/*              FoamFileHierarchyManager.java                                   */
/*                                                                              */
/*      Maintain project file hierarchy                                         */
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



package edu.brown.cs.foam.foam;

import java.io.File;

/**
 * Interface for managing file hierarchy within FOAM projects.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.B.1 - System shall maintain file hierarchy within project directory with following top-level directories
 */
public interface FoamFileHierarchyManager extends FoamConstants
{


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
 * @param projectDirectory The absolute path to the project directory
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
 * @param projectDirectory The absolute path to the project directory
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
 * @param projectDirectory The absolute path to the project directory
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
 * @param projectDirectory The absolute path to the project directory
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
 * @param projectDirectory The absolute path to the project directory
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
 * @param projectDirectory The absolute path to the project directory
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
 * @param projectDirectory The absolute path to the project directory
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
 * @param projectDirectory The absolute path to the project directory
 * @return The absolute path to the lib directory
 * @throws FoamException if project directory is invalid or lib directory cannot be accessed
 * @see II.B.1.e - lib directory for external software references and documentation
 * @test Test that valid project returns correct lib directory path
 * @test Test that invalid project directory throws appropriate exception
 */
File getTestsDirectory() throws FoamException;


}       // end of interface FoamFileHierarchyManager




/* end of FoamFileHierarchyManager.java */

