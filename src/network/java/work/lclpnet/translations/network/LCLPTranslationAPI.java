/*
 * Copyright (c) 2024 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.network;

import com.sun.istack.internal.Nullable;
import work.lclpnet.lclpnetwork.LCLPNetworkAPI;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.util.JsonBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static work.lclpnet.lclpnetwork.util.JsonBuilder.object;

public class LCLPTranslationAPI extends LCLPNetworkAPI {

    /**
     * The main instance for the public LCLPNetwork API.
     */
    public static final LCLPTranslationAPI INSTANCE = new LCLPTranslationAPI(APIAccess.PUBLIC);

    /**
     * Construct a new LCLPTranslationAPI object.
     *
     * @param access The API accessor to use.
     */
    public LCLPTranslationAPI(APIAccess access) {
        super(access);
    }

    /**
     * Fetches translations of given translation applications.
     *
     * @param applications A list of translation applications.
     * @param languages An optional list of translation languages. If null, every language will be fetched.
     * @return A completable future that will contain the Translations.
     */
    public CompletableFuture<List<TranslationApplication>> getTranslations(List<String> applications, @Nullable List<String> languages) {
        JsonBuilder builder = object().beginArray("applications").addAll(applications).endArray();
        if(languages != null)
            builder = builder.beginArray("languages").addAll(languages).endArray();

        return api.post("api/translations/get", builder.createObject()).thenApply(resp -> {
            if(resp.getResponseCode() != 200) return null;
            else return resp.getResponseAs(TranslationApplication.Collection.class);
        });
    }

}
