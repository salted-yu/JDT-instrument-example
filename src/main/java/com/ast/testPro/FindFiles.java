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
			// ?????????????????????
			addMainInstrument(p.toString());
			// ???????????????
			Funcs.addAll(getFuncNames(p.toString()));
		}

		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(Global.MvnTestFuncDir + "\\FunctionNames.txt"));
			//?????????????????????????????????????????????
	        for(String s : Funcs) {
	            //???????????????????????????????????????????????????
	            bw.write(s);
	            bw.newLine();
	            bw.flush();
	        }

	        //????????????
	        bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        
//        
//		List<Path> TestFilePath = new ArrayList<>(getFilePath(path, TestReg));
//		for(Path p:TestFilePath) {
//			// ?????????????????????
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
		Pattern pt = Pattern.compile(rex); //????????????
		Matcher m = pt.matcher(FilePath.toString()); //????????????
		String ClassName = "";
		if(m.find()) {
			ClassName = m.group(1); //?????????group(0)
			 
		};
		return ClassName;
	}
	
	
	
	public static void addMainInstrument(String FilePath) {
		String File = getFile(FilePath);
		Document document = new Document(File);
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setSource(document.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		// ????????????
//		parser.setResolveBindings(true);
//	    parser.setEnvironment(null, null, null, true);
//		parser.setUnitName("any_name");
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.recordModifications();
		cu.accept(new ASTVisitor() {
			
			HashSet<String> names = new HashSet<String>();
			@Override 
			public boolean visit(MethodDeclaration node) {
				// ??????System.out.println() ??????
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
				
				// ????????????????????????    ???????????????????????????????????????
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
				// ????????????????????????
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
				
				// ????????????
				ExpressionStatement es = ast.newExpressionStatement(methodInv);
				
				// ??????????????????????????????????????????????????????????????????????????????
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
		
		TextEdit edits = cu.rewrite(document,null);		//???????????????Unit????????????
		try {
			edits.apply(document);
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ?????????????????????
		FileWriter writer;
        try {
            writer = new FileWriter(FilePath.toString());
            writer.write("");//?????????????????????
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
		// ??????PrintStream?????????
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
		
		// ?????????????????????
		FileWriter writer;
        try {
            writer = new FileWriter(FilePath.toString());
            writer.write("");//?????????????????????
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

	    
	    
	    //  ??????catch block
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
				// ???????????????@test?????????????????????????????????
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
				
//				node.thrownExceptionTypes().add(ast.newSimpleType(ast.newSimpleName("FileNotFoundException"))); //????????????????????????try - catch
				
				
				// ??????System.out.println() ??????
				
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
				
				// ????????????????????????    ???????????????????????????????????????
				String ClassMethod = getClassName(FilePath);
				ClassMethod = ClassMethod + "#" + node.getName();
				StringLiteral sDone = ast.newStringLiteral();
				sDone.setEscapedValue("\"" + ClassMethod + "\"");
				methodInv.arguments().add(sDone);
				
				// ????????????
				ExpressionStatement es = ast.newExpressionStatement(methodInv);
				
				
				// ??????????????????????????????
				MethodInvocation setOut = ast.newMethodInvocation();
				SimpleName nameSetout = ast.newSimpleName("setOut");
				setOut.setExpression(ast.newSimpleName("System"));
				setOut.setName(nameSetout);
				
				SimpleName namePrintStream = ast.newSimpleName("PrintStream");
				StringLiteral sfile = ast.newStringLiteral();
				String Outdir = Global.getTestOutDir(ClassMethod);
				sfile.setEscapedValue("\"" + Outdir + ".txt\""); 
				// ????????????
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
		

        
        
        TextEdit edits = cu.rewrite(document,null);		//???????????????Unit????????????
		try {
			edits.apply(document);
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// ?????????????????????
		FileWriter writer;
        try {
            writer = new FileWriter(FilePath.toString());
            writer.write("");//?????????????????????
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
				// ????????????????????????    ???????????????????????????????????????
				String ClassMethod = getClassName(FilePath);
				String Name = node.getName().toString();
				String tmp = new String(Name.toString());
				
				// ????????????????????????
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
