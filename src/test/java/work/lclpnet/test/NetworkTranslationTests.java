/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.translations.Translations;
import work.lclpnet.translations.network.LCLPNetworkTranslations;
import work.lclpnet.translations.network.LCLPTranslationAPI;
import work.lclpnet.translations.network.TranslationApplication;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NetworkTranslationTests {

    private static final Logger logger = LoggerFactory.getLogger("test");

    @Test
    void testFetch() {
        List<String> applications = Collections.singletonList("mc_server");
        List<TranslationApplication> apps = LCLPTranslationAPI.INSTANCE.getTranslations(applications, null).join();
        assertNotNull(apps);
    }

    @Test
    void testAddFetched() throws IOException {
        LCLPNetworkTranslations.loadApplications(logger, "mc_server").join();
        assertTrue(Translations.hasTranslation("en_us", "mc-link.requesting"));
    }
}
