/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;

public interface Translator {

    @Nonnull
    String translate(String locale, String key, Object... substitutes);

    /**
     * Check if there is a specific translation for a given language.
     *
     * @param locale The locale to check.
     * @param key    The translation key.
     * @return True, if there is a translation for the given language.
     */
    boolean hasTranslation(String locale, String key);

    @Nonnull
    SimpleDateFormat getDateFormat(String locale);
}
