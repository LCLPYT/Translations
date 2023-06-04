/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.translations.loader.TranslationProvider;
import work.lclpnet.translations.loader.language.ClassLoaderLanguageLoader;
import work.lclpnet.translations.loader.language.LanguageLoader;

import java.util.Collections;
import java.util.List;

public class CodeSourceTranslationProvider implements TranslationProvider {

    private final Logger logger = LoggerFactory.getLogger(CodeSourceTranslationProvider.class);

    @Override
    public LanguageLoader create() {
        ClassLoader classLoader = CodeSourceTranslationProvider.class.getClassLoader();
        List<String> resourceDirectories = Collections.singletonList("lang/");

        return new ClassLoaderLanguageLoader(classLoader, resourceDirectories, logger);
    }
}
