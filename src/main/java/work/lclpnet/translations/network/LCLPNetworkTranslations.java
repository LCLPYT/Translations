/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.network;

import org.slf4j.Logger;
import work.lclpnet.translations.Translator;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class to load LCLPNetwork translations to {@link Translator}.
 */
public class LCLPNetworkTranslations {

    private final Logger logger;
    private final Translator translator;

    public LCLPNetworkTranslations(Logger logger, Translator translator) {
        this.logger = logger;
        this.translator = translator;
    }

    /**
     * Fetch LCLPNetwork translations and load them to {@link Translator}.
     *
     * @param applications An array of {@link TranslationApplication} names to be fetched.
     * @return A completable future that will be notified when the operation is done.
     * @throws IOException If there was an IO-error of any kind.
     */
    public CompletableFuture<Void> loadApplications(String... applications) throws IOException {
        return loadApplications(Arrays.asList(applications), null);
    }

    /**
     * Fetch LCLPNetwork translations and load them to {@link Translator}.
     *
     * @param applications A list of {@link TranslationApplication} names to be fetched.
     * @param languages An optional list of languages to be fetched. If null, every language will be fetched.
     * @return A completable future that will be notified when the operation is done.
     * @throws IOException If there was an IO-error of any kind.
     */
    public CompletableFuture<Void> loadApplications(List<String> applications, @Nullable List<String> languages) throws IOException {
        LCLPNetworkTranslationLoader loader = new LCLPNetworkTranslationLoader(applications, languages, logger);
        return translator.loadAsyncFrom(loader);
    }
}
