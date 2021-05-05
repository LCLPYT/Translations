/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.network;

import work.lclpnet.translations.io.IAsyncTranslationLoader;

import javax.annotation.Nullable;
import java.io.IOException;
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
public class LCLPNetworkTranslationLoader implements IAsyncTranslationLoader {

    private final List<String> applications;
    private final List<String> languages;
    private final LCLPTranslationAPI api;

    /**
     * @param applications A list of LCLPNetwork translation applications that should be fetched.
     * @param languages An optional list of languages to load. If null, every language will be loaded.
     */
    public LCLPNetworkTranslationLoader(List<String> applications, @Nullable List<String> languages) {
        this(applications, languages, LCLPTranslationAPI.INSTANCE);
    }

    /**
     * @param applications A list of LCLPNetwork translation applications that should be fetched.
     * @param languages An optional list of languages to load. If null, every language will be loaded.
     * @param api The api to be used.
     */
    public LCLPNetworkTranslationLoader(List<String> applications, @Nullable List<String> languages, LCLPTranslationAPI api) {
        this.applications = Objects.requireNonNull(applications);
        this.languages = languages;
        this.api = Objects.requireNonNull(api);
    }

    @Nullable
    @Override
    public CompletableFuture<Map<String, Map<String, String>>> load() throws IOException {
        return api.getTranslations(this.applications, this.languages).thenApply(apps -> {
            if(apps == null) return null;

            Map<String, Map<String, String>> translations = new HashMap<>();

            for(TranslationApplication app : apps) {
                for(TranslationLanguage lang : app.getLanguages()) {
                    Map<String, String> langEntries;
                    if(translations.containsKey(lang.getLocale())) langEntries = translations.get(lang.getLocale());
                    else {
                        langEntries = new HashMap<>();
                        translations.put(lang.getLocale(), langEntries);
                    }

                    for(TranslationEntry entry : lang.getEntries())
                        langEntries.put(entry.getKey(), entry.getValue());
                }
            }

            return translations;
        });
    }

}
