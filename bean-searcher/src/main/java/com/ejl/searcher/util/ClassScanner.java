package com.ejl.searcher.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.ejl.searcher.SearcherException;


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
            String className = className(classFile, baseDir);
            try {
                classList.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new SearcherException("加载类失败：", e);
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
		            classList.add(Class.forName(className));
		        }
		    }  
		    jarFile.close();
		} catch (Exception e) {
			throw new SearcherException("JAR[" + jarName + ".jar]遍历异常：", e);
		}
    	return classList;
    }
    
	 
    private static String className(File classFile, String classRootPath) {
        String filePath = classFile.getPath().replaceAll("\\\\", "/");
        String className = filePath.substring(classRootPath.length() - 1, filePath.indexOf(".class"));
        if (className.startsWith("/")) {
            className = className.substring(className.indexOf("/") + 1);
        }
        return className.replaceAll("/", ".");
    }
 
 
}
