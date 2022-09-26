package com.ast.testPro;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
 
public class DemoVisitor extends ASTVisitor {
 
	@Override
	public boolean visit(FieldDeclaration node) {
		for (Object obj: node.fragments()) {
			VariableDeclarationFragment v = (VariableDeclarationFragment)obj;
			System.out.println("Field:\t" + v.getName());
		}
		
		return true;
	}
 
	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println("Method:\t" + node.getName());
		return true;
	}
 
	@Override
	public boolean visit(TypeDeclaration node) {
		System.out.println("Class:\t" + node.getName());
		return true;
	}
}