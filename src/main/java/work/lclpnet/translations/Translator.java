/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations;

import work.lclpnet.translations.io.IAsyncTranslationLoader;
import work.lclpnet.translations.io.ITranslationLoader;
import work.lclpnet.translations.model.Language;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Translator {

    private final String defaultLanguage;
    private final Map<String, Language> languages = new HashMap<>();
    private final Map<String, SimpleDateFormat> dateFormats = new HashMap<>();

    public Translator() {
        this("en_us");
    }

    public Translator(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Loads translation files from a given loader and adds all translations to the {@link Translator} translation list.
     * If there are duplicate keys, the one which was loaded last will be used.
     *
     * @param loader The loader used to load translation files.
     * @throws IOException If there was an I/O error.
     */
    public void loadFrom(ITranslationLoader loader) throws IOException {
        Map<String, Map<String, String>> loaded = loader.load();
        if (loaded != null) addLoaded(loaded);
    }

    public CompletableFuture<Void> loadAsyncFrom(IAsyncTranslationLoader loader) throws IOException {
        return loader.load().thenAccept(loaded -> {
            if (loaded != null) addLoaded(loaded);
        });
    }

    private void addLoaded(Map<String, Map<String, String>> locales) {
        locales.forEach((locale, translations) -> {
            Language alreadyLoaded = languages.computeIfAbsent(locale, key -> new Language());
            alreadyLoaded.addAll(translations);
        });
    }

    public String translate(String locale, String key, Object... substitutes) {
        Language language = languages.get(locale);
        if (language == null) {
            language = getDefaultLanguage();
            if (language == null) return key;
        }

        String translation = language.get(key);
        if (translation == null) {
            language = getDefaultLanguage();
            if (language == null) return key;

            translation = language.get(key);
            if (translation == null) return key;
        }

        return String.format(translation, substitutes);
    }

    @Nullable
    private Language getDefaultLanguage() {
        return languages.get(defaultLanguage);
    }

    /**
     * Check if there is a specific translation for a given language.
     *
     * @param locale The locale to check.
     * @param key    The translation key.
     * @return True, if there is a translation for the given language.
     */
    public boolean hasTranslation(String locale, String key) {
        Language language = languages.get(locale);
        if (language == null) return false;

        return language.has(key);
    }

    @Nonnull
    public SimpleDateFormat getDateFormat(String locale) {
        if (dateFormats.containsKey(locale)) {
            return dateFormats.get(locale);
        }

        if (!hasTranslation(locale, "date.format")) {
            locale = defaultLanguage;
            if (dateFormats.containsKey(defaultLanguage)) return dateFormats.get(defaultLanguage);

            if (!hasTranslation(defaultLanguage, "date.format")) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                dateFormats.put(defaultLanguage, format);
                return format;
            }
        }

        SimpleDateFormat format = new SimpleDateFormat(translate(locale, "date.format"));
        dateFormats.put(locale, format);

        return format;
    }

    /**
     * Gets the main translator.
     * @return The main translator.
     */
    public static Translator getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final Translator instance = new Translator();
    }
}
