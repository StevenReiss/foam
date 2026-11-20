/********************************************************************************/
/*                                                                              */
/*              FoamElementManager.java                                         */
/*                                                                              */
/*      Manage elements in the specifications                                   */
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
 * Interface for managing links between elements in FOAM documents.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.L.1 - System shall support linking elements between description files
 * @see II.L.2 - System shall create links implicitly based on text content or JavaDoc keywords
 * @see II.L.3 - System shall support explicit textual references to other elements
 * @see II.L.4 - System shall find all links from or to an element
 * @see II.L.5 - System shall compute links on-the-fly as needed
 * @see II.L.6 - System shall support FIND command with source file and position to determine 
 *      relevant element and find references
 */
public interface FoamLinkManager extends FoamConstants
{


/**
 * Creates a link between two elements.
 * 
 * @param sourceProject The project containing the source element
 * @param sourceFile The file containing the source element
 * @param sourceElement The source element name
 * @param targetProject The project containing the target element
 * @param targetFile The file containing the target element
 * @param targetElement The target element name
 * @throws FoamException if link creation fails
 * @see II.L.1 - System shall support linking elements between description files
 * @test Test that valid links are created successfully
 * @test Test that invalid parameters throw appropriate exceptions
 * @test Test that link integrity is maintained across different projects and files
 */
void createLink(String sourceProject, String sourceFile, String sourceElement, 
      String targetProject, String targetFile, String targetElement) 
   throws FoamException;

/**
 * Finds all links from an element.
 * 
 * @param project The project containing the element
 * @param file The file containing the element
 * @param elementName The name of the element
 * @return A list of linked elements
 * @throws FoamException if link finding fails
 * @see II.L.4 - System shall find all links from or to an element
 * @test Test that all outgoing links are found correctly
 * @test Test that empty element returns empty list
 * @test Test that invalid parameters throw appropriate exceptions
 */
List<FoamLink> findLinksFrom(String project, String file, String elementName) 
     throws FoamException;

/**
 * Finds all links to an element.
 * 
 * @param project The project containing the element
 * @param file The file containing the element
 * @param elementName The name of the element
 * @return A list of linked elements
 * @throws FoamException if link finding fails
 * @see II.L.4 - System shall find all links from or to an element
 * @test Test that all incoming links are found correctly
 * @test Test that empty element returns empty list
 * @test Test that invalid parameters throw appropriate exceptions
 */
List<FoamLink> findLinksTo(String project, String file, String elementName)
    throws FoamException;

/**
 * Finds references to an element.
 * 
 * @param project The project containing the element
 * @param file The file containing the element
 * @param elementName The name of the element
 * @return A list of reference locations
 * @throws FoamException if reference finding fails
 * @see II.L.6 - System shall support FIND command with source file and position to 
 *      determine relevant element and find references
 * @test Test that all reference locations are found correctly
 * @test Test that reference positions are accurate
 * @test Test that invalid parameters throw appropriate exceptions
 */
List<FoamReferenceLocation> findReferences(String project, String file, String elementName)
             throws FoamException;

/**
 * Computes links on-the-fly as needed.
 * 
 * @param project The project containing the elements
 * @param fileName The name of the file to process
 * @throws FoamException if link computation fails
 * @see II.L.5 - System shall compute links on-the-fly as needed
 * @test Test that links are computed correctly when needed
 * @test Test that computation handles large documents efficiently
 * @test Test that computation preserves existing link information
 */
void computeLinks(String project, String fileName) throws FoamException;


}       // end of interface FoamElementManager




/* end of FoamElementManager.java */

