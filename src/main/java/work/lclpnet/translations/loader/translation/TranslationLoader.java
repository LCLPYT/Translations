/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader.translation;

import work.lclpnet.translations.model.LanguageCollection;

import java.util.concurrent.CompletableFuture;

public interface TranslationLoader {

    CompletableFuture<? extends LanguageCollection> load();
}
