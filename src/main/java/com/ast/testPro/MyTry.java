package com.ast.testPro;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MyTry {

	static String testPath = "F:/Code/Java/eclipse/testPro/src/main/java/com/ast/testPro/printTest.java";

	static String readFile(String filePath) {
		/*
		 * 读取.java文件转换为字符串
		 */
		byte[] input = null;
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
			input = new byte[bufferedInputStream.available()];
			bufferedInputStream.read(input);
			bufferedInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(input);
	}

	static CompilationUnit createCompilationUnit(String file) {
		/*
		 * 读入传入的字符串，创建AST，并返回结点
		 */
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setSource(file.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);

//	    parser.setEnvironment(new String[] {dir}, null, null, true);
		parser.setUnitName("any_name");
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		return cu;
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

	
	
	public static void main(String[] args) {
		
//		System.out.println(readFile(testPath)); 
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setSource(readFile(testPath).toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

//		parser.setResolveBindings(true);
//		parser.setUnitName("dontCare");
//		parser.setEnvironment(null, null, null, true);
//		parser.setEnvironment(new String[] {  testPath },
//	            new String[] { "" }, new String[] { "UTF-8" }, true);

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
//		
//		System.out.println("-----------begin annotation test-----------");
//		TypeDeclaration type = (TypeDeclaration) cu.types().get(0);
//		MethodDeclaration[] methods = type.getMethods();
//		for (int i = 0; i < methods.length; i++) {
//			MethodDeclaration method = methods[i];
//			Javadoc doc = method.getJavadoc();
//			if (doc != null) {
//				System.out.println(doc);
//			}
//		}
//		System.out.println("-----------end  annotation  test-----------");
		
		
		System.out.println("==========Testing Begin==========");
		print(cu);
//		cu.accept(new MyVisitor());
		System.out.println("==========Testing   End==========");
	}
}
