package com.ast.testPro;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Global {
	// 控制插桩结果输出的相对项目的路径
	private static final String MvnTestOutDir = "src/test/TestInsResults/";
	
	public static final String MvnTestFuncDir = "F:/Code/Java/eclipse/testPro/src/test";
	
	
	public static void main(String[] args) {
		String RootPath = System.getProperty("user.dir");
		System.out.println(RootPath);
		Path p = Paths.get(RootPath, MvnTestOutDir);
		System.out.println(p.toString());
		File file = p.toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = p.toString();
        System.out.println(path);
        String newPath = path.replace("\\","/");
        System.out.println(newPath);
	}
	
	
	
	
	public static String getTestOutDir(String FileName) {
		String RootPath = System.getProperty("user.dir");
		Path p = Paths.get(RootPath, MvnTestOutDir);
		File file = p.toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        p = Paths.get(file.getAbsolutePath(), FileName);
        String path = p.toString();
        path = path.replace("\\","/");
		return path;
	}
	

}
