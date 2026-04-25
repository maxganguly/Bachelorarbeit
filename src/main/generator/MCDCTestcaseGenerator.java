package main.generator;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import main.Data;
import main.Pair;
import main.Testcase;
import main.ast.ASTTree;
import main.dynamic.DynamicTestcase;

public class MCDCTestcaseGenerator extends Generator<DynamicTestcase> {

	private ASTTree tree;
	private List<DynamicTestcase> testcases;
	private String[] methods;
	
	/**
	 * Takes the ASTTree of a class and generates MCDC Testcases for it
	 * @param tree the Tree of the class
	 */
	public MCDCTestcaseGenerator(ASTTree tree) {
		this.tree = tree;
	}
	
	/**
	 * Takes the ASTTree of a class and generates MCDC Testcases for the methods with the given methodsignatures
	 * @param tree the Tree of the class
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
		List<ASTTree> methods = tree.getTreesWithTag("methode").stream().filter(a -> a.name.equals(methodsignature)).collect(Collectors.toList());
		if(methods.size() == 0) {
			System.err.println("No method found for signature: "+methodsignature);
			return cases;
		}
		if(methods.size() > 1) {
				System.err.println("Multiple methods found for signature: "+methodsignature+"\n generating testcases for the first one found");
		}
		ASTTree method = methods.getFirst();
		for(ASTTree child: method.children) {
			if(child.name.equals("head")) {
				parameters = child.children.stream().map(c -> new Pair<String,Class>(c.name, Data.STRING_TO_CLASS.get(c.type))).toList();
				break;
			}
		}
		
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
		
		return conditions;
	}
	
	
	@Override
	public List<DynamicTestcase> generateTestcases() {
		//Caching but the List is mutable
		/*
		if(testcases != null)
			return testcases;
		*/
		this.testcases = new LinkedList<DynamicTestcase>();
		List<ASTTree> methods = tree.getTreesWithTag("method");
		for(ASTTree t : methods) {
			testcases.addAll(generateTestcases(t,t.name, ""));
		}
		return this.testcases;
	}

	@Override
	public boolean saveToDirectory(Path pathToDirectory) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DynamicTestcase> loadFromDirectory(Path pathToDirectory) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
