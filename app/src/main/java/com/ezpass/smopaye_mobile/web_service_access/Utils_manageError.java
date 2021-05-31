package com.ezpass.smopaye_mobile.web_service_access;

import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Me permet de convertire toutes les erreurs
 *
 * @see Utils_manageError
 */
public class Utils_manageError {

    /**
     * recuperer toutes les réponses en cas d'erreurs et appel l'objet chargé de les formater
     * @param response
     * @exception e
     * @return un objet de type ApiError
     */
    public static ApiError convertErrors(ResponseBody response){
        Converter<ResponseBody, ApiError> converter = RetrofitBuilder.getRetrofit().responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError apiError = null;

        try {
            apiError = converter.convert(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiError;
    }
}
