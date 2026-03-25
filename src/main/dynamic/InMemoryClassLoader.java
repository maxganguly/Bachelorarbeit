package main.dynamic;

import java.util.Map;

public class InMemoryClassLoader extends ClassLoader{

	private InMemoryFileManager manager;

	/**
	 * Generates a new ClassLoader
	 * @param parent the classLoader to be used
	 * @param manager the FileManager to be used to manage the INMemoryClasses
	 */
    public InMemoryClassLoader(ClassLoader parent, InMemoryFileManager manager) {
        super(parent);
        if(manager == null)
        	throw new IllegalArgumentException("Manager must not be null");
        this.manager = manager;
    }
    
    /**
     * Finds a class containing the name in the available classes 
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        Map<String, JavaClassAsBytes> compiledClasses = manager.getBytesMap();
        for(String n : compiledClasses.keySet()) {
        	if(n.contains(name)) {
        		name = n;
        		break;
        	}
        }
        
        if (compiledClasses.containsKey(name)) {
            byte[] bytes = compiledClasses.get(name).getBytes();
            return defineClass(name, bytes, 0, bytes.length);
        } else {
            throw new ClassNotFoundException();
        }
    }
}
