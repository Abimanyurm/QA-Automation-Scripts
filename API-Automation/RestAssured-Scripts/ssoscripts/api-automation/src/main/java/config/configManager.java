package config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class configManager {
	
	static String FilePath ="src/main/resources/config.properties";

	private static final Properties properties = new Properties();

	static {
		try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
			properties.load(fis);
		} catch (IOException e) {
			throw new RuntimeException(" Failed to load config.properties file", e);
		}
	}

	public static String getProperty(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			throw new RuntimeException(" Missing property in config.properties: " + key);
		}

		return value.trim();
	}

	// ✅ New method to write/update property
	public static void setProperty(String key, String value) {
		try {
			properties.setProperty(key, value);

			try (FileOutputStream out = new FileOutputStream("src/main/resources/config.properties")) {
				properties.store(out, "Updated by automation framework");
			}
		} catch (IOException e) {
			throw new RuntimeException(" Failed to update config.properties", e);
		}

	}
}
