/********************************************************************************/
/*                                                                              */
/*              FoamElement.java                                                */
/*                                                                              */
/*      An element with a specification type                                    */
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

import edu.brown.cs.ivy.xml.IvyXmlWriter;


/**
 * Interface representing an element in a FOAM document.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.E.2 - System shall identify ELEMENTs of components
 */
public interface FoamElement extends FoamConstants
{


/**
 * Gets the name of the element.
 * 
 * @return The name of the element
 * @test Test that getName returns correct element name
 * @test Test that setName properly sets the element name
 */
String getName();

/**
 * Sets the name of the element.
 * 
 * @param name The name to set
 * @test Test that setName properly sets the element name
 * @test Test that setting name preserves element integrity
 */
void setName(String name);

/**
 * Gets the type of the element.
 * 
 * @return The type of the element
 * @test Test that getType returns correct element type
 * @test Test that setType properly sets the element type
 */
FoamElementType getType();

/**
 * Sets the type of the element.
 * 
 * @param type The type to set
 * @test Test that setType properly sets the element type
 * @test Test that setting type preserves element integrity
 */
void setType(FoamElementType type);

/**
 * Gets the content of the element.
 * 
 * @return The content of the element
 * @test Test that getContent returns correct element content
 * @test Test that setContent properly sets the element content
 */
String getContent();

/**
 * Sets the content of the element.
 * 
 * @param content The content to set
 * @test Test that setContent properly sets the element content
 * @test Test that setting content preserves element integrity
 */
void setContent(String content);

/**
 * Gets the parent element, if any.
 * 
 * @return The parent element or null if none
 * @test Test that getParent returns correct parent element
 * @test Test that setParent properly sets the parent element
 */
FoamElement getParent();

/**
 * Sets the parent element.
 * 
 * @param parent The parent element to set
 * @test Test that setParent properly sets the parent element
 * @test Test that setting parent preserves element integrity
 */
void setParent(FoamElement parent);

/**
 * Gets the child elements.
 * 
 * @return The list of child elements
 * @test Test that getChildren returns correct list of child elements
 * @test Test that setChildren properly sets the child elements
 */
List<FoamElement> getChildren();

/**
 * Sets the child elements.
 * 
 * @param children The list of child elements to set
 * @test Test that setChildren properly sets the child elements
 * @test Test that setting children preserves element integrity
 */
void setChildren(List<FoamElement> children);


/**
 * Returns the full path name of this element
 */
String getFullName();


/**
 * Output the element in XML
 **/
void outputXml(IvyXmlWriter xw);


}       // end of interface FoamElement




/* end of FoamElement.java */

