/********************************************************************************/
/*                                                                              */
/*              FoamProjectManager.java                                         */
/*                                                                              */
/*      Project Manager interface                                               */
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

public interface FoamConstants {

enum FoamDocumentType {
   DESCRIPTION,
   REQUIREMENTS,
   FEATURES,
   SCENARIO,
   UI_IMAGE,
   UI_DESIGN,
   TEST_CASE,
   DESIGN,
   RESOURCE,
   OVERVIEW,
   ARCHITECTURE,
   LIBRARY,
}


enum FoamElementType {
   DOCUMENT,
   DESIGN_ELEMENT,
   OUTLINE_SECTION,
   SECTION,
}


enum FoamCommandType {
   UPDATE,
   EDITED,
   HIERARCHY,
   STATUS,
   QUERY,
   EDIT,
   SETVERSION,
   COMMIT,
   AGENT,
   FIND,
   DIFFS,
   SYNC,
   PING,
   EXIT,
   PROJECT,
   CREATE,
   CLEAR,
   SHELL,
   VERSIONS,
   SETMODEL,
}

enum FoamLinkType {
   IMPLICIT,
   EXPLICIT,
   CROSS_DOCUMENT,
}


enum FoamDocumentStatus {
   UNKNOWN,
   EDITED,
   UPDATED,
   OUT_OF_DATE
}


String [] FOAM_PROJECT_DIRS = {
      "metadata",
      "resources",
      "specification",
      "design",
      "design",
      "tests",
      "scenarios",
      "userinterfaces",
      "lib",
};


}
