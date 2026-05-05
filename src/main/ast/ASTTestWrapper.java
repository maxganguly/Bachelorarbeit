package main.ast;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import main.AbstractTestWrapper;
import main.Main;
import main.Pair;

public class ASTTestWrapper extends AbstractTestWrapper {

	ASTTester tester;
	
	public ASTTestWrapper(Path solution) throws IOException{
		super(solution);
		String filename = solution.getFileName().toString();
		var atg = new ASTTestGenerator(Main.generateAST(solution).getFirst());
		//System.out.println(atg.code.code);
		String pureName = filename.substring(0, filename.lastIndexOf('.'));
		atg.loadFromDirectory(Path.of(Main.p.getProperty("Testcases")+"/"+pureName));
		tester = new ASTTester();
		tester.addTestcases(atg.getTestcases());
		if(Main.p.getProperty("SaveTestcases").equalsIgnoreCase("true")){
			Main.debug("Saving: "+pureName);
			atg.saveToDirectory(Path.of(Main.p.getProperty("Testcases")+"/"+pureName));
		}
	}

	@Override
	public List<Pair<String, Integer>> test(Path submission) {
		return tester.runAllTestcases(submission);
	}

}
