package main.conditions;

public class ConditionUnary implements Condition {

	private String value;
	private Condition.TYPE type;

	public ConditionUnary(String value, Condition.TYPE type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public TYPE getType() {
		return type;
	}

	@Override
	public void add(Condition cond) {
		// TODO Auto-generated method stub

	}

}
