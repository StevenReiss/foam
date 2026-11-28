/********************************************************************************/
/*                                                                              */
/*              FoamDocumentManager.java                                        */
/*                                                                              */
/*      Manage the various documents for a project                              */
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
import java.util.Collection;
import java.util.List;

/**
 * Interface for managing specification documents within FOAM projects.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.C.1 - System shall support multiple specification formats
 */
public interface FoamDocumentManager extends FoamConstants
{


/**
 * Creates a new specification document with specified format.
 * 
 * @param fileName The name of the file to create
 * @param documentType The type of document to create
 * @throws FoamException if document creation fails due to invalid parameters or file system issues
 * @see II.C.1 - System shall support multiple specification formats
 * @test Test that valid document creation succeeds for all supported document types
 * @test Test that invalid parameters throw appropriate exceptions
 * @test Test that document is created with proper file extension based on type
 */
void createDocument(String fileName,FoamDocumentType documentType) 
   throws FoamException;

/**
 * Gets the root element of a document.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to process
 * @return The root element of the document
 * @throws FoamException if document cannot be processed or root element cannot be identified
 * @see II.D.2 - System shall identify and return root element of a file given its name
 * @test Test that valid document returns correct root element
 * @test Test that invalid document throws appropriate exception
 * @test Test that markdown outline parsing works correctly for different formats
 */
FoamElement getRootElement(String fileName)
   throws FoamException;


/**
 * Get type of document
 **/
FoamDocumentType getDocumentType(String filename) throws FoamException;


/**
 * Identifies elements within a document.
 * 
 * @param fileName The name of the file to process
 * @param elementType The type of element to identify
 * @return A list of identified elements
 * @throws FoamException if element identification fails
 * @see II.E.2 - System shall identify ELEMENTs of components
 * @test Test that correct elements are identified for different document types
 * @test Test that element identification works for both design and outline specifications
 * @test Test that invalid parameters throw appropriate exceptions
 */
List<FoamElement> identifyElements(String fileName,FoamElementType elementType) 
            throws FoamException;


/**
 * Gets the absolute path to a document.
 * 
 * @param fileName The name of the file or element to locate
 * @return The absolute path to the document
 * @throws FoamException if document cannot be located
 * @see II.D.4 - System shall support document identification by absolute path, relative path, or filename
 * @test Test that valid document returns correct absolute path
 * @test Test that non-existent document throws appropriate exception
 * @test Test that relative paths are resolved correctly
 */
File getDocumentPath(String fileName) 
   throws FoamException;

/**
 * Updates a document with new content.
 * 
 * @param fileName The name of the file to update
 * @param content The new content for the document
 * @throws FoamException if document update fails due to invalid parameters or file system issues
 * @see II.G.1 - System shall support primary UPDATE command for converting between representations
 * @test Test that valid document update succeeds and preserves existing content where appropriate
 * @test Test that invalid parameters throw appropriate exceptions
 * @test Test that update maintains document structure integrity
 */
void updateDocument(String fileName, String content)
   throws FoamException;


/**
 * Adds missing information as comments to relevant elements when LLM provides additional details.
 * 
 * @param fileName The name of the file containing LLM output
 * @throws FoamException if comment addition fails
 * @see II.F.3 - System shall add missing information as comments to relevant elements when 
 *      LLM provides additional details
 * @test Test that appropriate comments are added to existing elements
 * @test Test that comments are properly formatted and positioned
 * @test Test that no comments are added to elements where information already exists
 */
void addCommentsFromLLM(String fileName) 
   throws FoamException;

/**
 * Return the list of all top-level documents
 **/
Collection<File> getAllDocuments();

/**
 * Return the contents of a particular specification type
 **/
String getSpecificationText(FoamDocumentType type);

}       // end of interface FoamDocumentManager




/* end of FoamDocumentManager.java */

