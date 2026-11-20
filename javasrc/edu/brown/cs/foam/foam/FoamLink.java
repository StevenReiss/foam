/********************************************************************************/
/*                                                                              */
/*              FoamLink.java                                                   */
/*                                                                              */
/*      Representation of a link                                                */
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
 * Interface representing a link between elements in FOAM documents.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.L.1 - System shall support linking elements between description files
 */
public interface FoamLink extends FoamConstants
{


/**
 * Gets the source element.
 * 
 * @return The source element
 * @test Test that getSource returns correct source element
 * @test Test that setSource properly sets the source element
 */
FoamElement getSource();

/**
 * Sets the source element.
 * 
 * @param source The source element to set
 * @test Test that setSource properly sets the source element
 * @test Test that setting source preserves link integrity
 */
void setSource(FoamElement source);

/**
 * Gets the target element.
 * 
 * @return The target element
 * @test Test that getTarget returns correct target element
 * @test Test that setTarget properly sets the target element
 */
FoamElement getTarget();

/**
 * Sets the target element.
 * 
 * @param target The target element to set
 * @test Test that setTarget properly sets the target element
 * @test Test that setting target preserves link integrity
 */
void setTarget(FoamElement target);

/**
 * Gets the link type.
 * 
 * @return The link type
 * @test Test that getLinkType returns correct link type
 * @test Test that setLinkType properly sets the link type
 */
FoamLinkType getLinkType();

/**
 * Sets the link type.
 * 
 * @param linkType The link type to set
 * @test Test that setLinkType properly sets the link type
 * @test Test that setting link type preserves link integrity
 */
void setLinkType(FoamLinkType linkType);


}       // end of interface FoamLink




/* end of FoamLink.java */

