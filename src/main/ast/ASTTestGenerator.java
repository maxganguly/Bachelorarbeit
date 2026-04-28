package main.ast;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import main.Main;
import main.generator.Generator;

public class ASTTestGenerator extends Generator<ASTTestcase>{

	ASTTree code;
	
	public ASTTestGenerator(ASTTree code) {
		super();
		this.code = code;
	}
	
	@Override
	public List<ASTTestcase> generateTestcases() {
		for(ASTTree method: code.getTreesWithTag("method")) {
			testcases.addAll(generateTestcases(method));
		}
		return testcases;
	}
	
	public List<ASTTestcase> generateTestcases(ASTTree method) {
		var list = new LinkedList<ASTTestcase>();
		
		list.add(new ASTTestcase(method.name+".Exact", method, 10));	
		ASTTree general = method.generalize();
		list.add(new ASTTestcase(method.name+".General", general, 5));
		ASTTree struct = general.keepOnlyStructure();
		list.add(new ASTTestcase(method.name+".Structure", struct, 1));
		ASTTree temp;
		ASTTree methodCalls = getAllMethodCalls(method);
		ASTTree temp1;
		if(methodCalls != null && methodCalls.depth() > 0) {
			temp1 = method.copyNode();
			temp1.children.add(methodCalls);
			temp1.order = ASTTree.ORDER.UNORDERED;
			list.add(new ASTTestcase(method.name+".MethodCalls", temp1, 1));
		}
		temp = getHighestNestedLoop(general);
		if(temp != null && temp.depth() > 0) {
			temp1 = method.copyNode();
			temp1.children.add(temp);
			list.add(new ASTTestcase(method.name+".Loops", temp1, 1));
		}
		temp = getRecursion(method);
		if(temp != null && temp.depth() > 0) {
			temp1 = method.copyNode();
			temp1.children.add(temp);
			list.add(new ASTTestcase(method.name+".Recursion", temp1, 1));
			temp1 = method.copyNode();
			temp1.eval_mode = ASTTree.EVALUATION_MODE.NONE;
			temp1.children.add(ASTTree.LOOP);
			list.add(new ASTTestcase(method.name+".NoLoops", temp1, 1));
			
		}
		return list;
	}
	
	private ASTTree getAllMethodCalls(ASTTree tree) {
		Set<String> existingmethods = new HashSet<String>();
		var mc = tree.getTreesWithTag("mc").stream().map(t -> t.keepOnly(
				te -> (te.tag.equals("mc") && existingmethods.add(te.name)))).toList();
		ASTTree t = new ASTTree();
		t.children.addAll(mc);
		return t;
	}
	private ASTTree getHighestNestedLoop(ASTTree tree) {
		return tree.keepOnly("loop");
	}
	
	private ASTTree getRecursion(ASTTree tree) {
		return tree.keepOnly(s -> 
		s.tag.equals("mc") && 
		s.name.equals(tree.name.substring(0, tree.name.indexOf("("))));
	}
	
	/**
	 * Saves the currently generated ASTTestcases to the directory
	 * Syntax of the saved files Name:score.ast
	 */
	public boolean saveToDirectory(Path pathToDirectory) {
		String path = pathToDirectory.toString();
		for (ASTTestcase t : testcases) {
			String[] split = t.name.split("\\.");
			split[split.length-1] += ":"+t.score+".ast";
			Path p = Path.of(path, split);
			if(!Main.printToFile(p, t.tree.toString(),false)) {
				return false;
			}
			Main.debug("Wrote: "+p.toString()+" to disk");
		}
		return true;
	}
	private String prefix = "";
	/**
	 * Loads ASTTestcases from the directory
	 * Syntax of the saved files Name:score.ast
	 */
	public List<ASTTestcase> loadFromDirectory(Path pathToDirectory) {

		String root = pathToDirectory.getFileName().toString();
		FileVisitor<Path> files = new FileVisitor<Path>() {
			
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				//exc.printStackTrace();
				return FileVisitResult.TERMINATE;
			}
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if(!attrs.isDirectory()) {
					String name = file.getFileName().toString();
					name = name.substring(0,name.lastIndexOf("."));
					String[] elements = name.split(":");
					if(elements.length != 2) {
						main.Main.debug("The file: \""+file.getFileName().toString()+"\" does not fit the given structure of <Name>:<Score>.ast and has been skipped");
						return FileVisitResult.CONTINUE;
					}
					testcases.add(new ASTTestcase(prefix+elements[0], new ASTTree(Main.getFromPath(file)), Integer.parseInt(elements[1])));
				}
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if(!dir.equals(pathToDirectory))
				prefix += dir.getFileName().toString()+".";
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if(!dir.equals(pathToDirectory))
				prefix = prefix.substring(0,prefix.lastIndexOf(dir.getFileName().toString()));
				return FileVisitResult.CONTINUE;
			}
		};
		try {
			Files.walkFileTree(pathToDirectory, files);
		} catch (IOException e) {
			Main.debug("File unable to be loaded");
		}
		return testcases;
	}
}
