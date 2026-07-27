package main.dynamic;
/**
 * A class to show that a method was not found
 */
public class MethodNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public MethodNotFoundException(String name) {
		super("The method with the name: "+name+" was not found!");
	}
}
