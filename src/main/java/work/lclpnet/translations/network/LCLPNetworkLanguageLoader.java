/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.network;

import org.slf4j.Logger;
import work.lclpnet.translations.loader.language.LanguageLoader;
import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.model.MutableLanguage;
import work.lclpnet.translations.model.StaticLanguageCollection;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Loads translations from the LCLPNetwork translation API.
 *
 * @author LCLP
 */
public class LCLPNetworkLanguageLoader implements LanguageLoader {

    private final List<String> applications;
    private final List<String> languages;
    private final LCLPTranslationAPI api;
    private final Logger logger;

    /**
     * @param applications A list of LCLPNetwork translation applications that should be fetched.
     * @param languages    An optional list of languages to load. If null, every language will be loaded.
     * @param logger       A logger for information.
     */
    public LCLPNetworkLanguageLoader(List<String> applications, @Nullable List<String> languages, Logger logger) {
        this(applications, languages, LCLPTranslationAPI.INSTANCE, logger);
    }

    /**
     * @param applications A list of LCLPNetwork translation applications that should be fetched.
     * @param languages    An optional list of languages to load. If null, every language will be loaded.
     * @param api          The api to be used.
     * @param logger       A logger for information.
     */
    public LCLPNetworkLanguageLoader(List<String> applications, @Nullable List<String> languages, LCLPTranslationAPI api, Logger logger) {
        this.applications = Objects.requireNonNull(applications);
        this.languages = languages;
        this.api = Objects.requireNonNull(api);
        this.logger = logger;
    }

    @Override
    public CompletableFuture<LanguageCollection> loadLanguages() {
        logger.info(String.format("Fetching translations for applications: %s for languages: %s", this.applications, this.languages == null ? "ALL" : this.languages));

        return api.getTranslations(this.applications, this.languages).thenApply(apps -> {
            if (apps == null) {
                logger.error(String.format("There was an error fetching translations for applications: %s", this.applications));
                return null;
            }

            Map<String, MutableLanguage> translations = new HashMap<>();

            for (TranslationApplication app : apps) {
                for (TranslationLanguage lang : app.getLanguages()) {
                    MutableLanguage language = translations.computeIfAbsent(lang.getLocale(),
                            locale -> new MutableLanguage());

                    for (TranslationEntry entry : lang.getEntries()) {
                        language.add(entry.getKey(), entry.getValue());
                    }
                }
            }

            logger.info(String.format("Loaded %s locales", translations.size()));

            return new StaticLanguageCollection(translations);
        });
    }
}
