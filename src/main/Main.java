package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.sun.source.util.JavacTask;

import main.ast.ASTScannerText;
import main.ast.ASTTree;
import main.ast.ASTTreeScanner;

public class Main {
	public static final PrintStream SYSOUT = System.out;
	public static boolean DEBUG = false;
	public static String PROPERTIESPATH = "./autograder.properties";
	public static final Properties p = loadProperties();
	public static boolean TIME = false;

    public static void main(String[] args) {
    	for(int i = 0; i < args.length;i++) {
    		if(args[i].trim().equals("debug"))
    			DEBUG = true;
    		else if(args[i].trim().equals("time"))
    			TIME = true;
    		else if(args[i].trim().startsWith("properties")) {
    			String temp = args[i].trim();
    			PROPERTIESPATH = temp.substring(temp.indexOf('=')+1);
    			debug("Set Propertiespath to: "+ PROPERTIESPATH);
    		}
    	}
    	long start = System.currentTimeMillis();
        /*
    	String input = "src/testfiles/Aufgabe3.java";
    	String output = "src/output/Aufgabe3.txt";
    	*/
    	/*
    	var set = ConditionElement.existing;
    	Condition c = ConditionUtils.toCondition(
    			"(((((0 < arr1.length()) && ((0 * 1) >= arr1.length())) && (0 < [arr1.length()].length())) && (0 >= [Math.max(arr1[i].length(),arr2[i].length())].length())) && ((0 * 1) < [arr1.length()].length()))\n"
    			, Set.of("arr1","arr2"));
    	Condition e = c.evaluate();
    	System.out.println(e);
    	Path input = Path.of("testfiles/Test2.java");
    	Path outputTestcases = Path.of("testfiles/Test2_conditions.txt");
    	ASTTree tree = loadFromPath(input);
    	
    	if(tree == null) {
			return;
		}
    	printToFile(Path.of("testfiles/Test2.ast"), tree.toString(), true);
    	
    	
    	MCDCTestcaseGenerator mcdc = new MCDCTestcaseGenerator(tree);
    	List<String> methods = tree.getTreesWithTag("method").stream().map(t -> t.name).toList();
    	StringBuilder sb = new StringBuilder();
    	for(String method: methods) {
    		sb.append(method);
    		System.out.println(method);
        	sb.append('\n');
        	var conditions = MCDCTestcaseGenerator.generateConditions(tree, method, "", false);
        	System.out.println("Generated: "+conditions.size()+" Conditions");
        	for(var condition: conditions) {
        		System.out.println(condition);
        		sb.append(condition);
        		sb.append('\n');
        	}
    	}
    	printToFile(outputTestcases, sb.toString(), true);
    	
    	*/
    	
    /*

    	long startTime = System.currentTimeMillis();
    	long endTime = System.currentTimeMillis();
        System.out.println("executed in " + (endTime - startTime) + "ms");
    	System.out.println(getUsedMem());
    	startTime = System.currentTimeMillis();
    	Test t = new Test();
    	t.writeToResults();
    	endTime = System.currentTimeMillis();
        System.out.println("executed in " + (endTime - startTime) + "ms");
    	System.out.println(getUsedMem());
    	/*
    	MCDCTestcaseGenerator mtg = new MCDCTestcaseGenerator(tree);
    	mtg.generateTestcases();

    	String input = "src/testfiles/Test.java";
    	String input2 = "src/testfiles/Test.java";
    	String output = "src/output/Test.txt";
    	String output2 = "src/output/Test2.txt";
    	String output3 = "src/output/Test3.txt";
    	String testcases = "src/testfiles/testcases.txt";
    	Executor e1 = new Executor(input, "Test");
    	Executor e2 = new Executor(input, "Test");
    	int[][][] arr = new int[][][] {{{1,2},{3,4}},{{5,6},{7,8}}};
    	Tester dt = new DynamicTester(e1, e2, testcases);
    	List<Pair<String,Integer>> results = dt.runAllTestcases();
    	int score = 0;
    	for(Pair<String, Integer> result: results) {
    		if(!result.first().endsWith("successfull"))
    			System.out.println(result);
    		score += result.second();
    	}
    	System.out.println("Score: "+ score);

        //*/
    	
    	 
    	//*
    	//Generate and run tests
    	try {
			Test t = new Test();
			//t.test();
			t.writeToResults();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	//*/
    	System.out.println("Finished");
    	if(TIME) {
    		long end = System.currentTimeMillis();
    		long diff = end-start;
    		System.out.printf("%2dmin, %2ds, %3dms%n",diff/60_000,(diff/1_000)%60,diff%1000);
    	}
    	System.exit(0);

    }

    /**
     * Prints a given String to a given File, overwrites the current content of the file, creates a new if it does not exist
     * Prints the stacktrace to the error if it fails
     * @param file the Path to the file to be written to
     * @param content the content to be written in the file
     * @param should the file be overwritten if it already exists
     * @return true if the writing succeeded, false if it failed
     */
    public static boolean printToFile(Path file, String content, boolean overwriteIfExists) {
    	if(Files.exists(file)&&!overwriteIfExists) {
			return true;
		}
    	try {
    		Files.createDirectories(file.getParent());
			Files.write(file, content.getBytes(), StandardOpenOption.CREATE , StandardOpenOption.TRUNCATE_EXISTING);
			Main.debug("Wrote: "+file.toString()+" to disk");
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
        LinkedList<ASTTree> results = new LinkedList<ASTTree>();
        for (var compUnitTree : task.parse()) {
        	results.add(compUnitTree.accept(scanner, null));
        }

    	return results;//sb.toString();
    }
    /**
     * Generates a textual AST in xml format<br >
     * <b>Deprecated use generateAST toString</b>
     * @param javaSrc
     * @return
     * @throws IOException
     */
    @Deprecated 
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
		br.close();
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

    public static ASTTree loadFromPath(Path p) {
        if (p.toFile().exists()) {
    	 if (p.getFileName().toString().endsWith(".java")) {
             try {
                 // check for ++i and --i pattern and report
             	//result = generateASTText(path);
                 return generateAST(p).getFirst();
             } catch (IOException exc) {
                 // report parse failures and continue scanning other files
                 System.err.printf("parsing failed for %s : %s\n", p.toAbsolutePath().toString(), exc);
             }
         }else {
         	System.err.println("Given file <"+ p +"> is not a Java file");
         }
         } else {
         	System.err.println("Given file <"+ p +"> does not exist");
         }
		return null;
    }

    public static void debug(String msg) {
    	if(DEBUG) {
			System.err.println(msg);
		}
    }
    
    public static void debug(Exception msg) {
    	if(DEBUG) {
			msg.printStackTrace();
		}
    }



    public static Properties loadProperties() {
		Properties p = new Properties();
    	if(Files.exists(Path.of(PROPERTIESPATH))) {
        	try {
				p.load(new FileReader(PROPERTIESPATH));
	        	return p;
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	p.setProperty("Testcases", "./testcases");
    	p.setProperty("SolutionInputDir", "./solution");
    	p.setProperty("ToTestInputDirs", "./test");
    	p.setProperty("ResultOutputDir", "./results/");
    	p.setProperty("GenerateTestcases", "true");
    	p.setProperty("PrintAllTests", "true");
    	p.setProperty("SaveTestcases", "true");
    	p.setProperty("OverwriteTestcases", "false");
    	p.setProperty("DelimiterCSV", "#");
    	try {
			p.store(new FileWriter(PROPERTIESPATH), "Properties for the Autograder");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return p;
    }

    private static String getUsedMem() {
        final long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long m = (mem/1024)%1024;
        return (m/1024)+"MB " + (m) + "KB";
    }
    
    

}
