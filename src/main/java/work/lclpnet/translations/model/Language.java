/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.model;

import work.lclpnet.translations.util.Pair;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public interface Language {

    @Nullable
    String get(String key);

    Iterable<String> keys();

    boolean has(String key);

    Stream<Pair<String, String>> stream();
}
