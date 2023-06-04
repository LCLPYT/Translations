/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations;

import work.lclpnet.translations.loader.translation.TranslationLoader;
import work.lclpnet.translations.model.Language;
import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.model.StaticLanguageCollection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DefaultLanguageTranslator implements Translator {

    private final TranslationLoader translationLoader;
    private final String defaultLanguage;
    private LanguageCollection languages = new StaticLanguageCollection(Collections.emptyMap());
    private final Map<String, SimpleDateFormat> dateFormats = new HashMap<>();

    public DefaultLanguageTranslator(TranslationLoader translationLoader) {
        this(translationLoader, "en_us");
    }

    public DefaultLanguageTranslator(TranslationLoader translationLoader, String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        this.translationLoader = translationLoader;
    }

    @Override
    public @Nonnull String translate(String locale, String key, Object... substitutes) {
        Language language;

        synchronized (this) {
            language = languages.get(locale);
        }

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

    @Override
    public boolean hasTranslation(String locale, String key) {
        final Language language;

        synchronized (this) {
            language = languages.get(locale);
        }

        if (language == null) return false;

        return language.has(key);
    }

    @Override
    @Nonnull
    public SimpleDateFormat getDateFormat(String locale) {
        synchronized (this) {
            if (dateFormats.containsKey(locale)) {
                return dateFormats.get(locale);
            }
        }

        if (!hasTranslation(locale, "date.format")) {
            locale = defaultLanguage;

            synchronized (this) {
                if (dateFormats.containsKey(defaultLanguage)) {
                    return dateFormats.get(defaultLanguage);
                }
            }

            if (!hasTranslation(defaultLanguage, "date.format")) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

                synchronized (this) {
                    dateFormats.put(defaultLanguage, format);
                }

                return format;
            }
        }

        SimpleDateFormat format = new SimpleDateFormat(translate(locale, "date.format"));

        synchronized (this) {
            dateFormats.put(locale, format);
        }

        return format;
    }

    @Nullable
    private Language getLanguage(String key) {
        synchronized (this) {
            return languages.get(key);
        }
    }

    @Nullable
    private Language getDefaultLanguage() {
        return getLanguage(defaultLanguage);
    }

    public CompletableFuture<Void> reload() {
        return translationLoader.load().thenAccept(this::setLanguages);
    }

    private void setLanguages(LanguageCollection languages) {
        if (languages == null) throw new IllegalArgumentException("Languages might not me null");

        synchronized (this) {
            this.languages = languages;
            this.dateFormats.clear();  // reset cached dateFormats
        }
    }

    /**
     * Creates a new {@link DefaultLanguageTranslator} and loads translations from the supplied loader.
     * @param loader The translation source.
     * @return A new and loaded translator instance.
     */
    public static CompletableFuture<DefaultLanguageTranslator> create(TranslationLoader loader) {
        DefaultLanguageTranslator translator = new DefaultLanguageTranslator(loader);

        return translator.reload().thenApply(nil -> translator);
    }
}
