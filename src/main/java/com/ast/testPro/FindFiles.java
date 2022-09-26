package com.ast.testPro;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import com.ast.testPro.Global;


public class FindFiles {
	
	public static void main(String[] args) {
		String path = "F:\\Code\\Java\\eclipse\\Java-Example";
		String MainReg = "regex:.*\\\\src\\\\main\\\\java.*\\\\.*.java";
		String TestReg = "regex:.*\\\\src\\\\test\\\\java.*\\\\.*.java";
		
		List<String> Funcs = new ArrayList<>();
		
		List<Path> MainFilePath = new ArrayList<>(getFilePath(path, MainReg));
		for(Path p:MainFilePath) {
			// 对待测函数插桩
			addMainInstrument(p.toString());
			// 获得函数名
			Funcs.addAll(getFuncNames(p.toString()));
		}

		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(Global.MvnTestFuncDir + "\\FunctionNames.txt"));
			//遍历集合，得到每一个字符串数据
	        for(String s : Funcs) {
	            //调用字符缓冲输出流对象的方法写数据
	            bw.write(s);
	            bw.newLine();
	            bw.flush();
	        }

	        //释放资源
	        bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        
//        
//		List<Path> TestFilePath = new ArrayList<>(getFilePath(path, TestReg));
//		for(Path p:TestFilePath) {
//			// 对测试函数插桩
//			addTestInstrument(p.toString());
//		}
		
