/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.model;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Language {

    private final Map<String, String> mapping = new HashMap<>();

    @Nullable
    public String get(String key) {
        return mapping.get(key);
    }

    public Iterable<String> keys() {
        return mapping.keySet();
    }

    public void add(String key, String value) {
        mapping.put(key, value);
    }

    public void addAll(Map<String, String> mapping) {
        mapping.forEach(this::add);
    }

    public boolean has(String key) {
        return mapping.containsKey(key);
    }
}
