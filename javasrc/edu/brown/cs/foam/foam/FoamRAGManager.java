/********************************************************************************/
/*                                                                              */
/*              FoamRAGManager.java                                             */
/*                                                                              */
/*      Maintain the RAG for the LLM                                            */
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
 * Interface for the Retrieval-Augmented Generation system.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.P.1 - System shall provide Retrieval-Augmented Generation (RAG) for all 
 *      project files and relevant external libraries/systems
 * @see II.P.2 - System shall keep RAG up-to-date as files change
 * @see II.P.3 - System shall maintain RAG in form available to LLM manager
 */
public interface FoamRAGManager extends FoamConstants
{

/**
 * Updates the RAG index with changes to project files.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @throws FoamException if RAG update fails
 * @see II.P.2 - System shall keep RAG up-to-date as files change
 * @test Test that RAG index is updated correctly when project files change
 * @test Test that updates handle multiple file changes properly
 * @test Test that update preserves existing index integrity
 */
void updateRAG(String projectDirectory) throws FoamException;

/**
 * Gets relevant information for an LLM query.
 * 
 * @param query The query string to search for
 * @return Relevant information for the query
 * @throws FoamException if information retrieval fails
 * @see II.P.1 - System shall provide Retrieval-Augmented Generation (RAG) for all project 
 *      files and relevant external libraries/systems
 * @test Test that relevant information is retrieved correctly for valid queries
 * @test Test that query results are properly formatted and structured
 * @test Test that invalid queries return appropriate empty or error responses
 */
String getRelevantInformation(String query) throws FoamException;

/**
 * Gets the RAG index in a format suitable for LLM manager.
 * 
 * @return The RAG index as a string
 * @throws FoamException if RAG index cannot be retrieved
 * @see II.P.3 - System shall maintain RAG in form available to LLM manager
 * @test Test that RAG index is returned in correct format for LLM consumption
 * @test Test that index contains all necessary project information
 * @test Test that index format is consistent and parseable
 */
String getRAGIndex() throws FoamException;

/**
 * Updates the RAG index with changes to a specific file.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file that changed
 * @throws FoamException if RAG update fails for this file
 * @see II.P.2 - System shall keep RAG up-to-date as files change
 * @test Test that specific file updates work correctly
 * @test Test that partial updates don't affect other index entries
 * @test Test that file changes are properly indexed and searchable
 */
void updateRAGForFile(String projectDirectory, String fileName) 
   throws FoamException;

}       // end of interface FoamRAGManager




/* end of FoamRAGManager.java */

