/********************************************************************************/
/*                                                                              */
/*              FoamException.java                                              */
/*                                                                              */
/*      Exceptions for errors in FOAM processing                                */
/*                                                                              */
/********************************************************************************/
/*      Copyright 2025 Brown University -- Steven P. Reiss                    */
/*********************************************************************************
 *  Copyright 2025, Brown University, Providence, RI.                            *
 *                                                                               *
 *                        All Rights Reserved                                    *
 *                                                                               *
 * This program and the accompanying materials are made available under the      *
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, *
 * and is available at                                                           *
 *      http://www.eclipse.org/legal/epl-v10.html                                *
 *                                                                               *
 ********************************************************************************/



package edu.brown.cs.foam.foam;


/**
 * Main exception type for FOAM system.
 * 
 * @author FOAM System
 * @version 1.0
 */
public class FoamException extends Exception
{


/********************************************************************************/
/*                                                                              */
/*      Private stoarge                                                         */
/*                                                                              */
/********************************************************************************/

private static final long serialVersionUID = 1;


/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

/**
 * Constructs a new FOAMException with the specified detail message.
 * 
 * @param message The detail message
 */
public FoamException(String message) {
   super(message);
}

/**
 * Constructs a new FOAMException with the specified detail message and cause.
 * 
 * @param message The detail message
 * @param cause The cause of the exception
 */
public FoamException(String message, Throwable cause) {
   super(message, cause);
}



}       // end of class FoamException




/* end of FoamException.java */

