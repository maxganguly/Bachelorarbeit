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
	 * Maximum amount a loop is being recalculated to generate Testcases
	 * minimum 1
	 */
	public static int loopDepth = 3;

	/**
	 * Takes the ASTTree of a class and generates MCDC Testcases for it
	 * 
	 * @param tree the ASTTree of the class
	 */
	public MCDCTestcaseGenerator(ASTTree tree) {
		this.tree = tree;
	}

	/**
	 * Takes the ASTTree of a class and generates MCDC Testcases for the methods
	 * with the given methodsignatures
	 * 
	 * @param tree    the ASTTree of the class
	 * @param strings the names of the methods mc/dc testcases should be generated
	 */
	public MCDCTestcaseGenerator(ASTTree tree, String... strings) {
		this(tree);
		this.methods = strings;
	}

	/**
	 * Generates MC/DC Testcases for the method with the given methodsignature
	 * 
	 * @param methodname the name of the method for which the testcases should be
	 *                   generated
	 * @return a Set of Testcases for the method
	 */
	public static List<DynamicTestcase> generateTestcases(ASTTree tree, String methodsignature, String preconditions) {
		List<Pair<String, Class<?>>> parameters = new LinkedList<Pair<String, Class<?>>>();
		List<ASTTree> methods = tree.getTreesWithTag("method").stream().filter(a -> a.name.equals(methodsignature))
				.collect(Collectors.toList());
		if (methods.size() == 0) {
			System.err.println("No method found for signature: " + methodsignature);
			return new LinkedList<DynamicTestcase>();
		}
		if (methods.size() > 1) {
			System.err.println("Multiple methods found for signature: " + methodsignature
					+ "\n generating testcases for the first one found");
		}
		ASTTree method = methods.getFirst();
		// The parameters must be the first part of the method

		parameters = method.children.get(0).children.stream()
				.map(c -> new Pair<String, Class<?>>(c.name, Data.STRING_TO_CLASS.get(c.type))).toList();
		var variables = new HashMap<String, String>();
		// Put all parameters in the var Map to "fix" their value to themselves
		for (var p : parameters) {
			variables.put(p.first(), p.first());
		}
		var conditions = getConditions(tree, variables, null);
		var testcases = conditionsToTestcases(conditions);
		return testcases;
	}

	/**
	 * Calculates the specifics for all conditions of the given part of the program
	 * Might not work very well with loops
	 * 
	 * @param tree      an ASTTree of the current program
	 * @param variables all variables currently found in the program, parameters
	 *                  given as names otherwise relative or absolute value
	 * @param branches  an List to get all generated branches from the function, serves as a pseudo return value
	 * @return
	 */
	private static List<String> getConditions(ASTTree tree, Map<String, String> variables, List<Map<String,String>> branches) {
		List<String> conditions = new LinkedList<String>();
		//List<Map<String, String>> branches = new LinkedList<Map<String,String>>();
		//conditions while, dowhile, for foreach
		switch(tree.tag) {
		case "for" -> {
			var head = tree.children.getFirst();
			for(var t: head.children) {
				if(t.tag.equals("init"))
					for(var init:t.children)
						toCode(init, variables);
			}
			ASTTree condition = null, update = null;
			for(var t: head.children) {
				if(t.tag.equals("init"))
					for(var init:t.children)
						toCode(init, variables);
				if(t.tag.equals("condition"));
					condition = t;
				if(t.tag.equals("condition"));
					update = t;
				
			}
			var body = tree.children.get(1);
			var m = variables;
			var notm = m;
			/*
			 * Evaluate the expression and split to do the rest and do the loop
			 * ?Copy? the vars and apply the condition walk through the body of the loop and split
			 * 1. evaluate the rest with the current variables
			 * 2. redo it loopDepth times
			 */
			for(int i = 0; i < loopDepth;i++) {
				String expression = toCode(condition, m);
				
				conditions.add(expression);
				for(var statement : body.children) {
					conditions.addAll(getConditions(statement, variables, branches));
				}
			}
		}
		}
		return conditions;
	}
	
	private static <X,Y> Map<X,Y> cloneMap(Map<X,Y> origin){
		var map = new HashMap<X,Y>(origin.size());
		for(var key: origin.keySet()) {
			map.put(key, origin.get(key));
		}
		return map;
	}

	/**
	 * Negates a condition by checking the outermost boolean evaluation
	 * @param condition a well formed condition in which every part is checked with parentheses
	 * @return a negation of the given condition
	 */
	private static String negate(String condition) {
		if(condition.charAt(0) == '!') {
			return condition.substring(1);
		}
		String outermost= "";
		int temp = 0;
		String operator = "";
		String left = "", right = "";
		boolean first = true;
		int start = 0,end =0;
		int parts = 0;
		for(int i = 0; i < condition.length();i++) {
			if(condition.charAt(i) == '(') {
				temp++;
				if(temp == 1) {
					end = i;
					if(start != 0) {
						operator = condition.substring(start,end).trim();
					}
				}
			}
			if(condition.charAt(i) == ')') {
				temp--;
				if(temp == 0) {
					start = i+1;
					parts++;
					if(first) {
						left = condition.substring(0, start);
					}else {
						right =condition.substring(end, condition.length());
					}
				}
			}
		}
		if(parts < 2)
			return "!("+condition+")";
		switch(operator) {
		case "==" -> operator = "!=";
		case "!=" -> operator = "==";
		case "<=" -> operator = ">";
		case ">=" -> operator = "<";
		case "<" -> operator = ">=";
		case ">" -> operator = "<=";
		}
		return left+operator+right;
		
	}
	/**
	 * Only intende to be used within a methode
	 * only data flow not control flow <b>NO if, or loops</b>
	 * @param tree
	 * @param replace
	 * @return
	 */
	private static String toCode(ASTTree tree, Map<String, String> replace) {
		// If nothing return empty
		if (tree.tag == null || tree.tag.isBlank())
			return "";
		// if literal return the literal
		if (tree.tag.equals("lit")) {
			if (tree.tag.matches(".*[a-zA-z].*"))// It#s a string
				return '\"' + tree.name + '\"';
			return tree.name;
		}
		//get type of current tree type 
		switch(tree.tag) {
		case "var"-> {
			// Is it already known?
			if (!replace.containsKey(tree.name))
				replace.put(tree.name, "");
			// is an assignment?
			if (!tree.children.isEmpty()) {
				// A value is assigned
				replace.put(tree.name, toCode(tree.children.getFirst(), replace));// is only allowed to have one child
			}
			if (tree.children.size() > 1)// Sanitycheck
				System.err.println("Variable assignment has more than 1 child [" + tree.children.size() + "], tree: "
						+ tree.toString());
			return replace.get(tree.name);
		}case "unary"-> {
			if(tree.type.contains("COMPLEMENT")) {
				replace.put(tree.children.getFirst().name, main.Data.KIND_TO_OPERATOR.get(tree.type)+'('+(toCode(tree.children.getFirst(),replace))+')');
				return replace.get(tree.children.getFirst().name);
			}
			String change = "";
			if(tree.type.contains("DECREMENT")) {
				change = "+1";
			}else {
				change = "-1";
			}
			if(tree.type.startsWith("PRE")) {
				replace.put(tree.children.getFirst().name, '('+(toCode(tree.children.getFirst(),replace))+')'+change);
				return replace.get(tree.children.getFirst().name);
			}else {
				String temp = replace.get(tree.children.getFirst().name);
				replace.put(tree.children.getFirst().name, '('+(toCode(tree.children.getFirst(),replace))+')'+change);
				return temp;
			}
		}case "binary"-> {
			String op = main.Data.KIND_TO_OPERATOR.get(tree.type);
			return '('+toCode(tree.children.getFirst(),replace)+' '+op+' '+toCode(tree.children.getLast(),replace)+')';
		}case "block"-> {
			StringBuilder sb = new StringBuilder("{+\n");
			for(ASTTree t: tree.children) {
				sb.append(toCode(t, replace));
				sb.append('\n');
			}
			return sb.toString();
		}case "mc"-> {
			StringBuilder sb = new StringBuilder();
			if(tree.type!= null) { //reference call
				if(replace.containsKey(tree.type)) {
					sb.append('('+replace.get(tree.type)+").");
				}else {
					sb.append(tree.type+".");
				}
			}
			sb.append(tree.name+"(");
			for(ASTTree t: tree.children) {
				sb.append(toCode(t, replace));
				sb.append(',');
			}
			if(sb.charAt(sb.length()-1) == ',')
				sb.deleteCharAt(sb.length()-1);
			sb.append(")");
			return sb.toString();
		}case "ternary"->{
			StringBuilder sb = new StringBuilder();
			sb.append('(');
			sb.append(toCode(tree.children.getFirst(), replace));
			sb.append(")?");
			sb.append(toCode(tree.children.get(1), replace));
			sb.append(":");
			sb.append(toCode(tree.children.get(2), replace));
			return sb.toString();
		}default-> {}
		
		}
		
		
		return "nuthing"+tree.toString();
	}

	private static List<DynamicTestcase> conditionsToTestcases(List<String> conditions) {
		List<DynamicTestcase> cases = new LinkedList<DynamicTestcase>();

		return cases;
	}

	@Override
	public List<DynamicTestcase> generateTestcases() {
		this.testcases = new LinkedList<DynamicTestcase>();
		List<ASTTree> methods = tree.getTreesWithTag("method");
		for (ASTTree t : methods) {
			testcases.addAll(generateTestcases(t, t.name, ""));
		}
		return this.testcases;
	}

	/**
	 * Saves the currently generated ASTTestcases to the directory Syntax of the
	 * saved file MCDCTestcases.dt
	 */
	@Override
	public boolean saveToDirectory(Path pathToDirectory) {
		Path p = Path.of(pathToDirectory.toString(), "MCDCTestcases.dt");
		return Main.printToFile(p, String.join("\n", this.testcases.stream().map(dt -> dt.toString()).toList()), false);
	}

	private String prefix = "";

	/**
	 * Loads ASTTestcases from the directory Syntax of the saved files *.dt
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
 				if (!attrs.isDirectory()) {
					String name = file.getFileName().toString();
					if (!name.endsWith(".dt")) {
						main.Main.debug("The file: \"" + file.getFileName().toString()
								+ "\" does not fit the given structure of *.dt and has been skipped");
						return FileVisitResult.TERMINATE;
					}
					String[] split = Main.getFromPath(file).split("\n");
					for (String s : split) {
						testcases.add(new DynamicTestcase(s));
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				prefix += dir.getFileName().toString() + ".";
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				prefix = prefix.substring(0, prefix.lastIndexOf(dir.getFileName().toString()));
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
