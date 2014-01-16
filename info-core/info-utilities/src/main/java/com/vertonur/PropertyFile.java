/**
 * 
 */
package com.vertonur;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Vertonur
 * 
 */
public class PropertyFile {

	public static String getValue(String filePath, String key) {
		InputStream inputStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(filePath);
		Properties propertiesFile = new Properties();
		try {
			propertiesFile.load(inputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return propertiesFile.getProperty(key);
	}

	public static void setValue(String readFilePath, String writeFilePath,
			String key, String value) {
		InputStream inputStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(readFilePath);
		Properties propertiesFile = new Properties();
		try {
			propertiesFile.load(inputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		propertiesFile.setProperty(key, value);
		try {
			File theFile = new File(writeFilePath);
			theFile.setWritable(true);
			FileOutputStream fileOutputStream = new FileOutputStream(theFile);
			propertiesFile.store(fileOutputStream, "comments");
			theFile.setWritable(false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
