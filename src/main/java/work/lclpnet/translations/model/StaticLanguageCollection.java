/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.model;

import javax.annotation.Nullable;
import java.util.Map;

public class StaticLanguageCollection implements LanguageCollection {

    private final Map<String, ? extends Language> languages;

    public StaticLanguageCollection(Map<String, ? extends Language> languages) {
        this.languages = languages;
    }

    @Nullable
    @Override
    public Language get(String key) {
        return languages.get(key);
    }

    @Override
    public Iterable<String> keys() {
        return languages.keySet();
    }
}
