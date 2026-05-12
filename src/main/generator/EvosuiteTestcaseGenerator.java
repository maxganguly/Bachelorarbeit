package main.generator;

import java.nio.file.Path;
import java.util.List;
import main.dynamic.DynamicTestcase;

public class EvosuiteTestcaseGenerator extends Generator<DynamicTestcase> {

	public EvosuiteTestcaseGenerator(Path pathToSolution) {
		super();
	}
	
	@Override
	public List<DynamicTestcase> generateTestcases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveToDirectory(Path pathToDirectory) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DynamicTestcase> loadFromDirectory(Path pathToDirectory) {
		// TODO Auto-generated method stub
		return null;
	}

}
