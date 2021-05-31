package com.ezpass.smopaye_mobile.web_service_access;

import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Me permet de convertire toutes les erreurs lors de l'enregistrement
 *
 * @see Utils_manageErrorRegister
 */
public class Utils_manageErrorRegister {

    /**
     * recuperer toutes les réponses en cas d'erreurs lors de l'enregistrement et appel l'objet chargé de les formater
     * @param response
     * @exception e
     * @return un objet de type ApiErrorRegister
     */
    public static ApiErrorRegister convertErrors(ResponseBody response){
        Converter<ResponseBody, ApiErrorRegister> converter = RetrofitBuilder.getRetrofit().responseBodyConverter(ApiErrorRegister.class, new Annotation[0]);

        ApiErrorRegister apiErrorRegister = null;

        try {
            apiErrorRegister = converter.convert(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiErrorRegister;
    }
}
