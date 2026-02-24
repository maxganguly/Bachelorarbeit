package main;

import java.util.stream.Collectors;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;

public class ASTScannerText extends TreeScanner<String, Integer> {
	public static final String OFFSET = "    ";
	@Override
	public String reduce(String r1, String r2) {
		if (r1 == null && r2 == null) {
			return null;
		}
		if(r1 == null)
			return r2;
		if(r2 == null)
			return r1;
		return r1+'\n'+r2;
	}

	@Override
	public String visitCompilationUnit(CompilationUnitTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Compilation");
		return node.getSourceFile().getName() + "\n" +super.visitCompilationUnit(node, p);
	}

	@Override
	public String visitPackage(PackageTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Package");
		return super.visitPackage(node, p);
	}

	@Override
	public String visitImport(ImportTree node, Integer p) {
		System.out.println("Import");
		return OFFSET.repeat(p)+"<import=\""+ node.getClass().getName() +"\">"  + "\n" + super.visitImport(node, p);
	}

	@Override
	public String visitClass(ClassTree node, Integer p) {
		System.out.println("Class");
		// Does ignore visibility, modifiers, interfaces, inheritance and will not work if anonymous
		String result = OFFSET.repeat(p)+"<class=\""+node.getSimpleName()+"\">\n";
		//result += node.accept(this, p+1)+'\n';
		for (Tree t : node.getMembers()) {
			result += t.accept(this, p+1)+'\n';
		}
		result += "</class>";
		return result  + "\n";// + super.visitClass(node, p);
	}

	@Override
	public String visitMethod(MethodTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Method");
		String result = OFFSET.repeat(p)+"<method=\""+node.getName()+
		node.getParameters().stream().sequential().map(t -> (t.getType().toString())).collect(Collectors.joining(",","(",")"))
		+"\">\n";
		//result += node.accept(this, p+1)+'\n';
		result += node.getBody().accept(this, p+1)+'\n';
		result += OFFSET.repeat(p)+"</method>";
		return result  + "\n";// + super.visitMethod(node, p);
		//return super.visitMethod(node, p);
	}

	@Override
	public String visitVariable(VariableTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Variable");
		
		return OFFSET.repeat(p)+ "<var=\""+ node.getName() +"\" type=\""+node.getType()+" \\>" + super.visitVariable(node, p);
	}

	@Override
	public String visitEmptyStatement(EmptyStatementTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("EmptyStatement");
		return super.visitEmptyStatement(node, p);
	}

	@Override
	public String visitBlock(BlockTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Block");
		return super.visitBlock(node, p);
	}

	@Override
	public String visitDoWhileLoop(DoWhileLoopTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("DoWhileLoop");
		return super.visitDoWhileLoop(node, p);
	}

	@Override
	public String visitWhileLoop(WhileLoopTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("WhileLoop");
		return super.visitWhileLoop(node, p);
	}

	@Override
	public String visitForLoop(ForLoopTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("ForLoop");
		return super.visitForLoop(node, p);
	}

	@Override
	public String visitEnhancedForLoop(EnhancedForLoopTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("EnhancedForLoop");
		return super.visitEnhancedForLoop(node, p);
	}

	@Override
	public String visitLabeledStatement(LabeledStatementTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("LabeledStatement");
		return super.visitLabeledStatement(node, p);
	}

	@Override
	public String visitSwitch(SwitchTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Switch");
		return super.visitSwitch(node, p);
	}

	@Override
	public String visitSwitchExpression(SwitchExpressionTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("SwitchExpression");
		return super.visitSwitchExpression(node, p);
	}

	@Override
	public String visitCase(CaseTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Case");
		return super.visitCase(node, p);
	}

	@Override
	public String visitSynchronized(SynchronizedTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Synchronized");
		return super.visitSynchronized(node, p);
	}

	@Override
	public String visitTry(TryTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Try");
		return super.visitTry(node, p);
	}

	@Override
	public String visitCatch(CatchTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Catch");
		return super.visitCatch(node, p);
	}

	@Override
	public String visitConditionalExpression(ConditionalExpressionTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("ConditionalExpression");
		return super.visitConditionalExpression(node, p);
	}

	@Override
	public String visitIf(IfTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("If");
		return super.visitIf(node, p);
	}

	@Override
	public String visitExpressionStatement(ExpressionStatementTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("ExpressionStatement");
		return super.visitExpressionStatement(node, p);
	}

	@Override
	public String visitBreak(BreakTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Break");
		return super.visitBreak(node, p);
	}

	@Override
	public String visitContinue(ContinueTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Continue");
		return super.visitContinue(node, p);
	}

