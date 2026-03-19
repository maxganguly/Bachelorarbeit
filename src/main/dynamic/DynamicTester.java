package main.dynamic;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.function.Function;

import main.Main;

/**
 * 
 */
public class DynamicTester {

	Executor solution, test;
	String pathToTestcases;
	PrintStream ps;
	BufferedReader br;
	static final Map<String, Function<String, Object>> mapping = Map.of(
			"byte", s -> (Object)Byte.valueOf(s),
			"short", s -> (Object)Short.valueOf(s),
			"char", s -> (Object)Character.valueOf(s.charAt(0)),
			"int", s -> (Object)Integer.valueOf(s),
			"float", s -> (Object)Float.valueOf(s),
			"long", s -> (Object)Long.valueOf(s),
			"double", s -> (Object)Double.valueOf(s),
			"boolean", s -> (Object)Boolean.valueOf(s),
			"String", s -> (Object)s);
	
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
	
	public List<Pair<Result,String>> runAndAnalyzeTestcases(){
		return analyzeTestcases(runTestcases());
	}
	
	
	public List<Result> runTestcases() {
		BufferedReader br;
		List<Result> results = new LinkedList<DynamicTester.Result>();
		if(ps != null)
		System.setOut(ps);
		try {
			br = new BufferedReader(new FileReader(Path.of(pathToTestcases).toAbsolutePath().toString()));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				results.add(runTestcase(line));
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.setOut(Main.SYSOUT);
		return results;
	}
	
	public List<Pair<Result,String>> analyzeTestcases(List<Result> testcases){
		return testcases.stream().map(r -> new Pair<Result,String>(r,analyzeTestcase(r))).toList();
	}
	
	public String analyzeTestcase(Result testcase) {
		String test = testcase.method.substring(testcase.method.indexOf(' ')+1, testcase.method.length());
		if(isEqual(testcase.expectedResult,testcase.gottenResult) && 
				isEqual(testcase.expectedOutput, testcase.gottenOutput))
			return "Testcase: "+ test +" successfull";
		StringBuilder sb = new StringBuilder("Testcase: ");
		sb.append(test);
		if(!isEqual(testcase.expectedResult,testcase.gottenResult)) {
			sb.append(" expected: ");
			sb.append(testcase.expectedResult);
			sb.append(" but recieved: ");
			sb.append(testcase.gottenResult);
			sb.append(" | ");
		}
		if(!isEqual(testcase.expectedOutput,testcase.gottenOutput)) {
			sb.append(" expected to print: ");
			sb.append(testcase.expectedOutput);
			sb.append(" but printed: ");
			sb.append(testcase.gottenOutput);
		}
		return sb.toString();
	}
	
	public boolean isEqual(Object o1, Object o2) {
		if(o1 == null ^ o2 == null)
			return false;
		if(o1 == null && o2 == null)
			return true;
		return o1.equals(o2);
	}
	public Result runTestcase(String testcase) throws IOException {
		Object[] params = Arrays.stream(testcase.substring(testcase.indexOf('(')+1, testcase.lastIndexOf(')')).split(", ")).map(s -> getParam(s)).toArray();
		String name = testcase.substring(testcase.indexOf(' ')+1, testcase.indexOf('('));
		Object returnSolution = solution.runMethod(name, params);
		String outSolution = readall();
		Object returnTest = test.runMethod(name, params);
		String outTest = readall();
		return new Result(testcase, returnSolution, returnTest, outSolution, outTest);
	}
	
	private String readall() throws IOException {
		StringBuilder sb = new StringBuilder(); 
		while(br.ready()) {
			sb.append(br.readLine());
		}
		return sb.toString();
	}
	
	public Object getParam(String param) {
		String type = param.substring(0, param.indexOf(' '));
		String value = param.substring(param.indexOf(' ')+1);
		if(type.equals("null"))
			return null;
		if(mapping.containsKey(type)) {
			return mapping.get(type).apply(value);
		}
		int dimensions = type.length() - type.replace("[]", "|").length();
		int[] depth = new int[] {0};
		return null;
	}
	//the depth is saved at index 0 of the depth array
	private Object[] getArray(String content, int[] depth) {
		return null;
	}
	
	public record Result(String method, Object expectedResult, Object gottenResult, String expectedOutput, String gottenOutput){}

	public class Pair<F,S> {
		public final F first;
		public final S second;
		public Pair(F first, S second) {
			this.first = first;
			this.second = second;
		}
		
	}
}
