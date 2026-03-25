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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import main.Main;
import main.Pair;

/**
 * 
 */
public class DynamicTester {

	Executor solution, test;
	String pathToTestcases;
	PrintStream ps;
	BufferedReader br;
	static final Map<String, Function<String, Object>> mapping = Map.of(
			"byte", s -> Byte.valueOf(s.trim()),
			"short", s -> Short.valueOf(s.trim()),
			"char", s -> Character.valueOf(s.charAt(0)),
			"int", s -> Integer.valueOf(s.trim()),
			"float", s -> Float.valueOf(s.trim()),
			"long", s -> Long.valueOf(s.trim()),
			"double", s -> Double.valueOf(s.trim()),
			"boolean", s -> Boolean.valueOf(s.trim()),
			"String", s -> s);
	static final Set<Class<?>> primitives = Set.of(
			byte[].class,
			short[].class,
			int[].class,
			long[].class,
			float[].class,
			double[].class,
			char[].class,
			boolean[].class);
	public DynamicTester(Executor solution, Executor test, String path) {
		this.pathToTestcases = path;
		this.solution = solution;
		this.test = test;
		try {
			PipedInputStream pis = new PipedInputStream();
			PipedOutputStream pos = new PipedOutputStream(pis);
			ps = new PrintStream(pos);
			br = new BufferedReader(new InputStreamReader(pis));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DynamicTester(Executor solution, Executor test, String path, PrintStream ps, BufferedReader br) {
		this.pathToTestcases = path;
		this.solution = solution;
		this.test = test;
		this.ps = ps;
		this.br = br;
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
	@SuppressWarnings("resource")
	public List<Result> runTestcases(boolean ignoreMethodNotFound) throws MethodNotFoundException {
		BufferedReader br;
		List<Result> results = new LinkedList<DynamicTester.Result>();
		if(ps != null)
		System.setOut(ps);
		try {
			br = new BufferedReader(new FileReader(Path.of(pathToTestcases).toAbsolutePath().toString()));
			String line = br.readLine();
			while (line != null) {
				try {
				results.add(runTestcase(line));
				line = br.readLine();
				} catch (MethodNotFoundException mnfe) {
					if(!ignoreMethodNotFound)
						throw mnfe;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.setOut(Main.SYSOUT);
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
		String test = testcase.method.substring(testcase.method.indexOf(' ')+1, testcase.method.lastIndexOf(')')+1);
		if(isEqual(testcase.expectedResult,testcase.gottenResult) && 
				isEqual(testcase.expectedOutput, testcase.gottenOutput))
			return "Testcase: "+ test +" successfull";
		StringBuilder sb = new StringBuilder("Testcase: ");
		sb.append(test);
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
	public Result runTestcase(String testcase) throws IOException, MethodNotFoundException {
		String p = testcase.substring(testcase.indexOf('(')+1, testcase.lastIndexOf(')'));
		Object[] params;
		if(p.isBlank()) {
			params = null;
		}else
			params = Arrays.stream(p.split(", ")).map(s -> getParam(s)).toArray();
		String name = testcase.substring(testcase.indexOf(' ')+1, testcase.indexOf('('));
		Object returnSolution = null;;
		String outSolution = null;
		Object returnTest;
		String outTest;
		boolean ranSolution = false;
		try {
			returnSolution = solution.runMethod(name, params);
			outSolution = readall();
			ranSolution = true;
			returnTest = test.runMethod(name, params);
			outTest = readall();
		} catch (MethodNotFoundException mnfe ) {
			if(!ranSolution)
				throw mnfe;
			return new Result(testcase, returnSolution, null, outSolution, null);
		}
		return new Result(testcase, returnSolution, returnTest, outSolution, outTest);
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
	
	/**
	 * Gets the Parameter from String
	 * @param param the parameter as String
	 * @return the parameter in it's correct Datatype cast to Object
	 */
	public static Object getParam(String param) {
		if(param == null || param.isBlank())
			return null;
		String type = param.substring(0, param.indexOf(' '));
		String value = param.substring(param.indexOf(' ')+1);
		if(type.equals("null"))
			return null;
		if(mapping.containsKey(type)) {
			return mapping.get(type).apply(value);
		}
		int dimensions = type.length() - type.replace("[]", "|").length();
		type = type.replace("[]", "");
		if(!mapping.containsKey(type)) {
			throw new IllegalArgumentException("No valid Datatype found:" +param);
		}
		Object parm = getArray(value, dimensions, type);
		return parm;
	}
	
	/**
	 * Generates an One-dimensional Array from the given String and type 
	 * @param c the values of the array {x, y, z}
	 * @param type the type of the array e.g int
	 * @return an Onedimensional array cast to Object
	 */
	private static Object get1DArray(String c, String type) {
		String content = c.substring(1, c.length()-1);
			switch(type) { //Oneliners simple
				case "int": return Arrays.stream(content.split(",")).map(mapping.get(type)).mapToInt(i -> ((Integer)i).intValue()).toArray();
				case "long": return Arrays.stream(content.split(",")).map(mapping.get(type)).mapToLong(i -> ((Long)i).longValue()).toArray();
				case "double": return Arrays.stream(content.split(",")).map(mapping.get(type)).mapToDouble(i -> ((Double)i).doubleValue()).toArray();
				case "String": return content.split(",");
				default: break;
			}
			if(type.equals("byte") || type.equals("short")) {
				int[] arr = Arrays.stream(content.split(",")).map(mapping.get(type)).mapToInt(i -> ((Integer)i).intValue()).toArray();
				if (type.equals("byte")) {
					byte[] barr = new byte[arr.length];
					for(int i = 0; i < arr.length;i++) {
						barr[i] = (byte) arr[i];
					}
					return barr;
				} else {
					short[] barr = new short[arr.length];
					for(int i = 0; i < arr.length;i++) {
						barr[i] = (short) arr[i];
					}
					return barr;
				}
			} else if(type.equals("float")) {
				double[] arr = Arrays.stream(content.split(",")).map(mapping.get(type)).mapToDouble(i -> ((Double)i).doubleValue()).toArray();
				float[] barr = new float[arr.length];
				for(int i = 0; i < arr.length;i++) {
					barr[i] = (float) arr[i];
				}
				return barr;
			} else if(type.equals("boolean")) {
				Boolean[] arr = (Boolean[]) Arrays.stream(content.split(",")).map(mapping.get(type)).toArray();
				boolean[] barr = new boolean[arr.length];
				for(int i = 0; i < arr.length;i++) {
					barr[i] = arr[i].booleanValue();
				}
				return barr;
			} else {
				throw new IllegalArgumentException("No valid datatype: "+ type);
				//return null;
			}
	}
	
	/**
	 * Generates an Multidimensional array with the given content, dimensions and type
	 * currently only int arrays can be multidimensional
	 * @param c the content of the array 
	 * @param dimensions the amount dimensions of the array
	 * @param type the type of the underlying datatype 
	 * @return the multidimensional array cast to Object
	 */
	private static Object getArray(String c, int dimensions, String type) {
		if(dimensions == 1)
			return get1DArray(c, type);

		String content = c.substring(1, c.length()-1);
		StringBuilder temp = new StringBuilder();
		List<String> elements = new LinkedList<String>();
		int brackets = 0;
		for(int i = 0; i < content.length(); i++) {
			if(content.charAt(i) == '{') {
				if(brackets == 0) {
					temp = new StringBuilder();
				}
				brackets++;
			}
			if(content.charAt(i) == '}') {
				brackets--;
				if(brackets == 0) {
					temp.append('}');
					elements.add(temp.toString());
				}
			}
			if(brackets < 0)
				throw new IllegalArgumentException("Invalid Brackets at Array input: "+c);
			temp.append(content.charAt(i));
		}
		Object array;
		int[] i = {0};
		if (type.equals("int")) {
			if(dimensions == 2) {
				array = new int[elements.size()][];
				elements.stream().forEach(e -> {((int[][])array)[i[0]++] = (int[])getArray(e,dimensions-1,type);});
			} else if(dimensions == 3) {
				array = new int[elements.size()][][];
				elements.stream().forEach(e -> {((int[][][])array)[i[0]++] = (int[][])getArray(e,dimensions-1,type);});
			} else if(dimensions == 4) {
				array = new int[elements.size()][][][];
				elements.stream().forEach(e -> {((int[][][][])array)[i[0]++] = (int[][][])getArray(e,dimensions-1,type);});
			}else {
				throw new IllegalArgumentException("Arrays with more than 4 dimensions are currently not allowed, feel free to add them in the function DynamicTester.getArray");
			}
		}else {
			throw new IllegalArgumentException("Type: "+type+"not currently implemented");
		}
		
		return array;//elements.stream().map(s -> getArray(s, dimensions-1, type)).toArray();
		
	}
	/**
	//Does not work as it creates Wrapper[] which are unable to be casted
	//the depth is saved at index 0 of the depth array
	private static Object getArray(String c, int[] depth, String type) {
		List<String> elements = new LinkedList<String>();
		String content = c.substring(1, c.length()-1);
		StringBuilder temp = new StringBuilder();
		int brackets = 0;
		for(int i = 0; i < content.length(); i++) {
			if(content.charAt(i) == '{') {
				if(brackets == 0) {
					temp = new StringBuilder();
				}
				brackets++;
			}
			if(content.charAt(i) == '}') {
				brackets--;
				if(brackets == 0) {
					temp.append('}');
					elements.add(temp.toString());
				}
			}
			if(brackets < 0)
				throw new IllegalArgumentException("Invalid Brackets at Array input: "+c);
			temp.append(content.charAt(i));
		}
		depth[0]++;//Increase depth
		if(elements.size() == 0) {//Contains only "primitives"
			switch(type) { //Oneliners simple
				case "int": return Arrays.stream(content.split(",")).map(mapping.get(type)).mapToInt(i -> ((Integer)i).intValue()).toArray();
				case "long": return Arrays.stream(content.split(",")).map(mapping.get(type)).mapToLong(i -> ((Long)i).longValue()).toArray();
				case "double": return Arrays.stream(content.split(",")).map(mapping.get(type)).mapToDouble(i -> ((Double)i).doubleValue()).toArray();
				case "String": return content.split(",");
				default: break;
			}
			if(type.equals("byte") || type.equals("short")) {
				int[] arr = Arrays.stream(content.split(",")).map(mapping.get(type)).mapToInt(i -> ((Integer)i).intValue()).toArray();
				if (type.equals("byte")) {
					byte[] barr = new byte[arr.length];
					for(int i = 0; i < arr.length;i++) {
						barr[i] = (byte) arr[i];
					}
					return barr;
				} else {
					short[] barr = new short[arr.length];
					for(int i = 0; i < arr.length;i++) {
						barr[i] = (short) arr[i];
					}
					return barr;
				}
			} else if(type.equals("float")) {
				double[] arr = Arrays.stream(content.split(",")).map(mapping.get(type)).mapToDouble(i -> ((Double)i).doubleValue()).toArray();
				float[] barr = new float[arr.length];
				for(int i = 0; i < arr.length;i++) {
					barr[i] = (float) arr[i];
				}
				return barr;
			} else if(type.equals("boolean")) {
				Boolean[] arr = (Boolean[]) Arrays.stream(content.split(",")).map(mapping.get(type)).toArray();
				boolean[] barr = new boolean[arr.length];
				for(int i = 0; i < arr.length;i++) {
					barr[i] = arr[i].booleanValue();
				}
				return barr;
			} else {
				throw new IllegalArgumentException("No valid datatype: "+ type);
				//return null;
			}
		}
		
		depth[0] -= elements.size()-1;	//So that only one of the elements increases the depth
		
		return elements.stream().map(s -> getArray(s, depth, type)).toArray();
	}

	*/
	public record Result(String method, Object expectedResult, Object gottenResult, String expectedOutput, String gottenOutput){}
}
