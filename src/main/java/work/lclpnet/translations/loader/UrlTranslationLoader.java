/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader;

import org.slf4j.Logger;
import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.util.JsonTranslationParser;
import work.lclpnet.translations.util.TranslationParser;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

/**
 * A translation loader that parses a translation json file from a given URL.
 */
public class UrlTranslationLoader implements TranslationLoader {

    private final URL url;
    private final String language;
    private final Executor executor;
    private final Logger logger;
    private final Supplier<TranslationParser> parserFactory;

    public UrlTranslationLoader(URL url, String language, Logger logger,
                                Supplier<TranslationParser> parserFactory) {
        this(url, language, ForkJoinPool.commonPool(), logger, parserFactory);
    }

    public UrlTranslationLoader(URL url, String language, Executor executor, Logger logger,
                                Supplier<TranslationParser> parserFactory) {
        this.url = url;
        this.language = language;
        this.executor = executor;
        this.logger = logger;
        this.parserFactory = parserFactory;
    }

    @Override
    public CompletableFuture<? extends LanguageCollection> load() {
        return CompletableFuture.supplyAsync(this::loadSync, executor);
    }

    private LanguageCollection loadSync() {
        final TranslationParser parser = parserFactory.get();

        parseTranslations(url, parser);

        return parser.build();
    }

    private void parseTranslations(URL url, TranslationParser parser) {
        try (InputStream in = url.openStream()) {
            parser.parse(in, language);
        } catch (Exception e) {
            logger.error("Failed to parse translations from {}", url, e);
        }
    }

    public static UrlTranslationLoader ofJson(URL url, String language, Logger logger) {
        return new UrlTranslationLoader(url, language, logger, () -> new JsonTranslationParser(logger));
    }
}
