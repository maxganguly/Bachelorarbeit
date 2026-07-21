package main.ast;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import main.Main;
import main.Pair;
import main.Tester;

public class ASTTester extends Tester<ASTTestcase> {

	ASTTree code;
	ASTTree generalized;

	public ASTTester() {
		this.code = null;
		this.generalized = null;
	}

	public ASTTester(ASTTree tree) {
		this.code = tree;
		this.generalized = code.generalize();
	}

	@Override
	public Pair<String, Integer> test(ASTTestcase testcase) {
		if(code.evaluate(testcase.tree)) {
			return new Pair<String, Integer>(testcase.name+ " successfull", testcase.score);
		}
		return new Pair<String, Integer>(testcase.name+ " failed", 0);
	}

	@Override
	public List<Pair<String, Integer>> runAllTestcases(Path p) {
		try {
			ASTTree temp = code;
			ASTTree tempg = generalized;
			code =  Main.generateAST(p).getFirst();
			this.generalized = code.generalize();
			var t= this.runAllTestcases();
			code = temp;
			this.generalized = tempg;
			return t;
		} catch (IOException e) {
			Main.debug(e);
		}
		return new LinkedList<Pair<String, Integer>>();
	}

	@Override
	public Pair<String, Integer> test(Path p, ASTTestcase testcase) {
		try {
			ASTTree temp = code;
			code = Main.generateAST(p).getFirst();
			var t= this.test(testcase);
			code = temp;
			return t;
		} catch (IOException e) {
			Main.debug(e);
			return new Pair<String,Integer>(e.getMessage(),-100);
		}
	}



}
