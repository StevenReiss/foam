/********************************************************************************/
/*                                                                              */
/*              FeltElement.java                                                */
/*                                                                              */
/*      Implmentation of a FoamElement                                          */
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.brown.cs.foam.foam.FoamElement;

class FeltElement implements FoamElement, FeltConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private String          element_name;
private FoamElementType element_type;
private String          element_content;
private FeltElement     element_parent;
private List<FeltElement> element_children;
@SuppressWarnings("unused")
private int             start_line;
@SuppressWarnings("unused")
private int             end_line;




/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

FeltElement(String name,FoamElementType type,FeltElement parent)
{
   element_name = name;
   element_type = type;
   element_content = "";
   element_parent = parent;
   element_children = null;
   start_line = 0;
   end_line = 0;
   
   if (element_parent != null) {
      element_parent.addChild(this);
    }
}



/********************************************************************************/
/*                                                                              */
/*      Access methods                                                          */
/*                                                                              */
/********************************************************************************/

@Override public String getName()               { return element_name; }
@Override public void setName(String name)
{
   element_name = name;
}



@Override public FoamElementType getType()      { return element_type; }
@Override public void setType(FoamElementType typ)
{
   element_type = typ;
}



@Override public String getContent()            { return element_content; }
@Override public void setContent(String cnt)
{
   element_content = cnt;
   // might want to mark doc as changed and needs to be written
}

void appendLine(String cnt)
{
   if (element_content == null) element_content = "";
   else if (!element_content.isBlank()) element_content += " ";
   element_content += cnt.trim();
}


void append(String cnt)
{
   if (element_content == null) element_content = cnt;
   else element_content += cnt + "\n";
}


@Override public FoamElement getParent()        { return element_parent; }
@Override public void setParent(FoamElement par)
{
   element_parent = (FeltElement) par;
}



@Override public List<FoamElement> getChildren()
{
   if (element_children == null) {
      return Collections.emptyList();
    }
   return new ArrayList<>(element_children);
}
@Override public void setChildren(List<FoamElement> chld)
{
   element_children = new ArrayList<>();
   for (FoamElement fe : chld) {
      element_children.add((FeltElement) fe);
    }
}
void addChild(FeltElement fe) {
   if (element_children == null) {
      element_children = new ArrayList<>();
    }
   element_children.add(fe);
}


void setStartLine(int start)
{
   start_line = start;
   end_line = start+1;
}

void setEndLine(int end)
{
   end_line = end;
}



}       // end of class FeltElement




/* end of FeltElement.java */

