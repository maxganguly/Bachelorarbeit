package main.dynamic;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import main.Main;
import main.generator.Generator;

public class DynamicTestcaseGenerator extends Generator<DynamicTestcase> {

	@Override
	public List<DynamicTestcase> generateTestcases() {
		// TODO: Generate testcases
		return super.testcases;
	}

	@Override
	public boolean saveToDirectory(Path pathToDirectory) {
		// TODO Auto-generated method stub
		String path =pathToDirectory.toString();
		Path p = Path.of(path, "DynamicTestcases.dt");
		StringBuilder content = new StringBuilder();
		for (DynamicTestcase t : testcases) {
			content.append(t.toString());
		}
		if(!Main.printToFile(p,content.toString() ,Main.p.getProperty("OverwriteTestcases").equalsIgnoreCase("true"))) {
			return false;
		}
		Main.debug("Wrote: "+p.toString()+" to disk");
		return true;
	}
	
	@Override
	public List<DynamicTestcase> loadFromDirectory(Path pathToDirectory) {

		String root = pathToDirectory.getFileName().toString();
		FileVisitor<Path> files = new FileVisitor<>() {

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				//exc.printStackTrace();
				return FileVisitResult.TERMINATE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if(!attrs.isDirectory()) {
					String name = file.getFileName().toString();
					if(!name.endsWith(".dt")) {
						return FileVisitResult.CONTINUE;
					}
					String[] testtext = Main.getFromPath(file)
							.split(System.lineSeparator());
					for(String testcase : testtext) {
						testcases.add(new DynamicTestcase(testcase));
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		};
		try {
			Files.walkFileTree(pathToDirectory, files);
		} catch (IOException e) {
			Main.debug("File unable to be loaded");
		}
		return testcases;
	}


}
