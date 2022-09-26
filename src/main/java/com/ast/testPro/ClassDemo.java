package com.ast.testPro;

public class ClassDemo {
	
	private String text = "Hello World!", text2;
	
	public static void print(String value) {
		System.out.println("ClassDemo@print");
		System.out.println(value);
	}
	

	public static void input(String value) {
		System.out.println("ClassDemo@input");
		String text2 = value;
		if(text2.equals("1")) {
			System.out.println("Wrong");
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println("ClassDemo@main");
		System.out.println("This is a test class file!");
		print("This is a Method Invocation");
		input("1");
		input("2");
		System.out.println("Finish class test!");
	}
}