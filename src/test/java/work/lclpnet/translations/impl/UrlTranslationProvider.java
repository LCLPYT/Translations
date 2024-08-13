/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.translations.loader.TranslationLoader;
import work.lclpnet.translations.loader.TranslationProvider;
import work.lclpnet.translations.loader.UrlArchiveTranslationLoader;

import java.net.URL;
import java.util.Collections;
import java.util.List;

public class UrlTranslationProvider implements TranslationProvider {

    private final Logger logger = LoggerFactory.getLogger(UrlTranslationProvider.class);

    @Override
    public TranslationLoader create() {
        URL[] url = UrlArchiveTranslationLoader.getResourceLocations(this);
        List<String> resourceDirectories = Collections.singletonList("lang/");

        return UrlArchiveTranslationLoader.ofJson(url, resourceDirectories, logger);
    }
}
