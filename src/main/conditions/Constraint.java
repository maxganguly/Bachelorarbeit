package main.conditions;

import java.util.LinkedList;
import java.util.List;

/** 
 * Allows to add Constraints to a specific Datatype to reduce the possible variables in the datatype
 */
public abstract class Constraint<T> {

	private List<Condition> conditions;
	
	public Constraint() {
		this.conditions = new LinkedList<Condition>();
	}
	
	public Constraint(List<Condition> list) {
		this.conditions = list;
	}
	
	/**
	 * Checks if there exists at least one valid Variable value for the given COnstraints
	 * @returns true if at least one variable with the given constraints exists
	 */
	public abstract boolean exists();
	
	
	/**
	 * Checks if the given value is within the Constraints
	 * @returns true if the value falls within the given constraints
	 */
	public abstract boolean exists(T value);
	
	
	public void addCondition(Condition c) {
		conditions.add(c);
	}
	
}
