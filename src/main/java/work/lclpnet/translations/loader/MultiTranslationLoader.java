/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader;

import work.lclpnet.translations.model.LanguageCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class MultiTranslationLoader implements MultiSourceTranslationLoader {

    private final List<TranslationLoader> loaders = new ArrayList<>(2);

    public void addLoader(TranslationLoader loader) {
        if (loader == null) {
            throw new IllegalArgumentException("Loader is null");
        }

        loaders.add(loader);
    }

    public void removeLoader(TranslationLoader loader) {
        loaders.remove(loader);
    }

    @Override
    public Stream<CompletableFuture<? extends LanguageCollection>> loadFromSources() {
        return loaders.stream().map(TranslationLoader::load);
    }
}
