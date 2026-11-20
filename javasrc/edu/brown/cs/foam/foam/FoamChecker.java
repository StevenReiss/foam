/********************************************************************************/
/*                                                                              */
/*              FoamChecker.java                                                */
/*                                                                              */
/*      Check completeness and consistency of specification                     */
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
 * Interface for checking completeness and consistency of FOAM specifications.
 * 
 * @author FOAM System Design Team
 * @version 1.0
 * @see II.Q.1 - System shall check completeness of LLM query results
 * @see II.Q.2 - System shall support extensible completeness checking based on prompts
 * @see II.Q.3 - System shall check consistency of specifications
 * @see II.Q.4 - System shall perform completeness and consistency checks simultaneously as single operation
 * @see II.Q.5 - System shall return list of change requests to be passed to LLM if issues found
 */
public interface FoamChecker extends FoamConstants
{


/**
 * Checks completeness and consistency of a specification.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to check
 * @return A list of change requests if issues are found
 * @throws FoamException if checking fails
 * @see II.Q.4 - System shall perform completeness and consistency checks simultaneously as single operation
 * @test Test that both completeness and consistency checks work together correctly
 * @test Test that valid specifications return empty change request list
 * @test Test that invalid specifications return appropriate change requests
 */
List<FoamChangeRequest> checkCompletenessAndConsistency(String projectDirectory, 
      String fileName) 
   throws FoamException;

/**
 * Checks if specification has enough information for code generation.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to check
 * @return True if specification has sufficient information, false otherwise
 * @throws FoamException if checking fails
 * @see II.Q.6.a - System shall support asking LLM to check if specification has enough information for code generation
 * @test Test that valid specifications return true for code generation readiness
 * @test Test that invalid specifications return false for code generation readiness
 * @test Test that checking handles different document types appropriately
 */
boolean isSufficientForCodeGeneration(String projectDirectory, String fileName) 
   throws FoamException;

/**
 * Checks if specification has enough information for test generation.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to check
 * @return True if specification has sufficient information, false otherwise
 * @throws FoamException if checking fails
 * @see II.Q.6.b - System shall support asking LLM to check if specification has 
 *      enough information for unit and system test generation
 * @test Test that valid specifications return true for test generation readiness
 * @test Test that invalid specifications return false for test generation readiness
 * @test Test that checking handles different document types appropriately
 */
boolean isSufficientForTestGeneration(String projectDirectory, String fileName) 
   throws FoamException;

/**
 * Checks if specification has enough information for verification condition generation.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to check
 * @return True if specification has sufficient information, false otherwise
 * @throws FoamException if checking fails
 * @see II.Q.6.c - System shall support asking LLM to check if specification has enough 
 *      information for verification condition generation
 * @test Test that valid specifications return true for verification generation readiness
 * @test Test that invalid specifications return false for verification generation readiness
 * @test Test that checking handles different document types appropriately
 */
boolean isSufficientForVerificationGeneration(String projectDirectory, String fileName)
   throws FoamException;

/**
 * Performs extensible completeness checking based on prompts.
 * 
 * @param projectDirectory The absolute path to the project directory
 * @param fileName The name of the file to check
 * @param prompt The prompt for checking
 * @return True if specification passes the check, false otherwise
 * @throws FoamException if checking fails
 * @see II.Q.2 - System shall support extensible completeness checking based on prompts
 * @test Test that different prompts work correctly with the checking system
 * @test Test that extensible checks return appropriate boolean results
 * @test Test that invalid prompts are handled gracefully
 */
boolean performExtensibleCheck(String projectDirectory, String fileName, String prompt) 
   throws FoamException;


}       // end of interface FoamChecker




/* end of FoamChecker.java */

