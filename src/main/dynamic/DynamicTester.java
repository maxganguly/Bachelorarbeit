package main.dynamic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.Data;
import main.Pair;

/**
 * 
 */
public class DynamicTester extends main.Tester<DynamicTestcase>{

	boolean cacheTestcases;
	Map<DynamicTestcase,Pair<Object,String>> cache;
	Executor solution, test;
	List<DynamicTestcase> testcases;
	PrintStream ps;
	BufferedReader br;
	
	static final Set<Class<?>> primitives = Set.of(
			byte[].class,
			short[].class,
			int[].class,
			long[].class,
			float[].class,
			double[].class,
			char[].class,
			boolean[].class);
	
	public DynamicTester(Executor solution, Executor test) {
		this.solution = solution;
		this.test = test;
		this.cacheTestcases = true;
		this.cache = new HashMap<DynamicTestcase,Pair<Object,String>>();
		try {
			PipedInputStream pis = new PipedInputStream();
			PipedOutputStream pos = new PipedOutputStream(pis);
			ps = new PrintStream(pos);
			this.br = new BufferedReader(new InputStreamReader(pis));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		testcases = new LinkedList<DynamicTestcase>();
	}
	public DynamicTester(Executor solution, Executor test, String path) {
		this(solution, test);
		if( path == null || path.isBlank())
			return;
		try {
			BufferedReader br = new BufferedReader(new FileReader(Path.of(path).toAbsolutePath().toString()));
			while(br.ready()) {
				String line = br.readLine();
				testcases.add(new DynamicTestcase(line));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DynamicTester(Executor solution, Executor test, String path, PrintStream ps, BufferedReader br) {
		this.cacheTestcases = true;
		this.solution = solution;
		this.test = test;
		this.ps = ps;
		this.br = br;
		this.cache = new HashMap<DynamicTestcase,Pair<Object,String>>();
	}
	
	/**
	 * Runs and Analyzes all Testcases given in the Constructor
	 * @return A List of all Analyzed Testcases
	 * @throws MethodNotFoundException if the Methode does not exist in the solution
	 */
	public List<Pair<Result,String>> runAndAnalyzeTestcases() throws MethodNotFoundException{
		return analyzeTestcases(runTestcases(false));
	}
	
	/**
	 * Runs and Analyzes all Testcases given in the Constructor
	 * @return A List of all Analyzed Testcases
	 */
	public List<Pair<Result,String>> runAndAnalyzeTestcases_IgnoreMethodNotFound() {
		try {
		return analyzeTestcases(runTestcases(true));
		} catch (MethodNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Runs all Testcases found in the file given in the constructor
	 * @param ignoreMethodNotFound if the MethodNotFoundException should fail quietly
	 * @return A List of all Results of all Testcases
	 * @throws MethodNotFoundException if ignoreMethodNotFound is false and the Methode of a given Testcase does not exist in the solution
	 */
	public List<Result> runTestcases(boolean ignoreMethodNotFound) throws MethodNotFoundException {
		List<Result> results = new LinkedList<DynamicTester.Result>();
		if(ps != null)
		System.setOut(ps);
		for( DynamicTestcase dt : testcases) {
			try {
				results.add(runTestcase(dt));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MethodNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		System.setOut(Data.SYSOUT);
		return results;
	}
	
	/**
	 * Analyzes all given Results with the analyzeTestcase methode
	 * @param testcases a List of all Results
	 * @return an Result:String Pair which gives a textual analyzation of the result
	 */
	public List<Pair<Result,String>> analyzeTestcases(List<Result> testcases){
		return testcases.stream().map(r -> new Pair<Result,String>(r,analyzeTestcase(r))).toList();
	}
	
	/**
	 * Retunrs a textual analyse of the given result, comparing the return value and the output 
	 * @param testcase the Result of the Testcase
	 * @return A textual analysis of the result
	 */
	public String analyzeTestcase(Result testcase) {
		String test = testcase.testcase.testcase().first();
		if(isEqual(testcase.expectedResult,testcase.gottenResult) && 
				isEqual(testcase.expectedOutput, testcase.gottenOutput))
			return "Testcase: "+ test +" successfull";
		StringBuilder sb = new StringBuilder("Testcase: ");
		sb.append(test);
		sb.append(" failed");
		if(!isEqual(testcase.expectedResult,testcase.gottenResult)) {
			sb.append(" expected: \"");
			sb.append(toString(testcase.expectedResult));
			sb.append("\" but recieved: \"");
			sb.append(toString(testcase.gottenResult));
			sb.append("\"");
		}
		if(!isEqual(testcase.expectedOutput,testcase.gottenOutput)) {
			sb.append(" expected to print: ");
			sb.append(testcase.expectedOutput);
			sb.append(" but printed: ");
			sb.append(testcase.gottenOutput);
		}
		return sb.toString();
	}
	
	/**
	 * Formats a given primitive or primitive Array to String, works only up to 3 Dimensions
	 * @param o the Object to be mate to String
	 * @return a String representation of the given Object
	 */
	public String toString(Object o) {
		if(o == null)
			return "null";
		if(o.getClass().isPrimitive())
			return ""+o;
		if (o.getClass().isArray()) {
			if(isPrimitiveArray(o)) {
				//char type = o1.getClass().getName().charAt(o1.getClass().getName().length()-1);
				switch (o.getClass().getName()) {//Dont know why I cant just compare the classes
				case "[B": return Arrays.toString((byte[])o);
				case "[S": return Arrays.toString((short[])o);
				case "[I": return Arrays.toString((int[])o);
				case "[J": return Arrays.toString((long[])o);
				case "[F": return Arrays.toString((float[])o);
				case "[D": return Arrays.toString((double[])o);
				case "[C": return Arrays.toString((char[])o);
				case "[Z": return Arrays.toString((boolean[])o);
				default:
					throw new IllegalArgumentException("Unexpected value: \"" + o.getClass().getName()+"\"");
				}
			}
			//Currently it only allows primitive Datatypes, so if it isn't a primitive dt It must be an Array of (multidimensional array)
			//Currently allows up to 3d arrays to be compared
			switch (o.getClass().getName()) {
			case "[[B": return Arrays.toString((byte[][])o);
			case "[[S": return Arrays.toString((short[][])o);
			case "[[I": return Arrays.toString((int[][])o);
			case "[[J": return Arrays.toString((long[][])o);
			case "[[F": return Arrays.toString((float[][])o);
			case "[[D": return Arrays.toString((double[][])o);
			case "[[C": return Arrays.toString((char[][])o);
			case "[[Z": return Arrays.toString((boolean[][])o);
			case "[[[B": return Arrays.toString((byte[][][])o);
			case "[[[S": return Arrays.toString((short[][][])o);
			case "[[[I": return Arrays.toString((int[][][])o);
			case "[[[J": return Arrays.toString((long[][][])o);
			case "[[[F": return Arrays.toString((float[][][])o);
			case "[[[D": return Arrays.toString((double[][][])o);
			case "[[[C": return Arrays.toString((char[][][])o);
			case "[[[Z": return Arrays.toString((boolean[][][])o);
			default:
				throw new IllegalArgumentException("Unexpected value: " + o.getClass().getName());
			}
		}
		return o.toString(); 		
	}
	/**
	 * Checks if two objects are equals
	 * Arrays work only on primitives and up to three dimensions
	 * @param o1
	 * @param o2
	 * @return true if the Objects are equals or both are null
	 */
	public static boolean isEqual(Object o1, Object o2) {
		if(o1 == null ^ o2 == null)
			return false;
		if(o1 == null && o2 == null)
			return true;
		if(o1.getClass() != o2.getClass())
			return false;
		if (o1.getClass().isArray()) {
			if(isPrimitiveArray(o1)) {
				//char type = o1.getClass().getName().charAt(o1.getClass().getName().length()-1);
				switch (o1.getClass().getName()) {//Dont know why I cant just compare the classes
				case "[B": return Arrays.equals((byte[])o1, (byte[])o2);
				case "[S": return Arrays.equals((short[])o1, (short[])o2);
				case "[I": return Arrays.equals((int[])o1, (int[])o2);
				case "[J": return Arrays.equals((long[])o1, (long[])o2);
				case "[F": return Arrays.equals((float[])o1, (float[])o2);
				case "[D": return Arrays.equals((double[])o1, (double[])o2);
				case "[C": return Arrays.equals((char[])o1, (char[])o2);
				case "[Z": return Arrays.equals((boolean[])o1, (boolean[])o2);
				default:
					throw new IllegalArgumentException("Unexpected value: \"" + o1.getClass().getName()+"\"");
				}
			}
			//Currently it only allows primitive Datatypes, so if it isn't a primitive dt It must be an Array of (multidimensional array)
			//Currently allows up to 3d arrays to be compared
			//MAybe cast to Object[]
			switch (o1.getClass().getName()) {
			case "[[B": return Arrays.deepEquals((byte[][])o1, (byte[][])o2);
			case "[[S": return Arrays.deepEquals((short[][])o1, (short[][])o2);
			case "[[I": return Arrays.deepEquals((int[][])o1, (int[][])o2);
			case "[[J": return Arrays.deepEquals((long[][])o1, (long[][])o2);
			case "[[F": return Arrays.deepEquals((float[][])o1, (float[][])o2);
			case "[[D": return Arrays.deepEquals((double[][])o1, (double[][])o2);
			case "[[C": return Arrays.deepEquals((char[][])o1, (char[][])o2);
			case "[[Z": return Arrays.deepEquals((boolean[][])o1, (boolean[][])o2);
			case "[[[B": return Arrays.deepEquals((byte[][][])o1, (byte[][][])o2);
			case "[[[S": return Arrays.deepEquals((short[][][])o1, (short[][][])o2);
			case "[[[I": return Arrays.deepEquals((int[][][])o1, (int[][][])o2);
			case "[[[J": return Arrays.deepEquals((long[][][])o1, (long[][][])o2);
			case "[[[F": return Arrays.deepEquals((float[][][])o1, (float[][][])o2);
			case "[[[D": return Arrays.deepEquals((double[][][])o1, (double[][][])o2);
			case "[[[C": return Arrays.deepEquals((char[][][])o1, (char[][][])o2);
			case "[[[Z": return Arrays.deepEquals((boolean[][][])o1, (boolean[][][])o2);
			default:
				throw new IllegalArgumentException("Unexpected value: " + o1.getClass().getName());
			}
		}
		return o1.equals(o2);
	}
	
	/**
	 * Checks if the given Object is an Onedimensional primitive Array
	 * @param o
	 * @return
	 */
	public static boolean isPrimitiveArray(Object o) {
		return primitives.contains(o.getClass());
	}
	
	/**
	 * Runs a singe Testcase given as String and returns the Result
	 * @param testcase the testcase to be run
	 * @return a Result containing the testcase the return of the solution and the test and the output of the solution and the test
	 * @throws IOException if the System read does not work (unlikely)
	 * @throws MethodNotFoundException if the Method in the testcase does not exist in the solution
	 */
	public Result runTestcase(DynamicTestcase testcase) throws IOException, MethodNotFoundException {
		
		Object returnSolution = null;
		String outSolution = null;
		Object returnTest;
		String outTest;
		boolean ranSolution = false;
		try {
			if(this.cacheTestcases) {
				if(this.cache.keySet().contains(testcase)) {
					var p = this.cache.get(testcase);
					returnSolution = p.first();
					outSolution = p.second();
					ranSolution = true;
				}else {
					returnSolution = solution.runMethod(testcase.name, testcase.params);
					outSolution = readall();
					ranSolution = true;
					this.cache.put(testcase, new Pair<Object,String>(returnSolution,outSolution));	
				}
			}
			returnTest = test.runMethod(testcase.name, testcase.params);
			outTest = readall();
		} catch (MethodNotFoundException mnfe ) {
			if(!ranSolution)
				throw mnfe;
			return new Result(testcase, false, returnSolution, null, outSolution, null);
		}
		return new Result(testcase, isEqual(returnSolution, returnTest) && isEqual(outSolution, outTest), returnSolution, returnTest, outSolution, outTest);
	}
	
	/**
	 * Read all from the Buffered Reader
	 * @return All the String currently in the BufferedReader
	 * @throws IOException if the BufferedReader is closed or has other problems
	 */
	private String readall() throws IOException {
		StringBuilder sb = new StringBuilder(); 
		while(br.ready()) {
			sb.append(br.readLine());
		}
		return sb.toString();
	}
	
	public record Result(DynamicTestcase testcase, boolean succesfull, Object expectedResult, Object gottenResult, String expectedOutput, String gottenOutput){}

	@Override
	public Pair<String, Integer> test(DynamicTestcase testcase) {
		// TODO Auto-generated method stub
		Result r;
		try {
			r = runTestcase(testcase);
		} catch (IOException e) {
			e.printStackTrace();
			return new Pair<String,Integer>(e.toString(), -1);
		} catch (MethodNotFoundException e) {
			e.printStackTrace();
			return new Pair<String,Integer>(e.toString(), -1);
		}
		String a = analyzeTestcase(r);
		return new Pair<String, Integer>(a, 
				r.succesfull? r.testcase().score : 0 );
	}
	public boolean isCacheTestcases() {
		return cacheTestcases;
	}
	public void setCacheTestcases(boolean cacheTestcases) {
		this.cacheTestcases = cacheTestcases;
	}
	public Executor getSolution() {
		return solution;
	}
	public void setSolution(Executor solution) {
		this.solution = solution;
		this.cache = new HashMap<DynamicTestcase,Pair<Object,String>>();
	}
	public Executor getTest() {
		return test;
	}
	public void setTest(Executor test) {
		this.test = test;
	}
	@Override
	public List<Pair<String, Integer>> runAllTestcases(Path p) {
		Executor temp = this.test;
		this.test = new Executor(p);
		var result = runAllTestcases();
		this.test = temp;
		return result;
	}
	@Override
	public Pair<String, Integer> test(Path p, DynamicTestcase testcase) {
		Executor temp = this.test;
		this.test = new Executor(p);
		var result = test(testcase);
		this.test = temp;
		return result;
	}
	
	
}
