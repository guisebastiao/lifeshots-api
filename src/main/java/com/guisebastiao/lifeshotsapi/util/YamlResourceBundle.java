package com.guisebastiao.lifeshotsapi.util;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class YamlResourceBundle extends ResourceBundle {

    private final Properties properties;

    public YamlResourceBundle(InputStream stream) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(stream);
        this.properties = flattenMap(map);
    }

    private Properties flattenMap(Map<String, Object> map) {
        Properties props = new Properties();
        flattenMap("", map, props);
        return props;
    }

    @SuppressWarnings("unchecked")
    private void flattenMap(String prefix, Map<String, Object> map, Properties props) {
        map.forEach((key, value) -> {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;

            if (value instanceof Map) {
                flattenMap(fullKey, (Map<String, Object>) value, props);
            } else {
                props.put(fullKey, value.toString());
            }
        });
    }

    @Override
    protected Object handleGetObject(String key) {
        return properties.getProperty(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(properties.stringPropertyNames());
    }

    public static class Control extends ResourceBundle.Control {

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "yml");

            try (InputStream stream = loader.getResourceAsStream(resourceName)) {
                if (stream != null) {
                    return new YamlResourceBundle(stream);
                }
            }

            return null;
        }

        @Override
        public List<String> getFormats(String baseName) {
            return Collections.singletonList("yml");
        }
    }
}