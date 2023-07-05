/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.network;

import com.google.gson.annotations.Expose;
import work.lclpnet.lclpnetwork.facade.JsonSerializable;

public class TranslationEntry extends JsonSerializable {

    @Expose
    private String key;
    @Expose
    private String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
