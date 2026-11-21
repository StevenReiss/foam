/********************************************************************************/
/*                                                                              */
/*              FideConstants.java                                              */
/*                                                                              */
/*      Constants for FOAM IDE interface                                        */
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



package edu.brown.cs.foam.fide;

import edu.brown.cs.foam.foam.FoamConstants;
import edu.brown.cs.ivy.xml.IvyXmlWriter;

interface FideConstants extends FoamConstants
{

interface FoamCommand { 
   boolean isImmediate();
   String getCommandName();
   void process(IvyXmlWriter xw);
}


String [] PROJECT_DIRS = {
      "metadata",
      "resources",
      "specification",
      "design",
      "design/main",
      "design/test",
      "scenarios",
      "lib",
};

String [] GIT_IGNORES = {
      "*.class",
      "bBUFFERS",
      "bBACKUP",
      "*.tmp",
      "xxx*",
      ".snapshot",
      ".snapshots",
      "*.log",
};



String PROPERTY_FILE = "Foam.props";
String GIT_ROOT = "Foam.git";
String GIT_IGNORE = "Foam.ignore";

}       // end of interface FideConstants




/* end of FideConstants.java */

