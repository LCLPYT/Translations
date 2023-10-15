/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.util;

import java.util.Map;

public interface Pair<K, V> {

    K getKey();

    V getValue();

    static <K, V> Pair<K, V> of(K key, V value) {
        return new ImmutablePair<>(key, value);
    }

    static <K, V> Pair<K, V> of(Map.Entry<K, V> entry) {
        return new ImmutablePair<>(entry.getKey(), entry.getValue());
    }
}
