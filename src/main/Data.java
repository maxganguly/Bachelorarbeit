package main;

import java.io.PrintStream;
import java.util.Map;
import java.util.function.Function;

public class Data {
	public static final PrintStream SYSOUT = System.out;

	public static final Map<String, Function<String, Object>> STRING_TO_PRIMITIVE = Map.of(
			"byte", s -> Byte.valueOf(s.trim()),
			"short", s -> Short.valueOf(s.trim()),
			"char", s -> Character.valueOf(s.charAt(0)),
			"int", s -> Integer.valueOf(s.trim()),
			"float", s -> Float.valueOf(s.trim()),
			"long", s -> Long.valueOf(s.trim()),
			"double", s -> Double.valueOf(s.trim()),
			"boolean", s -> Boolean.valueOf(s.trim()),
			"String", s -> s);
	
	public static final Map<String, Class<?>> STRING_TO_CLASS = Map.of(
			"byte", byte.class,
			"short", short.class,
			"char", char.class,
			"int", int.class,
			"float", float.class,
			"long", long.class,
			"double", double.class,
			"boolean", boolean.class,
			"String", String.class);
}
