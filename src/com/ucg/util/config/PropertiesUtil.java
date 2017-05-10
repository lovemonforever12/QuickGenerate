package com.ucg.util.config;

import java.io.File;
import java.util.Properties;

public class PropertiesUtil {
	protected static Prop prop = null;
	/**
	 * Load property file.
	 * @see #loadPropertyFile(String, String)
	 */
	public static Properties loadPropertyFile(String fileName) {
		return loadPropertyFile(fileName, Const.DEFAULT_ENCODING);
	}
	
	/**
	 * Load property file.
	 * Example:<br>
	 * loadPropertyFile("db_username_pass.txt", "UTF-8");
	 * 
	 * @param fileName the file in CLASSPATH or the sub directory of the CLASSPATH
	 * @param encoding the encoding
	 */
	public static Properties loadPropertyFile(String fileName, String encoding) {
		prop = PropKit.use(fileName, encoding);
		return prop.getProperties();
	}
	
	/**
	 * Load property file.
	 * @see #loadPropertyFile(File, String)
	 */
	public static Properties loadPropertyFile(File file) {
		return loadPropertyFile(file, Const.DEFAULT_ENCODING);
	}
	
	/**
	 * Load property file
	 * Example:<br>
	 * loadPropertyFile(new File("/var/config/my_config.txt"), "UTF-8");
	 * 
	 * @param file the properties File object
	 * @param encoding the encoding
	 */
	public static Properties loadPropertyFile(File file, String encoding) {
		prop = PropKit.use(file, encoding);
		return prop.getProperties();
	}
	
	public static void unloadPropertyFile(String fileName) {
		Prop uselessProp = PropKit.useless(fileName);
		if (prop == uselessProp)
			prop = null;
	}
	
	public  static void unloadAllPropertyFiles() {
		PropKit.clear();
		prop = null;
	}
	
	private static Prop getProp() {
		if (prop == null)
			throw new IllegalStateException("Load propties file by invoking loadPropertyFile(String fileName) method first.");
		return prop;
	}
	
	public static String getProperty(String key) {
		return getProp().get(key);
	}
	
	public static String getProperty(String key, String defaultValue) {
		return getProp().get(key, defaultValue);
	}
	
	public  static Integer getPropertyToInt(String key) {
		return getProp().getInt(key);
	}
	
	public static Integer getPropertyToInt(String key, Integer defaultValue) {
		return getProp().getInt(key, defaultValue);
	}
	
	public static Long getPropertyToLong(String key) {
		return getProp().getLong(key);
	}
	
	public static Long getPropertyToLong(String key, Long defaultValue) {
		return getProp().getLong(key, defaultValue);
	}
	
	public static Boolean getPropertyToBoolean(String key) {
		return getProp().getBoolean(key);
	}
	
	public static Boolean getPropertyToBoolean(String key, Boolean defaultValue) {
		return getProp().getBoolean(key, defaultValue);
	}}