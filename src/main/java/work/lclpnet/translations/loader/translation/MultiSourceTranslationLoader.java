/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader.translation;

import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.model.MutableLanguageCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class MultiSourceTranslationLoader implements TranslationLoader {

    @Override
    public final CompletableFuture<? extends LanguageCollection> load() {
        List<CompletableFuture<? extends LanguageCollection>> futures = new ArrayList<>();

        collectFutures(futures);

        CompletableFuture<?>[] array = futures.toArray(new CompletableFuture[0]);

        return CompletableFuture.allOf(array)
                .thenApply(ignored -> merge(futures.stream().map(CompletableFuture::join)));
    }

    protected abstract void collectFutures(List<CompletableFuture<? extends LanguageCollection>> futures);

    private MutableLanguageCollection merge(Stream<LanguageCollection> collections) {
        // reduction could be parallelized with collections.parallel()...

        return collections.reduce(new MutableLanguageCollection(), (partial, collection) -> {
            // add every collection from the stream to the partial collection

            partial.merge(collection);

            return partial;
        }, (partial, collection) -> {
            // combine mutable language collections

            partial.merge(collection);

            return partial;
        });
    }
}
