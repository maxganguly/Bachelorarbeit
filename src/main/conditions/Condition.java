package main.conditions;

public interface Condition {
	
	public enum TYPE{
		LITERAL,
		VARIABLE,
		COMPARISION,
		COMPOUND
	};
	
	public enum COMPARISON{
		EQUALS,
		NOT_EQUALS,
		LESS_THAN,
		LESS_OR_EQUALS,
		GREATER_THAN,
		GREATER_OR_EQUALS,
	};
	
	public enum BOOLCOMPOUND{
		OR,
		AND,
		XOR,
	};
	
	public enum NUMCOMPOUND{
		ADDITION,
		SUBTRACTION,
		MULTIPLICATION,
		DIVISION
	}
	
	/**
	 * Returns a 
	 * @return
	 */
	public String toString();
	
	/**
	 * Returns the type of the 
	 * @return
	 */
	public TYPE getType();
	
	public void add(Condition cond);
	
}


