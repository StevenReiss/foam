/********************************************************************************/
/*                                                                              */
/*              FeltOutlineParser.java                                          */
/*                                                                              */
/*      Parse outline mark-down documents                                       */
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

class FeltOutlineParser implements FeltConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private Path            for_file;

private static final Pattern OUTLINE_ITEM_PATTERN = Pattern.compile(
      "^\\s*(?:[-*+]\\s+)?([A-Za-z0-9]+(?:\\.[A-Za-z0-9]+)*)[.)]\\s+(.*)$"
);



/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

FeltOutlineParser(File f) 
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
   
   String enm = for_file.getFileName().toString();
   int idx = enm.indexOf(".");
   if (idx > 0) enm = enm.substring(0,idx);   
   FeltElement root = new FeltElement(enm,
         FoamElementType.DOCUMENT,null);
   root.setFile(for_file.toFile());
   levels.put(root,0);
   stack.push(root);
   FeltElement current = null;
  
   int lno = 0;
   for (String line : lines) {
      ++lno;
      if (line.isBlank()) continue;
      Matcher matcher = OUTLINE_ITEM_PATTERN.matcher(line);
      if (matcher.matches()) {
         String marker = matcher.group(1);
         String text = matcher.group(2).trim();
         int level = marker.split("\\.").length;
         while (!stack.isEmpty() && levels.get(stack.peek()) >= level) {
            stack.pop();
          }
         if (current != null && current != root) {
            current.setEndLine(lno);
          }
         FeltElement par = stack.peek();
         FeltElement elt = new FeltElement(marker,
               FoamElementType.OUTLINE_SECTION,par);
         elt.setContent(text.trim());
         elt.setStartLine(lno);
         levels.put(elt,level);
         stack.push(elt);
         current = elt;
       }
      else if (current != null) {
         current.appendLine(line);
       }
    }
   
   return root;
}


}       // end of class FeltOutlineParser




/* end of FeltOutlineParser.java */

