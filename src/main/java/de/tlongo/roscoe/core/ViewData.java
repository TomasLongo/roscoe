package de.tlongo.roscoe.core;

/**
 * Created by tomas on 30.12.14.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Container of key-value-pairs that is passed to the viewhandler
 * in order to populate a view with data.
 */
public class ViewData {
    Map<String, Object> data = new HashMap<>();

    /**
     * Adds a date associated with the passed key.
     * If the key is already present, the former value will be overwritten.
     */
    public ViewData add(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Map<String, Object> asMap() {
        return data;
    }


    public void forEach(BiConsumer<? super String, ? super Object> action) {
        data.forEach(action);
    }

    public <T> T get(String key) {
        return (T)data.get(key);
    }

    public int size() {
        return data.size();
    }
}
