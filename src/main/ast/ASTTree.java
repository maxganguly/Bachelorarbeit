package main.ast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ASTTree {
	public static final String OFFSET = "    ";

	public enum ORDER {
		ORDERED, UNORDERED;

		public static ORDER fromString(String s) {
			if (s.trim().equalsIgnoreCase("ORDERED")) {
				return ORDER.ORDERED;
			}
			if (s.trim().equalsIgnoreCase("UNORDERED")) {
				return ORDER.UNORDERED;
			}
			return null;
		}
	}

	public enum EVALUATION_MODE {
		ALL, ANY, NONE, OPTIONAL;// Currently optional will be ignored as it will always return true

		public static EVALUATION_MODE fromString(String s) {
			if (s.trim().equalsIgnoreCase("ALL")) {
				return EVALUATION_MODE.ALL;
			}
			if (s.trim().equalsIgnoreCase("ANY")) {
				return EVALUATION_MODE.ANY;
			}
			if (s.trim().equalsIgnoreCase("NONE")) {
				return EVALUATION_MODE.NONE;
			}
			if (s.trim().equalsIgnoreCase("OPTIONAL")) {
				return EVALUATION_MODE.OPTIONAL;
			}
			return null;
		}
	}

	public final String tag;
	public final String name;
	public final String type;
	/**
	 * default UNORDERED
	 */
	public ORDER order;
	/**
	 * default ALL
	 */
	public EVALUATION_MODE eval_mode;
	public final ASTTree parent;
	public final List<ASTTree> children;

	public final static Set<String> GENERALIZE_TO_LOOP = Set.of(new String[] { "for", "foreach", "while", "dowhile" });
	public final static Set<String> GENERALIZE_KEEP_NAMES = Set.of(new String[] { "lit", "mc", "import", "method" });
	public final static Set<String> REMOVE_FROM_GENERALIZE = Set
			.of(new String[] { "unary", "var", "assign", "binary", "init", "update"});
	public final static Set<String> PURE_STRUCTURE = Set.of(
			"method","forloop","loop","while","dowhile","if");
	public final static Set<String> OPERATORS_COMMUTATIVE = Set.of(
			"PLUS","MULTIPLY","EQUAL_TO","NOT_EQUAL_TO","AND","XOR","OR");
			
	/**
	 * Exists only to return the original code corresponding to the ASTTree, does not exist of it has been read from xml
	 * Maybe getCode might generate it dynamically sometime...
	 */
	public final String code;
	/**
	 *
	 * @param tag
	 * @param name
	 * @param type
	 * @param parent
	 * @param children
	 */
	public ASTTree(String tag, String name, String type, ASTTree parent, List<ASTTree> children) {
		this(tag,name,type,parent,children,null,null,null);
	}
	/**
	 *
	 * @param tag
	 * @param name
	 * @param type
	 * @param parent
	 * @param children
	 * @param code
	 */
	public ASTTree(String tag, String name, String type, ASTTree parent, List<ASTTree> children,String code) {
		this(tag,name,type,parent,children,null,null,code);
	}
	/**
	 *
	 * @param tag
	 * @param name
	 * @param type
	 * @param parent
	 * @param children
	 * @param order
	 * @param eval_mode
	 * @param code
	 */
	public ASTTree(String tag, String name, String type, ASTTree parent, List<ASTTree> children, ORDER order, EVALUATION_MODE eval_mode, String code) {
		this.tag = tag;
		this.name = name;
		this.type = type;
		this.parent = parent;
		children.remove(null);
		this.children = children;
		this.order = order;
		this.eval_mode = eval_mode;
		this.code = code;
	}

	/**
	 *
	 * @param tag
	 * @param name
	 * @param type
	 */
	public ASTTree(String tag, String name, String type) {
		this(tag, name, type, null, new LinkedList<>());
	}
	/**
	 *
	 * @param tag
	 * @param name
	 * @param type
	 * @param parent
	 */
	public ASTTree(String tag, String name, String type, ASTTree parent) {
		this(tag, name, type, parent, new LinkedList<>());
	}

	/**
	 *
	 * @param tag
	 * @param name
	 * @param type
	 * @param parent
	 * @param code
	 */
	public ASTTree(String tag, String name, String type, ASTTree parent, String code) {
		this(tag, name, type, parent, new LinkedList<>(), code);
	}
	/**
	 *
	 */
	public ASTTree() {
		this("","","");
	}
	/**
	 *
	 * @param tag
	 * @param name
	 * @param type
	 * @param parent
	 */
	public ASTTree(Object tag, Object name, Object type, ASTTree parent) {
		this.tag = tag != null ? tag.toString() : null;
		this.name = name != null ? name.toString() : null;
		this.type = type != null ? type.toString() : null;
		this.parent = parent;
		this.children = new LinkedList<>();
		this.code = null;
	}
	/**
	 *
	 * @param tag
	 * @param name
	 * @param type
	 * @param parent
	 * @param code
	 */
	public ASTTree(Object tag, Object name, Object type, ASTTree parent,String code) {
		this.tag = tag != null ? tag.toString() : null;
		this.name = name != null ? name.toString() : null;
		this.type = type != null ? type.toString() : null;
		this.parent = parent;
		this.children = new LinkedList<>();
		this.code = code;
	}
	/**
	 *
	 * @param source
	 */
	public ASTTree(String source) {
		this(source, null);
	}

	/**
	 * Generates an ASTTree from the xml in the given path
	 *
	 * @param p the path of the xml file of the AST to be generated
	 * @return an ASTTree based on the xml in the given path
	 * @throws IOException if the file does not exist
	 */
	public static ASTTree getFromPath(Path p) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(p.toAbsolutePath().toString()));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		return new ASTTree(sb.toString());
	}

	/**
	 * Is the current Tree at least as specific as the given on this Node, Does
	 * <b>NOT</b> check the parents, children or evaluation modi
	 *
	 * @param tree the AST to compare to
	 * @return true all if values written in the given tree are also in the current
	 *         tree
	 */
	public boolean isAtleastAsSpecific(ASTTree tree) {
		if (!strEqual(tree.tag,this.tag)) {
			if (GENERALIZE_TO_LOOP.contains(tree.tag) && this.tag.equalsIgnoreCase("loop") ||
					GENERALIZE_TO_LOOP.contains(this.tag) && tree.tag.equalsIgnoreCase("loop")) { // check if tree has been generalized																// and this has not
				return true;
			}
			return false;
		}
		if ((tree.type != null && !strEqual(tree.type, this.type)) || 
			(tree.name !=null && !strEqual(tree.name, this.name))) { // if not null and not the same -> false
			return false;
		}
		return true;
	}

	/**
	 * Generates an ASTTree based on a xml String with a given parent
	 *
	 * @param source the xml String of the Tree
	 * @param parent the parent of the xml tree, can be null
	 */
	public ASTTree(String source, ASTTree parent) {
		source = source.trim();
		this.parent = null;
		this.children = new LinkedList<>();
		this.code = null;
		int tagstart = source.indexOf('<');
		int i = 0;
		for (i = tagstart + 1; Character.isAlphabetic(source.charAt(i)); i++) {

		}
		this.tag = source.substring(tagstart + 1, i);
		if (tag.equals("lit")) {
			name = source.substring(source.indexOf("[[") + 2, source.indexOf("]]"));
			type = null;
			return;
		}
		String name = null;
		String type = null;

		if (source.charAt(i) == '=') {
			i += 2; // Move to the first character of the name
			int j = i;
			for (; source.charAt(i) != '\"'; i++) {

			}
			name = source.substring(j, i);
			i++; // move to the whitespace after the "
		}
		String params = source.substring(source.indexOf('<'), source.indexOf('>'));
		String temp;
		int j;
		if (params.contains("type")) {
			j = params.indexOf("type")+6;
			temp = params.substring(j);
			type = params.substring(j, j+temp.indexOf('\"'));
		}
		if (params.contains("mode")) {
			j = params.indexOf("mode")+6;
			temp = params.substring(j);
			this.eval_mode = EVALUATION_MODE.fromString(params.substring(j, j+temp.indexOf('\"')));
		}
		if (params.contains("order")) {
			j = params.indexOf("order")+7;
			temp = params.substring(j);
			this.order = ORDER.fromString(params.substring(j, j+temp.indexOf('\"')));
		}
		this.name = name;
		this.type = type;
		if (source.indexOf("</" + tag + ">") == -1) {
			System.out.println("suboptimal");
		}
		String block = source.substring(source.indexOf("\n") + 1, findcutoff(source, tag));
		if (block.lastIndexOf("\n") != -1) {
			int cutoff = 0;
			while (!block.isBlank()) {
				// if (block.startsWith(OFFSET.repeat(offset + 1))) {
				block = block.trim();
				ASTTree tree = new ASTTree(block, this);
				this.children.add(tree);
				if (tree.tag.equals("lit")) {
					cutoff = block.indexOf("</lit>") + 7;
				} else {
					cutoff = findcutoff(block, tree.tag);
				}
				if (cutoff >= block.length()) {
					return;
				}
				if (cutoff == -1) {
					System.out.println("Ungut");
				}
				block = block.substring(cutoff + 3 + tree.tag.length(), block.length());
				// }
			}
		}

	}

	private int findcutoff(String block, String tag) {

		int flagcounter = 0; // as is should start with none as the startflag is in the block
		int i = 0;
		String startflag = "<" + tag;
		String endflag = "</" + tag + ">";
		int startflaglength = startflag.length();
		int endflaglength = endflag.length();
		String temp;
		for (; i < block.length(); i++) {
			temp = block.substring(i, i + startflaglength);
			if (temp.equals(startflag)) {
				flagcounter++;
			}
			temp = block.substring(i, i + endflaglength);
			if (temp.equals(endflag)) {
				flagcounter--;
				if (flagcounter == 0) {
					return i;
				}
			}
		}
		if (i != block.length()) {
			return i;
		}
		return -1;
		/*
		 *
		 * return block.indexOf("</" + tag + ">") + (OFFSET.length() * (offset + 1) + 1)
		 * // add offset length + (4 + tag.length()); // add </tag> length;
		 */
	}

	/**
	 * Returns an xml representation of the ASTTree
	 */
	@Override
	public String toString() {
		return toString(0, true);
	}

	/**
	 * Returns a to String representation of the current node only
	 * @return
	 */
	public String toStringSingle() {
		return toString(0,false);
	}

	private String toString(int offset, boolean recursion) {
		if (tag.equals("lit")) {
			return OFFSET.repeat(offset) + "<lit>[[" + name + "]]</lit>\n";
		}
		StringBuilder sb = new StringBuilder();

		if (tag == null || tag.isEmpty()) {
			if(recursion) {
				for (ASTTree t : children) {
					sb.append(t.toString(offset,true));
				}
			}
			return sb.toString();
		}

		sb.append(OFFSET.repeat(offset));
		sb.append('<' + tag);
		if (name != null && !name.isBlank()) {
			sb.append("=\"");
			sb.append(name);
			sb.append("\"");
		}
		if (type != null && !type.isBlank()) {
			sb.append(" type=\"");
			sb.append(type);
			sb.append("\"");
		}
		if (order != null) {
			sb.append(" order=\"");
			sb.append(order);
			sb.append("\"");
		}
		if (eval_mode != null) {
			sb.append(" mode=\"");
			sb.append(eval_mode);
			sb.append("\"");
		}
		sb.append(">\n");
		if(recursion) {
			if (children != null && children.size() != 0) {
				for (ASTTree t : children) {
					sb.append(t.toString(offset + 1,true));
				}
			}
		}
		sb.append(OFFSET.repeat(offset));
		sb.append("</" + tag + ">\n");
		return sb.toString();
	}

	/**
	 * Check if two strings are equal, nullsave,
	 *
	 * @param s1 first String to be compared
	 * @param s2 second String to be compared
	 * @return true if both are null or both are equals
	 */
	private static boolean strEqual(String s1, String s2) {
		if (s1 != null ^ s2 != null) {
			return false;
		}
		if (s1 == null && s2 == null) {
			return true;
		}
		return s1.equals(s2);
	}

	/**
	 * Check if two ASTTrees are exactly equals
	 *
	 * @param other the ASTTree to be compared with this
	 * @return true if they are exactly equals
	 */
	public boolean equals(ASTTree other) {
		if (strEqual(this.tag, other.tag) && strEqual(this.name, other.name) && strEqual(this.type, other.type)
				&& this.children.size() == other.children.size()) {
			for (ASTTree a1 : this.children) {
				boolean matched = false;
				for (ASTTree a2 : other.children) {
					if (a1.equals(a2)) {
						matched = true;
						break;
					}
				}
				if (!matched) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Checks if the current tree contains a given ASTTree with the same parameters
	 * and general structure
	 *
	 * @param search the other ASTTree which needs to be contained in this
	 * @return true if it is contained
	 */
	public boolean containsExact(ASTTree search) {
		if (this.children.isEmpty()) {
			if (!search.children.isEmpty()) {
				// searching for elements with children but has no children
				return false;
			}
			return equals(search);
		}
		if (strEqual(this.tag, search.tag) && strEqual(this.name, search.name) && strEqual(this.type, search.type)) {
			// if root type is accepted check if any child has all children of the searched
			// tree
			for (ASTTree a1 : this.children) {
				boolean matched = true;
				for (ASTTree a2 : search.children) {
					if (!a1.containsExact(a2)) {
						matched = false;
						break;
					}
				}
				if (matched) {
					return true;
				}
			}
			return true;

		}
		return false;
	}

	/**
	 * Checks if the structure of the other is contained in this tree, only
	 * structure (xml tags) not values, types or names
	 *
	 * @param search the tree which needs to be conatined in this
	 * @return true if the structure is contained in this tree
	 */
	public boolean containsStructure(ASTTree search) {
		if (this.children.isEmpty()) {
			if (!search.children.isEmpty()) {
				// searching for elements with children but has no children
				return false;
			}
			return strEqual(this.tag, search.tag);
		}
		if (strEqual(this.tag, search.tag)) {
			// if root type is accepted check if any child has all children of the searched
			// tree
			for (ASTTree a1 : this.children) {
				boolean matched = true;
				for (ASTTree a2 : search.children) {
					if (!a1.containsStructure(a2)) {
						matched = false;
						break;
					}
				}
				if (matched) {
					return true;
				}
			}
			return true;

		}
		return false;
	}

	public boolean evaluate(ASTTree evaluation) {
		if(evaluation.tag == null || evaluation.tag.isBlank()) {
			return true;
		}
		if (this.children.isEmpty()) {
			if (!evaluation.children.isEmpty()) {
				return false;
			}
			return this.isAtleastAsSpecific(evaluation);
		}

		//if this is a filler or is at least as specific as the searched element
		if (evaluation.tag == null || 
				evaluation.tag.isBlank() || 
				this.isAtleastAsSpecific(evaluation)) {
			// if root type is accepted check if any child has all children of the searched
			//if (evaluation.order == null || evaluation.order == ORDER.UNORDERED) {

				boolean needsOrder = (evaluation.order == ORDER.ORDERED);

				// True if needs to be found else otherwise to allow simpler switching
				int count_found = 0;
				int last_found = -1;

				for (ASTTree a1 : evaluation.children) {
					boolean matched = false;
					int index = 0;
					this.children.remove(null);
					for (ASTTree a2 : this.children) {
						//check if the ordering is correct in the children
						if(needsOrder && index > last_found) {
							break;
						}
						// if child of searched node has been found in child of current node
						if (a2.evaluate(a1)) {
							matched = true;
							last_found = index;
							// has already matched
							break;
						}
						index++;
					}
					if (matched) {
						if(evaluation.eval_mode == EVALUATION_MODE.ANY) {
							return true;
						}
						if(evaluation.eval_mode == EVALUATION_MODE.NONE) {
							return false;
						}
						count_found++;
					}
				}
				//If all children of the evaluation node have been found return true
				//Flips the result if eval mode none
				return (count_found == evaluation.children.size()) ^ (evaluation.eval_mode == EVALUATION_MODE.NONE);
			//}
		} else {
			boolean matched = false;
			this.children.remove(null);//Somewhere null is added to the children}
			//As this is not as specifics as the searched we can go to the 
			var list = this.getAll(t -> t.isAtleastAsSpecific(evaluation), false);
			
			for (ASTTree a1 : list) {
				if(a1 == null) {
					continue;
				}
				matched = a1.evaluate(evaluation);
				if (matched) {
					//No need to check for none as it is checked in the deeper recursion
					return true;
				}
			}
			if(!matched && (evaluation.eval_mode == EVALUATION_MODE.NONE))
				return true;
			
		}
		return false;
	}

	/**
	 * Returns all ASTTrees with the given tag, returns only the highest trees with
	 * the given tags
	 *
	 * @param tag the given tag
	 * @return A LIst of all found trees with the tag
	 */
	public List<ASTTree> getTreesWithTag(String tag) {
		return getAll(t -> t.tag.equals(tag), false);
	}
	
	/**
	 * Filters for all elements in the tree
	 * @param filter 
	 * @param depth should the children of a found element be searched
	 * @return a List of all found subtrees
	 */
	public List<ASTTree> getAll(Predicate<ASTTree> filter, boolean depth){
		LinkedList<ASTTree> ll = new LinkedList<>();
		if (filter.test(this)) {
			ll.add(this);
			if(!depth)
			return ll;
		}
		for (ASTTree t : children) {
			if(t!= null)
			ll.addAll(t.getAll(filter, depth));
		}
		return ll;
	}
	
	public ASTTree getFirst(Predicate<ASTTree> filter) {
		if (filter.test(this)) {
			return this;
		}
		for (ASTTree t : children) {
			var temp = t.getFirst(filter);
			if(temp != null)
				return temp;
		}
		return null;
	}

	/**
	 * Returns an equivalent code
	 * Works only if the AST has been generated not if it has been read from xml
	 * @return
	 */
	public String getCode() {
		return this.code;
		/*
		if(this.tag == null || this.tag.isBlank())
			return "";
		if(this.tag.equals("lit"))
			return name;
		StringBuilder sb = new StringBuilder();
		if(this.tag.equals("var")) {
			if(this.children.isEmpty())
				return ((this.type != null)?type + " ":"")+ this.name;
			sb.append(this.type + " " + this.name);
		}
		return sb.toString();
		*/
	}

	/**
	 * Generalizes a given Tree based on the Generalize and Remove Rules in the
	 * class GENERALIZE_TO_LOOP,GENERALIZE_KEEP_NAMES, REMOVE_FROM_GENERALIZE
	 *
	 * @return a new ASTTree which is now generalized
	 */
	public ASTTree generalize() {
		String tag = this.tag;
		String name = this.name;
		if (REMOVE_FROM_GENERALIZE.contains(tag)) {
			return null;
		}
		if (GENERALIZE_TO_LOOP.contains(tag)) {
			tag = "loop";
		}
		if (!GENERALIZE_KEEP_NAMES.contains(tag)) { // remove all names exept literals
			name = null;
		}
		
		ASTTree astTree = new ASTTree(tag, name, null, parent);
		
		ASTTree temp;
		children.remove(null);
		for (ASTTree child : children) {
			if(child == null)
				continue;
			temp = child.generalize();
			if (temp != null) {
				astTree.children.add(temp);
			}
		}
		return astTree;
	}

	/**
	 * Generalizes a given Tree based on the Generalize and Remove rules
	 *
	 * @param generalize_to the generalize Rules e.g (for -> loop)
	 * @param remove        tags to remove e.g (unary)
	 * @return A new generalized ASTTree
	 
	public ASTTree generalize(Map<String, String> generalize_to, Set<String> remove) {
		String tag = this.tag;
		String name = this.name;
		if (remove.contains(tag)) {
			return null;
		}
		if (generalize_to.keySet().contains(tag)) {
			tag = generalize_to.get(tag);
		}
		if (!GENERALIZE_KEEP_NAMES.contains(tag)) { // remove all names except literals
			name = null;
		}
		ASTTree astTree = new ASTTree(tag, name, null, parent);
		ASTTree temp;
		for (ASTTree child : children) {
			temp = child.generalize();
			if (temp != null) {
				astTree.children.add(temp);
			}
		}
		return astTree;
	}*/

	public ASTTree keepOnlyStructure() {
		return keepOnly(PURE_STRUCTURE);
	}

	public ASTTree keepOnly(String... s) {
		return keepOnly(Set.of(s));
	}

	public ASTTree keepOnly(Set<String> tagsToKeep) {
		return keepOnly(s -> tagsToKeep.contains(s.tag));
	}

	public ASTTree keepOnly(Predicate<ASTTree> toKeep) {
		ASTTree astTree;
		if(toKeep.test(this)) {
			astTree = new ASTTree(tag, name, null, parent);
		}else {
			astTree = new ASTTree();
			if(parent == null) {
				astTree = new ASTTree();
			}
		}
		ASTTree temp;
		for (ASTTree child : children) {
			temp = child.keepOnly(toKeep);
			if (temp != null) {
				//if this is an empty tag
				if(temp.tag == null || temp.tag.isBlank()) {
					astTree.children.addAll(temp.children);
				} else {
					astTree.children.add(temp);
				}
			}
		}
		return astTree;
	}

	public ASTTree apply(Function<ASTTree,ASTTree> apply) {
		ASTTree astTree = apply.apply(this);
		for (ASTTree child : astTree.children) {
			child.apply(apply);
		}
		return astTree;
	}

	public int depth() {
		int d = (this.tag != null && !this.tag.isBlank())?1:0;
		if(this.children.size() == 0) {
			return d;
		}
		int maxd = 0;
		for (var t : children) {
			maxd = Math.max(maxd, t.depth());
		}
		return d+maxd;
	}

	/**
	 * Generates an ASTTree based on a xml String with a given parent
	 *
	 * @param source the xml String of the Tree
	 * @return a new ASTTree based on the given xml
	 */
	public static ASTTree fromString(String source) {
		return new ASTTree(source);
	}

	/**
	 * Returns an equivalent node but without the children or parents
	 * @return
	 */
	public ASTTree copyNode() {
		return new ASTTree(tag,name,type,null,new LinkedList<>(),order,eval_mode, code);
	}

	public void walk(Consumer<ASTTree> consumer) {
		consumer.accept(this);
		for(ASTTree child : this.children) {
			child.walk(consumer);
		}
	}
	public void walkBack(Consumer<ASTTree> consumer) {
		for(ASTTree child : this.children) {
			child.walk(consumer);
		}
		consumer.accept(this);
	}


}
