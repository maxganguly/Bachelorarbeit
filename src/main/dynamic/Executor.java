package main.dynamic;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import main.Main;

import javax.tools.JavaFileObject;
public class Executor {

	private String pathToFile;
	private String name;
	private Class<?> clazz;
	
	private PrintStream os;
	
	public Executor(String path, String name) {
		this.pathToFile = path;
		this.name = name;
		try {
			this.clazz = getClass(name, Main.getFromPath(path));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setOutput(PrintStream os) {
		this.os = os;
	}
	
	public void runMain() {
		Method[] meth = clazz.getDeclaredMethods();
		for(Method m : meth) {
			//System.out.println(m.getName());
			if(m.getName().equals("main")) {
				try {
					m.invoke(null, (Object) (new String[] {}));
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public Object runMethod(String methodname, Object... params) {
		Method[] meth = clazz.getDeclaredMethods();
		if(os != null)
			System.setOut(os);
		Object ret = null;
		for(Method m : meth) {
			//System.out.println(m.getName());
			if(m.getName().equals(methodname)) {
				try {
					ret = m.invoke(null, params);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		if(os != null)
			System.setOut(Main.SYSOUT);
		return ret;
		
	}
	
	public static Class<?> getClass(String name, String code) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
	    InMemoryFileManager manager = new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));

	    List<JavaFileObject> sourceFiles = new LinkedList<JavaFileObject>();
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
	
}
