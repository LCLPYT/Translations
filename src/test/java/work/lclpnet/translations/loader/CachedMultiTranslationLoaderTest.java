/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader;

import org.junit.jupiter.api.Test;
import work.lclpnet.translations.model.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CachedMultiTranslationLoaderTest {

    @Test
    void load_cached_notLoadedAgain() {
        Map<String, String> en = new HashMap<>();
        en.put("hello", "Hello");

        Map<String, String> de = new HashMap<>();
        en.put("hello", "Hallo");

        Map<String, Language> m1 = new HashMap<>();
        m1.put("en_us", new StaticLanguage(en));

        Map<String, Language> m2 = new HashMap<>();
        m2.put("de_de", new StaticLanguage(de));

        StaticLanguageCollection c1 = new StaticLanguageCollection(m1);
        StaticLanguageCollection c2 = new StaticLanguageCollection(m2);

        TranslationLoader first = spy(new TestLoader(c1));
        TranslationLoader second = spy(new TestLoader(c2));

        // load first initially
        CachedMultiTranslationLoader cachedLoader = spy(new CachedMultiTranslationLoader());
        cachedLoader.add(first);

        assertLanguageCollectionsEqual(c1, cachedLoader.load().join());

        // now add second
        cachedLoader.add(second);

        MutableLanguageCollection merged = MutableLanguageCollection.merge(Stream.of(c1, c2));

        // load again two times
        assertLanguageCollectionsEqual(merged, cachedLoader.load().join());
        assertLanguageCollectionsEqual(merged, cachedLoader.load().join());

        // both loaders should only have been called once
        verify(first, times(1)).load();
        verify(second, times(1)).load();
        verify(cachedLoader, times(3)).load();
    }

    @Test
    void remove_load_translationsGone() {
        Map<String, String> en = new HashMap<>();
        en.put("hello", "Hello");

        Map<String, String> de = new HashMap<>();
        en.put("hello", "Hallo");

        Map<String, Language> m1 = new HashMap<>();
        m1.put("en_us", new StaticLanguage(en));

        Map<String, Language> m2 = new HashMap<>();
        m2.put("de_de", new StaticLanguage(de));

        StaticLanguageCollection c1 = new StaticLanguageCollection(m1);
        StaticLanguageCollection c2 = new StaticLanguageCollection(m2);

        TranslationLoader first = spy(new TestLoader(c1));
        TranslationLoader second = spy(new TestLoader(c2));

        // load first initially
        CachedMultiTranslationLoader cachedLoader = spy(new CachedMultiTranslationLoader());
        cachedLoader.add(first);

        assertLanguageCollectionsEqual(c1, cachedLoader.load().join());

        // now add second
        cachedLoader.add(second);

        MutableLanguageCollection merged = MutableLanguageCollection.merge(Stream.of(c1, c2));
        assertLanguageCollectionsEqual(merged, cachedLoader.load().join());

        // remove first
        cachedLoader.remove(first);

        assertLanguageCollectionsEqual(c2, cachedLoader.load().join());

        // both loaders should only have been called once
        verify(first, times(1)).load();
        verify(second, times(1)).load();
    }

    @Test
    void load_reinserted_loadedAgain() {
        Map<String, String> en = new HashMap<>();
        en.put("hello", "Hello");

        Map<String, String> de = new HashMap<>();
        en.put("hello", "Hallo");

        Map<String, Language> m1 = new HashMap<>();
        m1.put("en_us", new StaticLanguage(en));

        Map<String, Language> m2 = new HashMap<>();
        m2.put("de_de", new StaticLanguage(de));

        StaticLanguageCollection c1 = new StaticLanguageCollection(m1);
        StaticLanguageCollection c2 = new StaticLanguageCollection(m2);

        TranslationLoader first = spy(new TestLoader(c1));
        TranslationLoader second = spy(new TestLoader(c2));

        // load first initially
        CachedMultiTranslationLoader cachedLoader = spy(new CachedMultiTranslationLoader());
        cachedLoader.add(first);
        cachedLoader.add(second);

        MutableLanguageCollection merged = MutableLanguageCollection.merge(Stream.of(c1, c2));
        assertLanguageCollectionsEqual(merged, cachedLoader.load().join());

        // now add second
        cachedLoader.remove(second);
        cachedLoader.add(second);

        cachedLoader.load().join();

        assertLanguageCollectionsEqual(merged, cachedLoader.load().join());

        // second loader should be loaded again
        verify(first, times(1)).load();
        verify(second, times(2)).load();
    }

    static void assertLanguageCollectionsEqual(LanguageCollection expected, LanguageCollection actual) {
        Set<String> expectedKeys = new HashSet<>();
        expected.keys().forEach(expectedKeys::add);

        Set<String> actualKeys = new HashSet<>();
        actual.keys().forEach(actualKeys::add);

        assertEquals(expectedKeys, actualKeys);

        for (String key : expectedKeys) {
            Language expectedLanguage = expected.get(key);
            Language actualLanguage = actual.get(key);

            assertNotNull(expectedLanguage);
            assertNotNull(actualLanguage);

            assertLanguagesEqual(expectedLanguage, actualLanguage);
        }
    }

    static void assertLanguagesEqual(Language expected, Language actual) {
        Set<String> expectedKeys = new HashSet<>();
        expected.keys().forEach(expectedKeys::add);

        Set<String> actualKeys = new HashSet<>();
        actual.keys().forEach(actualKeys::add);

        assertEquals(expectedKeys, actualKeys);

        for (String key : expectedKeys) {
            String expectedTranslation = expected.get(key);
            String actualTranslation = actual.get(key);

            assertNotNull(expectedTranslation);
            assertNotNull(actualTranslation);

            assertEquals(expectedTranslation, actualTranslation);
        }
    }

    static class TestLoader implements TranslationLoader {

        final LanguageCollection collection;

        TestLoader(LanguageCollection collection) {
            this.collection = collection;
        }

        @Override
        public CompletableFuture<? extends LanguageCollection> load() {
            return CompletableFuture.completedFuture(collection);
        }
    }
}