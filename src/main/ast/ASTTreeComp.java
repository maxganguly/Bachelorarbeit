package main.ast;

public class ASTTreeComp {
	
	public enum Checktype {
		OPTIONAL,
		ALL,
		NONE
	}

	public ASTTree[] parts;
	public Checktype type;
	
	public ASTTreeComp(Checktype type, ASTTree... parts) {
		this.type = type;
		this.parts = parts;
	}
	
	
	
	

}
