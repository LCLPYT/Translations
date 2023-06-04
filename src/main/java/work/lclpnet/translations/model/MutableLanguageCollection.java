/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.model;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MutableLanguageCollection implements LanguageCollection {

    private final Map<String, MutableLanguage> languages = Collections.synchronizedMap(new HashMap<>());

    @Nullable
    @Override
    public Language get(String key) {
        return languages.get(key);
    }

    @Override
    public Iterable<String> keys() {
        return languages.keySet();
    }

    public void merge(LanguageCollection other) {
        if (this == other) return;

        for (String key : other.keys()) {
            Language otherLanguage = other.get(key);
            if (otherLanguage == null) continue;

            MutableLanguage language = languages.computeIfAbsent(key, ignored -> new MutableLanguage());
            language.addAll(otherLanguage);
        }
    }
}
