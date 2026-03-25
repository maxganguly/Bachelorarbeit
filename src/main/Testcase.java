package main;

/**
 * Interface to be intended to allow easy Testcase modularity 
 */
public interface Testcase {
	/**
	 * Tests the current testcase and returns an String result and and score (Points to be awarded/removed
	 * @return A Pair of the textual result and the corresponding score
	 */
	public Pair<String, Integer> test();
}
