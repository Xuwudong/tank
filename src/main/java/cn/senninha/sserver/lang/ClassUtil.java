package cn.senninha.sserver.lang;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cn.senninha.sserver.lang.message.Message;

/**
 * 包扫描工具类
 * 
 * @author senninha on 2017年11月4日
 *
 */
public class ClassUtil {

	public static void main(String[] args) {
		/** ClassFilter 接口里提供扫描到的接口的处理方法 **/
		scanPackage("com.senninha", false, new ClassFilter<Message>() {
			@Override
			public boolean filter(Class<Message> clazz) {
				Message m = clazz.getAnnotation(Message.class);
				if (m != null) {
					System.out.println(clazz.getName() + " cmd:" + m.cmd());
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 包扫描，如果输入 com.senninha 那么将扫描包内的 com.senninha
	 * 包下的所有class文件，包括com.senninha.a包下的.class文件
	 * 
	 * @param packageName
	 * @param noLimit ture的情况，扫描所有的包
	 */
	public static void scanPackage(String packageName, boolean noLimit, ClassFilter<?> filter) {
		String classpath = System.getProperty("java.class.path");
		
		if(classpath.contains("tomcat-juli.jar")){//处理tomcat流氓改cp的问题
			classpath = ClassUtil.class.getResource("/").toString();   
		}
		
		String[] classpaths = null;
		classpaths = classpath.split(File.pathSeparator);

		for (String s : classpaths) {
			scanDir(packageName, s, noLimit, filter);
		}
	}

	/**
	 * 扫描文件夹
	 * 
	 * @param dir
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	private static void scanDir(String targetPackage, String dir, boolean noLimit, ClassFilter<?> filter) {
		File file = new File(dir);
		if (!file.exists()) {
			return;
		}

		if (file.isDirectory()) {
			for (File childFile : file.listFiles()) {
				scanDir(targetPackage, childFile.getAbsolutePath(), noLimit, filter);
			}
		} else if (file.getPath().endsWith(".class")) {
			String fileName = file.getPath();
			try {
				fileName = convertToPackage(fileName);
				if (noLimit || fileName.lastIndexOf(targetPackage) == 0) {
					@SuppressWarnings("rawtypes")
					Class clazz = Class.forName(fileName, false, ClassUtil.class.getClassLoader());
					filter.filter(clazz);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (file.getPath().endsWith(".jar")) {
			try {
				JarFile jar = new JarFile(file);
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					scanJar(entry, targetPackage, noLimit, filter);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 扫描jar包
	 * 
	 * @param jar
	 * @param targetPackage
	 *            目标包
	 * @param filter
	 */
	@SuppressWarnings("unchecked")
	private static void scanJar(JarEntry entry, String targetPackage, boolean noLimit, ClassFilter<?> filter) {
		String entryName = entry.getName();
		if (entryName.endsWith(".class")) {
			entryName = convertToPackageFromJarEntry(entryName);
			if (noLimit || entryName.lastIndexOf(targetPackage) == 0) {
				try {
					@SuppressWarnings("rawtypes")
					Class clazz = Class.forName(entryName, false, ClassUtil.class.getClassLoader());
					filter.filter(clazz);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 将class文件名转化为包名
	 * 
	 * @param fileName
	 * @return
	 */
	private static String convertToPackage(String fileName) {
		String result = fileName.substring(fileName.indexOf("classes") + 8, fileName.length() - 6);
		return result.replaceAll(regexSeprator(), ".");
	}

	/**
	 * 将JarEntry里的名字转化为包名
	 * 
	 * @param entryName
	 * @return
	 */
	private static String convertToPackageFromJarEntry(String entryName) {
		String result = entryName.substring(0, entryName.length() - 6);
		return result.replaceAll(regexSeprator(), ".");
	}
	
	/**
	 * 返回对应操作系统下的分隔符
	 * @return
	 */
	private static String regexSeprator(){
		if(File.separator.equals("\\")){
			return "\\\\";
		}else{
			return File.separator;
		}
	}

}
