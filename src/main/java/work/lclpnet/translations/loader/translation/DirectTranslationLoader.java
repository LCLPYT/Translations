/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader.translation;

import work.lclpnet.translations.loader.language.LanguageLoader;
import work.lclpnet.translations.model.LanguageCollection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link TranslationLoader} that loads translations directly from given {@link LanguageLoader}s.
 */
public class DirectTranslationLoader extends MultiSourceTranslationLoader {

    private final LanguageLoader[] languageLoaders;

    public DirectTranslationLoader(LanguageLoader loader, LanguageLoader... additionalLoaders) {
        this.languageLoaders = new LanguageLoader[1 + additionalLoaders.length];
        this.languageLoaders[0] = loader;

        System.arraycopy(additionalLoaders, 0, languageLoaders, 1, additionalLoaders.length);
    }

    @Override
    protected void collectFutures(List<CompletableFuture<? extends LanguageCollection>> futures) {
        for (LanguageLoader loader : languageLoaders) {
            futures.add(loader.loadLanguages());
        }
    }
}
