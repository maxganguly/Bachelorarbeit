package main.conditions;

public class ConditionBoolCompound implements Condition {

	private Condition left,right;
	private Condition.BOOLCOMPOUND compound;

	public ConditionBoolCompound(Condition left, Condition.BOOLCOMPOUND compound, Condition right) {
		this.left = left;
		this.right = right;
		this.compound = compound;
	}

	@Override
	public String toString() {
		return left.toString() + " " + compound.toString() + " " + right.toString();
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