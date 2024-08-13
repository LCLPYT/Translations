/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.util;

import work.lclpnet.translations.model.LanguageCollection;

import java.io.InputStream;

public interface TranslationParser {

    void parse(InputStream input, String language) throws Exception;

    LanguageCollection build();
}
