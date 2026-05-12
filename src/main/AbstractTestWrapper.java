package main;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public abstract class AbstractTestWrapper {

	protected Path solution;
	public AbstractTestWrapper(Path solution) throws IOException{
		this.solution = solution;
	}

	public abstract List<Pair<String,Integer>> test(Path submission);

}
