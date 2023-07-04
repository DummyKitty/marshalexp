package xyz.eki.marshalexp.rmi.utils;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClassLoaderSpi;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 重写RMI ClassLoader 强制加载远程类，获得相关信息
 *
 * @author Eki
 */
public class CustomRMIClassLoader extends RMIClassLoaderSpi {

    private static RMIClassLoaderSpi originalLoader = RMIClassLoader.getDefaultProviderInstance();
    private static HashMap<String,Set<String>> codebases = new HashMap<>();

    @Override
    public Class<?> loadClass(String codebase, String name, ClassLoader defaultLoader) throws MalformedURLException, ClassNotFoundException {
        Class<?> resolvedClass = null;

        //不从远程加载取消codebase https://docs.oracle.com/javase/7/docs/technotes/guides/rmi/codebase.html
        codebase = null;
        try{
            if (name.endsWith("_Stub"))
                ReflectUtils.makeLegacyStub(name);
            //System.out.println(name);

            resolvedClass = originalLoader.loadClass(codebase,name,defaultLoader);
        }catch (CannotCompileException |NotFoundException e){
            ExceptionHandler.internalError("loadClass", "Unable to compile unknown stub class.");
        }

        return resolvedClass;
    }

    @Override
    public Class<?> loadProxyClass(String codebase, String[] interfaces, ClassLoader defaultLoader) throws MalformedURLException, ClassNotFoundException {
        Class<?> resolvedClass = null;
        try{
            for (String interfaceName:
                 interfaces) {
                //System.out.println(interfaceName);
                ReflectUtils.makeInterface(interfaceName);
                //addCodebase(codebase,interfaceName);
            }

            System.out.println("==================");

            codebase = null;
            resolvedClass = originalLoader.loadProxyClass(codebase,interfaces,defaultLoader);

        } catch (CannotCompileException e) {
            ExceptionHandler.internalError("loadProxyClass", "Unable to compile unknown interface class.");
        }

        return resolvedClass;
    }

    @Override
    public ClassLoader getClassLoader(String codebase) throws MalformedURLException {
        codebase = null;
        return originalLoader.getClassLoader(codebase);
    }

    @Override
    public String getClassAnnotation(Class<?> cl) {
        return originalLoader.getClassAnnotation(cl);
    }

    /**
     * Adds the codebase - className pair into a HashMap. If the codebase was already
     * added before, the className is appended to the Set within the value of the
     * HashMap. Classes that are part of common default packages like java.* are
     * ignored.
     *
     * @param codebase value enumerated by the loader
     * @param className that should be loaded from the codebase
     */
    private void addCodebase(String codebase, String className)
    {
        if( codebase == null )
            return;

        if( className.startsWith("java.") || className.startsWith("[Ljava") || className.startsWith("javax.") )
            codebases.putIfAbsent(codebase, new HashSet<String>());

        else if( codebases.containsKey(codebase) ) {
            Set<String> classNames = codebases.get(codebase);
            classNames.add(className);

        } else {
            Set<String> classNames = new HashSet<String>();
            classNames.add(className);
            codebases.put(codebase, classNames);
        }
    }
}
