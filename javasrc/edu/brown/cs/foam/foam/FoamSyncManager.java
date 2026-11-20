/********************************************************************************/
/*                                                                              */
/*              FoamSyncManager.java                                            */
/*                                                                              */
/*      Handle synchronizations between documents                               */
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


/**
 * Interface for managing specification synchronization.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.M.1 - System shall track which description files are out-of-date with respect to others
 * @see II.M.2 - System shall provide meta command for synchronizing specifications 
 *      (mapping from newest to out-of-date elements)
 * @see II.M.3 - System shall implement STATUS command to return status of all ELEMENTS
 * @see II.M.4 - System shall implement SYNC command for specification synchronization
 */
public interface FoamSyncManager extends FoamConstants
{


/**
 * Checks if a document is up-to-date with respect to others.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to check
 * @return True if document is up-to-date, false otherwise
 * @throws FoamException if status checking fails
 * @see II.M.1 - System shall track which description files are out-of-date with respect to others
 * @test Test that up-to-date documents return true
 * @test Test that out-of-date documents return false
 * @test Test that status checking works for different document types
 */
boolean isDocumentUpToDate(String projectDirectory, String fileName) 
   throws FoamException;

/**
 * Returns the status of all elements in a document.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to check
 * @return Status information for all elements
 * @throws FoamException if status retrieval fails
 * @see II.M.3 - System shall implement STATUS command to return status of all ELEMENTS
 * @test Test that complete status information is returned for all elements
 * @test Test that status format is consistent and informative
 * @test Test that status reflects actual document state accurately
 */
String getStatus(String projectDirectory, String fileName) 
   throws FoamException;

/**
 * Synchronizes specifications between documents.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param sourceFileName The name of the source file
 * @param targetFileName The name of the target file
 * @throws FoamException if synchronization fails
 * @see II.M.4 - System shall implement SYNC command for specification synchronization
 * @test Test that valid synchronization works correctly between documents
 * @test Test that existing content is preserved during synchronization
 * @test Test that synchronization handles conflicts appropriately
 */
void synchronizeSpecifications(String projectDirectory, String sourceFileName,
      String targetFileName) 
   throws FoamException;

/**
 * Maps elements from newest to out-of-date elements.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param sourceFileName The name of the source file
 * @param targetFileName The name of the target file
 * @throws FoamException if mapping fails
 * @see II.M.2 - System shall provide meta command for synchronizing specifications 
 *      (mapping from newest to out-of-date elements)
 * @test Test that element mapping works correctly between documents
 * @test Test that mapping preserves element integrity and relationships
 * @test Test that mapping handles different document structures appropriately
 */
void mapElements(String projectDirectory, String sourceFileName, 
      String targetFileName) 
   throws FoamException;


}       // end of interface FoamSyncManager




/* end of FoamSyncManager.java */

