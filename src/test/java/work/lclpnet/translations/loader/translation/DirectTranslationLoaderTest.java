/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader.translation;

import org.junit.jupiter.api.Test;
import work.lclpnet.translations.loader.language.LanguageLoader;
import work.lclpnet.translations.model.Language;
import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.model.StaticLanguage;
import work.lclpnet.translations.model.StaticLanguageCollection;
import work.lclpnet.translations.util.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectTranslationLoaderTest {

    @Test
    void loadSingleSource_keys_matches() throws ExecutionException, InterruptedException {
        Map<String, Language> languages = new HashMap<>();

        languages.put("en_us", new StaticLanguage(new HashMap<>()));
        languages.put("de_de", new StaticLanguage(new HashMap<>()));

        DirectTranslationLoader loader = new DirectTranslationLoader(staticLoader(languages));

        LanguageCollection collection = loader.load().get();

        assertEquals(setOf("en_us", "de_de"), collection.stream()
                .map(Pair::getKey)
                .collect(Collectors.toSet()));
    }

    @Test
    void loadMultiSource_keys_merge() throws ExecutionException, InterruptedException {
        Map<String, Language> languages = new HashMap<>();

        languages.put("en_us", new StaticLanguage(new HashMap<>()));
        languages.put("de_de", new StaticLanguage(new HashMap<>()));

        Map<String, Language> other = new HashMap<>();

        other.put("en_us", new StaticLanguage(new HashMap<>()));
        other.put("ja_jp", new StaticLanguage(new HashMap<>()));

        DirectTranslationLoader loader = new DirectTranslationLoader(staticLoader(languages), staticLoader(other));

        LanguageCollection collection = loader.load().get();

        assertEquals(setOf("en_us", "de_de", "ja_jp"), collection.stream()
                .map(Pair::getKey)
                .collect(Collectors.toSet()));
    }

    @Test
    void loadSingleSource_entries_matches() throws ExecutionException, InterruptedException {
        Map<String, Language> languages = new HashMap<>();

        HashMap<String, String> en = new HashMap<>();
        en.put("hello", "Hello");
        languages.put("en_us", new StaticLanguage(en));

        HashMap<String, String> de = new HashMap<>();
        de.put("hello", "Hallo");
        languages.put("de_de", new StaticLanguage(de));

        DirectTranslationLoader loader = new DirectTranslationLoader(staticLoader(languages));

        LanguageCollection collection = loader.load().get();

        assertEquals(setOf("Hello"), Objects.requireNonNull(collection.get("en_us")).stream()
                .map(Pair::getValue)
                .collect(Collectors.toSet()));

        assertEquals(setOf("Hallo"), Objects.requireNonNull(collection.get("de_de")).stream()
                .map(Pair::getValue)
                .collect(Collectors.toSet()));
    }

    @Test
    void loadMultiSource_entries_merge() throws ExecutionException, InterruptedException {
        Map<String, Language> languages = new HashMap<>();

        HashMap<String, String> en1 = new HashMap<>();
        en1.put("hello", "Hello");
        languages.put("en_us", new StaticLanguage(en1));

        HashMap<String, String> de = new HashMap<>();
        de.put("hello", "Hallo");
        languages.put("de_de", new StaticLanguage(de));

        Map<String, Language> other = new HashMap<>();

        Map<String, String> en2 = new HashMap<>();
        en2.put("world", "World");
        other.put("en_us", new StaticLanguage(en2));

        Map<String, String> jp = new HashMap<>();
        jp.put("hello", "こんにちは");
        other.put("ja_jp", new StaticLanguage(jp));

        DirectTranslationLoader loader = new DirectTranslationLoader(staticLoader(languages), staticLoader(other));

        LanguageCollection collection = loader.load().get();

        assertEquals(setOf("Hello", "World"), Objects.requireNonNull(collection.get("en_us")).stream()
                .map(Pair::getValue)
                .collect(Collectors.toSet()));

        assertEquals(setOf("Hallo"), Objects.requireNonNull(collection.get("de_de")).stream()
                .map(Pair::getValue)
                .collect(Collectors.toSet()));

        assertEquals(setOf("こんにちは"), Objects.requireNonNull(collection.get("ja_jp")).stream()
                .map(Pair::getValue)
                .collect(Collectors.toSet()));
    }

    private static LanguageLoader staticLoader(Map<String, ? extends Language> languages) {
        return () -> CompletableFuture.completedFuture(new StaticLanguageCollection(languages));
    }

    @SafeVarargs
    private static <T> Set<T> setOf(T... items) {
        return new HashSet<>(Arrays.asList(items));
    }
}