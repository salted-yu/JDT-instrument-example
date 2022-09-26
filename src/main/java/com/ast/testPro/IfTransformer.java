package com.ast.testPro;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;

public class IfTransformer extends ASTVisitor {
	@Override 
	public boolean visit(IfStatement node) {
		InfixExpression ie = (InfixExpression) node.getExpression();
		ie.setOperator(Operator.NOT_EQUALS);
		
		node.setElseStatement((Block)ASTNode.copySubtree(node.getAST(), node.getThenStatement()));
		AST ast = node.getAST();
		
		MethodInvocation methodInv = ast.newMethodInvocation();
		
		SimpleName nameSystem = ast.newSimpleName("System");
		SimpleName nameOut = ast.newSimpleName("out");
		SimpleName namePrintln = ast.newSimpleName("println");
		
		QualifiedName nameSystemOut = ast.newQualifiedName(nameSystem, nameOut);
		methodInv.setExpression(nameSystemOut);
		methodInv.setName(namePrintln);
		
		StringLiteral sDone = ast.newStringLiteral();
		sDone.setEscapedValue("\"Done!\"");
		
		methodInv.arguments().add(sDone);
		
		ExpressionStatement es = ast.newExpressionStatement(methodInv);
		Block block = ast.newBlock();
		block.statements().add(es);
		
		node.setElseStatement(block);
		return false;
	}
}
