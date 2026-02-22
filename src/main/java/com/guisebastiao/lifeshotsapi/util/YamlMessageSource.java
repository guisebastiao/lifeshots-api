package com.guisebastiao.lifeshotsapi.util;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class YamlMessageSource extends ResourceBundleMessageSource {

    public YamlMessageSource(String basename) {
        setBasename(basename);
        setDefaultEncoding("UTF-8");
    }

    @Override
    protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
        return ResourceBundle.getBundle(basename, locale, new YamlResourceBundle.Control());
    }
}