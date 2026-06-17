package main.conditions;

public class ConditionComparison implements Condition {

	public final Condition left,right;
	public final Condition.COMPARISON comparison;

	public ConditionComparison(Condition left, Condition.COMPARISON comparison, Condition right) {
		this.left = left;
		this.right = right;
		this.comparison = comparison;
	}

	@Override
	public String toString() {
		return "("+left.toString() + " " + comparison.toString() + " " + right.toString()+")";
	}

	@Override
	public TYPE getType() {
		return Condition.TYPE.COMPOUND;
	}

	@Override
	public Condition negate() {
		Condition.COMPARISON nc = null;
		switch(comparison) {
		case EQUALS -> nc = COMPARISON.NOT_EQUALS;
		case NOT_EQUALS -> nc = COMPARISON.EQUALS;
		case GREATER_OR_EQUALS -> nc = COMPARISON.LESS_THAN;
		case GREATER_THAN -> nc = COMPARISON.LESS_OR_EQUALS;
		case LESS_OR_EQUALS -> nc = COMPARISON.GREATER_THAN;
		case LESS_THAN -> nc = COMPARISON.GREATER_OR_EQUALS;
		}
		return new ConditionComparison(left, nc, right);
	}

	@Override
	public Condition evaluate() {
		Condition eleft = left.evaluate();
		Condition eright = right.evaluate();
		if(eleft.equals(eright)) {
			//If both sides are the same check for equality checks
			return  ConditionElement.fromBoolean(comparison == COMPARISON.EQUALS ||
					comparison == COMPARISON.GREATER_OR_EQUALS ||
					comparison == COMPARISON.LESS_OR_EQUALS);
		}
		if(!(eleft instanceof ConditionElement) ||
				!(eright instanceof ConditionElement))
			return new ConditionComparison(eleft, comparison, eright);
		ConditionElement cleft, cright;
		cleft = (ConditionElement) eleft;
		cright = (ConditionElement) eright;
		if(cleft.type == TYPE.VARIABLE || cright.type == TYPE.VARIABLE) {
			return new ConditionComparison(eleft, comparison, eright);
		}
		try {
			double l = Double.parseDouble(cleft.value);
			double r = Double.parseDouble(cright.value);
			switch(comparison) {
			case EQUALS : return ConditionElement.fromBoolean(l==r);
			case NOT_EQUALS : return ConditionElement.fromBoolean(l!=r);
			case GREATER_OR_EQUALS : return ConditionElement.fromBoolean(l>=r);
			case GREATER_THAN : return ConditionElement.fromBoolean(l>r);
			case LESS_OR_EQUALS : return ConditionElement.fromBoolean(l<=r);
			case LESS_THAN : return ConditionElement.fromBoolean(l<r);
			}
		} catch (NumberFormatException e) {}
		return new ConditionComparison(cleft, comparison, cright);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(!(obj instanceof ConditionComparison)) {
			return false;
		}
		ConditionComparison e = (ConditionComparison) obj;
		return left.equals(e.left) && comparison==e.comparison && right.equals(right);
	}

}