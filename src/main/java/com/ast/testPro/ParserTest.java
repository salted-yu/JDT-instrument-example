package com.ast.testPro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ParserTest {

//use ASTParse to parse string

	public static void parse(String str) {

		ASTParser parser = ASTParser.newParser(AST.JLS3);

		parser.setSource(str.toCharArray());

		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {

			Set names = new HashSet();

			public boolean visit(VariableDeclarationFragment node) {

				SimpleName name = node.getName();

				this.names.add(name.getIdentifier());
				
				

				System.out.println("Declaration of '" + name + "' at line"

						+ cu.getLineNumber(name.getStartPosition())  +  name.getIdentifier());

				return false; // do not continue

			}

			public boolean visit(SimpleName node) {

				if (this.names.contains(node.getIdentifier())) {

					System.out.println("Usage of '" + node + "' at line "

							+ cu.getLineNumber(node.getStartPosition()));

				}

				return true;

			}

		});

	}

//read file content into a string

	public static String readFileToString(String filePath) throws IOException {

		StringBuilder fileData = new StringBuilder(1000);

		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];

		int numRead = 0;

		while ((numRead = reader.read(buf)) != -1) {

			System.out.println(numRead);

			String readData = String.valueOf(buf, 0, numRead);

			fileData.append(readData);

			buf = new char[1024];

		}

		reader.close();

		return fileData.toString();

	}

//loop directory to get file list

	public static void ParseFilesInDir() throws IOException {

		File dirs = new File("F:/Code/Java/eclipse/testPro");

		String dirPath = dirs.getCanonicalPath() + File.separator + "src" + File.separator;

		File root = new File("F:/Code/Java/eclipse/testPro/src/main/java/com/ast/testPro");
		

//System.out.println(rootDir.listFiles());

		File[] files = root.listFiles();

		String filePath = null;
		
		if(files.length>0) {
			System.out.println("Find file");
		}
		
		

		for (File f : files) {

			filePath = f.getAbsolutePath();
			System.out.println(filePath.toString());
			if (f.isFile()) {

				parse(readFileToString(filePath));

			}

		}

	}

	public static void main(String[] args) throws IOException {

		ParseFilesInDir();
		System.out.println("Finish test");

	}
}