/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.model;

import org.jetbrains.annotations.Nullable;
import work.lclpnet.translations.util.Pair;

import java.util.Map;
import java.util.stream.Stream;

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

    @Override
    public Stream<Pair<String, String>> stream() {
        return mapping.entrySet().stream().map(Pair::of);
    }
}
