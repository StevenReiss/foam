/********************************************************************************/
/*                                                                              */
/*              FoamVersioning.java                                             */
/*                                                                              */
/*      Handle version management of files                                      */
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

import java.util.List;

/**
 * Interface for managing version control in FOAM projects.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.J.1 - System shall provide robust UNDO/REDO facility for file hierarchy
 * @see II.J.2 - System shall support named safe points that users can navigate back to
 * @see II.J.3 - System shall maintain history across system runs (persistent version control)
 * @see II.J.4 - System shall implement SETVERSION command for UNDO, REDO, and GOTO operations
 * @see II.J.5 - System shall implement COMMIT command for numbering and naming current versions
 * @see II.J.6 - System shall support DIFFS command to access previous version of documents
 */
public interface FoamVersionManager extends FoamConstants
{


/**
 * Creates a new version point.
 * 
 * @param projectName The name of the project
 * @param versionName The name for this version; can be null (new name)
 * @throws FoamException if version creation fails
 * @returns version name
 * @see II.J.5 - System shall implement COMMIT command for numbering and naming current versions
 * @test Test that valid version creation succeeds with proper naming
 * @test Test that version names are unique within project
 * @test Test that version creation preserves all project state
 */
String commit(String versionName) throws FoamException;

/**
 * Sets the current version.
 * 
 * @param projectName The name of the project
 * @param versionName The name of the version to set
 * @returns version name
 * @throws FoamException if version setting fails
 * @see II.J.4 - System shall implement SETVERSION command for UNDO, REDO, and GOTO operations
 * @test Test that valid version setting works correctly
 * @test Test that invalid version names throw appropriate exceptions
 * @test Test that version setting updates system state properly
 */
String setVersion(String versionName) throws FoamException;

/**
 * Performs an undo operation.
 * 
 * @param projectName The name of the project
 * @throws FoamException if undo fails
 * @returns version name
 * @see II.J.1 - System shall provide robust UNDO/REDO facility for file hierarchy
 * @test Test that undo operations work correctly and restore previous state
 * @test Test that multiple consecutive undo operations work properly
 * @test Test that undo preserves system integrity
 */
String undo() throws FoamException;

/**
 * Performs a redo operation.
 * 
 * @param projectName The name of the project
 * @throws FoamException if redo fails
 * @returns version name
 * @see II.J.1 - System shall provide robust UNDO/REDO facility for file hierarchy
 * @test Test that redo operations work correctly and restore future state
 * @test Test that multiple consecutive redo operations work properly
 * @test Test that redo preserves system integrity
 */
String redo() throws FoamException;

/**
 * Gets differences between versions.
 * 
 * @param elementName The name of the file or element
 * @param version1 The first version to compare
 * @param version2 The second version to compare
 * @return The differences between the versions
 * @throws FoamException if difference calculation fails
 * @see II.J.6 - System shall support DIFFS command to access previous version of documents
 * @test Test that differences are calculated correctly between versions
 * @test Test that different formats (XML/JSON) are supported for output
 * @test Test that comparison handles edge cases properly
 */
String getDifferences(String elementName, String version1, String version2) 
   throws FoamException;

/**
 * Gets all available versions for a project.
 * 
 * @param projectName The name of the project
 * @return A list of version names
 * @throws FoamException if version listing fails
 * @see II.J.2 - System shall support named safe points that users can navigate back to
 * @test Test that all versions are properly listed for valid projects
 * @test Test that empty project returns empty list
 * @test Test that invalid project names throw appropriate exceptions
 */
List<String> getAvailableVersions() throws FoamException;

}       // end of interface FoamVersionManager



/* end of FoamVersioning.java */

