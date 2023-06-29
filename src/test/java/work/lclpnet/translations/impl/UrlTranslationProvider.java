/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.translations.loader.TranslationProvider;
import work.lclpnet.translations.loader.language.LanguageLoader;
import work.lclpnet.translations.loader.language.UrlLanguageLoader;

import java.net.URL;
import java.util.Collections;
import java.util.List;

public class UrlTranslationProvider implements TranslationProvider {

    private final Logger logger = LoggerFactory.getLogger(UrlTranslationProvider.class);

    @Override
    public LanguageLoader create() {
        URL[] url = UrlLanguageLoader.getResourceLocations(this);
        List<String> resourceDirectories = Collections.singletonList("lang/");

        return new UrlLanguageLoader(url, resourceDirectories, logger);
    }
}
