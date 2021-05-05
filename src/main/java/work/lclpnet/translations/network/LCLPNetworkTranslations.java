/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.network;

import work.lclpnet.translations.Translations;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class to load LCLPNetwork translations to {@link Translations}.
 */
public class LCLPNetworkTranslations {

    /**
     * Fetch LCLPNetwork translations and load them to {@link Translations}.
     *
     * @param applications An array of {@link TranslationApplication} names to be fetched.
     * @return A completable future that will be notified when the operation is done.
     * @throws IOException If there was an I/O-error of any kind.
     */
    public static CompletableFuture<Void> loadApplications(String... applications) throws IOException {
        return loadApplications(Arrays.asList(applications), null);
    }

    /**
     * Fetch LCLPNetwork translations and load them to {@link Translations}.
     *
     * @param applications A list of {@link TranslationApplication} names to be fetched.
     * @param languages An optional list of languages to be fetched. If null, every language will be fetched.
     * @return A completable future that will be notified when the operation is done.
     * @throws IOException If there was an I/O-error of any kind.
     */
    public static CompletableFuture<Void> loadApplications(List<String> applications, @Nullable List<String> languages) throws IOException {
        LCLPNetworkTranslationLoader loader = new LCLPNetworkTranslationLoader(applications, languages);
        return Translations.loadAsyncFrom(loader);
    }

}
