package main.conditions;

public class ConditionComparison implements Condition {

	private Condition left,right;
	private Condition.COMPARISON comparison;
	
	public ConditionComparison(Condition left, Condition.COMPARISON comparison, Condition right) {
		this.left = left;
		this.right = right;
		this.comparison = comparison;
	}
	
	@Override
	public String toString() {
		return left.toString() + " " + comparison.toString() + " " + right.toString();
	}
	
	@Override
	public TYPE getType() {
		return Condition.TYPE.COMPOUND;
	}

	@Override
	public void add(Condition cond) {
		// TODO Auto-generated method stub
		
	}
	
}