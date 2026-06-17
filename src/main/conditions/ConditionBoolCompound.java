package main.conditions;

public class ConditionBoolCompound implements Condition {

	public final Condition left,right;
	public final Condition.BOOLCOMPOUND compound;

	public ConditionBoolCompound(Condition left, Condition.BOOLCOMPOUND compound, Condition right) {
		this.left = left;
		this.right = right;
		this.compound = compound;
	}

	@Override
	public String toString() {
		return "("+left.toString() + " " + compound.toString() + " " + right.toString()+")";
	}

	@Override
	public TYPE getType() {
		return Condition.TYPE.COMPOUND;
	}

	@Override
	public Condition negate() {
		if(left.getType() != TYPE.LITERAL &&
				left.getType() != TYPE.VARIABLE &&
				right.getType() != TYPE.LITERAL &&
				right.getType() != TYPE.VARIABLE && 
				compound != BOOLCOMPOUND.XOR) {
			BOOLCOMPOUND bc = this.compound;
			if(this.compound == BOOLCOMPOUND.AND)
				bc = BOOLCOMPOUND.OR;
			else
				bc = BOOLCOMPOUND.OR;
			return new ConditionBoolCompound(left.negate(), bc, right.negate());
		}
		return new ConditionNot(this);
		
	}

	@Override
	public Condition evaluate() {
		Condition left = this.left.evaluate();
		Condition right = this.right.evaluate();
		if(compound == BOOLCOMPOUND.AND) {
			if(left == ConditionElement.FALSE || right == ConditionElement.FALSE)
				return ConditionElement.FALSE;
			if(left == ConditionElement.TRUE && right == ConditionElement.TRUE)
				return ConditionElement.TRUE;
			if(left == ConditionElement.TRUE)
				return right;
			if(right == ConditionElement.TRUE)
				return left;
			if(left instanceof ConditionComparison && right instanceof ConditionComparison) {
				var ccl = (ConditionComparison) left;
				var ccr = (ConditionComparison) right;
				if(ccl.left.equals(ccr.left) && 
						ccl.comparison == ccr.comparison.invert() &&
						ccl.right.equals(ccr.right))
					return ConditionElement.FALSE;
				if(ccl.left.equals(ccr.right) && 
						ccl.comparison == ccr.comparison &&
						ccl.right.equals(ccr.left))
					return ConditionElement.FALSE;
			}
			return new ConditionBoolCompound(left, compound, right);
		}
		
		if(compound == BOOLCOMPOUND.OR) {
			if(left == ConditionElement.FALSE && right == ConditionElement.FALSE)
				return ConditionElement.FALSE;
			if(left == ConditionElement.TRUE || right == ConditionElement.TRUE)
				return ConditionElement.TRUE;
			if(left == ConditionElement.FALSE)
				return right;
			if(right == ConditionElement.FALSE)
				return left;
			return new ConditionBoolCompound(left, compound, right);
		}
		if(compound == BOOLCOMPOUND.XOR) {
			if((left == ConditionElement.FALSE && right == ConditionElement.FALSE) || 
					(left == ConditionElement.TRUE && right == ConditionElement.TRUE))
				return ConditionElement.FALSE;
			if((left == ConditionElement.FALSE && right == ConditionElement.TRUE) || 
					(left == ConditionElement.TRUE && right == ConditionElement.FALSE))
				return ConditionElement.TRUE;
			return new ConditionBoolCompound(left, compound, right);
		}
		return ConditionElement.TRUE;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(!(obj instanceof ConditionBoolCompound)) {
			return false;
		}
		ConditionBoolCompound e = (ConditionBoolCompound) obj;
		return left.equals(e.left) && compound==e.compound && right.equals(right);
	}

}