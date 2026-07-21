package main.dynamic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import main.AbstractTestWrapper;
import main.Main;
import main.Pair;

public class DynamicTestWrapper extends AbstractTestWrapper {

	
	private DynamicTester tester;
	public DynamicTestWrapper(Path solution) throws IOException {
		super(solution);
		tester = new DynamicTester(new Executor(solution,true), null);
		String filename = solution.getFileName().toString();
		//TODO: Add Dynamic Testcase generation
		DynamicTestcaseGenerator dtg = new DynamicTestcaseGenerator();
		if(Main.p.getProperty("GenerateTestcases").equalsIgnoreCase("true")){
			dtg.generateTestcases();
		}
		String pureName = filename.substring(0, filename.lastIndexOf('.'));
		dtg.loadFromDirectory(Path.of(Main.p.getProperty("Testcases")+"/"+pureName));
		tester.addTestcases(dtg.getTestcases());
		if (Main.p.getProperty("SaveTestcases").equalsIgnoreCase("true")){
			Main.debug("Saving: "+pureName);
			dtg.saveToDirectory(Path.of(Main.p.getProperty("Testcases")+"/"+pureName));
		}
		
		
	}

	@Override
	public List<Pair<String, Integer>> test(Path submission) {
		return tester.runAllTestcases(submission);
	}

}
