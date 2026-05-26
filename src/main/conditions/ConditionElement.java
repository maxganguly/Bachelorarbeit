package main.conditions;

import java.awt.IllegalComponentStateException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConditionElement implements Condition {

	public static final ConditionElement TRUE = new ConditionElement("true", TYPE.LITERAL);
	public static final ConditionElement FALSE = new ConditionElement("false", TYPE.LITERAL);
	public final String value;
	public final Condition.TYPE type;
	public static final Set<ConditionElement> existing;
	public Set<ConditionElement> ex = existing;
	static {
		existing = new HashSet<ConditionElement>();
		existing.add(TRUE);
		existing.add(FALSE);
	}

	private ConditionElement(String value, Condition.TYPE type) {
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
	public Condition negate() {
		if(value.equals("true"))
			return FALSE;
		if(value.equals("false"))
			return TRUE;
		throw new IllegalComponentStateException("A variable or Literal cant be negated");
	}

	@Override
	public Condition evaluate() {
		return this;
	}
	
	public static ConditionElement newElement(String value, Condition.TYPE type) {
		if(value.equalsIgnoreCase("true"))
			return TRUE;
		if(value.equalsIgnoreCase("false"))
			return FALSE;
		ConditionElement n = new ConditionElement(value, type);
		if(existing.contains(n))
			return existing.stream().filter(e -> e.equals(n)).toList().getFirst();
		existing.add(n);
		return n;
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(!(obj instanceof ConditionElement)) {
			return false;
		}
		ConditionElement e = (ConditionElement) obj;
		return this.type == e.type && this.value.equals(e.value);
	}
	
	public static ConditionElement fromBoolean(boolean b) {
		if(b)
			return TRUE;
		return FALSE;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value, type);
	}

}
