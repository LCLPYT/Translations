/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader.language;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.model.MutableLanguage;
import work.lclpnet.translations.model.StaticLanguageCollection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonLanguageCollectionBuilder {

    private final Gson gson = new Gson();
    private final Map<String, MutableLanguage> languages = Collections.synchronizedMap(new HashMap<>());
    private final Logger logger;

    public JsonLanguageCollectionBuilder(Logger logger) {
        this.logger = logger;
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

    public LanguageCollection build() {
        return new StaticLanguageCollection(languages);
    }
}
