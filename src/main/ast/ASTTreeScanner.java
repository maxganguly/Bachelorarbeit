package main.ast;

import java.util.stream.Collectors;

import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreeScanner;

public class ASTTreeScanner extends TreeScanner<ASTTree, ASTTree> {
	public static final boolean DEBUG = false;

	@Override
	public ASTTree reduce(ASTTree r1, ASTTree r2) {
		if (r1 == null)
			return r2;
		if (r2 == null)
			return r1;
		ASTTree t = new ASTTree();
		t.children.add(r1);
		t.children.add(r2);
		return t;
	}

	@Override
	public ASTTree visitCompilationUnit(CompilationUnitTree node, ASTTree p) {
		ASTTree r = new ASTTree("file", null, null, p, node.toString());
		ASTTree temp = scan(node.getImports(), r);
		if (temp != null)
			r.children.add(temp);
		temp = scan(node.getTypeDecls(), r);
		if (temp != null)
			r.children.add(temp);
		temp = scan(node.getModule(), r);
		if (temp != null)
			r.children.add(temp);
		return r;
	}

	@Override
	public ASTTree visitImport(ImportTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("import", node.getQualifiedIdentifier().toString(), null, p, node.toString());
		return result;
	}

	@Override
	public ASTTree visitClass(ClassTree node, ASTTree p) {
		debugOutput(node);
		// Does ignore visibility, modifiers, interfaces, inheritance and will not work
		// if anonymous
		ASTTree result = new ASTTree("class", node.getSimpleName(), null, p, node.toString());
		// result += node.accept(this, p+1)+'\n';
		if (node.getMembers() != null)
			for (Tree t : node.getMembers()) {
				result.children.add(t.accept(this, result));
			}
		return result;// + super.visitClass(node, p);
	}

	@Override
	public ASTTree visitMethod(MethodTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("method", node.getName().toString() + node.getParameters().stream().sequential()
				.map(t -> (t.getType().toString())).collect(Collectors.joining(",", "(", ")")), null, p, node.toString());
		ASTTree head = new ASTTree("head", null, null, result, node.toString());
		result.children.add(head);
		if (node.getParameters() != null)
			for (Tree t : node.getParameters()) {
				head.children.add(t.accept(this, head));
			}
		if (node.getBody() != null)
			result.children.add(node.getBody().accept(this, result));
		return result;// + super.visitMethod(node, p);
		// return super.visitMethod(node, p);
	}

