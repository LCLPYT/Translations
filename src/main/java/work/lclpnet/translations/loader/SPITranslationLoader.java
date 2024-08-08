/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader;

import work.lclpnet.translations.model.LanguageCollection;

import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SPITranslationLoader implements MultiSourceTranslationLoader {

    private final ServiceLoader<TranslationProvider> serviceLoader;

    public SPITranslationLoader(ClassLoader classLoader) {
        serviceLoader = ServiceLoader.load(TranslationProvider.class, classLoader);
    }

    @Override
    public Stream<CompletableFuture<? extends LanguageCollection>> loadFromSources() {
        return StreamSupport.stream(serviceLoader.spliterator(), false)
                .map(TranslationProvider::create)
                .map(TranslationLoader::load);
    }
}
