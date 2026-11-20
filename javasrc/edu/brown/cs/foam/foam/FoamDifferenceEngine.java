/********************************************************************************/
/*                                                                              */
/*              FoamDifferenceEngine.java                                       */
/*                                                                              */
/*      Handle differencing elements and files                                  */
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
 * Interface for the differencing engine that compares structured documents.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.R.1 - System shall support differencing requests between versions of structured documents
 * @see II.R.2 - System shall provide differences in terms of changed, added, or deleted elements
 * @see II.R.3 - System shall encode differences as XML or JSON and return to front end
 * @see II.R.4 - System shall access previous version of single elements
 * @see II.R.5 - System shall implement smart differencing engine
 */
public interface FoamDifferenceEngine extends FoamConstants
{


/**
 * Computes differences between two versions of a document.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to compare
 * @param version1 The first version to compare
 * @param version2 The second version to compare
 * @return The differences encoded as XML or JSON
 * @throws FoamException if difference computation fails
 * @see II.R.1 - System shall support differencing requests between versions of structured documents
 * @test Test that differences are computed correctly between versions
 * @test Test that different output formats (XML/JSON) work properly
 * @test Test that comparison handles edge cases and large documents efficiently
 */
String computeDifferences(String projectDirectory,
      String fileName, String version1, String version2) 
   throws FoamException;

/**
 * Gets the previous version of an element.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file containing the element
 * @param elementName The name of the element
 * @param version The version to retrieve
 * @return The content of the element at the specified version
 * @throws FoamException if element retrieval fails
 * @see II.R.4 - System shall access previous version of single elements
 * @test Test that previous versions are retrieved correctly
 * @test Test that invalid versions return appropriate error responses
 * @test Test that element content is preserved properly across versions
 */
String getPreviousElementVersion(String projectDirectory, String fileName,
      String elementName, String version) 
   throws FoamException;

/**
 * Gets the current differences for a document.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to check
 * @return The current differences encoded as XML or JSON
 * @throws FoamException if difference retrieval fails
 * @see II.R.3 - System shall encode differences as XML or JSON and return to front end
 * @test Test that current differences are computed correctly
 * @test Test that output format is consistent with expected XML/JSON structure
 * @test Test that differences reflect actual document changes
 */
String getCurrentDifferences(String projectDirectory, String fileName) 
   throws FoamException;

/**
 * Computes smart differences between documents.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to compare
 * @return Smart differences encoded as XML or JSON
 * @throws FoamException if smart difference computation fails
 * @see II.R.5 - System shall implement smart differencing engine
 * @test Test that smart differences are computed correctly with intelligent analysis
 * @test Test that smart differences provide meaningful insights beyond simple diff
 * @test Test that smart computation handles various document types appropriately
 */
String computeSmartDifferences(String projectDirectory, String fileName)
   throws FoamException;

}       // end of interface FoamDifferenceEngine




/* end of FoamDifferenceEngine.java */

