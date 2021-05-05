/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.io;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import work.lclpnet.translations.util.ILogger;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class loads translations located by a {@link ITranslationLocator} using a {@link ResourceLoader}.
 *
 * @author LCLP
 */
public class ResourceTranslationLoader implements ITranslationLoader {

    private ITranslationLocator translationLocator;
    public final ResourceLoader resourceLoader;
    public final ILogger logger;

    /**
     * @param translationLocator A translation file locator.
     * @param resourceLoader A resource loader which will receive the resource names located by the {@link ITranslationLocator}
     * @param logger An optional logger to receive feedback.
     */
    public ResourceTranslationLoader(ITranslationLocator translationLocator, ResourceLoader resourceLoader, @Nullable ILogger logger) {
        this.translationLocator = Objects.requireNonNull(translationLocator);
        this.resourceLoader = Objects.requireNonNull(resourceLoader);
        this.logger = logger == null ? ILogger.SILENT : logger;
    }

    /**
     * Set the {@link ITranslationLocator} that is used to locate the translation resources.
     *
     * @param translationLocator A custom locator.
     */
    public void setTranslationLocator(ITranslationLocator translationLocator) {
        this.translationLocator = translationLocator;
    }

    @Nullable
    @Override
    public Map<String, Map<String, String>> load() throws IOException {
        logger.info("Locating translation files...");

        List<String> translationFiles = translationLocator.locate();

        logger.info(String.format("Located %s translation files.", translationFiles.size()));
        logger.info("Loading translations...");

        Map<String, Map<String, String>> languages = new HashMap<>();

        final Gson gson = new Gson();
        for (String file : translationFiles) {
            JsonObject translationObj;

            logger.info(String.format("Trying to load language file '%s'...", file));

            try (InputStream in = resourceLoader.openResource(file)) {
                if(in == null) throw new FileNotFoundException(String.format("Resource '%s' could not be found.", file));

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    translationObj = gson.fromJson(reader, JsonObject.class);
                } catch (JsonSyntaxException e) {
                    logger.error(String.format("Failed to load language file '%s'.", file));
                    throw e;
                }
            }

            String[] parts = file.split("/");
            String fileName = parts[parts.length - 1];
            String language = fileName.substring(0, fileName.length() - 5); // remove .json ending

            Map<String, String> translations = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : translationObj.entrySet()) {
                JsonElement value = entry.getValue();
                String key = entry.getKey();
                try {
                    String val = value.getAsString();
                    translations.put(key, val);
                } catch (ClassCastException e) {
                    logger.warn(String.format("Unexpected value type '%s' of key '%s' in '%s'.", value.getClass().getName(), key, fileName));
                }
            }

            Map<String, String> alreadyPut = languages.get(language);
            if(alreadyPut == null) languages.put(language, translations);
            else alreadyPut.putAll(translations);
        }

        int entries = 0;
        for(Map<String, String> langTranslations : languages.values())
            entries += langTranslations.size();

        logger.info(String.format("Loaded %s locales with a total of %s translation entries.", languages.size(), entries));
        return languages;
    }

}
