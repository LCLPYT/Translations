/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.network;

import com.google.gson.annotations.Expose;
import work.lclpnet.lclpnetwork.facade.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a LCLPNetwork translation application.
 */
public class TranslationApplication extends JsonSerializable {

    @Expose
    private long id;
    @Expose
    private String name;
    @Expose
    private List<TranslationLanguage> languages;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<TranslationLanguage> getLanguages() {
        return languages;
    }

    public static class Collection extends ArrayList<TranslationApplication> {
        
    }

}
