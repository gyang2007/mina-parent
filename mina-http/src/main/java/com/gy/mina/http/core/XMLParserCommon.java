package com.gy.mina.http.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解析项目配置文件
 * 
 * @author gyang
 *
 */
public class XMLParserCommon {
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLParserCommon.class);
	
	public XMLParserCommon() {
		
	}
	
	/**
	 * 获取指定包下的所有CLASS文件
	 * 
	 * @param packageName
	 * @return Set<Class<?>>
	 */
	public Set<Class<?>> getClasses(String packageName) {
		LOGGER.info("Package name " + packageName + "...");
		Set<Class<?>> classes = new HashSet<Class<?>>();
		
		String packageDirName = packageName.replace('.', '/');
		try {
			Enumeration<URL> enumeration = URLMappingResourceManager.class.getClassLoader().getResources(packageDirName);
			URL url;
			while(enumeration.hasMoreElements()) {
				url = enumeration.nextElement();
				String protocol = url.getProtocol();
				if("file".equals(protocol)) {
					classes.addAll(getFileClasses(packageName, url));
				}
				else if("jar".equals(protocol)) {
					classes.addAll(getJarFileClasses(packageName, url));
				}
				else {
					LOGGER.warn("Unknown protocol...");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return classes;
	}
	
	/**
	 * 加载文件系统内的类文件
	 * 
	 * @param packageName
	 * @param url
	 * @return
	 */
	private Set<Class<?>> getFileClasses(String packageName, URL url) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		try {
			File dir = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
			if(!dir.exists()) {
				throw new IllegalArgumentException("Package " + packageName + " not found...");
			}
			
			File[] fs = dir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					if(pathname.isDirectory()) {
						return false;
					}
					
					String name = pathname.getName();
					if(!name.endsWith(".class")) {
						return false;
					}
					
					return true;
				}
			});
			
			for(File f : fs) {
				try {
					String className = f.getName().substring(0, f.getName().length() - 6);
					className = packageName + "." + className;
					classes.add(Class.forName(className));
					LOGGER.info("Add class " + className + "...");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return classes;
	}
	/**
	 * 加载jar文件内的类文件
	 * 
	 * @param packageName
	 * @param url
	 * @return
	 */
	private Set<Class<?>> getJarFileClasses(String packageName, URL url) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		String path = url.getFile();	// eg: file:/data/develop/junoworkspace/omatrix/omatrix.mina.server/omatrix.mina.server_fat.jar!/com/omatrix/controller
		int matchIndex1 = path.indexOf(':');
		int matchIndex2 = path.lastIndexOf('!');
		String jarFilePath = path.substring(matchIndex1 + 1, matchIndex2);
		String packageDir = path.substring(matchIndex2 + 2);
		try {
			JarFile jarFile = new JarFile(jarFilePath);
			Enumeration<JarEntry> jarEntrys = jarFile.entries();
			while(jarEntrys.hasMoreElements()) {
				JarEntry jarEntry = jarEntrys.nextElement();
				String name = jarEntry.getName();
				if(!jarEntry.isDirectory() && name.endsWith(".class")) {
					int lastIndex = name.lastIndexOf('/');
					String head = name.substring(0, lastIndex);
					String tail = name.substring(lastIndex + 1);
					if(head.equals(packageDir) && tail.endsWith(".class")) {
						try {
							classes.add(Class.forName(packageName + "." + (tail.substring(0, tail.lastIndexOf('.')))));
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return classes;
	}
}
