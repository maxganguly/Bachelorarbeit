package main.generator;

import java.util.List;

public interface Generator {
	List<? extends main.Testcase> generateTestcases();
}
