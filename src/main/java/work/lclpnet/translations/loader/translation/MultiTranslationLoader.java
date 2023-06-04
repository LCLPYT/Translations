/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader.translation;

import work.lclpnet.translations.model.LanguageCollection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MultiTranslationLoader extends MultiSourceTranslationLoader {

    private final TranslationLoader[] loaders;

    public MultiTranslationLoader(TranslationLoader... loaders) {
        this.loaders = loaders;
    }

    protected void collectFutures(List<CompletableFuture<? extends LanguageCollection>> futures) {
        for (TranslationLoader provider : loaders) {
            CompletableFuture<? extends LanguageCollection> future = provider.load();
            futures.add(future);
        }
    }
}
