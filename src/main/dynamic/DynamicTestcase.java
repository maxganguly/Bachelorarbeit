package main.dynamic;

import java.util.Arrays;

import java.util.LinkedList;
import java.util.List;

import main.Data;
import main.Pair;
import main.Testcase;

public class DynamicTestcase extends Testcase {

	String name;
	Object returntype;
	Object[] params;
	
	
	public DynamicTestcase(String testcase) {
		super(testcase.substring(0, testcase.lastIndexOf(' ')),Integer.parseInt(testcase.substring(testcase.lastIndexOf(' ')+1)));
		this.name = testcase.substring(testcase.indexOf(' ')+1, testcase.indexOf('('));
		String p = testcase.substring(testcase.indexOf('(')+1, testcase.lastIndexOf(')'));
		if(p.isBlank()) {
			params = null;
		}else
			params = Arrays.stream(p.split(", ")).map(s -> getParam(s)).toArray();
	}
	
	public static DynamicTestcase toDynamicTestcase(Testcase t) {
		if(t instanceof DynamicTestcase)
			return (DynamicTestcase)t;
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
		if(param == null || param.isBlank())
			return null;
		String type = param.substring(0, param.indexOf(' '));
		String value = param.substring(param.indexOf(' ')+1);
		if(type.equals("null"))
			return null;
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
				case "int": return Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).mapToInt(i -> ((Integer)i).intValue()).toArray();
				case "long": return Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).mapToLong(i -> ((Long)i).longValue()).toArray();
				case "double": return Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).mapToDouble(i -> ((Double)i).doubleValue()).toArray();
				case "String": return content.split(",");
				default: break;
			}
			if(type.equals("byte") || type.equals("short")) {
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
				double[] arr = Arrays.stream(content.split(",")).map(Data.STRING_TO_PRIMITIVE.get(type)).mapToDouble(i -> ((Double)i).doubleValue()).toArray();
				float[] barr = new float[arr.length];
				for(int i = 0; i < arr.length;i++) {
					barr[i] = (float) arr[i];
				}
				return barr;
			} else if(type.equals("boolean")) {
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
	
	public String getMethodName() {
		return this.name;
	}

}
