/********************************************************************************/
/*                                                                              */
/*              FoamToolAgent.java                                              */
/*                                                                              */
/*      Handle tool agent calls                                                 */
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
 * Interface for tool agents that provide access to FOAM system resources.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.O.1 - System shall provide suite of tool agents to access various files and portions of files
 * @see II.O.2 - Tool agents shall return contents of ELEMENTs
 * @see II.O.3 - Tool agents shall return link information for specified ELEMENTs
 * @see II.O.4 - Tool agents shall provide access to auxiliary files (sample 
 *      resources, test cases, library descriptions, UI descriptions)
 */
public interface FoamToolAgentManager extends FoamConstants
{


/**
 * Returns contents of an element.
 * 
 * @param project The project containing the element
 * @param file The file containing the element
 * @param elementName The name of the element
 * @return The content of the element
 * @throws FoamException if element content cannot be retrieved
 * @see II.O.2 - Tool agents shall return contents of ELEMENTs
 * @test Test that valid element content is returned correctly
 * @test Test that invalid element names throw appropriate exceptions
 * @test Test that content format is preserved properly
 */
String getElementContent(String project, String file, String elementName) 
   throws FoamException;

/**
 * Returns link information for an element.
 * 
 * @param project The project containing the element
 * @param file The file containing the element
 * @param elementName The name of the element
 * @return The link information for the element
 * @throws FoamException if link information cannot be retrieved
 * @see II.O.3 - Tool agents shall return link information for specified ELEMENTs
 * @test Test that link information is returned correctly
 * @test Test that empty links return appropriate empty structure
 * @test Test that invalid element names throw appropriate exceptions
 */
String getElementLinks(String project, String file, String elementName) 
   throws FoamException;

/**
 * Returns contents of an auxiliary file.
 * 
 * @param project The project containing the file
 * @param fileName The name of the auxiliary file
 * @return The content of the auxiliary file
 * @throws FoamException if auxiliary file cannot be retrieved
 * @see II.O.4 - Tool agents shall provide access to auxiliary files (sample resources, 
 *      test cases, library descriptions, UI descriptions)
 * @test Test that valid auxiliary file content is returned correctly
 * @test Test that invalid file names throw appropriate exceptions
 * @test Test that different auxiliary file types are handled properly
 */
String getAuxiliaryFileContent(String project, String fileName) throws FoamException;

/**
 * Returns JSON string for passing back to LLM.
 * 
 * @param project The project containing the element
 * @param file The file containing the element
 * @param elementName The name of the element
 * @return A JSON string representation of the element information
 * @throws FoamException if JSON generation fails
 * @see II.O.6 - AGENTS command shall return JSON string for passing back to LLM
 * @test Test that valid JSON is generated correctly
 * @test Test that JSON format is consistent and parseable
 * @test Test that invalid parameters throw appropriate exceptions
 */
String getAgentData(String project, String file, String elementName) 
   throws FoamException;

/**
 * Invokes an agent command with specified arguments.
 * 
 * @param project The project containing the element
 * @param fileName The name of the file to process
 * @param agentCommand The agent command to execute
 * @return The result of the agent command execution
 * @throws FoamException if agent command execution fails
 * @see II.O.5 - Agents shall be invoked indirectly by LLM manager using AGENT command
 * @test Test that valid agent commands execute successfully
 * @test Test that command results are returned correctly
 * @test Test that invalid commands throw appropriate exceptions
 */
String invokeAgent(String project, String fileName, String agentCommand) 
   throws FoamException;


}       // end of interface FoamToolAgent




/* end of FoamToolAgent.java */

