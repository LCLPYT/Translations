/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.model;

import org.jetbrains.annotations.Nullable;
import work.lclpnet.translations.util.Pair;

import java.util.stream.Stream;

public interface Language {

    @Nullable
    String get(String key);

    Iterable<String> keys();

    boolean has(String key);

    Stream<Pair<String, String>> stream();
}
