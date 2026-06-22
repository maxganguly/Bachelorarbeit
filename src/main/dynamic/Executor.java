package main.dynamic;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import main.Data;
import main.Main;
public class Executor {


	private Class<?> clazz;

	private PrintStream os;

	public Executor(String path, boolean replaceprivate) {
		this(Path.of(path),replaceprivate);
	}

	public Executor(Path path, boolean replaceprivate) {
		try {
			String code = Main.getFromPath(path);
			if(replaceprivate) {
				code = code.replaceAll("private", "public");
				code = code.replace("static public", "public static");
				code = code.replace("    static", "    public static");
			}
			this.clazz = getClass(code);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			Main.debug(e);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			Main.debug(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Main.debug(e);
		}
	}

	public Executor(Path path, String name) {
		try {
			this.clazz = getClass(name, Main.getFromPath(path));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			Main.debug(e);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			Main.debug(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Main.debug(e);
		}
	}

	public Executor(String path, String name) {
		this(Path.of(path),name);
	}

	/**
	 * Sets the Printstream which should be used for the Testcases
	 * @param os the Printstream is to be used for the Testcases
	 */
	public void setOutput(PrintStream os) {
		this.os = os;
	}

	/**
	 * Runs the Main method of the given class
	 * @throws MethodNotFoundException if there is no main Method in the class
	 */
	public void runMain() throws MethodNotFoundException, InvocationTargetException {
		runMethod("main", (Object) (new String[] {}));
	}

	/**
	 * Runs a method with the given name and the given parameters
	 * @param methodname the name of the method to be invoked
	 * @param params the parameters to be given to the method
	 * @return the return of the invoked method null if void
	 * @throws MethodNotFoundException if the method was not found
	 */
	public Object runMethod(String methodname, Object... params) throws MethodNotFoundException, InvocationTargetException {
		Method[] meth = clazz.getDeclaredMethods();
		if(os != null) {
			System.setOut(os);
		}
		Object ret = null;
		boolean ran = false;
		for(Method m : meth) {
			//System.out.println(m.getName());
			if(m.getName().equals(methodname)) {
				try {
					ret = m.invoke(null, params);
					ran = true;
					break;
				} catch (IllegalAccessException e) {
					Main.debug(e);
				} catch (InvocationTargetException e) {
					Main.debug(e);
					throw e;
				} catch (IllegalArgumentException e) {
					//When overloading Methods this might be triggered
					//e.printStackTrace();
				}
			}
		}
		if(os != null) {
			System.setOut(Data.SYSOUT);
		}
		if(!ran) {
			throw new MethodNotFoundException(methodname);
		}
		return ret;

	}

	/**
	 * Checks if the given Method exists in the class
	 * @param name the name of the method
	 * @return true if the method exists in the class
	 */
	public boolean containsMethod(String name) {
		Method[] meth = clazz.getDeclaredMethods();
		for(Method m : meth) {
			//System.out.println(m.getName());
			if(m.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Generates a Class with the given NAme from the given java Code
	 * @param name the name of the class
	 * @param code the source code of the class
	 * @return an inmemory compiled class
	 * @throws ClassNotFoundException if there is no viable class in the java File
	 * @throws InstantiationException if the class can't be instantiated/Compiler errors
	 */
	public static Class<?> getClass(String name, String code) throws ClassNotFoundException, InstantiationException {
	    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
	    InMemoryFileManager manager = new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));

	    List<JavaFileObject> sourceFiles = new LinkedList<>();
	    		sourceFiles.add(new JavaSourceFromString(name, code));

	    JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sourceFiles);

	    boolean result = task.call();

	    if (!result) {
	        diagnostics.getDiagnostics()
	          .forEach(d -> System.out.println(String.valueOf(d)));
	    }
	        ClassLoader classLoader = manager.getClassLoader(null);
	        Class<?> clazz = classLoader.loadClass(name);
	        return clazz;
	}

	/**
	 * Generates a Class with the given NAme from the given java Code
	 * @param name the name of the class
	 * @param code the source code of the class
	 * @return an inmemory compiled class
	 * @throws ClassNotFoundException if there is no viable class in the java File
	 * @throws InstantiationException if the class can't be instantiated/Compiler errors
	 */
	public static Class<?> getClass(String code) throws ClassNotFoundException, InstantiationException {
	    String name = code.substring(code.indexOf("class")+6);
	    name = name.substring(0,Math.min(name.indexOf(' '),name.indexOf('{')));
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
	    InMemoryFileManager manager = new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));

	    List<JavaFileObject> sourceFiles = new LinkedList<>();
	    		sourceFiles.add(new JavaSourceFromString(name, code));

	    JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sourceFiles);

	    boolean result = task.call();

	    if (!result) {
	        diagnostics.getDiagnostics()
	          .forEach(d -> System.out.println(String.valueOf(d)));
	    }
	    	try {
	        ClassLoader classLoader = manager.getClassLoader(null);
	        Class<?> clazz = classLoader.loadClass(name);
	        return clazz;
	    	} catch(ClassNotFoundException e) {
	    		Main.debug(e);
	    		return null;
	    	}
	}
	
	public boolean hasClass() {
		return this.clazz != null;
	}

}
