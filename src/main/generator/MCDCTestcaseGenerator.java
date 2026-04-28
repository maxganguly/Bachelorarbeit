package main.generator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import main.Data;
import main.Main;
import main.Pair;
import main.Testcase;
import main.ast.ASTTestcase;
import main.ast.ASTTree;
import main.dynamic.DynamicTestcase;

public class MCDCTestcaseGenerator extends Generator<DynamicTestcase> {

	private ASTTree tree;
	private List<DynamicTestcase> testcases;
	private String[] methods;
	
	/**
	 * Takes the ASTTree of a class and generates MCDC Testcases for it
	 * @param tree the ASTTree of the class
	 */
	public MCDCTestcaseGenerator(ASTTree tree) {
		this.tree = tree;
	}
	
	/**
	 * Takes the ASTTree of a class and generates MCDC Testcases for the methods with the given methodsignatures
	 * @param tree the ASTTree of the class
	 * @param strings the names of the methods mc/dc testcases should be generated
	 */
	public MCDCTestcaseGenerator(ASTTree tree, String... strings ) {
		this(tree);
		this.methods = strings;
	}
	
	/**
	 * Generates MC/DC Testcases for the method with the given methodsignature
	 * @param methodname the name of the method for which the testcases should be generated
	 * @return a Set of Testcases for the method
	 */
	public static List<DynamicTestcase> generateTestcases(ASTTree tree, String methodsignature, String preconditions) {
		List<DynamicTestcase> cases = new LinkedList<DynamicTestcase>();
		List<Pair<String,Class>> parameters = new LinkedList<Pair<String,Class>>();
		List<ASTTree> methods = tree.getTreesWithTag("method").stream().filter(a -> a.name.equals(methodsignature)).collect(Collectors.toList());
		if(methods.size() == 0) {
			System.err.println("No method found for signature: "+methodsignature);
			return cases;
		}
		if(methods.size() > 1) {
				System.err.println("Multiple methods found for signature: "+methodsignature+"\n generating testcases for the first one found");
		}
		ASTTree method = methods.getFirst();
		//The parameters must be the first part of the method
		
		parameters = method.children.get(0).children.stream().map(c -> new Pair<String,Class>(c.name, Data.STRING_TO_CLASS.get(c.type))).toList();
		
		
		return cases;
	}
	
	/**
	 * Calculates the specifics for all conditions of the given part of the program
	 * Might not work very well with loops
	 * @param tree an ASTTree of the current program
	 * @param variables all variables currently found in the program, parameters given as names otherwise relative or absolute value 
	 * @return
	 */
	private static List<String> getConditions(ASTTree tree, Map<String,String> variables){
		List<String> conditions = new LinkedList<String>();
		if(tree.tag.equals("assign")) {
			StringBuilder sb = new StringBuilder();
			
		}
		return conditions;
	}
	
	
	@Override
	public List<DynamicTestcase> generateTestcases() {
		this.testcases = new LinkedList<DynamicTestcase>();
		List<ASTTree> methods = tree.getTreesWithTag("method");
		for(ASTTree t : methods) {
			testcases.addAll(generateTestcases(t,t.name, ""));
		}
		return this.testcases;
	}

	
	/**
	 * Saves the currently generated ASTTestcases to the directory
	 * Syntax of the saved file MCDCTestcases.dt
	 */
	@Override
	public boolean saveToDirectory(Path pathToDirectory) {
		Path p = Path.of(pathToDirectory.toString(), "MCDCTestcases.dt");
		return Main.printToFile(p, 
				String.join("\n", this.testcases.stream().map(dt -> dt.toString()).toList()),false);
	}
	private String prefix = "";
	/**
	 * Loads ASTTestcases from the directory
	 * Syntax of the saved files *.dt
	 */
	public List<DynamicTestcase> loadFromDirectory(Path pathToDirectory) {

		FileVisitor<Path> files = new FileVisitor<Path>() {
			
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				exc.printStackTrace();
				return FileVisitResult.TERMINATE;
			}
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if(!attrs.isDirectory()) {
					String name = file.getFileName().toString();
					if(!name.endsWith(".dt")) {
						main.Main.debug("The file: \""+file.getFileName().toString()+"\" does not fit the given structure of *.dt and has been skipped");
						return FileVisitResult.TERMINATE;
					}
					String[] split = Main.getFromPath(file).split("\n");
					for(String s:split) {
						testcases.add(new DynamicTestcase(s));
					}
				}
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				prefix += dir.getFileName().toString()+".";
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				prefix = prefix.substring(0,prefix.lastIndexOf(dir.getFileName().toString()));
				return FileVisitResult.CONTINUE;
			}
		};
		try {
			Files.walkFileTree(pathToDirectory, files);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return testcases;
	}
	
	

}
