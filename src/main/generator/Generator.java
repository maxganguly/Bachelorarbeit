package main.generator;

import java.nio.file.Path;
import java.util.List;

public interface Generator<E extends main.Testcase> {
	List<E> generateTestcases();
	public abstract boolean saveToDirectory(Path pathToDirectory);
	public abstract List<E> loadFromDirectory(Path pathToDirectory);
}
