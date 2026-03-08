package main.ast;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;

public class ASTTreeScanner extends TreeScanner<ASTTree, ASTTree> {
	public static final boolean DEBUG = false;

	@Override
	public ASTTree reduce(ASTTree r1, ASTTree r2) {
		if (r1== null)
			return r2;
		if(r2==null)
			return r1;
		return super.reduce(r1, r2);
	}
	
	@Override
	public ASTTree visitCompilationUnit(CompilationUnitTree node, ASTTree p) {
		return super.visitCompilationUnit(node, p);
	}
	
	@Override
	public ASTTree visitClass(ClassTree node, ASTTree p) {
		debugOutput(node);
		// Does ignore visibility, modifiers, interfaces, inheritance and will not work if anonymous
		ASTTree result = new ASTTree("class", node.getSimpleName(), null, p);
		//result += node.accept(this, p+1)+'\n';
		for (Tree t : node.getMembers()) {
			result.children.add(t.accept(this, result));
		}
		return result;// + super.visitClass(node, p);
	}

	@Override
	public ASTTree visitMethod(MethodTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("method", node.getName().toString() +
				node.getParameters().stream().sequential().map(t -> (t.getType().toString())).collect(Collectors.joining(",","(",")")),
				null ,		p);
		result.children.add(node.getBody().accept(this, result));
		return result;// + super.visitMethod(node, p);
		//return super.visitMethod(node, p);
	}

	@Override
	public ASTTree visitVariable(VariableTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("var", node.getName(), node.getType(), p);
		result.children.add(node.getInitializer().accept(this, result));
		return result;
		}


	@Override
	public ASTTree visitBlock(BlockTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("block", null, null, p);
		//result += node.accept(this, p+1)+'\n';
		for (Tree t : node.getStatements()) {
			result.children.add(t.accept(this, result));
		}
		return result;//super.visitBlock(node, p);
	}
	
	@Override
	public ASTTree visitDoWhileLoop(DoWhileLoopTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("dowhile", null, null, p);
		ASTTree head = new ASTTree("head", null, null, result);
		ASTTree condition = new ASTTree("condition", null, null, head);
		condition.children.add(node.getCondition().accept(this, condition));
		head.children.add(condition);
		result.children.add(head);
		ASTTree body = new ASTTree("body", null, null, result);
		body.children.add(node.getStatement().accept(this, body));
		result.children.add(body);
		return result;
	}

	@Override
	public ASTTree visitWhileLoop(WhileLoopTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("while", null, null, p);
		ASTTree head = new ASTTree("head", null, null, result);
		ASTTree condition = new ASTTree("condition", null, null, head);
		condition.children.add(node.getCondition().accept(this, condition));
		head.children.add(condition);
		result.children.add(head);
		ASTTree body = new ASTTree("body", null, null, result);
		body.children.add(node.getStatement().accept(this, body));
		result.children.add(body);
		return result;
	}

	@Override
	public ASTTree visitForLoop(ForLoopTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("forloop", null, null, p);
		ASTTree head = new ASTTree("head", null, null, result);
		
		ASTTree init = new ASTTree("init", null, null, head);
		for (Tree t : node.getInitializer()) {
			init.children.add(t.accept(this, init));
		}
		head.children.add(init);
		
		ASTTree condition = new ASTTree("condition", null, null, head);
		condition.children.add(node.getCondition().accept(this, condition));
		head.children.add(condition);
		
		ASTTree update = new ASTTree("update", null, null, head);
		for (Tree t : node.getUpdate()) {
			update.children.add(t.accept(this, update));
		}
		head.children.add(update);
		result.children.add(head);
		
		
		ASTTree body = new ASTTree("body", null, null, result);
		body.children.add(node.getStatement().accept(this, body));
		result.children.add(body);
		
		return result;
	}

	@Override
	public ASTTree visitEnhancedForLoop(EnhancedForLoopTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("forloop", null, null, p);
		ASTTree head = new ASTTree("head", null, null, result);
		
		ASTTree init = new ASTTree("iteration", node.getVariable(), node.getVariable().getType(), head);
		head.children.add(init);
		result.children.add(head);
		
		
		ASTTree body = new ASTTree("body", null, null, result);
		body.children.add(node.getStatement().accept(this, body));
		result.children.add(body);
		
		return result;
	}

	@Override
	public ASTTree visitLabeledStatement(LabeledStatementTree node, ASTTree p) {
		debugOutput(node);
		return new ASTTree("label", node.getLabel(), null, p);
	}

	@Override
	public ASTTree visitSwitch(SwitchTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("switch", null, null, p);
		ASTTree head = new ASTTree("head", null, null, result);
		head.children.add(node.getExpression().accept(this, head));
		result.children.add(head);
		
		
		ASTTree body = new ASTTree("body", null, null, result);
		for (Tree t : node.getCases()) {
			body.children.add(t.accept(this, body));
		}
		result.children.add(body);
		
		return result;
	}

	@Override
	public ASTTree visitCase(CaseTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("case", null, null, p);
		ASTTree head = new ASTTree("head", null, null, result);
		for (Tree t : node.getExpressions()) {
			head.children.add(t.accept(this, head));
		}
		result.children.add(head);
		
		
		ASTTree body = new ASTTree("body", null, null, result);
		if(node.getStatements() != null)
		for (Tree t : node.getStatements()) {
			body.children.add(t.accept(this, body));
		}
		result.children.add(body);
		
		return result;
	}

