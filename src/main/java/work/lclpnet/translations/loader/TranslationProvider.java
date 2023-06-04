/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader;

import work.lclpnet.translations.loader.language.LanguageLoader;

public interface TranslationProvider {

    LanguageLoader create();
}
