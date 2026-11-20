/********************************************************************************/
/*                                                                              */
/*              FoamChangeRequest.java                                          */
/*                                                                              */
/*      Change needed for consistency or completeness                           */
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
 * Interface representing a change request for specification completeness checking.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.Q.5 - System shall return list of change requests to be passed to LLM if issues found
 */
public interface FoamChangeRequest extends FoamConstants
{

/**
 * Gets the description of the change request.
 * 
 * @return The description
 * @test Test that getDescription returns correct description
 * @test Test that setDescription properly sets the description
 */
String getDescription();

/**
 * Sets the description of the change request.
 * 
 * @param description The description to set
 * @test Test that setDescription properly sets the description
 * @test Test that setting description preserves request integrity
 */
void setDescription(String description);

/**
 * Gets the element that needs to be changed.
 * 
 * @return The element
 * @test Test that getElement returns correct element
 * @test Test that setElement properly sets the element
 */
FoamElement getElement();

/**
 * Sets the element that needs to be changed.
 * 
 * @param element The element to set
 * @test Test that setElement properly sets the element
 * @test Test that setting element preserves request integrity
 */
void setElement(FoamElement element);


}       // end of interface FoamChangeRequest




/* end of FoamChangeRequest.java */

