package main;

import java.util.LinkedList;
import java.util.List;

public abstract class Tester<E extends main.Testcase> {
	
	List<E> testcases;
	
	public Tester() {
		testcases = new LinkedList<E>();
	}
	/**
	 * Returns all saved Testcases
	 * @return a list of all Testcases in the Tester
	 */
	public List<E> getTestcases() {
		return this.testcases;
	}
	/**
	 * Adds an additional Testcase to the Tester, no duplicate
	 * @param test the testcase to be added
	 */
	public void addTestcase(E test) {
		this.testcases.add(test);
		
	}
	/**
	 * Adds multiple Testcases to the Tester, ignores duplicates
	 * @param testcases the testcases to be added
	 */
	public void addTestcases(E... testcases) {
		for(E t : testcases ) {
			addTestcase(t);
		}
	}
	/**
	 * Runs all Testcases and returns the Result as defined in the Testcase interface
	 * @return a List of the results of all Testcases
	 */
	public List<Pair<String, Integer>> runAllTestcases(){
		var results = new LinkedList<Pair<String, Integer>>();
		for(E test:testcases) {
			results.add(test(test));
		}
		return results;
	}
	
	
	/**
	 * Runs a single given testcase
	 * @param testcase the testcase to test
	 * @return The Results  (Message, Evaluated_Score)
	 */
	public abstract Pair<String, Integer> test(E testcase);

	

	
}
