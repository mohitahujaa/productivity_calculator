package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    public static Properties load(String filename) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filename)) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("Could not load " + filename);
        }
        return props;
    }
}