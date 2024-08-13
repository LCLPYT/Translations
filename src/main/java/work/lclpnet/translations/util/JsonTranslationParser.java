/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.model.MutableLanguage;
import work.lclpnet.translations.model.StaticLanguageCollection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonTranslationParser implements TranslationParser {

    private final Gson gson = new Gson();
    private final Map<String, MutableLanguage> languages = Collections.synchronizedMap(new HashMap<>());
    private final Logger logger;

    public JsonTranslationParser(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void parse(InputStream input, String language) throws IOException, JsonSyntaxException {
        String json = IOUtil.readString(input, StandardCharsets.UTF_8);

        parse(json, language);
    }

    public void parse(String json, String languageName) throws JsonSyntaxException {
        JsonObject obj = gson.fromJson(json, JsonObject.class);

        MutableLanguage language = languages.computeIfAbsent(languageName, key -> new MutableLanguage());

        loadJson(obj, language);
    }

    private void loadJson(JsonObject json, MutableLanguage language) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonElement value = entry.getValue();
            String key = entry.getKey();

            String val;
            try {
                val = value.getAsString();
            } catch (ClassCastException e) {
                logger.warn(String.format("Unexpected json value type '%s' of key '%s'.", value.getClass().getName(), key));
                continue;
            }

            language.add(key, val);
        }
    }

    @Override
    public LanguageCollection build() {
        return new StaticLanguageCollection(languages);
    }
}
