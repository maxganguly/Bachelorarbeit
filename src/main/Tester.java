package main;

import java.util.List;

public interface Tester {
	
	/**
	 * Returns all saved Testcases
	 * @return a list of all Testcases in the Tester
	 */
	public List<main.Testcase> getTestcases();
	/**
	 * Adds an additional Testcase to the Tester, no duplicate
	 * @param test the testcase to be added
	 */
	public void addTestcase(main.Testcase test);
	/**
	 * Adds multiple Testcases to the Tester, ignores duplicates
	 * @param testcases the testcases to be added
	 */
	public void addTestcases(main.Testcase...testcases );
	/**
	 * Runs all Testcases and returns the Result as defined in the Testcase interface
	 * @return a List of the results of all Testcases
	 */
	public List<Pair<String, Integer>> runAllTestcases();
}
