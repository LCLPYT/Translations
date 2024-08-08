/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.model;

import org.jetbrains.annotations.Nullable;
import work.lclpnet.translations.util.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MutableLanguageCollection implements LanguageCollection {

    private final Map<String, MutableLanguage> languages = Collections.synchronizedMap(new HashMap<>());

    @Nullable
    @Override
    public Language get(String key) {
        return languages.get(key);
    }

    @Override
    public Iterable<String> keys() {
        return languages.keySet();
    }

    public void merge(LanguageCollection other) {
        if (this == other) return;

        for (String key : other.keys()) {
            Language otherLanguage = other.get(key);
            if (otherLanguage == null) continue;

            MutableLanguage language = languages.computeIfAbsent(key, ignored -> new MutableLanguage());
            language.addAll(otherLanguage);
        }
    }

    @Override
    public Stream<Pair<String, ? extends Language>> stream() {
        return languages.entrySet().stream().map(Pair::of);
    }

    public static MutableLanguageCollection merge(Stream<? extends LanguageCollection> collections) {
        // reduction could be parallelized with collections.parallel()...

        return collections.reduce(new MutableLanguageCollection(), (partial, collection) -> {
            // add every collection from the stream to the partial collection

            partial.merge(collection);

            return partial;
        }, (partial, collection) -> {
            // combine mutable language collections

            partial.merge(collection);

            return partial;
        });
    }
}
