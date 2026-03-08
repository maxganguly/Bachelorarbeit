package main;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;

import main.conditions.Condition;

/**
 * Should only be used to calculate the post condition of a method or smaller block
 */
public class StrongestPostcondition extends TreeScanner<Condition, Condition> {

	private Condition currentCondition;
	
	public StrongestPostcondition(Condition preondition) {
		this.currentCondition = preondition;
	}

	@Override
	public Condition reduce(Condition old, Condition addition) {
		System.err.println("Should never be called");
		return null;
	}

	@Override
	public Condition visitVariable(VariableTree node, Condition p) {
		// TODO Auto-generated method stub
		
		return super.visitVariable(node, p);
	}

	@Override
	public Condition visitDoWhileLoop(DoWhileLoopTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitDoWhileLoop(node, p);
	}

	@Override
	public Condition visitWhileLoop(WhileLoopTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitWhileLoop(node, p);
	}

	@Override
	public Condition visitForLoop(ForLoopTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitForLoop(node, p);
	}

	@Override
	public Condition visitEnhancedForLoop(EnhancedForLoopTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitEnhancedForLoop(node, p);
	}

	@Override
	public Condition visitLabeledStatement(LabeledStatementTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitLabeledStatement(node, p);
	}

	@Override
	public Condition visitSwitch(SwitchTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitSwitch(node, p);
	}

	@Override
	public Condition visitSwitchExpression(SwitchExpressionTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitSwitchExpression(node, p);
	}

	@Override
	public Condition visitCase(CaseTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitCase(node, p);
	}

	@Override
	public Condition visitTry(TryTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitTry(node, p);
	}

	@Override
	public Condition visitCatch(CatchTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitCatch(node, p);
	}

	@Override
	public Condition visitConditionalExpression(ConditionalExpressionTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitConditionalExpression(node, p);
	}

	@Override
	public Condition visitIf(IfTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitIf(node, p);
	}

	@Override
	public Condition visitExpressionStatement(ExpressionStatementTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitExpressionStatement(node, p);
	}

	@Override
	public Condition visitBreak(BreakTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitBreak(node, p);
	}

	@Override
	public Condition visitContinue(ContinueTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitContinue(node, p);
	}

	@Override
	public Condition visitReturn(ReturnTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitReturn(node, p);
	}

	@Override
	public Condition visitThrow(ThrowTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitThrow(node, p);
	}

	@Override
	public Condition visitAssert(AssertTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitAssert(node, p);
	}

	@Override
	public Condition visitMethodInvocation(MethodInvocationTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitMethodInvocation(node, p);
	}

	@Override
	public Condition visitNewArray(NewArrayTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitNewArray(node, p);
	}

	@Override
	public Condition visitLambdaExpression(LambdaExpressionTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitLambdaExpression(node, p);
	}

	@Override
	public Condition visitAssignment(AssignmentTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitAssignment(node, p);
	}

	@Override
	public Condition visitCompoundAssignment(CompoundAssignmentTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitCompoundAssignment(node, p);
	}

	@Override
	public Condition visitUnary(UnaryTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitUnary(node, p);
	}

	@Override
	public Condition visitBinary(BinaryTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitBinary(node, p);
	}

	@Override
	public Condition visitInstanceOf(InstanceOfTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitInstanceOf(node, p);
	}

	@Override
	public Condition visitArrayAccess(ArrayAccessTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitArrayAccess(node, p);
	}

	@Override
	public Condition visitLiteral(LiteralTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitLiteral(node, p);
	}

	@Override
	public Condition visitArrayType(ArrayTypeTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitArrayType(node, p);
	}

	@Override
	public Condition visitUses(UsesTree node, Condition p) {
		// TODO Auto-generated method stub
		return super.visitUses(node, p);
	}
	
}