	@Override
	public ASTTree visitTry(TryTree node, ASTTree p) {
		debugOutput(node);
		ASTTree block = new ASTTree("block", null, null, p);
		ASTTree tryblock = new ASTTree("try", null, null, block);
		tryblock.children.add(node.getBlock().accept(this, tryblock));
		block.children.add(tryblock);
		//ASTTree catchblock = new ASTTree("catch", null, null, block);
		for (Tree t : node.getCatches()) {
			block.children.add(t.accept(this, block));
		}
		//block.children.add(catchblock);
		ASTTree finallyblock = new ASTTree("finally", null, null, block);
		finallyblock.children.add(node.getFinallyBlock().accept(this, block));
		block.children.add(finallyblock);
		return block;
	}

	@Override
	public ASTTree visitCatch(CatchTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("catch", null, null, p);
		result.children.add(node.getBlock().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitConditionalExpression(ConditionalExpressionTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("ternary", null, null, p);
		ASTTree head = new ASTTree("head", null, null, result);
		head.children.add(node.getCondition().accept(this, head));
		result.children.add(head);
		ASTTree then = new ASTTree("then", null, null, result);
		then.children.add(node.getCondition().accept(this, then));
		result.children.add(then);
		ASTTree otherwise = new ASTTree("else", null, null, result);
		otherwise.children.add(node.getCondition().accept(this, otherwise));
		result.children.add(otherwise);
		
		return result;
	}

	@Override
	public ASTTree visitIf(IfTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("if", null, null, p);
		ASTTree head = new ASTTree("head", null, null, result);
		head.children.add(node.getCondition().accept(this, head));
		result.children.add(head);
		ASTTree then = new ASTTree("then", null, null, result);
		then.children.add(node.getCondition().accept(this, then));
		result.children.add(then);
		ASTTree otherwise = new ASTTree("else", null, null, result);
		otherwise.children.add(node.getCondition().accept(this, otherwise));
		result.children.add(otherwise);
		return result;
	}


	@Override
	public ASTTree visitBreak(BreakTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("break", node.getLabel(), null, p);
		return result;
	}

	@Override
	public ASTTree visitContinue(ContinueTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("continue", node.getLabel(), null, p);
		return result;
	}

	@Override
	public ASTTree visitReturn(ReturnTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("return", null, null, p);
		result.children.add(node.getExpression().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitThrow(ThrowTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("throw", node.getExpression(), null, p);
		return result;
	}

	@Override
	public ASTTree visitAssert(AssertTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("assert", null, null, p);
		ASTTree condition = new ASTTree("condition", null, null, result);
		condition.children.add(node.getCondition().accept(this, condition));
		result.children.add(condition);
		ASTTree detail = new ASTTree("detail", null, null, result);
		detail.children.add(node.getCondition().accept(this, detail));
		result.children.add(detail);
		return result; //result.append(super.visitAssert(node, p)).toASTTree();
	}

	@Override
	public ASTTree visitMethodInvocation(MethodInvocationTree node, ASTTree p) {
		debugOutput(node);
		//TODO: fix the parameter display
		ASTTree result = new ASTTree("mc", node.getMethodSelect(), null, p);
		for (Tree t : node.getArguments()) {
			result.children.add(t.accept(this, result));
		}
		return result;
	}

	@Override
	public ASTTree visitNewArray(NewArrayTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("array", node.getType(), null, p);
		if(node.getDimensions() != null) {
			ASTTree head = new ASTTree("dimensions", null, null, result);
			for (Tree t : node.getDimensions()) {
				result.children.add(t.accept(this, result));
			}
			result.children.add(head);
		}
		if(node.getDimensions() != null) {
			ASTTree head = new ASTTree("initializer", null, null, result);
			for (Tree t : node.getInitializers()) {
				result.children.add(t.accept(this, result));
			}
			result.children.add(head);
		}
		return result;
		//return super.visitNewArray(node, p);
	}

	@Override
	public ASTTree visitLambdaExpression(LambdaExpressionTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("lambda", null,
				node.getParameters().stream().sequential().map(t -> (t.getType().toString())).collect(Collectors.joining(",","(",")"))
				,		p);
		result.children.add(node.getBody().accept(this, result));
		return result;
	}

	@Override
	public ASTTree visitAssignment(AssignmentTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("assign", node.getVariable().toString(), null, p);
		result.children.add(node.getExpression().accept(this, result));
		return result;	
	}

	@Override
	public ASTTree visitUnary(UnaryTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("unary", null, null, p);
		result.children.add(node.getExpression().accept(this, result));
		return result;	
	}

	@Override
	public ASTTree visitBinary(BinaryTree node, ASTTree p) {
		debugOutput(node);
		ASTTree result = new ASTTree("binary", node.getKind(), null, p);
		result.children.add(node.getLeftOperand().accept(this, result));
		result.children.add(node.getRightOperand().accept(this, result));
		return result;	
	}

	@Override
	public ASTTree visitTypeCast(TypeCastTree node, ASTTree p) {
		debugOutput(node);
		//Probably not needed
		ASTTree result = new ASTTree("cast", node.getType(), null, p);
		result.children.add(node.getExpression().accept(this, result));
		return result;	
	}


	@Override
	public ASTTree visitIdentifier(IdentifierTree node, ASTTree p) {
		debugOutput(node);
		//Probably not needed single variable name
		return new ASTTree("var", node.getName(),null,p);
	}

	@Override
	public ASTTree visitLiteral(LiteralTree node, ASTTree p) {
		debugOutput(node);
		return new ASTTree("lit", node.getValue()!=null?node.getValue():"null", null,p);
	}

	@SuppressWarnings("unused")
	private static void debugOutput(ASTTree output) {
		if(DEBUG)
			System.out.println(output);
	}
	
	private static void debugOutput(Tree output) {
		if(DEBUG)
			System.out.println(output.getKind().toString());
	}

}
