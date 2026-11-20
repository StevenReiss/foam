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

import org.w3c.dom.Element;


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
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to create
 * @param documentType The type of document to create
 * @throws FoamException if document creation fails due to invalid parameters or file system issues
 * @see II.C.1 - System shall support multiple specification formats
 * @test Test that valid document creation succeeds for all supported document types
 * @test Test that invalid parameters throw appropriate exceptions
 * @test Test that document is created with proper file extension based on type
 */
void createDocument(String projectDirectory, String fileName, 
      FoamDocumentType documentType) 
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
Element getRootElement(String projectDirectory, String fileName)
   throws FoamException;

/**
 * Gets the absolute path to a document.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to locate
 * @return The absolute path to the document
 * @throws FoamException if document cannot be located
 * @see II.D.4 - System shall support document identification by absolute path, relative path, or filename
 * @test Test that valid document returns correct absolute path
 * @test Test that non-existent document throws appropriate exception
 * @test Test that relative paths are resolved correctly
 */
String getDocumentPath(String projectDirectory, String fileName) 
   throws FoamException;

/**
 * Updates a document with new content.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to update
 * @param content The new content for the document
 * @throws FoamException if document update fails due to invalid parameters or file system issues
 * @see II.G.1 - System shall support primary UPDATE command for converting between representations
 * @test Test that valid document update succeeds and preserves existing content where appropriate
 * @test Test that invalid parameters throw appropriate exceptions
 * @test Test that update maintains document structure integrity
 */
void updateDocument(String projectDirectory, String fileName, String content)
   throws FoamException;

/**
 * Parses LLM output to create Java source files for design specifications.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file containing LLM output
 * @throws FoamException if parsing fails or Java file creation fails
 * @see II.F.1 - System shall parse LLM output to create Java source files for design specifications
 * @test Test that valid LLM output is parsed correctly into Java source files
 * @test Test that invalid LLM output throws appropriate exception
 * @test Test that generated Java files have proper structure and syntax
 */
void parseLLMOutputForDesign(String projectDirectory, String fileName) 
   throws FoamException;

/**
 * Creates structured outline files that can easily extract subelements.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to process
 * @throws FoamException if outline creation fails
 * @see II.F.2 - System shall create structured outline files that can easily extract subelements
 * @test Test that structured outline is created with proper hierarchy
 * @test Test that outline supports easy extraction of subelements
 * @test Test that outline format is consistent across different document types
 */
void createStructuredOutline(String projectDirectory, String fileName) 
   throws FoamException;

/**
 * Adds missing information as comments to relevant elements when LLM provides additional details.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file containing LLM output
 * @throws FoamException if comment addition fails
 * @see II.F.3 - System shall add missing information as comments to relevant elements when 
 *      LLM provides additional details
 * @test Test that appropriate comments are added to existing elements
 * @test Test that comments are properly formatted and positioned
 * @test Test that no comments are added to elements where information already exists
 */
void addCommentsFromLLM(String projectDirectory, String fileName) 
   throws FoamException;


}       // end of interface FoamDocumentManager




/* end of FoamDocumentManager.java */

