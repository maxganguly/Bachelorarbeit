package main.ast;

import main.Testcase;

public class ASTTestcase extends Testcase {

	public final String name;
	public final ASTTree tree;
	
	public ASTTestcase(String name, String testcase, int score) {
		super(testcase, score);
		this.tree = new ASTTree(testcase);
		this.name = name;
	}
	
	public ASTTestcase(String name, ASTTree testcase, int score) {
		super(testcase.toString(), score);
		this.tree = testcase;
		this.name = name;
	}
	
}
