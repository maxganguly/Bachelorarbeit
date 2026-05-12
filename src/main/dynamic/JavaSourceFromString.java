package main.dynamic;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaSourceFromString extends SimpleJavaFileObject {

    private String sourceCode;

    /**
     * Creates an SimpleJavaFileObject with the given name, from the given String
     * @param name the name of the SJFO
     * @param sourceCode the content of the SJFO
     */
    public JavaSourceFromString(String name, String sourceCode) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
            Kind.SOURCE);
        if(sourceCode == null) {
			throw new IllegalArgumentException("Source code must not be null");
		}
        this.sourceCode = sourceCode;
    }

    /**
     * returns the source code of the SJFO
     */
    @Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceCode;
    }
}
