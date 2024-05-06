package org.example.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadP {

    private static final String PROPERTIES_FILE = "sql.properties";

    public static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
//        try (InputStream input = DatabaseProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
//            properties.load(input);
//        }
        return properties;
    }

}
