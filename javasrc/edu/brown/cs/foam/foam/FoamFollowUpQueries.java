/********************************************************************************/
/*                                                                              */
/*              FoamFollowUpQueries.java                                        */
/*                                                                              */
/*      Handle follow up queries                                                */
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
 * Interface for managing follow-up queries to LLMs.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.S.1 - System shall support follow-up questions to LLM
 * @see II.S.2 - System shall support retry operations based on additional user input
 * @see II.S.3 - System shall implement QUERY command for passing queries or requests to LLM
 */
public interface FoamFollowUpQueries extends FoamConstants
{


/**
 * Sends a follow-up query to an LLM.
 * 
 * @param originalQuery The original query that triggered this follow-up
 * @param followUpQuestion The follow-up question to send to the LLM
 * @return The response from the LLM
 * @throws FoamException if LLM communication fails or parsing fails
 * @see II.S.1 - System shall support follow-up questions to LLM
 * @test Test that valid follow-up queries return appropriate responses
 * @test Test that context is properly maintained between original and follow-up queries
 * @test Test that invalid follow-up questions throw appropriate exceptions
 */
String sendFollowUpQuery(String originalQuery, String followUpQuestion) 
   throws FoamException;

/**
 * Retries an operation based on additional user input.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file being processed
 * @param userInput Additional user input for retrying
 * @return The result of the retry operation
 * @throws FoamException if retry fails
 * @see II.S.2 - System shall support retry operations based on additional user input
 * @test Test that retry operations work correctly with user input
 * @test Test that retry preserves previous state where appropriate
 * @test Test that invalid user input is handled gracefully
 */
String retryOperation(String projectDirectory, String fileName, String userInput) 
   throws FoamException;

/**
 * Sends a query to an LLM.
 * 
 * @param query The query string to send to the LLM
 * @return The response from the LLM
 * @throws FoamException if LLM communication fails or parsing fails
 * @see II.S.3 - System shall implement QUERY command for passing queries or requests to LLM
 * @test Test that valid queries return appropriate responses
 * @test Test that query format is handled correctly
 * @test Test that invalid queries throw appropriate exceptions
 */
String sendQuery(String query) throws FoamException;


}       // end of interface FoamFollowUpQueries




/* end of FoamFollowUpQueries.java */

