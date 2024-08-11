/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader;

import org.slf4j.Logger;
import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.util.IOUtil;
import work.lclpnet.translations.util.JsonLanguageCollectionBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * A translation loader that parses a translation json file from a given URL.
 */
public class UrlJsonTranslationLoader implements TranslationLoader {

    private final URL url;
    private final String language;
    private final Executor executor;
    private final Logger logger;

    public UrlJsonTranslationLoader(URL url, String language, Logger logger) {
        this(url, language, ForkJoinPool.commonPool(), logger);
    }

    public UrlJsonTranslationLoader(URL url, String language, Executor executor, Logger logger) {
        this.url = url;
        this.language = language;
        this.executor = executor;
        this.logger = logger;
    }

    @Override
    public CompletableFuture<? extends LanguageCollection> load() {
        return CompletableFuture.supplyAsync(this::loadSync, executor);
    }

    private LanguageCollection loadSync() {
        final JsonLanguageCollectionBuilder builder = new JsonLanguageCollectionBuilder(logger);

        try {
            parseTranslations(url, builder);
        } catch (IOException e) {
            logger.error("Failed to parse translations from {}", url, e);
        }

        return builder.build();
    }

    private void parseTranslations(URL url, JsonLanguageCollectionBuilder builder) throws IOException {
        try (InputStream in = url.openStream()) {
            String json = IOUtil.readString(in, StandardCharsets.UTF_8);

            builder.parse(json, language);
        }
    }
}
