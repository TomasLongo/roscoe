package de.tlongo.roscoe.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tomas on 11.01.15.
 */
public class ConfigManager {
    public final class ConfigItem {
        JsonElement configItem;

        public String asString() {
            return configItem.getAsString();
        }

        public Integer asInteger() {
            return configItem.getAsInt();
        }

        public JsonElement jsonElement() {
            return configItem;
        }

        public void setItem(JsonElement item) {
            configItem = item;
        }
    }

    ConfigItem configItem = new ConfigItem();

    Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    String roscoeRoot;
    File configDir;
    ViewHandler viewHandler;

    Map<String, JsonObject> configs = new HashMap<>();

    public ConfigManager() {
        roscoeRoot = System.getProperty("roscoe.root");
        configDir = new File(roscoeRoot + "/conf");
        try {
            configs.put("routes", new JsonParser().parse(new FileReader(configDir + "/routes.json")).getAsJsonObject());
            configs.put("core", new JsonParser().parse(new FileReader(configDir + "/core.json")).getAsJsonObject());
        } catch (FileNotFoundException e) {
            logger.error("Could not load routes config {}", configDir + ("/routes.json"));
            throw new RuntimeException("Could not load routes config.");
        }
    }

    public ConfigItem getConfigItem(String configName, String key) {
        JsonObject config = configs.getOrDefault(configName, null);
        if (config == null) {
            logger.error("{} is not a valid config.");
            throw new RuntimeException("Error loading config");
        }

        configItem.setItem(config.get(key));

        return configItem;
    }
}
