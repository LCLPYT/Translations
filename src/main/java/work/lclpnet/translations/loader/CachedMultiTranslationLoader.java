/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader;

import work.lclpnet.translations.model.LanguageCollection;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * A translation loader that loads from multiple underlying translation loaders.
 * It is assumed that the underlying sources are static and thereby won't change.
 * The enables caching so that translations don't have to be loaded multiple times when reloading.
 */
public class CachedMultiTranslationLoader implements MultiSourceTranslationLoader {

    private final Set<TranslationLoader> loaders = new LinkedHashSet<>();  // <-- preserves insertion order
    private final Map<TranslationLoader, LanguageCollection> cache = new HashMap<>();

    @Override
    public Stream<CompletableFuture<? extends LanguageCollection>> loadFromSources() {
        return loaders.stream().map(loader -> {
            LanguageCollection cached = cache.get(loader);

            if (cached != null) {
                return CompletableFuture.completedFuture(cached);
            }

            return loader.load().thenApply(collection -> {
                cache.put(loader, collection);
                return collection;
            });
        });
    }

    public void add(TranslationLoader loader) {
        Objects.requireNonNull(loader, "Translation loader is null");

        loaders.add(loader);
    }

    public void remove(TranslationLoader loader) {
        loaders.remove(loader);
        cache.remove(loader);
    }
}
