/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.io;

import java.io.IOException;
import java.util.List;

/**
 * Used to locate translation files.
 */
public interface ITranslationLocator {

    /**
     * Locate translation files from somewhere.
     *
     * @return A list of translation file names.
     * @throws IOException If there was an I/O error locating the files.
     */
    List<String> locate() throws IOException;

}
