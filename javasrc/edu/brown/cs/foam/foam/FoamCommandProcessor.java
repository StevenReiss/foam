/********************************************************************************/
/*                                                                              */
/*              FoamCommandProcessor.java                                       */
/*                                                                              */
/*      Handle commands and command processing                                  */
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
 * Interface for processing commands in the FOAM system.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.N.1 - System shall provide command-line tool for creating new projects
 * @see II.N.2 - System shall support PROJECT command for setting project workspace
 * @see II.N.3 - System shall support CREATE command for creating new (empty) workspace
 * @see II.N.4 - System shall support CLEAR command for clearing LLM history
 */
public interface FoamCommandProcessor extends FoamConstants
{


/**
 * Processes a command with specified arguments.
 * 
 * @param command The command to process
 * @param arguments The arguments for the command
 * @return The result of command processing
 * @throws FoamException if command processing fails
 * @see II.N.1 - System shall provide command-line tool for creating new projects
 * @see II.N.2 - System shall support PROJECT command for setting project workspace
 * @see II.N.3 - System shall support CREATE command for creating new (empty) workspace
 * @see II.N.4 - System shall support CLEAR command for clearing LLM history
 * @test Test that valid commands execute successfully and return expected results
 * @test Test that invalid commands throw appropriate exceptions
 * @test Test that command arguments are properly parsed and validated
 */
String processCommand(String command, List<String> arguments) 
   throws FoamException;

/**
 * Clears the LLM history.
 * 
 * @throws FoamException if clearing history fails
 * @see II.N.4 - System shall support CLEAR command for clearing LLM history
 * @test Test that LLM history is properly cleared without affecting other system state
 * @test Test that clearing works when history exists and when it doesn't
 */
void clearLLMHistory() throws FoamException;

/**
 * Processes an UPDATE command with specified arguments.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to update
 * @param conversionType The type of conversion to perform
 * @return The modified file content or filename as response
 * @throws FoamException if update command processing fails
 * @see II.G.1 - System shall support primary UPDATE command for converting between representations
 * @test Test that valid conversions work correctly for all supported types
 * @test Test that existing documents are preserved during updates where appropriate
 * @test Test that conversion maintains document integrity and structure
 */
String processUpdateCommand(String projectDirectory, String fileName, 
      FoamDocumentType conversionType) 
   throws FoamException;

/**
 * Processes an EDIT command with specified arguments.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to edit
 * @param changeDescription The textual description of changes
 * @return The modified file content or filename as response
 * @throws FoamException if edit command processing fails
 * @see II.H.1 - System shall support EDIT command for mapping elements to new 
 *      versions based on textual descriptions of changes
 * @test Test that valid change descriptions are processed correctly
 * @test Test that editing preserves existing content while applying changes
 * @test Test that invalid change descriptions throw appropriate exceptions
 */
String processEditCommand(String projectDirectory, String fileName, 
      String changeDescription) 
   throws FoamException;




}       // end of interface FoamCommandProcessor




/* end of FoamCommandProcessor.java */

