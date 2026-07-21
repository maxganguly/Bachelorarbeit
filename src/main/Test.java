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

import main.ast.ASTTestWrapper;
import main.dynamic.DynamicTestWrapper;

public class Test {

	private Map<String,List<? extends AbstractTestWrapper>> testers;


	public Test() throws IOException {
		this.testers = new HashMap<>();
		//Get the Solution files to generate the Testcase
		FileVisitor<Path> files = new FileVisitor<>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String filename = file.getFileName().toString();
				if(filename.endsWith(".java")) {
					String pureName = filename.substring(0, filename.lastIndexOf('.'));
					var testerlist = new LinkedList<AbstractTestWrapper>();
					testerlist.add(new ASTTestWrapper(file));
					testerlist.add(new DynamicTestWrapper(file));
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
		List<Tripel<String,Integer,Path>> srcDirs = new LinkedList<>( getAllSrcDirectories());
		srcDirs.sort((arg0, arg1) -> {
			 int res = 0;
			 res = arg0.first().compareTo(arg1.first());
			 if(res != 0)
				 return res;
			 return arg0.second().compareTo(arg1.second());
		 });
		for(var src: srcDirs) {
			if(src.third() == null) {
				Main.debug("Unable to find src directory for: "+src.toString() +"skipping...");
				continue;
			}
			var student = new Pair<>(src.first(),src.second());
			var resultsstudent = new LinkedList<Pair<String,List<Pair<String,Integer>>>>();
			Main.SYSOUT.println("Currently working on: "+ student);
			try {
				List<Path> children = Files.list(src.third()).toList();
				for(Path p: children) {
					String filename = p.getFileName().toString();
					if(filename.startsWith(".") || !filename.endsWith(".java")) {
						continue;
					}
					var resultsfile = new LinkedList<Pair<String,Integer>>();
					String pureName = filename.substring(0, filename.lastIndexOf('.'));
					var li = this.testers.get(pureName);
					if(li != null) { //If no tester exists for a file (Should normally not happen
						for(AbstractTestWrapper testers: li) {
						resultsfile.addAll(testers.test(p));
						}
					}
					resultsstudent.add(new Pair<>(pureName,resultsfile));
				}
			} catch (IOException e) {
				Main.debug(src.third().toString());
				e.printStackTrace();
			}
			results.add(
					new Pair<>
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
		StringBuilder csv = new StringBuilder("Name");
		boolean saveEverything = Boolean.parseBoolean(Main.p.getProperty("PrintAllTests"));
		csv.append(";Mat.Nr.");
		String category = "";
		String oldcat = "";
		for(Pair<String,Integer> element: results.getFirst().second().getFirst().second()) {
			String col = element.first();
			if(col.indexOf('(') != -1)
			category = col.substring(0, col.indexOf('('));
			/*
			col = col.substring(0,col.indexOf('('))+ col.substring(col.indexOf(')')+1, col.length());
			col = col.strip();
			col = col.substring(0, col.lastIndexOf(' '));
			csv.append(";"+col);*/
			if(!category.equals(oldcat)) {
				csv.append(";"+category);
				oldcat = category;
			}
		}
		oldcat = "";
		csv.append(System.lineSeparator());
		char delim = Main.p.get("DelimiterCSV").toString().charAt(0);
		for(var students: results) {
			String studentPath = students.first().first()+students.first().second();
			csv.append(students.first().first()+";"+students.first().second());
			csv.append(";");
			oldcat = "";
			StringBuilder content = new StringBuilder();
			//content.append('\"');
			for(var file: students.second()) {
				StringBuilder sb = new StringBuilder("Score: "+ file.second().stream().mapToInt(p -> p.second()).sum()+"\n");
				csv.append(delim);
				for(var f : file.second()) {
					String result = f.first();
					if(result.contains("successfull")) {
						result = "";
					}
					//result = result.replace("\"", "\\\"");
					if(f.first().indexOf('(') != -1)
					category = f.first().substring(0, f.first().indexOf('('));
					
					if(!category.equals(oldcat)) {
						String c = content.toString();
						if(c.endsWith(System.lineSeparator())) {
							int size = content.length();
							content = content.delete(size-System.lineSeparator().length(), size);
						}
						if(!oldcat.equals("")) {
							content.append(delim+";"+delim);
						}
						oldcat = category;
						csv.append(content);
						content = new StringBuilder();
					}
					if(!result.isBlank()) {
						content.append(result);
						content.append(System.lineSeparator());
					}
					if(f.second().intValue() != 0 || saveEverything) {
						sb.append(f.first()+" score: "+f.second()+"\n");
					} else {
						sb.append(f.first()+" fucked up: "+f.second()+"\n");
					}

				}
				csv.append(content);
				//if(!content.isEmpty()) {
					csv.append(delim);
					csv.append(System.lineSeparator());
					//csv.append(content);
				//}
				//csv.append('\"');
				/*text += file.second().stream()
						//.filter(p -> (p.second().intValue() != 0 || saveEverything))
						.map(p -> p.first()+" score: "+p.second()) .collect(Collectors.joining("\n"));
						*/
				Main.printToFile(Path.of(rootPath,studentPath,file.first()), sb.toString(), true);
			}

		}
		Main.printToFile(Path.of(rootPath, "results.csv"), csv.toString(), true);
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
			 name = name.substring(1);
		 }else {
			 matrnr = Integer.parseInt(names[names.length-1]);
			 names[names.length-1] = "";
			 name = String.join("_", names);
			 name = name.substring(0, name.length()-1);
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
			Main.debug(e);
			return null;
		}
		 return new Tripel<>(name,matrnr,src);
    }
}
