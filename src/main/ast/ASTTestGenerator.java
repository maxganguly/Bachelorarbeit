package main.ast;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import main.generator.Generator;

public class ASTTestGenerator implements Generator{

	ASTTree code;
	
	public ASTTestGenerator(ASTTree code) {
		this.code = code;
	}
	
	@Override
	public List<ASTTestcase> generateTestcases() {
		var list = new LinkedList<ASTTestcase>();
		for(ASTTree method: code.getTreesWithTag("method")) {
			list.addAll(generateTestcases(method));
		}
		return list;
	}
	
	public List<ASTTestcase> generateTestcases(ASTTree method) {
		var list = new LinkedList<ASTTestcase>();
		
		//list.add(new ASTTestcase(method.name+"Exact", method, 10));	
		ASTTree general = method.generalize();
		//list.add(new ASTTestcase(method.name+"General", general, 5));
		ASTTree struct = general.keepOnlyStructure();
		//list.add(new ASTTestcase(method.name+"Structure", struct, 1));
		ASTTree temp = getAllMethodCalls(general);
		ASTTree temp1;
		if(temp != null && temp.depth() > 0) {
			temp1 = method.copyNode();
			temp1.children.add(temp);
			list.add(new ASTTestcase(method.name+"MethodCalls", temp1, 1));
		}
		temp = getHighestNestedLoop(general);
		if(temp != null && temp.depth() > 0) {
			temp1 = method.copyNode();
			temp1.children.add(temp);
			list.add(new ASTTestcase(method.name+"Loops", temp1, 1));
		}return list;
	}
	
	private ASTTree getAllMethodCalls(ASTTree tree) {
		var mc = tree.getTreesWithTag("mc").stream().map(t -> t.keepOnly("mc")).distinct().toList();
		ASTTree t = new ASTTree();
		t.children.addAll(mc);
		return t;
	}
	private ASTTree getHighestNestedLoop(ASTTree tree) {
		var loop = tree.keepOnly("loop");
		return tree.keepOnly("loop");
	}
	
	private ASTTree getRecursion(ASTTree tree) {
		var rec = tree.keepOnly("mc");
		
		return rec;
	}
	
	public boolean saveToDirectory(Path pathToDirectory) {
		return false;
	}
	
	public List<ASTTestcases> loadFromDirectory(Path pathToDirectory) {
		return null;
	}
}
