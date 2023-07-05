/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.network;

import com.google.gson.annotations.Expose;
import work.lclpnet.lclpnetwork.facade.JsonSerializable;

import java.util.List;

/**
 * Represents a language for a LCLPNetwork translation application.
 */
public class TranslationLanguage extends JsonSerializable {

    @Expose
    private long id;
    @Expose
    private String locale;
    @Expose
    private List<TranslationEntry> entries;

    public long getId() {
        return id;
    }

    public String getLocale() {
        return locale;
    }

    public List<TranslationEntry> getEntries() {
        return entries;
    }

}
