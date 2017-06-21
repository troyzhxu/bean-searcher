package com.ejl.searcher.util.clazz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.ejl.searcher.util.file.FileScanner;


/**
 * 类 扫描器
 * @author Troy.Zhou
 *
 */
public class ClassScanner {

	
	/**
	 * @param baseDir 存放 classes 的跟路径
	 * @param packageName 检索的 package 名称
	 * @return 扫描到的类
	 * */
	public static List<Class<?>> scan(String baseDir, String packageName) {
		String packageDir = packageName.replace(".", "/");
        return findClasses(baseDir, packageDir);
	}
	
	/**
	 * @param baseDir 存放 jar 的路径
	 * @param jarName 可检索的 jar 名称
	 * @param packageName 检索的 package 名称
	 * @return 扫描到的类
	 * */
	public static List<Class<?>> scan(String baseDir, String jarName, String packageName) {
		String packageDir = packageName.replace(".", "/");
		return findClassesInJar(baseDir, jarName, packageDir);
	}
	

	private static List<Class<?>> findClasses(String baseDir, String packageDir) {
    	if (!baseDir.endsWith("/") && !baseDir.endsWith("\\")) {
    		baseDir += "/";
    	}
    	String fullPath = baseDir + packageDir;
        List<Class<?>> classList = new ArrayList<>();
        List<File> classFileList = FileScanner.findFiles(fullPath, "*.class");
        for (File classFile : classFileList) {
            String className = className(classFile, "/classes");
            try {
                Class<?> clazz = Class.forName(className);
                classList.add(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classList;
    }
 

	private static List<Class<?>> findClassesInJar(String baseDir, String jarName, String packageDir) {
    	List<Class<?>> classList = new ArrayList<>();
    	if (!baseDir.endsWith("/") && !baseDir.endsWith("\\")) {
    		baseDir += "/";
    	}
    	String jarPath = baseDir + jarName + ".jar";
		try {
			JarFile jarFile = new JarFile(jarPath);
			Enumeration<JarEntry> entrys = jarFile.entries();
		    while (entrys.hasMoreElements()) {  
		        JarEntry jarEntry = entrys.nextElement();  
		        String entryName = jarEntry.getName();
		        if (entryName.startsWith(packageDir) && entryName.endsWith(".class")) {
		        	String className = entryName.substring(0, entryName.length() - 6).replace("/", ".");
		        	try {
		                classList.add(Class.forName(className));
		            } catch (ClassNotFoundException e) {
		                e.printStackTrace();
		            }
		        } 
		    }  
		    jarFile.close();
		} catch (IOException e) {
			throw new ReadJarFileErrorException(jarName + ".jar", e);
		}
    	return classList;
    }
    
	 
    private static String className(File classFile, String pre) {
        String objStr = classFile.toString().replaceAll("\\\\", "/");
        String className;
        className = objStr.substring(objStr.indexOf(pre) + pre.length(),
                objStr.indexOf(".class"));
        if (className.startsWith("/")) {
            className = className.substring(className.indexOf("/") + 1);
        }
        return className.replaceAll("/", ".");
    }
 
 
}
