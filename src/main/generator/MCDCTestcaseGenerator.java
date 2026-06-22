package main.generator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import main.Data;
import main.Main;
import main.Pair;
import main.ast.ASTTree;
import main.conditions.Condition;
import main.conditions.ConditionUtils;
import main.dynamic.DynamicTestcase;

public class MCDCTestcaseGenerator extends Generator<DynamicTestcase> {

	private ASTTree tree;
	private List<DynamicTestcase> testcases;
	private String[] methods;
	/**
	 * Maximum amount a loop is being recalculated to generate Testcases minimum 1
	 */
	public static int loopDepth = 2;

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
	 * Generates all conditions for all branches for the given methode
	 *
	 * @param methodname the name of the method for which the testcases should be
	 *                   generated
	 * @return a List of conditions for all branches of the methode
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

		parameters = getParameters(method);
				var variables = new HashMap<String, String>();
		// Put all parameters in the var Map to "fix" their value to themselves
		for (var p : parameters) {
			variables.put(p.first(), p.first());
		}
		var conditions = getConditions(method.children.get(1), variables, "");
			conditions = removeConstantAndDuplicateConditions(conditions);
		
		System.out.println("Generated: "+conditions.size()+" conditions");
		
		return conditionsToTestcases(Pair.toMap(parameters), 
				conditions.stream().map(e -> e.first()).toList()
				, methodsignature);
	}
	/**
	 * Generates all conditions for all branches for the given methode
	 *
	 * @param methodname the name of the method for which the testcases should be
	 *                   generated
	 * @return a List of conditions for all branches of the methode
	 */
	 public static List<Condition> generateConditions(ASTTree tree, String methodsignature, String preconditions, boolean clean) {
		List<ASTTree> methods = tree.getTreesWithTag("method").stream().filter(a -> a.name.equals(methodsignature))
				.collect(Collectors.toList());
		if (methods.size() == 0) {
			System.err.println("No method found for signature: " + methodsignature);
			return new LinkedList<Condition>();
		}
		if (methods.size() > 1) {
			System.err.println("Multiple methods found for signature: " + methodsignature
					+ "\n generating testcases for the first one found");
		}
		ASTTree method = methods.getFirst();
		// The parameters must be the first part of the method
		List<Pair<String, Class<?>>> parameters;
		parameters = getParameters(method);
		Set<String> parms = Pair.toMap(parameters).keySet();
		
		return getConditionsStringMap(parameters, clean, method).stream().map(
				e -> ConditionUtils.toCondition(e.first(), parms).evaluate()).distinct().toList();
	}

	/**
	 * @param clean
	 * @param method
	 * @return
	 */
	private static List<Pair<String, Map<String, String>>> getConditionsStringMap(List<Pair<String, Class<?>>> parameters, boolean clean, ASTTree method) {
				var variables = new HashMap<String, String>();
		// Put all parameters in the var Map to "fix" their value to themselves
		for (var p : parameters) {
			variables.put(p.first(), p.first());
		}
		var conditions = getConditions(method.children.get(1), variables, "true");
		if(clean) {
			conditions = removeConstantAndDuplicateConditions(conditions);
		}
		
		return conditions;
	}

	public static List<Pair<String,Class<?>>> getParameters(ASTTree method){
		return method.children.get(0).children.stream()
				.map(c -> new Pair<String, Class<?>>(c.name, Data.STRING_TO_CLASS.get(c.type))).toList();
		
	}
	/**
	 * Calculates the specifics for all conditions of the given part of the program
	 * Might not work very well with loops, maybe set would be more useful Does not
	 * work with foreach, solution code should be done without foreach
	 * 
	 * @param tree       (your snippet of code, recursive) an ASTTree of the current
	 *                   program
	 * @param variables  (your branch) all variables currently found in the program,
	 *                   parameters given as names otherwise relative or absolute
	 *                   value
	 * @param precondition the current applicable conditions for the 
	 * @return (all newly generated branches (should contain your changed
	 *         branch))branches an List to get all generated branches from the
	 *         function, serves as a pseudo return value
	 */
	private static List<Pair<String,Map<String, String>>> getConditions(ASTTree tree, Map<String, String> variables, String precondition) {

		// List<Map<String, String>> branches = new LinkedList<Map<String,String>>();
		// conditions while, dowhile, for foreach
		List<Pair<String,Map<String, String>>> branches = new LinkedList<Pair<String,Map<String,String>>>();
		variables = cloneMap(variables);
		branches.add(new Pair<>(precondition, variables));
		switch (tree.tag) {
		case "for" -> {
			return getConditionsFor(tree, variables, precondition, branches);
		}
		case "while" -> {
			return getConditionsWhile(tree, variables, precondition, branches);
			
		}
		case "if" -> {
			return getConditionsIf(tree, variables, precondition, branches);
		}
		case "switch" -> {
			return getConditionsSwitch(tree, variables, precondition, branches);
		}

		}
		if (TOCODE_EVALUATEABLE.contains(tree.tag)) {
			// evaluate, no need to delve deeper
			toCode(tree, variables);
			return branches;
		}
		for (var child : tree.children) {
			// shallow copy is fine
			var currentbranches = branches;
			branches = new LinkedList<Pair<String,Map<String, String>>>(branches);
			for (var branch : currentbranches) {
				//remove previous branch as it might be changed but will be readded
				branches.remove(branch);
				// change the shallow to deepcopy
				var nbranches = getConditions(child, branch.second(), branch.first());
				branches.addAll(nbranches);
			}
			
		}
		return branches;
	}

	/**
	 * @param tree
	 * @param variables
	 * @param precondition
	 * @param branches
	 * @return
	 */
	private static List<Pair<String, Map<String, String>>> getConditionsSwitch(ASTTree tree,
			Map<String, String> variables, String precondition, List<Pair<String, Map<String, String>>> branches) {
		String expression = toCode(tree.children.getFirst().children.getFirst(), variables);
		// for every case
		var m = variables;
		for (var c : tree.children.get(1).children) {
			m = cloneMap(m);
			String caseExpression = "(" + expression + " == " + toCode(c.children.getFirst().children.getFirst(), m)+")";				
			if(!precondition.isBlank()) {
				caseExpression = '('+precondition + " && " +  caseExpression+')';
			}
			branches.addAll(getConditions(c.children.get(1), m, caseExpression));
		}
		return branches;
	}

	/**
	 * @param tree
	 * @param variables
	 * @param precondition
	 * @param branches
	 * @return
	 */
	private static List<Pair<String, Map<String, String>>> getConditionsIf(ASTTree tree, Map<String, String> variables,
			String precondition, List<Pair<String, Map<String, String>>> branches) {
		var head = tree.children.getFirst();
		ASTTree condition = head.children.getFirst();
		// the first part after the head must be the body
		var then = tree.children.get(1);
		var m = variables;
		String expression = toCode(condition, m);
		String nexpr = negate(expression);
		if(!precondition.isBlank()) {
			expression = '('+precondition + " && " +  expression+')';
			nexpr = '('+precondition + " && " +  nexpr+')';
		}
		var el = cloneMap(m);
		// add branches for all new conditions after the body has been executed
		branches.addAll(getConditions(then, m, expression));
		// check if it has an else
		if (tree.children.size() == 3) {
			branches.addAll(getConditions(tree.children.get(2), el, nexpr));
		}
		return branches;
	}

	/**
	 * @param tree
	 * @param variables
	 * @param precondition
	 * @param branches
	 * @return
	 */
	private static List<Pair<String, Map<String, String>>> getConditionsWhile(ASTTree tree,
			Map<String, String> variables, String precondition, List<Pair<String, Map<String, String>>> branches) {
		var head = tree.children.getFirst();
		ASTTree condition = null;
		for (var t : head.children) {

			if (t.tag.equals("condition")) {
				condition = t;
			}
		}
		// the first part after the head must be the body
		var body = tree.children.get(1);
		List<Pair<String,Map<String, String>>> nbranches = new LinkedList<Pair<String,Map<String,String>>>();
		nbranches.add(new Pair<String,Map<String,String>>(precondition, variables));
		for (int i = 0; i < loopDepth; i++) {
			//Save new branches to nbranches, move nbranches to temp to work only with the newest branches
			var temp= nbranches;
			nbranches = new LinkedList<Pair<String,Map<String,String>>>();
			for(var branch : temp) {
				String expression = toCode(condition.children.getFirst(), branch.second());
				String nexpr = negate(expression);
				if(!branch.first().isBlank()) {
					expression = '('+branch.first() + " && " +  expression+')';
					nexpr = '('+branch.first() + " && " +  nexpr+')';
				}
				branches.add(new Pair<String, Map<String,String>>(nexpr, cloneMap(branch.second())));
				//nbranches.add(new Pair<String, Map<String,String>>(expression, variables));
				// add branches for all new conditions after the body has been executed
				var nbr= getConditions(body, branch.second(), expression);
				nbranches.addAll(nbr);
			}
			branches.addAll(nbranches);
		}
		// finished evaluating the loop end recursion to let the higher order continue
		// the program
		return branches;
	}

	/**
	 * @param tree
	 * @param variables
	 * @param precondition
	 * @param branches
	 * @return
	 */
	private static List<Pair<String, Map<String, String>>> getConditionsFor(ASTTree tree, Map<String, String> variables,
			String precondition, List<Pair<String, Map<String, String>>> branches) {
		var head = tree.children.getFirst();
		ASTTree condition = null, update = null;
		ASTTree init = null;
		for (var t : head.children) {
			if (t.tag.equals("init")) {
				init = t;
			}
			if (t.tag.equals("condition")) {
				condition = t;
			}
			if (t.tag.equals("update")) {
				update = t;
			}

		}
		var body = tree.children.get(1);
		List<Pair<String,Map<String, String>>> nbranches = new LinkedList<Pair<String,Map<String,String>>>();
		if (init != null) {
			for (var in : init.children) {
				toCode(in, variables);
			}
		}
		nbranches.add(new Pair<String,Map<String,String>>(precondition, variables));
		for (int i = 0; i < loopDepth; i++) {
			//Save new branches to nbranches, move nbranches to temp to work only with the newest branches
			var temp= nbranches;
			nbranches = new LinkedList<Pair<String,Map<String,String>>>();
			for(var branch : temp) {
				String expression = toCode(condition.children.getFirst(), branch.second());
				String nexpr = negate(expression);
				if(!branch.first().isBlank()) {
					expression = '('+branch.first() + " && " +  expression+')';
					nexpr = '('+branch.first() + " && " +  nexpr+')';
				}
				branches.add(new Pair<String, Map<String,String>>(nexpr, cloneMap(branch.second())));
				//nbranches.add(new Pair<String, Map<String,String>>(expression, variables));
				// add branches for all new conditions after the body has been executed
				var nbr= getConditions(body, branch.second(), expression);
				if (update != null) {
					for (var u : update.children) {
						for(var b: nbr)
						toCode(u, b.second());
					}
				}
				nbranches.addAll(nbr);
			}
			branches.addAll(nbranches);
		}
		// finished evaluating the loop end recursion to let the higher order continue
		// the program
		return branches;
	}
	
	//currently does not remove 
	public static List<Pair<String,Map<String, String>>> removeConstantAndDuplicateConditions(List<Pair<String,Map<String, String>>>conditions){
		return conditions.stream().filter(
				s -> !s.first().matches("([\\^|\\(| ][a-zA-Z_]+[$| |\\)|])|^[a-zA-Z_]+")
		).distinct().toList();
	}
	

	public static <X, Y> Map<X, Y> cloneMap(Map<X, Y> origin) {
		var map = new HashMap<X, Y>(origin.size());
		for (var key : origin.keySet()) {
			map.put(key, origin.get(key));
		}
		return map;
	}

	/**
	 * Negates a condition by checking the outermost boolean evaluation
	 *
	 * @param condition a well formed condition in which every part is checked with
	 *                  parentheses
	 * @return a negation of the given condition
	 */
	public static String negate(String condition) {
		if (condition.charAt(0) == '!') {
			return condition.substring(1);
		}
		String outermost = "";
		int temp = 0;
		String operator = "";
		String left = "", right = "";
		boolean first = true;
		int start = 0, end = 0;
		int parts = 0;
		for (int i = 0; i < condition.length(); i++) {
			if (condition.charAt(i) == '(') {
				temp++;
				if (temp == 1) {
					end = i;
					if (start != 0) {
						operator = condition.substring(start, end).trim();
					}
				}
			}
			if (condition.charAt(i) == ')') {
				temp--;
				if (temp == 0) {
					start = i + 1;
					parts++;
					if (first) {
						left = condition.substring(0, start);
					} else {
						right = condition.substring(end, condition.length());
					}
				}
			}
		}
		if (parts == 1) {
			if (condition.charAt(0) == '(' && condition.charAt(condition.length()-1) == ')') {
				int opind = condition.indexOf('=');
				if(opind == -1)
					opind = condition.indexOf('<');
				if(opind == -1)
					opind = condition.indexOf('>');
				left = condition.substring(0, opind-1);
				operator = condition.substring(opind-1, opind+2);
				right = condition.substring(opind+2);
			}
		}
		right = right.trim();
		operator = operator.trim();
		left = left.trim();
		switch (operator) {
		case "==" -> operator = "!=";
		case "!=" -> operator = "==";
		case "<=" -> operator = ">";
		case ">=" -> operator = "<";
		case "<" -> operator = ">=";
		case ">" -> operator = "<=";
		default -> {return "!(" + condition + ")";}
		}
		return left + ' ' + operator + ' ' + right;

	}
	
	//private static ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
	
	
	
	public static String evaluate(String expression, Set<String> variables) {
			
		 return ConditionUtils.toCondition(expression, variables).toString();
	}

	private static final Set<String> TOCODE_EVALUATEABLE = Set.of(
			"lit", "var", "unary", "binary", "mc", "ternary", "cast", "index", "assign");

	/**
	 * Only intende to be used within a methode only data flow not control flow
	 * works with:lit, var, unary, binary, mc, ternary, cast, index, assign and block recursive
	 * 
	 * @param tree    the code to be truned to string
	 * @param replace variables to be replaced
	 * @return a String representation of the given code with the variables being
	 *         replaced by the Map
	 */
	private static String toCode(ASTTree tree, Map<String, String> replace) {
		// If nothing return empty
		if (tree.tag == null || tree.tag.isBlank()) {
			return "";
		}
		// if literal return the literal
		if (tree.tag.equals("lit")) {
			if (tree.name.matches("[a-zA-z]+")) { // It#s a string
				return '\"' + tree.name + '\"';
			}
			return tree.name;
		}
		// get type of current tree type
		switch (tree.tag) {
		case "var" -> {
			// Is it already known?
			if (!replace.containsKey(tree.name)) {
				replace.put(tree.name, "");
			}
			// is an assignment?
			if (!tree.children.isEmpty()) {
				// A value is assigned
				replace.put(tree.name, toCode(tree.children.getFirst(), replace));// is only allowed to have one child
			}
			StringBuilder sb = new StringBuilder();
			if (tree.children.size() > 1) { // Sanitycheck
				for(var child : tree.children) {
					sb.append(toCode(child, replace));
				}
			}
			
			return replace.get(tree.name)+sb.toString();
		}
		case "unary" -> {
			if (tree.name.contains("COMPLEMENT")) {
				replace.put(tree.children.getFirst().name, main.Data.KIND_TO_OPERATOR.get(tree.name) + '('
						+ (toCode(tree.children.getFirst(), replace)) + ')');
				return replace.get(tree.children.getFirst().name);
			}
			String change = "";
			if (tree.name.contains("DECREMENT")) {
				change = " - 1";
			} else {
				change = " + 1";
			}
			if (tree.name.startsWith("PRE")) {
				replace.put(tree.children.getFirst().name,
						'(' + (toCode(tree.children.getFirst(), replace)) + change + ')');
				return replace.get(tree.children.getFirst().name);
			} else {
				String temp = replace.get(tree.children.getFirst().name);
				replace.put(tree.children.getFirst().name,
						'(' + (toCode(tree.children.getFirst(), replace)) + change + ')');
				return temp;
			}
		}
		case "binary" -> {
			String op = main.Data.KIND_TO_OPERATOR.get(tree.name.replace("_ASSIGNMENT", ""));
			return '(' + toCode(tree.children.getFirst(), replace) + ' ' + op + ' '
					+ toCode(tree.children.getLast(), replace) + ')';
		}
		case "block" -> {
			StringBuilder sb = new StringBuilder("{+\n");
			for (ASTTree t : tree.children) {
				sb.append(toCode(t, replace));
				sb.append('\n');
			}
			return sb.toString();
		}
		case "mc" -> {
			StringBuilder sb = new StringBuilder();
			if (tree.type != null) { // reference call
				if (replace.containsKey(tree.type)) {
					sb.append('(' + replace.get(tree.type) + ").");
				} else {
					sb.append(tree.type + ".");
				}
			}
			sb.append(tree.name + "(");
			for (ASTTree t : tree.children) {
				if(t.tag.equals("target")) {
					sb.insert(0, toCode(t, replace)+'.');
					continue;
				}
				sb.append(toCode(t, replace));
				sb.append(',');
			}
			if (sb.charAt(sb.length() - 1) == ',') {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append(")");
			return sb.toString();
		}
		case "ternary" -> {
			StringBuilder sb = new StringBuilder();
			sb.append("((");
			sb.append(toCode(tree.children.getFirst(), replace));
			sb.append(")?");
			sb.append(toCode(tree.children.get(1), replace));
			sb.append(":");
			sb.append(toCode(tree.children.get(2), replace));
			sb.append(')');
			return sb.toString();
		}
		case "cast" ->{
			StringBuilder sb = new StringBuilder();
			sb.append("((");
			sb.append(tree.name);
			sb.append(") ");
			sb.append(toCode(tree.children.getFirst(), replace));
			sb.append(") ");
			return sb.toString();
		}
		case "index" ->{
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(toCode(tree.children.getFirst(), replace));
			sb.append("]");
			return sb.toString();
		}
		case "assign" ->{
			StringBuilder sb = new StringBuilder();
			sb.append(tree.name);
			sb.append(" = ");
			String result = toCode(tree.children.getFirst(), replace);
			replace.put(tree.name, result);
			sb.append(result);
			return sb.toString();
		}
		case "target"->{
			if(replace.containsKey(tree.name))
				return replace.get(tree.name);
			return tree.name;
		}
		case "dimensions" ->{
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(toCode(tree.children.getFirst(), replace));
			sb.append("]");
			return sb.toString();
		}
		default -> {
		}

		}
		if(!tree.children.isEmpty())
			return toCode(tree.children.getFirst(), replace);
		return "nuthing" + tree.toString();
	}

	
	private static List<DynamicTestcase> conditionsToTestcases(Map<String,Class<?>> parameters, Iterable<String> conditions, String methode) {
		List<DynamicTestcase> cases = new LinkedList<DynamicTestcase>();
		for(String condition : conditions) {
			cases.add(conditionToTestcases(parameters, condition, methode));
		}
		return cases;
	}
	private static DynamicTestcase conditionToTestcases(Map<String,Class<?>> parameters, String condition, String methode) {
		return new DynamicTestcase(methode + generateParameters(condition, parameters)+")");
	}

	@Override
	public List<DynamicTestcase> generateTestcases() {
		this.testcases = new LinkedList<DynamicTestcase>();
		List<ASTTree> methods = tree.getTreesWithTag("method");
		for (ASTTree t : methods) {
			testcases.addAll(generateTestcases( t, t.name, ""));
		}
		return this.testcases;
	}
	
	public static String generateParameters(String condition, Map<String,Class<?>> parameters) {
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
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
	@Override
	public List<DynamicTestcase> loadFromDirectory(Path pathToDirectory) {

		FileVisitor<Path> files = new FileVisitor<>() {

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
			Main.debug(e);
		}
		return testcases;
	}

}
