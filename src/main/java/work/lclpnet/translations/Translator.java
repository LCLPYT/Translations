/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

public interface Translator {

    @NotNull
    String translate(String locale, String key);

    /**
     * Check if there is a specific translation for a given language.
     *
     * @param locale The locale to check.
     * @param key    The translation key.
     * @return True, if there is a translation for the given language.
     */
    boolean hasTranslation(String locale, String key);

    @NotNull
    SimpleDateFormat getDateFormat(String locale);

    Iterable<String> getLanguages();

    @NotNull
    default String translate(String locale, String key, Object... substitutes) {
        return String.format(translate(locale, key), substitutes);
    }
}
