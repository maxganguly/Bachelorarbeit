package main.ast;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import main.AbstractTestWrapper;
import main.Start;
import main.Testcase;
import main.Pair;
/**
 * An implementation of the TestWrapper for static (AST) tests
 */
public class ASTTestWrapper extends AbstractTestWrapper {

	ASTTester tester;

	public ASTTestWrapper(Path solution) throws IOException{
		super(solution);
		String filename = solution.getFileName().toString();
		var atg = new ASTTestGenerator(Start.generateAST(solution).getFirst());
		//System.out.println(atg.code.code);
		if(Start.p.getProperty("GenerateTestcases").equalsIgnoreCase("true")){
			atg.generateTestcases();
		}
		String pureName = filename.substring(0, filename.lastIndexOf('.'));
		atg.loadFromDirectory(Path.of(Start.p.getProperty("Testcases")+"/"+pureName));
		tester = new ASTTester();
		tester.addTestcases(atg.getTestcases());
		if (Start.p.getProperty("SaveTestcases").equalsIgnoreCase("true")){
			Start.debug("Saving: "+pureName);
			atg.saveToDirectory(Path.of(Start.p.getProperty("Testcases")+"/"+pureName));
		}
	}

	@Override
	public List<Pair<String, Integer>> test(Path submission) {
		return tester.runAllTestcases(submission);
	}

	@Override
	public List<ASTTestcase> getAllTestcases() {
		return tester.getTestcases();
	}
}
