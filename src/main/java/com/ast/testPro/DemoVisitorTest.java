package com.ast.testPro;

import org.eclipse.jdt.core.dom.CompilationUnit;

import com.ast.testPro.JdtAstUtil;
import com.ast.testPro.DemoVisitor;
 
public class DemoVisitorTest {

	public DemoVisitorTest(String path) {
		CompilationUnit comp = JdtAstUtil.getCompilationUnit(path);
		
		DemoVisitor visitor = new DemoVisitor();
		comp.accept(visitor);
	}
}