package com.ast.testPro;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class CreateHelloWorld {
	public CompilationUnit CreateHelloWorld(){
		ASTParser parser = ASTParser.newParser(AST.JLS16);
    	parser.setSource("".toCharArray());
    	
    	CompilationUnit comp = (CompilationUnit) parser.createAST(null);
    	comp.recordModifications();
    	
    	AST ast = comp.getAST();
    	
    	TypeDeclaration classDec = ast.newTypeDeclaration();
    	classDec.setInterface(false);
    	
    	SimpleName className = ast.newSimpleName("HelloWorld");
    	Modifier classModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
    	classDec.setName(className);
    	classDec.modifiers().add(classModifier);
    	
    	comp.types().add(classDec);
    	
    	
    	MethodDeclaration methodDec = ast.newMethodDeclaration();
    	methodDec.setConstructor(true);
    	SimpleName methodName = ast.newSimpleName("HelloWorld");
    	Modifier methodModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
    	Block methodBody = ast.newBlock();
    	methodDec.setName(methodName);
    	methodDec.modifiers().add(methodModifier);
    	methodDec.setBody(methodBody);
    	
    	classDec.bodyDeclarations().add(methodDec);
    	
    	MethodInvocation methodInv = ast.newMethodInvocation();
    	SimpleName nameSystem = ast.newSimpleName("System");
    	SimpleName nameOut = ast.newSimpleName("out");
    	SimpleName namePrintln = ast.newSimpleName("println");
    	
    	QualifiedName nameSystemOut = ast.newQualifiedName(nameSystem, nameOut);
    	
    	methodInv.setExpression(nameSystemOut);
    	methodInv.setName(namePrintln);
    	
    	StringLiteral sHelloWorld = ast.newStringLiteral();
    	sHelloWorld.setEscapedValue("\"Hello World\"");
    	
    	methodInv.arguments().add(sHelloWorld);
    	
    	ExpressionStatement es = ast.newExpressionStatement(methodInv);
    	
    	methodBody.statements().add(es);
    	System.out.println("Hello World");
    	System.out.println(comp.toString());
    	return comp;
	}
	
	public static void main(String[] args) throws Exception {
		CreateHelloWorld test = new CreateHelloWorld();
		System.out.println("Hello World");
	}
}
