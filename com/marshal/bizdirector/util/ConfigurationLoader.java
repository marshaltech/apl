package com.marshal.bizdirector.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @author NaeemJamil
 *
 */
public class ConfigurationLoader {

	private static Properties		prop	= null;
	private ConfigurationLoader(){
		
	}

	static {
		InputStream input = ConfigurationLoader.class.getClassLoader().getResourceAsStream("drift.properties");
		try {
			prop = new Properties();
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getPropertyValue(String property, String defaultValue) {
    	return prop.getProperty(property, defaultValue).trim();
	}

}
