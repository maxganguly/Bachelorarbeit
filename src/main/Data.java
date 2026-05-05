package main;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.UnaryTree;

public class Data {
	public static final PrintStream SYSOUT = System.out;

	public static final Map<String, Function<String, Object>> STRING_TO_PRIMITIVE = Map.of("byte",
			s -> Byte.valueOf(s.trim()), "short", s -> Short.valueOf(s.trim()), "char",
			s -> Character.valueOf(s.charAt(0)), "int", s -> Integer.valueOf(s.trim()), "float",
			s -> Float.valueOf(s.trim()), "long", s -> Long.valueOf(s.trim()), "double", s -> Double.valueOf(s.trim()),
			"boolean", s -> Boolean.valueOf(s.trim()), "String", s -> s);

	public static final Map<String, Class<?>> STRING_TO_CLASS = Map.of("byte", byte.class, "short", short.class, "char",
			char.class, "int", int.class, "float", float.class, "long", long.class, "double", double.class, "boolean",
			boolean.class, "String", String.class);
	public static final Map<String, String> KIND_TO_OPERATOR;
	static {
		KIND_TO_OPERATOR = new HashMap<String, String>();
		KIND_TO_OPERATOR.put("POSTFIX_INCREMENT", "++");
		KIND_TO_OPERATOR.put("POSTFIX_DECREMENT", "--");
		KIND_TO_OPERATOR.put("PREFIX_INCREMENT", "++");
		KIND_TO_OPERATOR.put("PREFIX_DECREMENT", "--");
		KIND_TO_OPERATOR.put("UNARY_PLUS", "-");
		KIND_TO_OPERATOR.put("UNARY_MINUS", "-");
		KIND_TO_OPERATOR.put("BITWISE_COMPLEMENT", "~");
		KIND_TO_OPERATOR.put("LOGICAL_COMPLEMENT", "!");
		KIND_TO_OPERATOR.put("MULTIPLY", "*");
		KIND_TO_OPERATOR.put("DIVIDE", "/");
		KIND_TO_OPERATOR.put("REMAINDER", "%");
		KIND_TO_OPERATOR.put("PLUS", "+");
		KIND_TO_OPERATOR.put("MINUS", "-");
		KIND_TO_OPERATOR.put("LEFT_SHIFT", "<<");
		KIND_TO_OPERATOR.put("RIGHT_SHIFT", ">>");
		KIND_TO_OPERATOR.put("UNSIGNED_RIGHT_SHIFT", ">>>");
		KIND_TO_OPERATOR.put("LESS_THAN", "<");
		KIND_TO_OPERATOR.put("GREATER_THAN", ">");
		KIND_TO_OPERATOR.put("LESS_THAN_EQUAL", "<=");
		KIND_TO_OPERATOR.put("GREATER_THAN_EQUAL", ">=");
		KIND_TO_OPERATOR.put("EQUAL_TO", "==");
		KIND_TO_OPERATOR.put("NOT_EQUAL_TO", "!=");
		KIND_TO_OPERATOR.put("AND", "&");
		KIND_TO_OPERATOR.put("XOR", "^");
		KIND_TO_OPERATOR.put("OR", "|");
		KIND_TO_OPERATOR.put("CONDITIONAL_AND", "&&");
		KIND_TO_OPERATOR.put("CONDITIONAL_OR", "||");
	}

}
