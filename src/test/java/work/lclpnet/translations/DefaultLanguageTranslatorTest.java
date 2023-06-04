/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations;

import org.junit.jupiter.api.Test;
import work.lclpnet.translations.loader.translation.MultiTranslationLoader;
import work.lclpnet.translations.loader.translation.SPITranslationLoader;
import work.lclpnet.translations.loader.translation.TranslationLoader;
import work.lclpnet.translations.model.Language;
import work.lclpnet.translations.model.StaticLanguage;
import work.lclpnet.translations.model.StaticLanguageCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultLanguageTranslatorTest {

    @Test
    void testSimple() {
        final String testKey = "test.first";
        final String expectedEn = "This is a test", expectedGer = "Das ist ein Test";

        TranslationLoader translationLoader = () -> {
            Map<String, Language> languages = new HashMap<>();

            Map<String, String> en = new HashMap<>();
            en.put(testKey, expectedEn);
            en.put("test.other", "This too.");
            languages.put("en_us", new StaticLanguage(en));

            Map<String, String> ger = new HashMap<>();
            ger.put(testKey, expectedGer);
            ger.put("test.other", "Das auch");
            languages.put("de_de", new StaticLanguage(ger));

            return CompletableFuture.completedFuture(new StaticLanguageCollection(languages));
        };

        Translator translator = DefaultLanguageTranslator.create(translationLoader).join();

        assertEquals(expectedEn, translator.translate("en_us", testKey));
        assertEquals(expectedGer, translator.translate("de_de", testKey));
    }

    @Test
    void testSPI() {
        ClassLoader classLoader = DefaultLanguageTranslatorTest.class.getClassLoader();
        SPITranslationLoader translationLoader = new SPITranslationLoader(classLoader);

        Translator translator = DefaultLanguageTranslator.create(translationLoader).join();

        assertTrue(translator.hasTranslation("en_us", "hello"));
        assertTrue(translator.hasTranslation("de_de", "hello"));
    }

    @Test
    void mergeTest() {
        final String expectedTestFirst = "This is a test",
                expectedTestSecond = "This is the second test", expectedTestOther = "Overwritten.";

        TranslationLoader first = () -> {
            Map<String, Language> languages = new HashMap<>();

            Map<String, String> en = new HashMap<>();
            en.put("test.first", expectedTestFirst);
            en.put("test.other", "This too.");
            languages.put("en_us", new StaticLanguage(en));

            Map<String, String> ger = new HashMap<>();
            ger.put("test.first", "Das ist ein Test");
            ger.put("test.other", "Das auch");
            languages.put("de_de", new StaticLanguage(ger));

            return CompletableFuture.completedFuture(new StaticLanguageCollection(languages));
        };

        TranslationLoader second = () -> {
            Map<String, Language> languages = new HashMap<>();

            Map<String, String> en = new HashMap<>();
            en.put("test.second", expectedTestSecond);
            en.put("test.other", expectedTestOther);
            languages.put("en_us", new StaticLanguage(en));

            Map<String, String> ger = new HashMap<>();
            ger.put("test.second", "Das ist der zweite Test");
            ger.put("test.other", "Überschieben.");
            languages.put("de_de", new StaticLanguage(ger));

            return CompletableFuture.completedFuture(new StaticLanguageCollection(languages));
        };

        Translator translator = DefaultLanguageTranslator.create(new MultiTranslationLoader(first, second)).join();

        assertTrue(translator.hasTranslation("en_us", "test.first"));

        String testFirst = translator.translate("en_us", "test.first");
        assertEquals(expectedTestFirst, testFirst);

        String testSecond = translator.translate("en_us", "test.second");
        assertEquals(expectedTestSecond, testSecond);

        String testOther = translator.translate("en_us", "test.other");
        assertEquals(expectedTestOther, testOther);
    }
}