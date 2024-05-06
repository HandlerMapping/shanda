package org.example;


import org.example.entity.Miniuser;
import org.example.jdbc.DatabaseConnection;

import java.sql.SQLException;
import java.util.List;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationLoader {
    private static final String CONFIG_FILE = "/sql.properties";
    private Properties properties;

    public ConfigurationLoader() {
        loadProperties();
    }

    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = ConfigurationLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new IOException("Unable to load configuration file: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 可以根据需要处理异常
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String[] a(){
        ConfigurationLoader loader = new ConfigurationLoader();
        String databaseUrl = loader.getProperty("url");
        String databaseUsername = loader.getProperty("username");
        String databasePassword = loader.getProperty("password");
        String [] s = new String[3];
        s[0] = databaseUrl;
        s[1] = databaseUsername;
        s[2] = databasePassword;
        return s;
    }
}
