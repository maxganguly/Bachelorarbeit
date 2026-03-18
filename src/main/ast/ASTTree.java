package main.ast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ASTTree {
	public static final String OFFSET = "    ";

	public final String tag;
	public final String name;
	public final String type;
	public final ASTTree parent;
	public final List<ASTTree> children;

	public final static Set<String> GENERALIZE_TO_LOOP = Set.of(new String[] { "forloop", "loop", "while", "dowhile" });
	public final static Set<String> GENERALIZE_KEEP_NAMES = Set.of(new String[] { "lit", "mc" });
	public final static Set<String> REMOVE_FROM_GENERALIZE = Set
			.of(new String[] { "unary", "var", "assign", "binary" });

	public ASTTree(String tag, String name, String type, ASTTree parent, List<ASTTree> children) {
		this.tag = tag;
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.children = children;
	}

	public ASTTree(String tag, String name, String type, ASTTree parent) {
		this.tag = tag;
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.children = new LinkedList<ASTTree>();
	}

	public ASTTree(Object tag, Object name, Object type, ASTTree parent) {
		this.tag = tag != null ? tag.toString() : null;
		this.name = name != null ? name.toString() : null;
		this.type = type != null ? type.toString() : null;
		this.parent = parent;
		this.children = new LinkedList<ASTTree>();
	}

	public ASTTree(String source) {
		this(source, null);
	}

	public static ASTTree getFromPath(Path p) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(p.toAbsolutePath().toString()));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		return new ASTTree(sb.toString());
	}

	public ASTTree(String source, ASTTree parent) {
		this.parent = null;
		this.children = new LinkedList<ASTTree>();
		int tagstart = source.indexOf('<');
		int offset = tagstart / OFFSET.length();
		int i = 0;
		for (i = tagstart + 1; Character.isAlphabetic(source.charAt(i)); i++)
			;
		tag = source.substring(tagstart + 1, i);
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
			for (; source.charAt(i) != '\"'; i++)
				;
			name = source.substring(j, i);
			i++; // move to the whitespace after the "
		}
		if (source.charAt(++i) == 't') { // look for type at the next character
			i += 6; // Move to the first character of the type
			int j = i;
			for (; source.charAt(i) != '\"'; i++)
				;
			type = source.substring(j, i);
			i++; // move to the whitespace after the "
		}
		this.name = name;
		this.type = type;
		if (source.indexOf("\n" + OFFSET.repeat(offset) + "</" + tag + ">") == -1)
			System.out.println("suboptimal");
		String block = source.substring(source.indexOf("\n") + 1,
				source.indexOf("\n" + OFFSET.repeat(offset) + "</" + tag + ">") + 1);
		if (block.lastIndexOf("\n") != -1) {
			// block = block.substring(0,block.lastIndexOf("\n"));
			int cutoff = 0;
			while (!block.isBlank()) {
				if (block.startsWith(OFFSET.repeat(offset + 1))) {
					ASTTree tree = new ASTTree(block, this);
					this.children.add(tree);
					if (tree.tag.equals("lit")) {
						cutoff = block.indexOf("</lit>\n") + 7;
					} else {
						cutoff = block.indexOf("\n" + OFFSET.repeat(offset + 1) + "</" + tree.tag + ">")
								+ (OFFSET.length() * (offset + 1) + 1) // add offset length
								+ (4 + tree.tag.length()); // add </tag> length
					}
					if (cutoff >= block.length())
						return;
					block = block.substring(cutoff, block.length());
				}
			}
		}

	}

	public String toString() {
		return toString(0);
	}

	private String toString(int offset) {
		if (tag.equals("lit")) {
			return OFFSET.repeat(offset) + "<lit>[[" + name + "]]</lit>\n";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(OFFSET.repeat(offset));
		sb.append('<' + tag);
		if (name != null) {
			sb.append("=\"");
			sb.append(name);
			sb.append("\"");
		}
		if (type != null) {
			sb.append(" type=\"");
			sb.append(type);
			sb.append("\"");
		}
		sb.append(">\n");
		if (children != null && children.size() != 0)
			for (ASTTree t : children) {
				sb.append(t.toString(offset + 1));
			}
		sb.append(OFFSET.repeat(offset));
		sb.append("</" + tag + ">\n");
		return sb.toString();
	}

	private static boolean strEqual(String s1, String s2) {
		if (s1 != null ^ s2 != null) {
			return false;
		}
		if (s1 == null && s2 == null)
			return true;
		return s1.equals(s2);
	}

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
				if (matched == false) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

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
				if (matched == true) {
					return true;
				}
			}
			return true;

		}
		return false;
	}

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
				if (matched == true) {
					return true;
				}
			}
			return true;

		}
		return false;
	}

	public List<ASTTree> getTreesWithTag(String tag) {
		LinkedList<ASTTree> ll = new LinkedList<ASTTree>();
		if (this.tag.equals(tag)) {
			ll.add(this);
			return ll;
		}
		for (ASTTree t : children) {
			ll.addAll(t.getTreesWithTag(tag));
		}
		return ll;
	}

	public ASTTree generalize() {
		String tag = this.tag;
		String name = this.name;
		if (REMOVE_FROM_GENERALIZE.contains(tag))
			return null;
		if (GENERALIZE_TO_LOOP.contains(tag)) {
			tag = "loop";
		}
		if (!GENERALIZE_KEEP_NAMES.contains(tag)) { // remove all names exept literals
			name = null;
		}
		ASTTree astTree = new ASTTree(tag, name, null, parent);
		ASTTree temp;
		for (ASTTree child : children) {
			temp = child.generalize();
			if (temp != null)
				astTree.children.add(temp);
		}
		return astTree;
	}

	public ASTTree generalize(Map<String, String> generalize_to, Set<String> remove) {
		String tag = this.tag;
		String name = this.name;
		if (remove.contains(tag))
			return null;
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
			if (temp != null)
				astTree.children.add(temp);
		}
		return astTree;
	}

	public static ASTTree fromString(String source) {
		return new ASTTree(source);
	}
}
