/********************************************************************************/
/*                                                                              */
/*              FoamLimbaManager.java                                           */
/*                                                                              */
/*      Handle foam interactions with LLMs through Limba                        */
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
 * Interface for integrating with LLMs for code generation and analysis.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.E.1 - System shall generate queries to LLM and parse/interpret results
 * @see II.E.2 - System shall identify ELEMENTs of components
 */
public interface FoamLLMManager extends FoamConstants
{


/**
 * Sends a query to an LLM and returns the parsed result.
 * 
 * @param query The query string to send to the LLM
 * @return The parsed response from the LLM
 * @throws FoamException if LLM communication fails or parsing fails
 * @see II.E.1 - System shall generate queries to LLM and parse/interpret results
 * @test Test that valid query returns proper response format
 * @test Test that invalid query throws appropriate exception
 * @test Test that response parsing handles various formats correctly
 */
String sendQuery(String query) throws FoamException;

/**
 * Clear LLM memory
 **/
void clearHistory() throws FoamException;


/**
 * Set LLM model to use
 **/
void setModel(String mdl) throws FoamException;

}       // end of interface FoamLimbaManager




/* end of FoamLimbaManager.java */

