package main.ast;

import java.util.stream.Collectors;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;

public class ASTScannerText extends TreeScanner<String, Integer> {
	public static final String OFFSET = "    ";
	public static final boolean DEBUG = false;
	public static final boolean ALWAYSDISPLAYVARIABLES = true;
	@Override
	public String reduce(String r1, String r2) {
		System.out.println("concat");
		if ((r1 == null || r1.isBlank()) && (r2 == null || r2.isBlank())) {
			return null;
		}
		if(r1 == null || r1.isBlank())
			return r2;
		if(r2 == null || r2.isBlank())
			return r1;
		return r1+'\n'+r2;
	}

	@Override
	public String visitCompilationUnit(CompilationUnitTree node, Integer p) {
		debugOutput(node);
		return super.visitCompilationUnit(node, p);
	}

	@Override
	public String visitPackage(PackageTree node, Integer p) {
		debugOutput(node);
		return "";//super.visitPackage(node, p);
	}

	@Override
	public String visitImport(ImportTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p)+"<import=\""+ node.getClass().getName() +"\">";//  + "\n" + super.visitImport(node, p);
	}

	@Override
	public String visitClass(ClassTree node, Integer p) {
		debugOutput(node);
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
		debugOutput(node);
		String result = OFFSET.repeat(p)+"<method=\""+node.getName()+
		node.getParameters().stream().sequential().map(t -> (t.getType().toString())).collect(Collectors.joining(",","(",")"))
		+"\">\n";
		//result += node.accept(this, p+1)+'\n';
		result += node.getBody().accept(this, p+1)+'\n';
		result += OFFSET.repeat(p)+"</method>";
		return result;// + super.visitMethod(node, p);
		//return super.visitMethod(node, p);
	}

	@Override
	public String visitVariable(VariableTree node, Integer p) {
		debugOutput(node);
		if (ALWAYSDISPLAYVARIABLES) {
			boolean init = node.getInitializer() != null;
			return OFFSET.repeat(p)+ "<var=\""+ node.getName() +"\" type=\""+node.getType()+"\">\n"+ 
			node.getInitializer().accept(this, p+1) +		
			"\n"+OFFSET.repeat(p)+"</var>";// + super.visitVariable(node, p);
		}
		return super.visitVariable(node, p);
	}

	@Override
	public String visitEmptyStatement(EmptyStatementTree node, Integer p) {
		debugOutput(node);
		return super.visitEmptyStatement(node, p);
	}

	@Override
	public String visitBlock(BlockTree node, Integer p) {
		debugOutput(node);
		String result = OFFSET.repeat(p)+"<block>\n";
				//result += node.accept(this, p+1)+'\n';
		for (Tree t : node.getStatements()) {
			result += t.accept(this, p+1)+'\n';
		}
		result += OFFSET.repeat(p)+"</block>";
		return result;//super.visitBlock(node, p);
	}
	
	@Override
	public String visitDoWhileLoop(DoWhileLoopTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder();
		result.append(OFFSET.repeat(p)+"<dowhile >\n");
		result.append(OFFSET.repeat(p+1)+"<condition>\n");
		result.append(node.getCondition().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</condition>\n");
		//result += node.accept(this, p+1)+'\n';
		result.append(node.getStatement().accept(this, p+1)+'\n');
		result.append(OFFSET.repeat(p)+"</dowhile>");
		return result.toString();//result.append("\n") + super.visitDoWhileLoop(node, p);
	}

	@Override
	public String visitWhileLoop(WhileLoopTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder();
		result.append(OFFSET.repeat(p)+"<while >\n");
		result.append(OFFSET.repeat(p+1)+"<condition>\n");
		result.append(node.getCondition().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</condition>\n");
		//result += node.accept(this, p+1)+'\n';
		result.append(node.getStatement().accept(this, p+1)+'\n');
		result.append(OFFSET.repeat(p)+"</while>");
		return result.toString();//result.append(super.visitWhileLoop(node, p)).toString();
	}

	@Override
	public String visitForLoop(ForLoopTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder();
		result.append(OFFSET.repeat(p)+"<forloop>\n");
		result.append(OFFSET.repeat(p+1)+"<head>\n");
		result.append(OFFSET.repeat(p+2)+"<init>\n");
		for (Tree t : node.getInitializer()) {
			result.append(t.accept(this, p+3)+'\n');
		}
		result.append(OFFSET.repeat(p+2)+"</init>\n");
		result.append(OFFSET.repeat(p+2)+"<condition>\n");
		result.append(node.getCondition().accept(this, p+3)+'\n');
		result.append(OFFSET.repeat(p+2)+"</condition>\n");
		result.append(OFFSET.repeat(p+2)+"<update>\n");
		for (Tree t : node.getUpdate()) {
			result.append(t.accept(this, p+3)+'\n');
		}
		result.append(OFFSET.repeat(p+2)+"</update>\n");
		result.append(node.getCondition().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</head>\n");
		//result += node.accept(this, p+1)+'\n';
		result.append(node.getStatement().accept(this, p+1)+'\n');
		result.append(OFFSET.repeat(p)+"</forloop>");
		return result.toString(); //result.append(super.visitForLoop(node, p)).toString();
	}

	@Override
	public String visitEnhancedForLoop(EnhancedForLoopTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder();
		result.append(OFFSET.repeat(p)+"<forloop>\n");
		result.append(OFFSET.repeat(p+1)+"<head>\n");
		result.append(OFFSET.repeat(p+2)+"<iteration=\""+ node.getVariable().getName() +"\" type=\""+ node.getVariable().getType().toString() +"\"/>\n");
		result.append(OFFSET.repeat(p+1)+"</head>\n");
		//result += node.accept(this, p+1)+'\n';
		result.append(node.getStatement().accept(this, p+1)+'\n');
		result.append(OFFSET.repeat(p)+"</forloop>");
		return result.toString(); //result.append(super.visitForLoop(node, p)).toString();
	}

	@Override
	public String visitLabeledStatement(LabeledStatementTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p) + "<label=\""+node.getLabel().toString() + "\"/>\n" + super.visitLabeledStatement(node, p);
	}

	@Override
	public String visitSwitch(SwitchTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder(OFFSET.repeat(p)+"<switch>\n");
		result.append(OFFSET.repeat(p+1)+"<head>\n");
		result.append(node.getExpression().accept(this, p+1)+"\n");
		result.append(OFFSET.repeat(p+1)+"</head>\n");
		result.append(OFFSET.repeat(p+1)+"<body>\n");
		for (Tree t : node.getCases()) {
			result.append(t.accept(this, p+2)+'\n');
		}
		result.append(OFFSET.repeat(p+1)+"</body>\n");
		result.append(OFFSET.repeat(p)+"</switch>");
		return result.toString(); //super.visitSwitch(node, p);
	}

	@Override
	public String visitSwitchExpression(SwitchExpressionTree node, Integer p) {
		debugOutput(node);
		//Already in visitSwitch defined?
		return super.visitSwitchExpression(node, p);
	}

	@Override
	public String visitCase(CaseTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder(OFFSET.repeat(p)+"<case>\n");
		result.append(OFFSET.repeat(p+1)+"<head>\n");
		for (Tree t : node.getExpressions()) {
			result.append(t.accept(this, p+2)+'\n');
		}
		result.append(OFFSET.repeat(p+1)+"</head>\n");
		result.append(OFFSET.repeat(p+1)+"<body>\n");
		result.append(node.getBody().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</body>\n");
		result.append(OFFSET.repeat(p)+"</case>");
		return result.toString(); //super.visitCase(node, p);
	}

	@Override
	public String visitSynchronized(SynchronizedTree node, Integer p) {
		debugOutput(node);
		//Not currently needed
		return super.visitSynchronized(node, p);
	}

	@Override
	public String visitTry(TryTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder(OFFSET.repeat(p)+"<try>\n");
		result.append(node.getBlock().accept(this, p+1)+'\n');
		result.append(OFFSET.repeat(p)+"</try>\n");
		for (Tree t : node.getCatches()) {
			result.append(t.accept(this, p));
		}
		return result.toString();//super.visitTry(node, p);
	}

	@Override
	public String visitCatch(CatchTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder(OFFSET.repeat(p)+"<catch=\""+ node.getParameter().toString() +"\">\n");
		result.append(node.getBlock().accept(this, p+1)+'\n');
		result.append(OFFSET.repeat(p)+"</catch>");
		return result.toString();//super.visitCatch(node, p);
	}

	@Override
	public String visitConditionalExpression(ConditionalExpressionTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder(OFFSET.repeat(p)+"<ternary>\n");
		result.append(OFFSET.repeat(p+1)+"</head>\n");
		result.append(node.getCondition().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</head>\n");
		result.append(OFFSET.repeat(p+1)+"</then>\n");
		result.append(node.getTrueExpression().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</then>\n");
		if(node.getFalseExpression() != null ) {
			result.append(OFFSET.repeat(p+1)+"</else>\n");
			result.append(node.getFalseExpression().accept(this, p+2)+'\n');
			result.append(OFFSET.repeat(p+1)+"</else>\n");
		}
		result.append(OFFSET.repeat(p)+"</ternary>");
		return result.toString();//super.visitConditionalExpression(node, p);
	}

	@Override
	public String visitIf(IfTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder(OFFSET.repeat(p)+"<if>\n");
		result.append(OFFSET.repeat(p+1)+"</head>\n");
		result.append(node.getCondition().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</head>\n");
		result.append(OFFSET.repeat(p+1)+"</branchtrue>\n");
		result.append(node.getThenStatement().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</branchtrue>\n");
		if(node.getElseStatement() != null ) {
			result.append(OFFSET.repeat(p+1)+"</branchfalse>\n");
			result.append(node.getElseStatement().accept(this, p+2)+'\n');
			result.append(OFFSET.repeat(p+1)+"</branchfalse>\n");
		}
		result.append(OFFSET.repeat(p)+"</if>");
		return result.toString();//super.visitConditionalExpression(node, p);
	}

	@Override
	public String visitExpressionStatement(ExpressionStatementTree node, Integer p) {
		//TODO: reworking maybe needed
		debugOutput(node);
		//return OFFSET.repeat(p)+"exp:"+ node.getExpression().toString() + 
		return super.visitExpressionStatement(node, p);
	}

	@Override
	public String visitBreak(BreakTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p)+"<break"+ ((node.getLabel()!= null)?"=\""+ node.getLabel().toString() +"\"":"") +"/>";// super.visitBreak(node, p);
	}

	@Override
	public String visitContinue(ContinueTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p)+"<continue"+ ((node.getLabel()!= null)?"=\""+ node.getLabel().toString() +"\"":"") +"/>";//  super.visitContinue(node, p);
	}

	@Override
	public String visitReturn(ReturnTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p) + "<return>\n" +
				node.getExpression().accept(this, p+1) + "\n"+
				OFFSET.repeat(p) + "</return>";//super.visitReturn(node, p);
	}

	@Override
	public String visitThrow(ThrowTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p)+"<throw=\""+ node.getExpression().toString() +"\" />";//  super.visitContinue(node, p);
	}

	@Override
	public String visitAssert(AssertTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder();
		result.append(OFFSET.repeat(p)+"<assert>\n");
		result.append(OFFSET.repeat(p+1)+"<condition>\n");
		result.append(node.getCondition().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</condition>\n");
		result.append(OFFSET.repeat(p+1)+"<detail>\n");
		result.append(node.getDetail().accept(this, p+2)+'\n');
		result.append(OFFSET.repeat(p+1)+"</detail>\n");
		result.append(OFFSET.repeat(p)+"</assert>");
		return result.toString(); //result.append(super.visitAssert(node, p)).toString();
	}

	@Override
	public String visitMethodInvocation(MethodInvocationTree node, Integer p) {
		debugOutput(node);
		//TODO: fix the parameter display
		StringBuilder result = new StringBuilder(OFFSET.repeat(p)+"<mc=\""+ node.getMethodSelect().toString() +"\">\n");
		for (Tree t : node.getArguments()) {
			result.append(t.accept(this, p+1)+'\n');
		}
		result.append(OFFSET.repeat(p)+"</mc>");
		return result.toString();
		//return super.visitMethodInvocation(node, p);
	}

	@Override
	public String visitNewClass(NewClassTree node, Integer p) {
		debugOutput(node);
		//Not needed creates a new instance of a class (Constructor)
		return super.visitNewClass(node, p);
	}

	@Override
	public String visitNewArray(NewArrayTree node, Integer p) {
		debugOutput(node);
		StringBuilder result = new StringBuilder(OFFSET.repeat(p)+"<array=\""+ node.getType().toString() +"\">\n");
		if(node.getDimensions() != null) {
			result.append(OFFSET.repeat(p+1)+"<dimensions>\n");
			for (Tree t : node.getDimensions()) {
				result.append(t.accept(this, p+2)+'\n');
			}
			result.append(OFFSET.repeat(p+1)+"</dimensions>\n");
		}
		if(node.getInitializers() != null) {
			result.append(OFFSET.repeat(p+1)+"<initializer>\n");
			for (Tree t : node.getInitializers()) {
				result.append(t.accept(this, p+2)+'\n');
			}
			result.append(OFFSET.repeat(p+1)+"</initializer>\n");
		}
		result.append(OFFSET.repeat(p)+"</array>");
		return result.toString();
		//return super.visitNewArray(node, p);
	}

	@Override
	public String visitLambdaExpression(LambdaExpressionTree node, Integer p) {
		debugOutput(node);
		// TODO Auto-generated method stub
		debugOutput(node);
		StringBuilder result = new StringBuilder(OFFSET.repeat(p)+"<lambda parameters=\"("+
			node.getParameters().stream().sequential().map(t -> (t.getType().toString())).collect(Collectors.joining(",","(",")"))
			+"\">\n");
			//result += node.accept(this, p+1)+'\n';
			result.append(node.getBody().accept(this, p+1)+'\n');
			result.append(OFFSET.repeat(p)+"</lambda>");
		return result.toString();
		//return super.visitLambdaExpression(node, p);
	}

	@Override
	public String visitParenthesized(ParenthesizedTree node, Integer p) {
		//Probably not needed
		return super.visitParenthesized(node, p);
	}

	@Override
	public String visitAssignment(AssignmentTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p)+"<assign=\""+ node.getVariable().toString() +"\">\n"+ 
				node.getExpression().accept(this, p+1) +"\n" +
				OFFSET.repeat(p) +"</assign>";//super.visitAssignment(node, p);
	}

	@Override
	public String visitCompoundAssignment(CompoundAssignmentTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return super.visitCompoundAssignment(node, p);
	}

	@Override
	public String visitUnary(UnaryTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p) + "<unary>"+node.toString()+"</unary>";
		//return super.visitUnary(node, p);
	}

	@Override
	public String visitBinary(BinaryTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p) + "<binary type=\""+ node.getKind().toString() +"\">\n"+
		node.getLeftOperand().accept(this, p+1) + "\n" +
		node.getRightOperand().accept(this, p+1) + "\n" +
		OFFSET.repeat(p) + "</binary>";
		//return super.visitBinary(node, p);
	}

	@Override
	public String visitTypeCast(TypeCastTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return OFFSET.repeat(p)+"<cast to=\""+node.getType().toString()+"\">\n"+
		node.getExpression().accept(this, p+1)+"\n"+ 
		OFFSET.repeat(p) +"</cast>";//super.visitTypeCast(node, p);
	}

	@Override
	public String visitInstanceOf(InstanceOfTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return super.visitInstanceOf(node, p);
	}

	@Override
	public String visitAnyPattern(AnyPatternTree node, Integer p) {
		debugOutput(node);
		//Probably not needed (preview)
		return super.visitAnyPattern(node, p);
	}

	@Override
	public String visitStringTemplate(StringTemplateTree node, Integer p) {
		debugOutput(node);
		//Probably not needed (preview)
		return super.visitStringTemplate(node, p);
	}

	@Override
	public String visitBindingPattern(BindingPatternTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return super.visitBindingPattern(node, p);
	}

	@Override
	public String visitDefaultCaseLabel(DefaultCaseLabelTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return super.visitDefaultCaseLabel(node, p);
	}

	@Override
	public String visitConstantCaseLabel(ConstantCaseLabelTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return super.visitConstantCaseLabel(node, p);
	}

	@Override
	public String visitPatternCaseLabel(PatternCaseLabelTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return super.visitPatternCaseLabel(node, p);
	}

	@Override
	public String visitDeconstructionPattern(DeconstructionPatternTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return super.visitDeconstructionPattern(node, p);
	}

	@Override
	public String visitArrayAccess(ArrayAccessTree node, Integer p) {
		debugOutput(node);
		// TODO Auto-generated method stub
		return super.visitArrayAccess(node, p);
	}

	@Override
	public String visitMemberSelect(MemberSelectTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return super.visitMemberSelect(node, p);
	}

	@Override
	public String visitMemberReference(MemberReferenceTree node, Integer p) {
		debugOutput(node);
		//Probably not needed
		return super.visitMemberReference(node, p);
	}

	@Override
	public String visitIdentifier(IdentifierTree node, Integer p) {
		debugOutput(node);
		//Probably not needed single variable name
		return OFFSET.repeat(p)+"<var=\""+ node.getName().toString() +"\"/>" ;//super.visitLiteral(node, p);
	}

	@Override
	public String visitLiteral(LiteralTree node, Integer p) {
		debugOutput(node);
		return OFFSET.repeat(p)+"<lit>[["+(node.getValue()!=null?node.getValue().toString():"null")+"]]</lit>" ;//super.visitLiteral(node, p);
	}

	@Override
	public String visitPrimitiveType(PrimitiveTypeTree node, Integer p) {
		debugOutput(node);
		// Probably not needed maybe for special checks if specific datatypes have or haven't been used
		return super.visitPrimitiveType(node, p);
	}

	@Override
	public String visitArrayType(ArrayTypeTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitPrimitiveType
		return super.visitArrayType(node, p);
	}

	@Override
	public String visitParameterizedType(ParameterizedTypeTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see As they are beyond the scope of the course
		return super.visitParameterizedType(node, p);
	}

	@Override
	public String visitUnionType(UnionTypeTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitUnionType(node, p);
	}

	@Override
	public String visitIntersectionType(IntersectionTypeTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitIntersectionType(node, p);
	}

	@Override
	public String visitTypeParameter(TypeParameterTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitTypeParameter(node, p);
	}

	@Override
	public String visitWildcard(WildcardTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitWildcard(node, p);
	}

	@Override
	public String visitModifiers(ModifiersTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitModifiers(node, p);
	}

	@Override
	public String visitAnnotation(AnnotationTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitAnnotation(node, p);
	}

	@Override
	public String visitAnnotatedType(AnnotatedTypeTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitAnnotatedType(node, p);
	}

	@Override
	public String visitModule(ModuleTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitModule(node, p);
	}

	@Override
	public String visitExports(ExportsTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitExports(node, p);
	}

	@Override
	public String visitOpens(OpensTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitOpens(node, p);
	}

	@Override
	public String visitProvides(ProvidesTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitProvides(node, p);
	}

	@Override
	public String visitRequires(RequiresTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitRequires(node, p);
	}

	@Override
	public String visitUses(UsesTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitUses(node, p);
	}

	@Override
	public String visitOther(Tree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitOther(node, p);
	}

	@Override
	public String visitErroneous(ErroneousTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitErroneous(node, p);
	}

	@Override
	public String visitYield(YieldTree node, Integer p) {
		debugOutput(node);
		//Probably not needed see visitParameterizedType
		return super.visitYield(node, p);
	}
	
	@SuppressWarnings("unused")
	private static void debugOutput(String output) {
		if(DEBUG)
			System.out.println(output);
	}
	
	private static void debugOutput(Tree output) {
		if(DEBUG)
			System.out.println(output.getKind().toString());
	}

}
