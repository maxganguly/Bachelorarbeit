package main;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import main.ast.ASTTestGenerator;
import main.ast.ASTTester;

public class Test {

	private Map<String,List<? extends Tester<?>>> testers;
	
	    
	public Test() throws IOException {
		this.testers = new HashMap<String,List<? extends Tester<?>>>();
		//Get the Solution files to generate the Testcase
		FileVisitor<Path> files = new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String filename = file.getFileName().toString();
				if(filename.endsWith(".java")) {
					var testerlist = new LinkedList<Tester<?>>();
					var atg = new ASTTestGenerator(Main.generateAST(file).getFirst());
					String pureName = filename.substring(0, filename.lastIndexOf('.'));
					atg.loadFromDirectory(Path.of(Main.p.getProperty("Testcases")+"/"+pureName));
					ASTTester t = new ASTTester();
					t.addTestcases(atg.getTestcases());
					testerlist.add(t);
					if(Main.p.getProperty("SaveTestcases").equalsIgnoreCase("true")){
						Main.debug("Saving: "+pureName);
						atg.saveToDirectory(Path.of(Main.p.getProperty("Testcases")+"/"+pureName));
					}
					//TODO: Add Dynamic after implementing DynamicTestcases
					
					testers.put(pureName, testerlist);
					
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				exc.printStackTrace();
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
			
		};
		Files.walkFileTree(Path.of(Main.p.getProperty("SolutionInputDir")), files);
	}
	
	/**
	 * Evaluates all testcases for all students and all files
	 * Maybe there exists a better way the store the data than in this godless Thing
	 * @return a List of pairs of Name:Matrikelnr and a list of the file with each testcase output
	 */
	public List<Pair<Pair<String,Integer>,List<Pair<String,List<Pair<String,Integer>>>>>> test() {
		var results = new LinkedList<Pair<Pair<String,Integer>,List<Pair<String,List<Pair<String,Integer>>>>>> ();
		List<Tripel<String,Integer,Path>> srcDirs = getAllSrcDirectories();
		for(var src: srcDirs) {
			var student = new Pair<String,Integer>(src.first(),src.second());
			var resultsstudent = new LinkedList<Pair<String,List<Pair<String,Integer>>>>();
			try {
				List<Path> children = Files.list(src.third()).toList();
				for(Path p: children) {
					String filename = p.getFileName().toString();
					if(filename.startsWith(".") || !filename.endsWith(".java"))
						continue;
					var resultsfile = new LinkedList<Pair<String,Integer>>();
					String pureName = filename.substring(0, filename.lastIndexOf('.'));
					var li = this.testers.get(pureName);
					if(li != null)	//If no tester exists for a file (Should normally not happen
					for(Tester<?> testers: li) {
						resultsfile.addAll(testers.runAllTestcases(p));
					}
					resultsstudent.add(new Pair<String,List<Pair<String,Integer>>>(pureName,resultsfile));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			results.add(
					new Pair<Pair<String,Integer>,List<Pair<String,List<Pair<String,Integer>>>>>
				(student,resultsstudent));
			
		}
		return results;
	}
	
	/**
	 * Executes the test function and writes the results to the in the Property ToTestInputDirs
	 * defined directory
	 */
	public void writeToResults() {
		var results = test();
		String rootPath = Main.p.getProperty("ResultOutputDir");
		boolean saveEverything = Boolean.parseBoolean(Main.p.getProperty("PrintAllTests"));
		for(var students: results) {
			String studentPath = students.first().first()+"_"+students.first().second();
			for(var file: students.second()) {
				StringBuilder sb = new StringBuilder("Score: "+ file.second().stream().mapToInt(p -> p.second()).sum()+"\n");
				for(var f : file.second()) {
					if(f.second().intValue() != 0 || saveEverything)
						sb.append(f.first()+" score: "+f.second()+"\n");
					else
						sb.append(f.first()+" fucked up: "+f.second()+"\n");
						
				}
				
				/*text += file.second().stream()
						//.filter(p -> (p.second().intValue() != 0 || saveEverything))
						.map(p -> p.first()+" score: "+p.second()) .collect(Collectors.joining("\n"));
						*/
				Main.printToFile(Path.of(rootPath,studentPath,file.first()), sb.toString(), true);	
			}
			
		}
	}
	
	/**
	 * Lists all src directories of the Student solutions
	 * @return A list of tripel ofvName,Matriculations-number,Path_to_src
	 */
	public static List<Tripel<String,Integer,Path>> getAllSrcDirectories(){
		var list = new LinkedList<Tripel<String,Integer,Path>>();
		 try {
			return Files.walk(Path.of(Main.p.getProperty("ToTestInputDirs")), 1).map(Test::getSrcDirectory).filter(t -> t != null).toList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private static Path descendSingleChildren(Path path) {
        Path childDir;
        while ((childDir = getSingleChildDir(path)) != null) {
            path = childDir;
        }
        return path;
    }

    private static Path getSingleChildDir(Path path) {
        try {
            List<Path> children = Files.list(path).toList();
            return children.size() == 1 ? children.get(0) : null;
        } catch (IOException e) {
            return null;
        }
    }
    
    private static Tripel<String,Integer,Path> getSrcDirectory(Path path){
    	String[] names = path.getFileName().toString().split("_");
    	if(names.length < 2) {
    		return null;
    	}
		 String name = "";
		 int matrnr = 0;
		 if(Character.isDigit(names[0].charAt(0))){
			 matrnr = Integer.parseInt(names[0]);
			 names[0] = "";
			 name = String.join("_", names);
		 }else {
			 matrnr = Integer.parseInt(names[names.length-1]);
			 names[names.length-1] = "";
			 name = String.join("_", names);
		 }
		 Path src = null;
		 try {
			List<Path> children = Files.list(descendSingleChildren(path)).toList();
			for(Path c : children) {
				if(c.getFileName().toString().equals("src")) {
					src = c;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		 return new Tripel<String,Integer,Path>(name,matrnr,src);
    }
}
