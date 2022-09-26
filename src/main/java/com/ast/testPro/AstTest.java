package com.ast.testPro;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class AstTest {
	public static void main(String[] args) {
		String path = "F:\\Code\\Java\\eclipse/testPro/src/main/java/com/ast/testPro\\ClassDemo.java";
		addInstrument(path);
		System.out.println("Finish");
		
	}
	
	
	public static String getFile(String FilePath) {
		byte[] input = null;
		try {
		    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(FilePath));
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
	
	
	public static String getClassName(String FilePath) {
		String rex = ".*\\\\(.*).java";
		Pattern pt = Pattern.compile(rex); //编译对象
		Matcher m = pt.matcher(FilePath.toString()); //进行匹配
		String ClassName = "";
		if(m.find()) {
			ClassName = m.group(1); 
			 
		};
		return ClassName;
	}
	
	
	
	public static void addInstrument(String FilePath) {
		String File = getFile(FilePath);
		Document document = new Document(File);
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setSource(document.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.recordModifications();
		cu.accept(new ASTVisitor() {
			
			HashSet<String> names = new HashSet<String>();
			@Override 
			public boolean visit(MethodDeclaration node) {
				
				Block block = node.getBody();
				if(block==null) {
					return false;
				}
				
				// 构造System.out.println() 方法
				AST ast = node.getAST();
				MethodInvocation methodInv = ast.newMethodInvocation();
				SimpleName nameSystem = ast.newSimpleName("System");
				SimpleName nameOut = ast.newSimpleName("out");
				SimpleName namePrintln = ast.newSimpleName("println");
				
				QualifiedName nameSystemOut = ast.newQualifiedName(nameSystem, nameOut);
				methodInv.setExpression(nameSystemOut);
				methodInv.setName(namePrintln);
				
				// 构建类名和函数名    不采用解析，直接使用文件名
				String ClassMethod = getClassName(FilePath);
				String Name = node.getName().toString();
				String tmpName = new String(Name);
				// 解决函数重名问题
				int n = 0;
				if(this.names.contains(tmpName)) {
					while(this.names.contains(tmpName)) {
						n = n + 1;
						tmpName = new String(Name + n);
					}
					Name = new String(Name + n);
				}
				this.names.add(Name);
				
				ClassMethod = ClassMethod + "@" + Name;
				StringLiteral sDone = ast.newStringLiteral();
				sDone.setEscapedValue("\"" + ClassMethod + "\"");
				methodInv.arguments().add(sDone);
				
				// 添加语句
				ExpressionStatement es = ast.newExpressionStatement(methodInv);
				
				// 判断是不是构造函数，若是构造函数，将打印语句放到最后
				if(node.isConstructor()) {
					block.statements().add(es);
				}
				else {
					block.statements().add(0, es);
				}
				return false;
			}
		});
		
		TextEdit edits = cu.rewrite(document,null);		//使用创建的Unit进行写回
		try {
			edits.apply(document);
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 将修改写回文件
		FileWriter writer;
        try {
            writer = new FileWriter(FilePath.toString());
            writer.write("");//清空原文件内容
            writer.write(document.get());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}	

	

