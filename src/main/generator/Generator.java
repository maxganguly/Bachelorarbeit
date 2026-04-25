package main.generator;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public abstract class Generator<E extends main.Testcase> {
	protected List<E> testcases;
	public Generator() {
		testcases = new LinkedList<E>();
	}
	public Generator(List<E> testcases) {
		this.testcases = testcases;
	}
	/**
	 * Generates a List of Testcases for the Class
	 * @return A List of testcases
	 */
	public abstract List<E> generateTestcases();
	/**
	 * Returns all currently generated Testcases and will generate if there are currently none
	 * @return A List of all Testcases
	 */
	public List<E> getTestcases(){
		if(testcases.isEmpty())
			this.testcases = generateTestcases();
		return this.testcases;
	}
	/**
	 * Saves the current testcases into a directory
	 * @param pathToDirectory the directory to save the testcases into
	 * @return true if the writing was successfull
	 */
	public abstract boolean saveToDirectory(Path pathToDirectory);
	/**
	 * Loads the testcases from a given directory
	 * @param pathToDirectory
	 * @return
	 */
	public abstract List<E> loadFromDirectory(Path pathToDirectory);
}
