/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.io;

import java.io.InputStream;

/**
 * Used to get {@link InputStream}s of resources.
 */
@FunctionalInterface
public interface ResourceLoader {

    /**
     * Opens a new {@link InputStream} of a resource.
     *
     * @param resource The resource (e.g. file path or something, depending on implementation)
     * @return An {@link InputStream} of the resource.
     */
    InputStream openResource(String resource);

}
