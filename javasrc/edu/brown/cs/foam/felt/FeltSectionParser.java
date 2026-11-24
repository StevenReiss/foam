/********************************************************************************/
/*                                                                              */
/*              FeltSectionParser.java                                          */
/*                                                                              */
/*      Parse a markdown document into sections                                 */
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



package edu.brown.cs.foam.felt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.brown.cs.ivy.file.IvyLog;

class FeltSectionParser implements FeltConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private Path            for_file;

private static final Pattern HEADING_PATTERN =
   Pattern.compile("^(#{1,6})\\s+(.*)$");




/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

FeltSectionParser(File f)
{
   for_file = f.toPath();
}



/********************************************************************************/
/*                                                                              */
/*      Parsing method                                                          */
/*                                                                              */
/********************************************************************************/

FeltElement parse()
{
   List<String> lines;
   try {
      lines = Files.readAllLines(for_file);
    }
   catch (IOException e) {
      IvyLog.logE("FELT","Problem reading outline file ",e);
      return null;
    } 
  
   Deque<FeltElement> stack = new ArrayDeque<>();
   Map<FeltElement,Integer> levels = new HashMap<>();
   FeltElement root = new FeltElement("ROOT",
         FoamElementType.DOCUMENT,null);
   levels.put(root,0);
   stack.push(root);
   boolean incode = false;
   FeltElement current = null;
   
   int lno = 0;
   for (String line : lines) {
      ++lno;
      if (line.trim().startsWith("```") || line.trim().startsWith("~~~")) {
         incode = !incode;
       }
      if (!incode) {
         Matcher matcher = HEADING_PATTERN.matcher(line);
         if (matcher.matches()) {
            int level = matcher.group(1).length();
            String title = matcher.group(2).trim();
            // Pop until we find a parent with a strictly lower heading level
            while (!stack.isEmpty() && levels.get(stack.peek()) >= level) {
               stack.pop();
             }
            if (current != null && current != root) {
               current.setEndLine(lno);
             }
            FeltElement newnode = new FeltElement(title,
                  FoamElementType.SECTION,stack.peek());
            levels.put(newnode,level);    
            newnode.setStartLine(lno);
            current = newnode;
            stack.push(newnode);
            continue;
          }
       }
      stack.peek().append(line);
    }
   
   return root;
}



}       // end of class FeltSectionParser




/* end of FeltSectionParser.java */

