package main.dynamic;

public class MethodNotFoundException extends Exception {

	public MethodNotFoundException(String name) {
		super("The method with the name: "+name+" was not found!");
	}
}
