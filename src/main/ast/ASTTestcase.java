package main.ast;

import main.Testcase;

/**
 * AST Testcase, a static Testcase containing the name and an ASTree to check
 */
public class ASTTestcase extends Testcase {

	public final ASTTree tree;

	public ASTTestcase(String name, String testcase, int score) {
		super(testcase, score, name);
		this.tree = new ASTTree(testcase);
	}

	public ASTTestcase(String name, ASTTree testcase, int score) {
		super(testcase.toString(), score, name);
		this.tree = testcase;
	}

}
