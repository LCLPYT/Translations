/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader.language;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.translations.model.LanguageCollection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class UrlLanguageLoaderTest {

    private static final Logger logger = LoggerFactory.getLogger("test");

    @Test
    void loadFromFileSystem() {
        test(this, "lang/");
    }

    @Test
    void loadFromFileSystemNoTrailingSlash() {
        test(this, "lang");
    }

    @Test
    void loadFromClassLoaderDirectoryUrl() throws IOException {
        URL url = Paths.get("src", "test", "resources").toAbsolutePath().toUri().toURL();

        String s = url.toString();

        if (s.endsWith(File.separator)) {
            url = new URL(s.substring(0, s.length() - 1));
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[] {url})) {
            test(classLoader, "lang/");
        }
    }

    @Test
    void loadFromJar() {
        URL url = getClass().getClassLoader().getResource("test.jar");
        assertNotNull(url);

        test(url, "jarlang/");
    }

    private static void test(Object ref, String dir) {
        List<String> resourceDirectories = Collections.singletonList(dir);

        URL[] urls = UrlLanguageLoader.getResourceLocations(ref);

        UrlLanguageLoader loader = new UrlLanguageLoader(urls, resourceDirectories, logger);

        LanguageCollection languages = loader.loadLanguages().join();
        Set<String> keys = StreamSupport.stream(languages.keys().spliterator(), false).collect(Collectors.toSet());

        Set<String> expectedKeys = new HashSet<>();
        expectedKeys.add("en_us");
        expectedKeys.add("de_de");

        assertEquals(expectedKeys, keys);

        assertTrue(keys.stream()
                .map(languages::get)
                .allMatch(language -> language != null && language.has("hello")));
    }
}