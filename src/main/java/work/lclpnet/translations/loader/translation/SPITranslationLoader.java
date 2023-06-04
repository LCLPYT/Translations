/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader.translation;

import work.lclpnet.translations.loader.TranslationProvider;
import work.lclpnet.translations.loader.language.LanguageLoader;
import work.lclpnet.translations.model.LanguageCollection;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;

public class SPITranslationLoader extends MultiSourceTranslationLoader {

    private final ServiceLoader<TranslationProvider> serviceLoader;

    public SPITranslationLoader(ClassLoader classLoader) {
        serviceLoader = ServiceLoader.load(TranslationProvider.class, classLoader);
    }

    @Override
    protected void collectFutures(List<CompletableFuture<? extends LanguageCollection>> futures) {
        for (TranslationProvider provider : serviceLoader) {
            LanguageLoader manager = provider.create();

            CompletableFuture<? extends LanguageCollection> future = manager.loadLanguages();
            futures.add(future);
        }
    }
}
