package org.example.configuration;

import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;

@NoArgsConstructor
public class YamlParser {
    public static AppConfig parse(String path) {
        var yaml = new Yaml();
        InputStream inputStream = AppConfig.class.getClassLoader()
                .getResourceAsStream(path);
        return yaml.loadAs(inputStream, AppConfig.class);
    }
}
