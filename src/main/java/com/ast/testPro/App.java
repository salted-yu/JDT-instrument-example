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

/**
 * Hello world!
 *
 */
import com.ast.testPro.DemoVisitorTest;
public class App 
{
    public static void main( String[] args )
    {
//        System.out.println( "Hello World!" );
//        DemoVisitorTest a = new DemoVisitorTest("F:/Code/Java/eclipse/Java-Example/src/main/java/org/apache/commons/math3/ml/distance/CanberraDistance.java");
    	CompilationUnit comp = JdtAstUtil.getCompilationUnit("F:/Code/Java/eclipse/testPro/src/main/java/com/ast/testPro/ClassDemo.java");
    	IfTransformer visitor = new IfTransformer();
    	comp.accept(visitor);
    	System.out.println(comp.toString());
    }
    
}


















