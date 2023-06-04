/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.network;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LCLPTranslationAPITest {

    @Test
    void testFetch() {
        List<String> applications = Collections.singletonList("mc_server");
        List<TranslationApplication> apps = LCLPTranslationAPI.INSTANCE.getTranslations(applications, null).join();
        assertNotNull(apps);

        TranslationApplication mc_server = apps.stream()
                .filter(app -> "mc_server".equals(app.getName()))
                .findAny()
                .orElseThrow(NoSuchElementException::new);

        TranslationLanguage en_us = mc_server.getLanguages().stream()
                .filter(lang -> "en_us".equals(lang.getLocale()))
                .findAny()
                .orElseThrow(NoSuchElementException::new);

        assertTrue(en_us.getEntries().stream().anyMatch(entry -> "mc-link.requesting".equals(entry.getKey())));
    }
}