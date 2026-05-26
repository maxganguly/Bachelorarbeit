package main.conditions;

public class ConditionNot implements Condition{

	public final Condition condition;
	
	public ConditionNot(Condition condition) {
		this.condition = condition;
	}
	
	@Override
	public String toString() {
		return "!("+condition.toString()+")";
	}
	
	@Override
	public TYPE getType() {
		return Condition.TYPE.NOT;
	}

	@Override
	public Condition negate() {
		return condition;
	}

	@Override
	public Condition evaluate() {
		Condition e = condition.evaluate();
		if(e == ConditionElement.TRUE)
			return ConditionElement.FALSE;		
		else if(e == ConditionElement.FALSE)
				return ConditionElement.TRUE;
		return this;
	}

	
}
