package main;

/**
 * Interface to be intended to allow easy Testcase modularity 
 */
public interface Testcase {
	/**
	 * Returns the testcase and score (Points to be awarded/removed) of the testcase
	 * @return A Pair of the testcase and the corresponding score
	 */
	public Pair<String, Integer> testcase();
}
