package com.ast.testPro;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;

public class MyVisitor extends ASTVisitor {

//	@Override
//	public boolean visit(FieldDeclaration node) {
//		for (Object obj: node.fragments()) {
//			VariableDeclarationFragment v = (VariableDeclarationFragment)obj;
//			System.out.println("Field:\t" + v.getName());
//		}
//		
//		return true;
//	}
// 
//	@Override
//	public boolean visit(MethodDeclaration node) {
//		System.out.println("Method:\t" + node.getName());
//		return true;
//	}
// 
//	@Override
//	public boolean visit(TypeDeclaration node) {
//		System.out.println("Class:\t" + node.getName());
//		return true;
//	}
////	static Set names = new HashSet();
//	@Override
//	public boolean visit(MethodInvocation node) {
//		SimpleName MethodName = node.getName();
//		if (!this.names.contains(MethodName.getIdentifier())) {
//			this.names.add(MethodName.getIdentifier());
////			if (MethodName.toString().equals("print")||MethodName.toString().equals("input")) {
//				System.out.println("Method Invocation:\t" + MethodName);   
//				
////				Expression exp = node.getExpression();
////			    ITypeBinding typeBinding = node.getExpression().resolveTypeBinding();
////			    System.out.println("Type: " + typeBinding.toString());
//				
//				
//				Expression expression = node.getExpression();
//				if (expression != null) {
//					System.out.println("Expr: " + expression.toString());
//					ITypeBinding typeBinding1 = expression.resolveTypeBinding();
//
//					if (typeBinding1 != null) {
//						// System.out.println("Call: " + node.getName());
//						// System.out.println("Exprs: " + expression.toString());
//						System.out.println("Types: " + typeBinding1.getQualifiedName());
//						System.out.println("Types: " + typeBinding1.toString());
//					}
//				}
//				else {
//					System.out.println("There is no expression!");
//				}
//				IMethodBinding binding = node.resolveMethodBinding();
//				if (binding != null) {
//					ITypeBinding type = binding.getDeclaringClass();
//					if (type != null) {
//						System.out.println("User-defined class: " + type.getName());
//					}
//					else {
//						System.out.println("Can't get declared class");
//					}
//
//				}
//				else {
//					System.out.println("Can't resolve IMethodBinding");
//				}
////			}
//		}
//		
//
//		return false;
//	}
//	

	@Override
	public boolean visit(MethodDeclaration node) {
//		Block body = node.getBody();
//		if (body != null) {
//			System.out.println("The method is:" + node.getReceiverType());
//			System.out.println("The method is:" + node.getName());
//			List annotations = node.extraDimensions();
//			for (Iterator iterator = annotations.iterator(); iterator.hasNext();) {
//				System.out.println("Extra Dimensions:" + iterator);
//			}
////			System.out.println("The annotation is:" + node.getClass());
////			System.out.println("The method body is:" + node.getBody());
//			body.accept(new ASTVisitor() {
//				@Override
//				public boolean visit(MethodInvocation node) {
//					SimpleName MethodName = node.getName();
//					System.out.println("Method Invocation:\t" + MethodName);
//					Expression expression = node.getExpression();
//					if (expression != null) {
//						System.out.println("Expr: " + expression.toString());
//						ITypeBinding typeBinding1 = expression.resolveTypeBinding();
//
//						if (typeBinding1 != null) {
//							System.out.println("Call: " + node.getName());
//							System.out.println("Exprs: " + expression.toString());
////									System.out.println("Types: " + typeBinding1.getQualifiedName());
////									System.out.println("Types: " + typeBinding1.toString());
//						}
//					} else {
////						System.out.println("There is no expression!");
//					}
//					IMethodBinding binding = node.resolveMethodBinding();
//					if (binding != null) {
//						ITypeBinding type = binding.getDeclaringClass();
//						if (type != null) {
//							System.out.println("User-defined class: " + type.getName());
//						} else {
////							System.out.println("Can't get declared class");
//						}
//
//					} else {
////						System.out.println("Can't resolve IMethodBinding");
//					}
//					return false;
//				}
//
//			});
		String TestAnnotation = "@Test";
		String publicAnnotation = "public";
		
		List annotation = node.modifiers();
		HashSet<String> Annotations = new HashSet<String>();
		for(int i=0;i<annotation.size();i++) {
			Annotations.add(annotation.get(i).toString());
		}
		if(Annotations.contains(TestAnnotation) && Annotations.contains(publicAnnotation)) {
			System.out.println(node.getName().toString());
		}
		
//		ChildPropertyDescriptor child = annotation;
//		System.out.println(node.modifiers());
//		System.out.println(annotation.toString());
//		for(int i=0;i<annotation.;i++) {
//			System.out.println(i);
//		}
//		
//			node.accept(new ASTVisitor() {
//				int counter = 0;
//				@Override
//				public boolean visit(MarkerAnnotation annotation) {
//					System.out.println("MarkerAnnotation: " + annotation.getTypeName().getFullyQualifiedName());
//					return false;
//				}
//
//				@Override
//				public boolean visit(SingleMemberAnnotation annotation) {
//					System.out.println("SingleMemberAnnotation: " + annotation.getTypeName().getFullyQualifiedName());
//					counter += 1;
//					return false;
//				}
//
//				@Override
//				public boolean visit(NormalAnnotation annotation) {
//					System.out.println("NormalAnnotation: " + annotation.getTypeName().getFullyQualifiedName());
//					return false;
//				}
//			});
//		}
		System.out.println("================\n");
		return true;
	}
}