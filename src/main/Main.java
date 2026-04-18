package main;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.tools.*;
import com.sun.source.util.*;
import main.ast.*;
import main.dynamic.DynamicTester;
import main.dynamic.DynamicTester.Result;
import main.dynamic.Executor;
import testfiles.Test;

public class Main {
	public static final PrintStream SYSOUT = System.out;
	
    public static void main(String[] args) throws Exception {
        // a single argument that is directory from which .java sources are scanned.
        // If no argument supplied, use the current directory
    	/*f
    	String input = "src/testfiles/Aufgabe3.java";
    	String output = "src/output/Aufgabe3.txt";
    	//*/ 
    	
    	//*
    	String input = "src/testfiles/Test.java";
    	String output = "src/output/Test.txt";
    	String changed = "src/output/Testcases.txt";
    	String testcases = "src/testfiles/testcases.txt";
    	Executor e1 = new Executor(input, "Test");
    	Executor e2 = new Executor(input, "Test");
    	int[][][] arr = new int[][][] {{{1,2},{3,4}},{{5,6},{7,8}}};
    	Tester dt = new DynamicTester(e1, e2, testcases);
    	List<Pair<String,Integer>> results = dt.runAllTestcases();
    	for(Pair<String, Integer> result: results) {
    		System.out.println(result);
    	}
    	
    	//*
    	Path path = Paths.get(input);
        String result = "";
        ASTTree tree = null;
        if (path.toFile().exists()) {
        if (input.endsWith(".java")) {
            try {
                // check for ++i and --i pattern and report
            	//result = generateASTText(path);
                tree = generateAST(path).getFirst();
            } catch (IOException exc) {
                // report parse failures and continue scanning other files
                System.err.printf("parsing failed for %s : %s\n", path.toAbsolutePath().toString(), exc);
            }
        }else {
        	System.err.println("Given file <"+ input +"> is not a Java file");
        	return;
        }
        } else {
        	System.err.println("Given file <"+ input +"> does not exist");
        	return;
        }
        //ASTTree cases = ASTTree.getFromPath(Paths.get(output));
        System.out.println(printToFile(Paths.get(output), tree.toString()));
        /*
        System.out.println(printToFile(Paths.get(output), cases.toString()));
        
        ASTTree rebuilt = new ASTTree(tree.toString());
        System.out.println(tree.equals(tree));
        System.out.println(rebuilt.equals(tree));
        System.out.println(tree.equals(rebuilt));
        List<ASTTree> methods = tree.getTreesWithTag("method");
        ASTTree[] meth = new ASTTree[methods.size()];
        methods.toArray(meth);
        ASTTree[] general = new ASTTree[methods.size()];
        {
        int i = 0;
        for(ASTTree t : meth) {
        	System.out.println(meth[i].name);
        	general[i] = meth[i].generalize();
        	i++;
        }
        }
        
        
        System.out.println(meth[0].equals(meth[0]));
        System.out.println(meth[1].equals(meth[1]));
        System.out.println(meth[2].equals(meth[2]));
        System.out.println("Contains exact");
        System.out.println(meth[1].containsExact(meth[1]));
        System.out.println(meth[2].containsExact(meth[2]));
        System.out.println(meth[2].containsExact(meth[1]));
        System.out.println(meth[1].containsExact(meth[2]));
        System.out.println("Contains structure");
        System.out.println(meth[1].containsStructure(meth[1]));
        System.out.println(meth[2].containsStructure(meth[2]));
        System.out.println(meth[2].containsStructure(meth[1]));
        System.out.println(meth[1].containsStructure(meth[2]));
        System.out.println("Generalized contains structure");
        System.out.println(meth[1].containsStructure(general[1]));
        System.out.println(meth[2].containsStructure(general[2]));
        System.out.println(meth[3].containsStructure(general[3]));
        System.out.println(meth[4].containsStructure(general[4]));
        System.out.println(general[1].containsStructure(meth[1]));
        System.out.println(general[2].containsStructure(meth[2]));
        System.out.println(general[3].containsStructure(meth[3]));
        System.out.println(general[4].containsStructure(meth[4]));
        System.out.println("Contains exact");
        System.out.println(general[1].containsExact(meth[1]));
        System.out.println(general[2].containsExact(meth[2]));
        System.out.println(general[2].containsExact(meth[1]));
        System.out.println(general[1].containsExact(meth[2]));
        System.out.println(meth[2].containsExact(general[1]));
        System.out.println(meth[1].containsExact(general[2]));
        //*/
        
    }
    
    /**
     * Prints a given String to a given File, overwrites the current content of the file, creates a new if it does not exist
     * Prints the stacktrace to the error if it fails
     * @param file the Path to the file to be written to
     * @param content the content to be written in the file
     * @return true if the writing succeeded, false if it failed
     */
    public static boolean printToFile(Path file, String content) {
    	try {
			Files.write(file, content.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }

    /**
     * Returns the current major Java version
     * @return the current Java version
     */
    private static int getJavaMajorVersion() {
        return Runtime.version().feature();
    }

    private static final List<String> OPTIONS = 
        List.of("--enable-preview", "--release=" + getJavaMajorVersion());

    // get the system java compiler instance
    private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    
    /**
     * Generates ASTTrees based on the java file given
     * @param javaSrc the Path to the java file
     * @return A List of ASTTrees of all classes contained in the java file
     * @throws IOException
     */
    public static List<ASTTree> generateAST(Path javaSrc) throws IOException{
        var compUnits = compiler.
                getStandardFileManager(null, null, null).
                getJavaFileObjects(javaSrc);
        var task = (JavacTask) compiler.getTask(null, null, null,
            OPTIONS, null, compUnits);
        var scanner = new ASTTreeScanner();
        StringBuilder sb = new StringBuilder();
        LinkedList<ASTTree> results = new LinkedList<ASTTree>();
        for (var compUnitTree : task.parse()) {
        	results.add(compUnitTree.accept(scanner, null));
        }

    	return results;//sb.toString();
    }
    @Deprecated
    /**
     * Generates a textual AST in xml format
     * Deprecated use generateAST toString
     * @param javaSrc
     * @return
     * @throws IOException
     */
    public static String generateASTText(Path javaSrc) throws IOException{
        var compUnits = compiler.
                getStandardFileManager(null, null, null).
                getJavaFileObjects(javaSrc);
        var task = (JavacTask) compiler.getTask(null, null, null,
            OPTIONS, null, compUnits);
        var scanner = new ASTScannerText();
        StringBuilder sb = new StringBuilder();
        for (var compUnitTree : task.parse()) {
        	String s = compUnitTree.accept(scanner, 0);
            System.out.println(s);
            sb.append(s);
        }

    	return sb.toString();
    }
    
    /**
     * Returns all Text from the given File
     * @param p the path to the file to be read
     * @return All Text from the file
     * @throws IOException it the file does not exist
     */
    public static String getFromPath(Path p) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(p.toAbsolutePath().toString()));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		return sb.toString();
	}

    /**
     * Returns all Text from the given File
     * @param p String of the path to the file to be read
     * @return All Text from the file
     * @throws IOException it the file does not exist
     */
    public static String getFromPath(String p) throws IOException {
		return getFromPath(Path.of(p));
	}
}
