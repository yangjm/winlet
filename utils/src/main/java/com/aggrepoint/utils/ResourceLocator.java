package com.aggrepoint.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * 用于定位资源
 * 
 * Class.getResource()方法沿CLASSPATH中指定的各个目录和JAR寻找指定的资源。而
 * ResourceLocator.getResource()方法可以指定一个参照类，寻找资源时先查找该参
 * 照类所处的目录或JAR，若查找不到，再沿CLASSPATH各个目录查找。
 * 
 * 假设在同一个JVM的CLASSPATH中存在两个JAR，这两个JAR分别有目录/prop/conf.xml
 * 用于存放各自的配置。如果这两个JAR中的代码都用Class.getResource(“/prop/conf.xml”)
 * 方法去获取配置资源，那么在运行时两个JAR中代码所访问到的将会是同一个conf.xml
 * 文件。那个JAR文件先出现在CLASSPATH中，那个JAR里包含的conf.xml就会被访问。若
 * 使用ResourceLocator.getResource()方法去访问conf.xml文件，则两个JAR可以各自 访问各自的conf.xml，互不影响。
 * 
 * Creation date: (2002-6-8)
 * 
 * @author: Yang Jiang Ming
 */
public class ResourceLocator {
	/**
	 * 构造函数，不能被实例化
	 */
	ResourceLocator() {
	}

	/**
	 * 将指定类的类名中所有.替换为/，并在后面补上.class
	 */
	static String getClassPathName(Class<?> c) {
		String strClassFullName = c.getName();

		StringTokenizer st = new StringTokenizer(strClassFullName, ".");
		StringBuffer sb = new StringBuffer();
		sb.append(st.nextToken());

		while (st.hasMoreTokens()) {
			sb.append("/");
			sb.append(st.nextToken());
		}

		sb.append(".class");
		return sb.toString();
	}

	/**
	 * 返回指定类的Class Loader用于加载该类的Class Path。返回的字符串以/结尾
	 */
	public static String getClassRootPath(Class<?> c) {
		String strClassPathName = getClassPathName(c);

		ClassLoader cl = c.getClassLoader();
		if (cl == null)
			return null;

		URL urlClass = cl.getResource(strClassPathName);
		if (urlClass == null)
			return null;

		String strUrl = urlClass.toString();
		return strUrl.substring(0, strUrl.lastIndexOf(strClassPathName));
	}

	/**
	 * 返回指定类的文件所在的路径。返回的字符串以/结尾
	 */
	public static String getClassPath(Class<?> c) {
		String strClassFullName = c.getName();
		String strClassName = strClassFullName.substring(strClassFullName
				.lastIndexOf(".") + 1);
		String strClassPathName = getClassPathName(c);

		ClassLoader cl = c.getClassLoader();
		if (cl == null) {
			cl = ClassLoader.getSystemClassLoader();
			return null;
		}

		URL urlClass = cl.getResource(strClassPathName);
		if (urlClass == null)
			return null;

		String strUrl = urlClass.toString();
		return strUrl.substring(0, strUrl.lastIndexOf(strClassName));
	}

	/**
	 * 根据加载类的路径，构造资源URL。不检查资源是否存在返回的URL处。
	 */
	public static URL constructResourceURL(Class<?> c, String resName) {
		String strUrl = "";

		if (resName.startsWith("/")) // 资源名称以"/"开头，从类所在的Class Path根目录开始定位资源
			strUrl = getClassRootPath(c) + resName.substring(1);
		else
			// 资源名称不以"/"开头，从类所在的目录开始定位资源
			strUrl = getClassPath(c) + resName;

		try {
			return new URL(strUrl);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 定位资源。 首先查找指定类的加载路径，如果找不到，再由指定类的Class Loader从其CLASSPATH路径中查找。
	 */
	public static URL getResource(Class<?> c, String resName) {
		URL url = constructResourceURL(c, resName);

		if (url == null)
			return c.getResource(resName);

		InputStream is = null;
		try {
			is = url.openStream();
			is.read();
		} catch (Exception e) {
			return c.getResource(resName);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
			}
		}

		return url;
	}
}