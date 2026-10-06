package main;

/**
 * Interface to be intended to allow easy Testcase modularity
 */
public abstract class Testcase {
	public final String testcase;
	public final int score;
	public final String name;
	
	public Testcase(String testcase, int score, String name) {
		this.testcase = testcase;
		this.score = score;
		this.name = name;
	}

	/**
	 * Returns the testcase and score (Points to be awarded/removed) of the testcase
	 * @return A Pair of the testcase and the corresponding score
	 */
	public Pair<String, Integer> testcase(){
		return new Pair<>(testcase,score);
	}
	
}