		addTestInstrument("F:\\Code\\Java\\eclipse/Java-Example/src/test"
				+ "/java/org/apache/commons/math3/util\\FastMathTest.java");
		addTestInstrument("F:\\Code\\Java\\eclipse/Java-Example/"
				+ "src/test/java/org/apache/commons/math3/fraction\\FractionTest.java");
		addTestInstrument("F:\\Code\\Java\\eclipse/Java-Example/src/"
				+ "test/java/org/apache/commons/math3/fraction/\\BigFractionTest.java");
		System.out.println("Finish");
		
	}
	

	
	public static List<Path> getFilePath(String DirPath, String Regex) {
		String path = DirPath;
		String reg = Regex;
		List<Path> SubFiles = new ArrayList<>();
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(reg);
		try {
			Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			        throws IOException {
			      if (pathMatcher.matches(file)) {
			        SubFiles.add(file);
			     }
			      return FileVisitResult.CONTINUE;
			    }
			  });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SubFiles;
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
			ClassName = m.group(1); //默认是group(0)
			 
		};
		return ClassName;
	}
	
	
	
	public static void addMainInstrument(String FilePath) {
		String File = getFile(FilePath);
		Document document = new Document(File);
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setSource(document.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		// 解析关系
//		parser.setResolveBindings(true);
//	    parser.setEnvironment(null, null, null, true);
//		parser.setUnitName("any_name");
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.recordModifications();
		cu.accept(new ASTVisitor() {
			
			HashSet<String> names = new HashSet<String>();
			@Override 
			public boolean visit(MethodDeclaration node) {
				// 构造System.out.println() 方法
				AST ast = node.getAST();
				Block block = node.getBody();
				if(block==null) {
					return false;
				}
				MethodInvocation methodInv = ast.newMethodInvocation();
				SimpleName nameSystem = ast.newSimpleName("System");
				SimpleName nameOut = ast.newSimpleName("out");
				SimpleName namePrintln = ast.newSimpleName("println");
				
				QualifiedName nameSystemOut = ast.newQualifiedName(nameSystem, nameOut);
				methodInv.setExpression(nameSystemOut);
				methodInv.setName(namePrintln);
				
				// 构建类名和函数名    不采用解析，直接使用文件名
				String ClassMethod = getClassName(FilePath);
//				
//				System.out.println("getReceiverType:" + node.getReceiverType());
//				System.out.println("extraDimensions:" + node.extraDimensions());
//				System.out.println("getReceiverQualifier:" + node.getReceiverQualifier());
//				System.out.println("getReturnType2:" + node.getReturnType2());
//				System.out.println("isCompactConstructor():" + node.isCompactConstructor());
//				System.out.println("==========================================");
				
//				String ClassMethod ="";
//				IMethodBinding binding = node.resolveBinding();
//				if (binding != null) {
//					ITypeBinding type = binding.getDeclaringClass();
//					if (type != null) {
//						ClassMethod = type.getPackage() + "." + type.getName();
//						ClassMethod = ClassMethod.replace("package ", "");
//					} 
//				}
				
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
				
				//System.out.println("find method\t" + ClassMethod + "\n" + node.toString());
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
	
	
	public static void addImport(String FilePath) {
		String File = getFile(FilePath);
		Document document = new Document(File);
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setSource(document.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		// 添加PrintStream的导入
		AST ast = cu.getAST();
		ImportDeclaration idout = ast.newImportDeclaration();
        idout.setName(ast.newName(new String[] {"java", "io", "PrintStream"}));
        ImportDeclaration idex = ast.newImportDeclaration();
        idex.setName(ast.newName(new String[] {"java", "io", "FileNotFoundException"}));
        
        ASTRewrite rewriter = ASTRewrite.create(ast);
        ListRewrite lrw = rewriter.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
        lrw.insertLast(idout, null);
        lrw.insertLast(idex, null);
        
        TextEdit edits = rewriter.rewriteAST(document, null);
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
	
	
	
	
	private static TryStatement createTryStatement(AST ast, ExpressionStatement st) {
	    TryStatement result = ast.newTryStatement();
	    Block body = ast.newBlock();
	    body.statements().add(st);
	    result.setBody(body);

	    
	    
	    //  生成catch block
	    CatchClause ex = ast.newCatchClause();

	    SingleVariableDeclaration exDecl = ast.newSingleVariableDeclaration();
	    exDecl.setType(ast.newSimpleType(ast.newSimpleName("FileNotFoundException")));
	    exDecl.setName(ast.newSimpleName("ex"));
	    ex.setException(exDecl);
	    Block catchBlock = ast.newBlock();
	    
	    MethodInvocation methodInv = ast.newMethodInvocation();
		SimpleName nameex = ast.newSimpleName("ex");
		SimpleName namePrintln = ast.newSimpleName("printStackTrace");
		
		methodInv.setExpression(nameex);
		methodInv.setName(namePrintln);
		ExpressionStatement es = ast.newExpressionStatement(methodInv);
		
		
	    catchBlock.statements().add(es);
	    ex.setBody(catchBlock);
	    
	    
	    
	    result.catchClauses().add(ex);
	    
	    return result;
	}
	
	
	
	public static void addTestInstrument(String FilePath) {
		addImport(FilePath);
		String File = getFile(FilePath);
		Document document = new Document(File);
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setSource(document.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.recordModifications();

		cu.accept(new ASTVisitor() {
			@Override 
			public boolean visit(MethodDeclaration node) {
				// 判断有没有@test注解，有的才是测试方法
				String TestAnnotation = "@Test";
				String publicAnnotation = "public";
				
				List annotation = node.modifiers();
				HashSet<String> Annotations = new HashSet<String>();
				for(int i=0;i<annotation.size();i++) {
					Annotations.add(annotation.get(i).toString());
				}
				if(!(Annotations.contains(TestAnnotation) && Annotations.contains(publicAnnotation))) {
					return false;
				}
				
				AST ast = node.getAST();
				
//				node.thrownExceptionTypes().add(ast.newSimpleType(ast.newSimpleName("FileNotFoundException"))); //不抛出异常，使用try - catch
				
				
				// 构造System.out.println() 方法
				
				Block block = node.getBody();
				if(block==null) {
					return false;
				}
				

				MethodInvocation methodInv = ast.newMethodInvocation();
				SimpleName nameSystem = ast.newSimpleName("System");
				SimpleName nameOut = ast.newSimpleName("out");
				SimpleName namePrintln = ast.newSimpleName("println");
				
				QualifiedName nameSystemOut = ast.newQualifiedName(nameSystem, nameOut);
				methodInv.setExpression(nameSystemOut);
				methodInv.setName(namePrintln);
				
				// 构建类名和函数名    不采用解析，直接使用文件名
				String ClassMethod = getClassName(FilePath);
				ClassMethod = ClassMethod + "#" + node.getName();
				StringLiteral sDone = ast.newStringLiteral();
				sDone.setEscapedValue("\"" + ClassMethod + "\"");
				methodInv.arguments().add(sDone);
				
				// 添加语句
				ExpressionStatement es = ast.newExpressionStatement(methodInv);
				
				
				// 添加输出流重定向语句
				MethodInvocation setOut = ast.newMethodInvocation();
				SimpleName nameSetout = ast.newSimpleName("setOut");
				setOut.setExpression(ast.newSimpleName("System"));
				setOut.setName(nameSetout);
				
				SimpleName namePrintStream = ast.newSimpleName("PrintStream");
				StringLiteral sfile = ast.newStringLiteral();
				String Outdir = Global.getTestOutDir(ClassMethod);
				sfile.setEscapedValue("\"" + Outdir + ".txt\""); 
				// 构造对象
				ClassInstanceCreation ci = ast.newClassInstanceCreation();
				ci.setType(ast.newSimpleType(namePrintStream));
				ci.arguments().add(sfile);
				setOut.arguments().add(ci);
				
				TryStatement tryso = createTryStatement(ast, ast.newExpressionStatement(setOut));
//				System.out.println(tryso.toString());

				block.statements().add(0, es);
				block.statements().add(0,tryso);
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

	
	public static List<String> getFuncNames(String FilePath) {
		String File = getFile(FilePath);
		Document document = new Document(File);
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		List<String> FuncNames = new ArrayList<>();
		parser.setSource(document.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		cu.accept(new ASTVisitor() {
			HashSet<String> names = new HashSet<String>();
			
			@Override 
			public boolean visit(MethodDeclaration node) {
				AST ast = node.getAST();
				Block block = node.getBody();
				if(block==null) {
					return false;
				}
				// 构建类名和函数名    不采用解析，直接使用文件名
				String ClassMethod = getClassName(FilePath);
				String Name = node.getName().toString();
				String tmp = new String(Name.toString());
				
				// 解决函数重名问题
				int n = 0;
				if(this.names.contains(tmp)) {
					while(this.names.contains(tmp)) {
						n = n + 1;
						tmp = new String(Name + n);
					}
					Name = new String(Name + n);
				}
				this.names.add(Name);
				
				ClassMethod = ClassMethod + "@" + Name;
				FuncNames.add(ClassMethod);
				return true;
			}
		});
		
		return FuncNames;
	}
}
