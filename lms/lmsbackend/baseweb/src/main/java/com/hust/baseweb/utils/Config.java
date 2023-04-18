package com.hust.baseweb.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
@Getter
@Setter
public class Config {

    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(Config.class);

    private final static Config config = new Config();

    private String googleApiKey;

    private Config() {
    }

    public static Config getConfig() {
        if (config.googleApiKey == null || config.googleApiKey.equals("")) {
            try {
                updateConfig();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    static String updateConfig() throws FileNotFoundException {

        File googleApiKeyFile = new File(Constant.GOOGLE_MAP_API_KEY_FILE);
        if (googleApiKeyFile.exists()) {
            String currentApiKey = new Scanner(googleApiKeyFile).useDelimiter("\\Z").next();
            if (!currentApiKey.equals(config.getGoogleApiKey())) {
                config.setGoogleApiKey(currentApiKey);
                return currentApiKey;
            }
            config.setGoogleApiKey(currentApiKey);
            return null;
        }
        return null;
    }
}
