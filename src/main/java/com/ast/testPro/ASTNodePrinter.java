package com.ast.testPro;



import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;

/** 
 * this class implements a simple ASTNode printer
 * the implementation is based on Eclipse JDT AST
 */
public class ASTNodePrinter {
	public static void main(String args[]){
		ASTParser parser = ASTParser.newParser(AST.JLS3);
//		parser.setSource("package sample.jdt.ast;\n import java.util.ArrayList;\n public class A { int i = 9;  \n int j=0 ; \n ArrayList<Integer> al = new ArrayList<Integer>();\n  }".toCharArray());
		//parser.setSource("/*abc*/".toCharArray());
		byte[] input = null;
		try {
		    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("F:/Code/Java/eclipse/testPro/src/main/java/com/ast/testPro/ClassDemo.java"));
		    input = new byte[bufferedInputStream.available()];
            bufferedInputStream.read(input);
            bufferedInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		parser.setSource(new String(input).toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		print(cu);
		System.out.println(cu.toString());
	}


	private static void print(ASTNode node) {
		List properties = node.structuralPropertiesForType();
		for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
			Object desciptor = iterator.next();
			if (desciptor instanceof SimplePropertyDescriptor) {
				SimplePropertyDescriptor simple = (SimplePropertyDescriptor) desciptor;
				Object value = node.getStructuralProperty(simple);
				System.out.println(simple.getId() + " (" + value.toString()
						+ ")");
			} else if (desciptor instanceof ChildPropertyDescriptor) {
				ChildPropertyDescriptor child = (ChildPropertyDescriptor) desciptor;
				ASTNode childNode = (ASTNode) node.getStructuralProperty(child);
				if (childNode != null) {
					System.out.println("Child (" + child.getId() + ") {");
					print(childNode);
					System.out.println("}");
				}
			} else {
				ChildListPropertyDescriptor list = (ChildListPropertyDescriptor) desciptor;
				System.out.println("List (" + list.getId() + "){");
				printNode((List) node.getStructuralProperty(list));
				System.out.println("}");
			}
		}
	}

	private static void printNode(List nodes) {
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
			ASTNode node = (ASTNode) iterator.next();
			print(node);
		}
	}
}
