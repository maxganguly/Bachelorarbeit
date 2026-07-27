package main;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * An Wrapper class for Testing
 */
public abstract class AbstractTestWrapper {

	/**
	 * The Path to the example solution
	 */
	protected Path solution;
	/**
	 * Constructor, saves the path to the example solution
	 * @param solution the example solution
	 */
	public AbstractTestWrapper(Path solution) {
		this.solution = solution;
	}

	/**
	 * Test the given submission against the example solution
	 * @param submission the students submission
	 * @return a List of all executed testcases with name and score 
	 */
	public abstract List<Pair<String,Integer>> test(Path submission);

}
