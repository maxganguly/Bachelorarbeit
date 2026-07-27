package main.dynamic;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import main.Data;
import main.Pair;
import main.Testcase;
/**
 * Testcase for dynamic Tests
 */
public class DynamicTestcase extends Testcase {

	String name;
	Object returntype;
	Object[] params;

	/**
	 * Constructor
	 * @param testcase expected something like: <br > int test(int[] {1,2,3},String abba, int 12) 3
	 */
	public DynamicTestcase(String testcase) {
		super(getMethodcall(testcase),getScore(testcase));
		this.name = testcase.substring(testcase.indexOf(' ')+1, testcase.indexOf('('));
		String p = testcase.substring(testcase.indexOf('(')+1, testcase.lastIndexOf(')'));
		if(p.isBlank()) {
			params = null;
		} else {
			params = Arrays.stream(p.split(", ")).map(s -> getParam(s)).toArray();
		}
	}
	
	/**
	 * Returns the methodcall e.g. void main from the given testcase
	 * @param testcase a testcase e.g. void main(String[] {abba,baab}) 1
	 * @return the methodcall e.g. void main from the given testcase or the whole testcase if it was not found
	 */
	public static String getMethodcall(String testcase) {
		if(testcase.charAt(testcase.lastIndexOf(' ')-1) == ')')
			return testcase.substring(0, testcase.lastIndexOf(' '));
		return testcase;
	}
	
	/**
	 * Returns the score from the given testcase or 1 if no score was found
	 * @param testcase a testcase e.g. void main(String[] {abba,baab}) 1
	 * @return the methodcall from the given testcase or 1 if no score was found or could be parsed to Integer
	 */
	public static Integer getScore(String testcase) {
		try {
		return Integer.parseInt(testcase.substring(testcase.lastIndexOf(' ')+1));
		}catch(NumberFormatException e) {
			return 1;
		}
	}

	/**
	 * Casts a Testcase tp a dynamic testcase
	 * @param t 
	 * @return a dynamic testcase
	 */
	public static DynamicTestcase toDynamicTestcase(Testcase t) {
		if(t instanceof DynamicTestcase) {
			return (DynamicTestcase)t;
		}
		return new DynamicTestcase(t.testcase().first()+" "+t.testcase().second());
	}

	@Override
	public Pair<String, Integer> testcase() {
		return new Pair<String, Integer>(super.testcase, super.score);
	}

	public String toString() {
		return super.testcase + " " + super.score;
	}

	/**
	 * Gets the Parameter from String
	 * @param param the parameter as String
	 * @return the parameter in it's correct Datatype cast to Object
	 */
	public static Object getParam(String param) {
		if(param == null || param.isBlank()) {
			return null;
		}
		if(param.indexOf(' ')==-1) {
			//No values
			if(param.contains("[")) {
				String[] res = param.split("\\[");
				String[] splits = new String[res.length-1];
				System.arraycopy(res, 1, splits, 0, splits.length);
				int[] dims = new int[splits.length];
				for(int i = 0; i < splits.length; i++) {
					splits[i] = splits[i].replace("]", "");
					dims[i] = Integer.parseInt(splits[i]);
				}
				return getArray(res[0], dims);
			}
			System.err.println("Something went wrong");
		}
		String type = param.substring(0, param.indexOf(' '));
		String value = param.substring(param.indexOf(' ')+1);
		if(type.equals("null")) {
			return null;
		}
		if(Data.STRING_TO_PRIMITIVE.containsKey(type)) {
			return Data.STRING_TO_PRIMITIVE.get(type).apply(value);
		}
		int dimensions = type.length() - type.replace("[]", "|").length();
		type = type.replace("[]", "");
		if(!Data.STRING_TO_PRIMITIVE.containsKey(type)) {
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
				case "int": if(c.equals("{}")) return new int[] {}; 
					return Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).mapToInt(i -> ((Integer)i).intValue()).toArray();
				case "long":  if(c.equals("{}")) return new long[] {}; 
				return Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).mapToLong(i -> ((Long)i).longValue()).toArray();
				case "double":  if(c.equals("{}")) return new double[] {}; 
				return Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).mapToDouble(i -> ((Double)i).doubleValue()).toArray();
				case "String":  if(c.equals("{}")) return new String[] {}; 
				return content.split(",");
				default: break;
			}
			if(type.equals("byte") || type.equals("short")) {
				 if(c.equals("{}")) {
					 if(type.equals("byte")) {
						 return new byte[] {}; 
					 }
					 return new short[] {};
				 }
					
				int[] arr = Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).mapToInt(i -> ((Integer)i).intValue()).toArray();
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
				if(c.equals("{}")) return new float[] {}; 
			
				double[] arr = Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).mapToDouble(i -> ((Double)i).doubleValue()).toArray();
				float[] barr = new float[arr.length];
				for(int i = 0; i < arr.length;i++) {
					barr[i] = (float) arr[i];
				}
				return barr;
			} else if(type.equals("boolean")) { 
				if(c.equals("{}")) return new boolean[] {}; 
			
				Boolean[] arr = (Boolean[]) Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).toArray();
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
		if(dimensions == 1) {
			return get1DArray(c, type);
		}

		String content = c.substring(1, c.length()-1);
		StringBuilder temp = new StringBuilder();
		List<String> elements = new LinkedList<>();
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
			if(brackets < 0) {
				throw new IllegalArgumentException("Invalid Brackets at Array input: "+c);
			}
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
	
	private static Object get1DArray(String c, int dimension) {
		switch(c) {
		case "byte": return new byte[dimension];
		case "short": return new short[dimension];
		case "char": return new char[dimension];
		case "int": return new int[dimension];
		case "long": return new long[dimension];
		case "float": return new float[dimension];
		case "double": return new double[dimension];
		case "boolean": return new boolean[dimension];
		case "String": return new String[dimension];
		}
		return null;
	}
	
	/**
	 * Generates an empty Multidimensional array with the given dimensions and type
	 * currently only int arrays can be multidimensional
	 * @param type the type of the underlying datatype
	 * @param dimensions the dimensions of the array
	 * @return the multidimensional array cast to Object
	 */
	private static Object getArray(String type, int... dimensions) {
		if(dimensions.length == 1) {
			return get1DArray(type, dimensions[0]);
		}
		if(type.equals("int")) {
			if(dimensions.length == 2) {
				return new int[dimensions[0]][dimensions[1]];
			} else if(dimensions.length == 3) {
				return new int[dimensions[0]][dimensions[1]][dimensions[2]];
			} else if(dimensions.length == 4) {
				return new int[dimensions[0]][dimensions[1]][dimensions[2]][dimensions[3]];
			}else {
				throw new IllegalArgumentException("Arrays with more than 4 dimensions are currently not allowed, feel free to add them in the function DynamicTester.getArray");
			}
		}else {
			throw new IllegalArgumentException("Type: "+type+"not currently implemented \n only int arrays are currently implemented");
		}
	}

	public String getMethodName() {
		return this.name;
	}

}
