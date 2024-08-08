/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader;

import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.model.MutableLanguageCollection;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MultiSourceTranslationLoader extends  TranslationLoader {

    /**
     * Supplies futures of language collections to be loaded in parallel.
     * All results are then merged into a resulting {@link LanguageCollection}.
     * @return A stream pipeline of all the futures of language collections.
     */
    Stream<CompletableFuture<? extends LanguageCollection>> loadFromSources();

    @Override
    default CompletableFuture<? extends LanguageCollection> load() {
        // load supplied futures in parallel, then merge all results
        return CompletableFuture.supplyAsync(() -> MutableLanguageCollection.merge(loadFromSources()
                .collect(Collectors.toList())  // intermediary toList() to dispatch every future at once before join()
                .stream()
                .map(CompletableFuture::join)));  // now join each future
    }
}
