package main.ast;

import main.Pair;
import main.Tester;

public class ASTTester extends Tester<ASTTestcase> {

	ASTTree code;
	
	public ASTTester(ASTTree tree) {
		this.code = tree;
	}
	
	@Override
	public Pair<String, Integer> test(ASTTestcase testcase) {
		if(code.evaluate(testcase.tree)) {
			return new Pair<String, Integer>(testcase.name+ " successfull", testcase.score);
		}
		return new Pair<String, Integer>(testcase.name+ " failed", 0);
	}
	


}
