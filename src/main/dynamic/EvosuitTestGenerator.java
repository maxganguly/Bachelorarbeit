package main.dynamic;

import java.nio.file.Path;
import java.util.List;

import main.generator.Generator;

public class EvosuitTestGenerator extends Generator<DynamicTestcase> {

	private Class<?> solution;

	public EvosuitTestGenerator(Class<?> solution) {
		this.solution = solution;
	}

	@Override
	public List<DynamicTestcase> generateTestcases() {

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
