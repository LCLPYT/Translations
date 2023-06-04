/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.model;

import javax.annotation.Nullable;
import java.util.Map;

public class StaticLanguage implements Language {

    private final Map<String, String> mapping;

    public StaticLanguage(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    @Nullable
    @Override
    public String get(String key) {
        return mapping.get(key);
    }

    @Override
    public Iterable<String> keys() {
        return mapping.keySet();
    }

    @Override
    public boolean has(String key) {
        return mapping.containsKey(key);
    }
}
