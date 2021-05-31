package com.ezpass.smopaye_mobile.web_service_access;

import com.squareup.moshi.Json;

/**
 * me permet de mapper mes reponses JSON (Nous utiliserons MOSHI au lieu de GSON pour retrofit)
 *
 * @see AccessToken
 * @author Bertin-dev
 * @powered by smopaye sarl
 * @version 1.4.0
 * @since 2019
 * @Copyright 2019-2021
 */
public class AccessToken {



    @Json(name = "token_type")
    private
    String tokenType;
    @Json(name = "expires_in")
    private
    int expiresIn;
    @Json(name = "access_token")
    private
    String accessToken;
    @Json(name = "refresh_token")
    private
    String refreshToken;

    public String getTokenType() {
        return tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
