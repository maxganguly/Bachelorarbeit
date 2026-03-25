package main.dynamic;

import java.util.Hashtable;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private Map<String, JavaClassAsBytes> compiledClasses;

    private ClassLoader loader; 

    /**
     * Generates a new InMemoryFileManager to manage the classes which will be used as files in Memory
     * @param standardManager
     */
    public InMemoryFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
        this.compiledClasses = new Hashtable<>();
        this.loader = new InMemoryClassLoader(this.getClass().getClassLoader(), this);
    }
    
    
    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
        String className, Kind kind, FileObject sibling) {

        JavaClassAsBytes classAsBytes = new JavaClassAsBytes(className, kind);
        compiledClasses.put(className, classAsBytes);

        return classAsBytes;
    }

    public Map<String, JavaClassAsBytes> getBytesMap() {
        return compiledClasses;
    }
    
    @Override
    public ClassLoader getClassLoader(Location location) {
        return loader;
    }
    
}