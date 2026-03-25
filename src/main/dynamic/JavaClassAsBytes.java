package main.dynamic;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaClassAsBytes extends SimpleJavaFileObject {

    protected ByteArrayOutputStream bos =
        new ByteArrayOutputStream();

    /**
     * Creates a new SJFO with a ByteArrayOutputStream to save the class as Byte Array
     * @param name the Name of the class
     * @param kind should be Kind.CLASS
     */
    public JavaClassAsBytes(String name, Kind kind) {
        super(URI.create("string:///" + name.replace('.', '/')
            + kind.extension), kind);
    }

    public byte[] getBytes() {
        return bos.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() {
        return bos;
    }
}