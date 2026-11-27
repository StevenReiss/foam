/********************************************************************************/
/*                                                                              */
/*              FeltJavaParser.java                                             */
/*                                                                              */
/*      Parse Java file into a hierarchy of elements                            */
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
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.brown.cs.ivy.file.IvyFile;
import edu.brown.cs.ivy.file.IvyLog;
import edu.brown.cs.ivy.jcomp.JcompAst;

class FeltJavaParser implements FeltConstants
{


/********************************************************************************/
/*                                                                              */
/*      Private Storage                                                         */
/*                                                                              */
/********************************************************************************/

private File    for_file;



/********************************************************************************/
/*                                                                              */
/*      Constructors                                                            */
/*                                                                              */
/********************************************************************************/

FeltJavaParser(File f)
{
   for_file = f;
}



/********************************************************************************/
/*                                                                              */
/*      Parsing method                                                          */
/*                                                                              */
/********************************************************************************/

FeltElement parse(FeltElement root)
{
   String cnts;
   try {
      cnts = IvyFile.loadFile(for_file);
    }
   catch (IOException e) {
      IvyLog.logE("FELT","Problem reading Java file",e);
      return null;
    }
   
   String enm = for_file.getName();
   int idx = enm.indexOf(".");
   if (idx > 0) enm = enm.substring(0,idx);
   
   ASTNode ast = JcompAst.parseSourceFile(cnts);
   FeltElement top = new FeltElement(enm,
         FoamElementType.DESIGN_ELEMENT,root); 
   ElementBuilder bldr = new ElementBuilder(top);
   ast.accept(bldr);
   
   return root;
}



/********************************************************************************/
/*                                                                              */
/*      Tree walker to build hierarchy                                          */
/*                                                                              */
/********************************************************************************/

private class ElementBuilder extends ASTVisitor {
   
   private Stack<FeltElement> element_stack;
   private CompilationUnit comp_unit;   
   
   ElementBuilder(FeltElement root) {
      element_stack = new Stack<>();
      element_stack.push(root);
    }
   
   @Override public void preVisit(ASTNode n) {
      if (n instanceof CompilationUnit) {
         comp_unit = (CompilationUnit) n;
       }
      else if (n instanceof BodyDeclaration) {
         BodyDeclaration bd = (BodyDeclaration) n;
         String nm = getElementName(bd);
         if (nm != null) {
            FeltElement felt = new FeltElement(getElementName(bd),
                  FoamElementType.DESIGN_ELEMENT,element_stack.peek());
            element_stack.push(felt);
            int spos = bd.getStartPosition();
            int epos = bd.getStartPosition() + bd.getLength();
            felt.setStartLine(comp_unit.getLineNumber(spos));
            felt.setEndLine(comp_unit.getLineNumber(epos));  // +1 ???
          }
         else {
            element_stack.push(element_stack.peek());
          }
       }
    }
   
   @Override public void postVisit(ASTNode n) {
      if (n instanceof BodyDeclaration) {
         element_stack.pop();
       }
    }
   
   private String getElementName(BodyDeclaration bd) {
      if (bd instanceof AbstractTypeDeclaration) {
         AbstractTypeDeclaration atd = (AbstractTypeDeclaration) bd;
         return atd.getName().getIdentifier();
       }
      else if (bd instanceof EnumConstantDeclaration) {
         EnumConstantDeclaration ecd = (EnumConstantDeclaration) bd;
         return ecd.getName().getIdentifier();
       }
      else if (bd instanceof FieldDeclaration) {
         FieldDeclaration fd = (FieldDeclaration) bd;
         for (Object o : fd.fragments()) {
            VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
            return vdf.getName().getIdentifier();
          }
       }
      else if (bd instanceof MethodDeclaration) {
         MethodDeclaration md = (MethodDeclaration) bd;
         return md.getName().getIdentifier();
       }
      return null;
    }
   
}       // end of inner class ElementBuilder

}       // end of class FeltEclipseParser




/* end of FeltEclipseParser.java */

