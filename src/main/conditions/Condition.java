package main.conditions;

public interface Condition {

	public enum TYPE{
		LITERAL,
		VARIABLE,
		COMPARISION,
		COMPOUND,
		NOT
	}

	public enum COMPARISON{
		EQUALS("=="),
		NOT_EQUALS("!="),
		LESS_THAN("<"),
		LESS_OR_EQUALS("<="),
		GREATER_THAN(">"),
		GREATER_OR_EQUALS(">=");
		
		final String representation;
		private COMPARISON(String representation) {
			this.representation = representation;
		}
		public static COMPARISON fromString(String s) {
			switch (s.trim()){
			case "==": return EQUALS;
			case "!=": return NOT_EQUALS;
			case "<=": return LESS_OR_EQUALS;
			case "<": return LESS_THAN;
			case ">": return GREATER_THAN;
			case ">=": return GREATER_OR_EQUALS;
			}
			return null;
		}
		
		@Override
		public String toString() {
			return representation;
		}
	}

	public enum BOOLCOMPOUND{
		OR("||"),
		AND("&&"),
		XOR("^");
		final String representation;
		private BOOLCOMPOUND(String representation) {
			this.representation = representation;
		}
		public static BOOLCOMPOUND fromString(String s) {
			switch (s.trim()){
			case "|": 
			case "||": return OR;
			case "&": 
			case "&&": return AND;
			case "^": return XOR;
			}
			return null;
		}
		@Override
		public String toString() {
			return representation;
		}
	}

	public enum NUMCOMPOUND{
		ADDITION("*"),
		SUBTRACTION("-"),
		MULTIPLICATION("*"),
		DIVISION("/"),
		MOD("%");
		final String representation;
		private NUMCOMPOUND(String representation) {
			this.representation = representation;
		}
		public static NUMCOMPOUND fromString(String s) {
			switch (s.trim()){
			case "+": return ADDITION;
			case "-": return SUBTRACTION;
			case "*": return MULTIPLICATION;
			case "/": return DIVISION;
			case "%": return MOD;
			}
			return null;
		}
		@Override
		public String toString() {
			return representation;
		}
	}

	/**
	 * Returns a string of the condition
	 * @return
	 */
	@Override
	public String toString();

	/**
	 * Returns the type of the
	 * @return
	 */
	public TYPE getType();	
	
	/**
	 * Will negate the current condition and 
	 * @return
	 */
	public Condition negate();

	
	public default boolean canNegate() {
		return this instanceof ConditionNot ||
				this instanceof ConditionComparison ||
				this instanceof ConditionBoolCompound;
	}
	
	/**
	 * Tries to simplify and evaluate the Condition
	 * @return the simpliefied or evaluated condition
	 */
	public Condition evaluate();
	
}