	@Override
	public String visitReturn(ReturnTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Return");
		return super.visitReturn(node, p);
	}

	@Override
	public String visitThrow(ThrowTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Throw");
		return super.visitThrow(node, p);
	}

	@Override
	public String visitAssert(AssertTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("Assert");
		return super.visitAssert(node, p);
	}

	@Override
	public String visitMethodInvocation(MethodInvocationTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("MethodInvocation");
		return super.visitMethodInvocation(node, p);
	}

	@Override
	public String visitNewClass(NewClassTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("NewClass");
		return super.visitNewClass(node, p);
	}

	@Override
	public String visitNewArray(NewArrayTree node, Integer p) {
		// TODO Auto-generated method stub
		System.out.println("NewArray");
		return super.visitNewArray(node, p);
	}

	@Override
	public String visitLambdaExpression(LambdaExpressionTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitLambdaExpression(node, p);
	}

	@Override
	public String visitParenthesized(ParenthesizedTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitParenthesized(node, p);
	}

	@Override
	public String visitAssignment(AssignmentTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitAssignment(node, p);
	}

	@Override
	public String visitCompoundAssignment(CompoundAssignmentTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitCompoundAssignment(node, p);
	}

	@Override
	public String visitUnary(UnaryTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitUnary(node, p);
	}

	@Override
	public String visitBinary(BinaryTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitBinary(node, p);
	}

	@Override
	public String visitTypeCast(TypeCastTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitTypeCast(node, p);
	}

	@Override
	public String visitInstanceOf(InstanceOfTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitInstanceOf(node, p);
	}

	@Override
	public String visitAnyPattern(AnyPatternTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitAnyPattern(node, p);
	}

	@Override
	public String visitStringTemplate(StringTemplateTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitStringTemplate(node, p);
	}

	@Override
	public String visitBindingPattern(BindingPatternTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitBindingPattern(node, p);
	}

	@Override
	public String visitDefaultCaseLabel(DefaultCaseLabelTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitDefaultCaseLabel(node, p);
	}

	@Override
	public String visitConstantCaseLabel(ConstantCaseLabelTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitConstantCaseLabel(node, p);
	}

	@Override
	public String visitPatternCaseLabel(PatternCaseLabelTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitPatternCaseLabel(node, p);
	}

	@Override
	public String visitDeconstructionPattern(DeconstructionPatternTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitDeconstructionPattern(node, p);
	}

	@Override
	public String visitArrayAccess(ArrayAccessTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitArrayAccess(node, p);
	}

	@Override
	public String visitMemberSelect(MemberSelectTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitMemberSelect(node, p);
	}

	@Override
	public String visitMemberReference(MemberReferenceTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitMemberReference(node, p);
	}

	@Override
	public String visitIdentifier(IdentifierTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitIdentifier(node, p);
	}

	@Override
	public String visitLiteral(LiteralTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitLiteral(node, p);
	}

	@Override
	public String visitPrimitiveType(PrimitiveTypeTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitPrimitiveType(node, p);
	}

	@Override
	public String visitArrayType(ArrayTypeTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitArrayType(node, p);
	}

	@Override
	public String visitParameterizedType(ParameterizedTypeTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitParameterizedType(node, p);
	}

	@Override
	public String visitUnionType(UnionTypeTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitUnionType(node, p);
	}

	@Override
	public String visitIntersectionType(IntersectionTypeTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitIntersectionType(node, p);
	}

	@Override
	public String visitTypeParameter(TypeParameterTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitTypeParameter(node, p);
	}

	@Override
	public String visitWildcard(WildcardTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitWildcard(node, p);
	}

	@Override
	public String visitModifiers(ModifiersTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitModifiers(node, p);
	}

	@Override
	public String visitAnnotation(AnnotationTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitAnnotation(node, p);
	}

	@Override
	public String visitAnnotatedType(AnnotatedTypeTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitAnnotatedType(node, p);
	}

	@Override
	public String visitModule(ModuleTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitModule(node, p);
	}

	@Override
	public String visitExports(ExportsTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitExports(node, p);
	}

	@Override
	public String visitOpens(OpensTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitOpens(node, p);
	}

	@Override
	public String visitProvides(ProvidesTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitProvides(node, p);
	}

	@Override
	public String visitRequires(RequiresTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitRequires(node, p);
	}

	@Override
	public String visitUses(UsesTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitUses(node, p);
	}

	@Override
	public String visitOther(Tree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitOther(node, p);
	}

	@Override
	public String visitErroneous(ErroneousTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitErroneous(node, p);
	}

	@Override
	public String visitYield(YieldTree node, Integer p) {
		// TODO Auto-generated method stub
		return super.visitYield(node, p);
	}
	
	

}
