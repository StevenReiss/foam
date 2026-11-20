/********************************************************************************/
/*                                                                              */
/*              FoamElementLocation.java                                        */
/*                                                                              */
/*      Location of an element                                                  */
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
 * Interface representing a reference location in FOAM documents.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.L.6 - System shall support FIND command with source 
 *      file and position to determine relevant element and find references
 */
public interface FoamReferenceLocation extends FoamConstants
{

/**
 * Gets the file containing the reference.
 * 
 * @return The file name
 * @test Test that getFile returns correct file name
 * @test Test that setFile properly sets the file name
 */
String getFile();

/**
 * Sets the file containing the reference.
 * 
 * @param file The file name to set
 * @test Test that setFile properly sets the file name
 * @test Test that setting file preserves location integrity
 */
void setFile(String file);

/**
 * Gets the line number of the reference.
 * 
 * @return The line number
 * @test Test that getLineNumber returns correct line number
 * @test Test that setLineNumber properly sets the line number
 */
int getLineNumber();

/**
 * Sets the line number of the reference.
 * 
 * @param lineNumber The line number to set
 * @test Test that setLineNumber properly sets the line number
 * @test Test that setting line number preserves location integrity
 */
void setLineNumber(int lineNumber);

/**
 * Gets the column number of the reference.
 * 
 * @return The column number
 * @test Test that getColumnNumber returns correct column number
 * @test Test that setColumnNumber properly sets the column number
 */
int getColumnNumber();

/**
 * Sets the column number of the reference.
 * 
 * @param columnNumber The column number to set
 * @test Test that setColumnNumber properly sets the column number
 * @test Test that setting column number preserves location integrity
 */
void setColumnNumber(int columnNumber);


}       // end of interface FoamElementLocation




/* end of FoamElementLocation.java */