	@Override
	public ASTTree visitVariable(VariableTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("var", node.getName(), node.getType(), p, node.toString());
		if (node.getInitializer() != null)
			result.children.add(node.getInitializer().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitBlock(BlockTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("block", null, null, p, node.toString());
		// result += node.accept(this, p+1)+'\n';
		if (node.getStatements() != null)
			for (Tree t : node.getStatements()) {
				result.children.add(t.accept(this, result));
			}
		return result;// super.visitBlock(node, p);
	}

	@Override
	public ASTTree visitDoWhileLoop(DoWhileLoopTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("dowhile", null, null, p, node.toString());
		ASTTree head = new ASTTree("head", null, null, result, node.toString());
		if (node.getCondition() != null) {
			ASTTree condition = new ASTTree("condition", null, null, head, node.toString());
			condition.children.add(node.getCondition().accept(this, condition));
			head.children.add(condition);
		}
		result.children.add(head);
		if (node.getStatement() != null) {
			ASTTree body = new ASTTree("body", null, null, result, node.toString());
			body.children.add(node.getStatement().accept(this, body));
			result.children.add(body);
		}
		return result;
	}

	@Override
	public ASTTree visitWhileLoop(WhileLoopTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("while", null, null, p, node.toString());
		ASTTree head = new ASTTree("head", null, null, result, node.toString());
		if (node.getCondition() != null) {
			ASTTree condition = new ASTTree("condition", null, null, head, node.toString());
			condition.children.add(node.getCondition().accept(this, condition));
			head.children.add(condition);
		}
		result.children.add(head);
		if (node.getStatement() != null) {
			ASTTree body = new ASTTree("body", null, null, result, node.toString());
			body.children.add(node.getStatement().accept(this, body));
			result.children.add(body);
		}
		return result;
	}

	@Override
	public ASTTree visitForLoop(ForLoopTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("for", null, null, p, node.toString());
		ASTTree head = new ASTTree("head", null, null, result, node.toString());

		if (node.getInitializer() != null) {

			ASTTree init = new ASTTree("init", null, null, head, node.toString());
			for (Tree t : node.getInitializer()) {
				init.children.add(t.accept(this, init));
			}
			head.children.add(init);
		}
		if (node.getCondition() != null) {
			ASTTree condition = new ASTTree("condition", null, null, head, node.toString());
			condition.children.add(node.getCondition().accept(this, condition));
			head.children.add(condition);
		}

		if (node.getUpdate() != null) {

			ASTTree update = new ASTTree("update", null, null, head, node.toString());
			for (Tree t : node.getUpdate()) {
				update.children.add(t.accept(this, update));
			}
			head.children.add(update);
		}
		result.children.add(head);

		ASTTree body = new ASTTree("body", null, null, result, node.toString());
		body.children.add(node.getStatement().accept(this, body));
		result.children.add(body);

		return result;
	}

	@Override
	public ASTTree visitEnhancedForLoop(EnhancedForLoopTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("foreach", null, null, p, node.toString());
		ASTTree head = new ASTTree("head", null, null, result, node.toString());
		if(node.getVariable() != null) {
		ASTTree init = new ASTTree("iteration", node.getVariable(), node.getVariable().getType(), head, node.toString());
		head.children.add(init);
		result.children.add(head);
		}
		if(node.getStatement() != null) {
		ASTTree body = new ASTTree("body", null, null, result, node.toString());
		body.children.add(node.getStatement().accept(this, body));
		result.children.add(body);
		}
		return result;
	}

	@Override
	public ASTTree visitLabeledStatement(LabeledStatementTree node, ASTTree p) {
		debugOutput(node);
		return new ASTTree("label", node.getLabel(), null, p, node.toString());
	}

	@Override
	public ASTTree visitSwitch(SwitchTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("switch", null, null, p, node.toString());
		ASTTree head = new ASTTree("head", null, null, result, node.toString());
		head.children.add(node.getExpression().accept(this, head));
		result.children.add(head);

		ASTTree body = new ASTTree("body", null, null, result, node.toString());
		for (Tree t : node.getCases()) {
			body.children.add(t.accept(this, body));
		}
		result.children.add(body);

		return result;
	}

	@Override
	public ASTTree visitCase(CaseTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("case", null, null, p, node.toString());
		ASTTree head = new ASTTree("head", null, null, result, node.toString());
		for (Tree t : node.getExpressions()) {
			head.children.add(t.accept(this, head));
		}
		result.children.add(head);

		ASTTree body = new ASTTree("body", null, null, result, node.toString());
		if (node.getStatements() != null)
			for (Tree t : node.getStatements()) {
				body.children.add(t.accept(this, body));
			}
		result.children.add(body);

		return result;
	}

	@Override
	public ASTTree visitTry(TryTree node, ASTTree p) {
		debugOutput(node);
		ASTTree block = new ASTTree("block", null, null, p, node.toString());
		ASTTree tryblock = new ASTTree("try", null, null, block, node.toString());
		tryblock.children.add(node.getBlock().accept(this, tryblock));
		block.children.add(tryblock);
		// ASTTree catchblock = new ASTTree("catch", null, null, block);
		for (Tree t : node.getCatches()) {
			block.children.add(t.accept(this, block));
		}
		// block.children.add(catchblock);
		if (node.getFinallyBlock() != null) {
			ASTTree finallyblock = new ASTTree("finally", null, null, block, node.toString());
			finallyblock.children.add(node.getFinallyBlock().accept(this, block));
			block.children.add(finallyblock);
		}
		return block;
	}

	@Override
	public ASTTree visitCatch(CatchTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("catch", null, null, p, node.toString());
		if(node.getBlock() != null)
		result.children.add(node.getBlock().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitConditionalExpression(ConditionalExpressionTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("ternary", null, null, p, node.toString());
		ASTTree head = new ASTTree("head", null, null, result, node.toString());
		head.children.add(node.getCondition().accept(this, head));
		result.children.add(head);
		ASTTree then = new ASTTree("then", null, null, result, node.toString());
		then.children.add(node.getTrueExpression().accept(this, then));
		result.children.add(then);
		if (node.getFalseExpression() != null) {
			ASTTree otherwise = new ASTTree("else", null, null, result, node.toString());
			otherwise.children.add(node.getFalseExpression().accept(this, otherwise));
			result.children.add(otherwise);
		}

		return result;
	}

	@Override
	public ASTTree visitIf(IfTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("if", null, null, p, node.toString());
		ASTTree head = new ASTTree("head", null, null, result, node.toString());
		head.children.add(node.getCondition().accept(this, head));
		result.children.add(head);
		ASTTree then = new ASTTree("then", null, null, result, node.toString());
		if(node.getThenStatement() != null)
		then.children.add(node.getThenStatement().accept(this, then));
		result.children.add(then);
		if (node.getElseStatement() != null) {
			ASTTree otherwise = new ASTTree("else", null, null, result, node.toString());
			otherwise.children.add(node.getElseStatement().accept(this, otherwise));
			result.children.add(otherwise);
		}
		return result;
	}

	@Override
	public ASTTree visitBreak(BreakTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("break", node.getLabel(), null, p, node.toString());
		return result;
	}

	@Override
	public ASTTree visitContinue(ContinueTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("continue", node.getLabel(), null, p, node.toString());
		return result;
	}

	@Override
	public ASTTree visitReturn(ReturnTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("return", null, null, p, node.toString());
		if (node.getExpression() != null)
			result.children.add(node.getExpression().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitThrow(ThrowTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("throw", node.getExpression(), null, p, node.toString());
		return result;
	}

	@Override
	public ASTTree visitAssert(AssertTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("assert", null, null, p, node.toString());
		ASTTree condition = new ASTTree("condition", null, null, result, node.toString());
		condition.children.add(node.getCondition().accept(this, condition));
		result.children.add(condition);
		ASTTree detail = new ASTTree("detail", null, null, result, node.toString());
		detail.children.add(node.getCondition().accept(this, detail));
		result.children.add(detail);
		return result; // result.append(super.visitAssert(node, p)).toASTTree();
	}

	@Override
	public ASTTree visitMethodInvocation(MethodInvocationTree node, ASTTree p) {
		debugOutput(node);
		ExpressionTree et = node.getMethodSelect();
		if (et.getKind() == Kind.MEMBER_SELECT)
			return visitMemberSelect((MemberSelectTree) et, p);
		ASTTree result = new ASTTree("mc", node.getMethodSelect(), null, p, node.toString());
		for (Tree t : node.getArguments()) {
			result.children.add(t.accept(this, result));
		}
		return result;
	}

	@Override
	public ASTTree visitNewArray(NewArrayTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("array", node.getType(), null, p, node.toString());
		if (node.getDimensions() != null) {
			ASTTree head = new ASTTree("dimensions", null, null, result, node.toString());
			for (Tree t : node.getDimensions()) {
				head.children.add(t.accept(this, head));
			}
			if (head.children.size() > 0)
				result.children.add(head);
		}
		if (node.getDimensions() != null && node.getInitializers() != null) {
			ASTTree head = new ASTTree("initializer", null, null, result, node.toString());
			for (Tree t : node.getInitializers()) {
				head.children.add(t.accept(this, head));
			}
			if (head.children.size() > 0)
				result.children.add(head);
		}
		return result;
		// return super.visitNewArray(node, p);
	}

	@Override
	public ASTTree visitLambdaExpression(LambdaExpressionTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("lambda", null, node.getParameters().stream().sequential()
				.map(t -> (t.getType().toString())).collect(Collectors.joining(",", "(", ")")), p, node.toString());
		result.children.add(node.getBody().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitAssignment(AssignmentTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("assign", node.getVariable().toString(), null, p, node.toString());
		if(node.getExpression() != null)
		result.children.add(node.getExpression().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitUnary(UnaryTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("unary", node.getKind(), null, p, node.toString());
		result.children.add(node.getExpression().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitBinary(BinaryTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("binary", node.getKind(), null, p, node.toString());
		result.children.add(node.getLeftOperand().accept(this, result));
		result.children.add(node.getRightOperand().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitTypeCast(TypeCastTree node, ASTTree p) {
		debugOutput(node);
		// Probably not needed
		ASTTree result = new ASTTree("cast", node.getType(), null, p, node.toString());
		result.children.add(node.getExpression().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitIdentifier(IdentifierTree node, ASTTree p) {
		debugOutput(node);
		// Probably not needed single variable name
		return new ASTTree("var", node.getName(), null, p, node.toString());
	}

	@Override
	public ASTTree visitLiteral(LiteralTree node, ASTTree p) {
		debugOutput(node);
		return new ASTTree("lit", node.getValue() != null ? node.getValue() : "null", null, p, node.toString());
	}

	@Override
	public ASTTree visitCompoundAssignment(CompoundAssignmentTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("assign", node.getVariable(), null, p, node.toString());

		ASTTree bin = new ASTTree("binary", node.getKind(), null, result, node.toString());
		bin.children.add(node.getVariable().accept(this, result));
		bin.children.add(node.getExpression().accept(this, result));
		result.children.add(bin);
		return result;
	}

	@Override
	public ASTTree visitArrayAccess(ArrayAccessTree node, ASTTree p) {
		debugOutput(node);

		ASTTree result = node.getExpression().accept(this, p);// new ASTTree("arrayaccess", node.getExpression(),
																// node.getKind(), p);
		ASTTree index = new ASTTree("index", null, null, result, node.toString());
		result.children.add(index);
		index.children.add(node.getIndex().accept(this, index));
		return result;
	}

	@Override
	public ASTTree visitMemberSelect(MemberSelectTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("mc", node.getIdentifier().toString(), node.getExpression().toString(), p, node.toString());
		//result.children.add(node.getExpression().accept(this, result));
		return result;
		// return super.visitMemberSelect(node, p);
	}

	@SuppressWarnings("unused")
	private static void debugOutput(ASTTree output) {
		if (DEBUG)
			System.out.println(output);
	}

	private static void debugOutput(Tree output) {
		if (DEBUG)
			System.out.println(output.getKind().toString());
	}

}
