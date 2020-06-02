package com.ezpass.smopaye_mobile.web_service_access;

import com.squareup.moshi.Json;

public class AccessToken {

    //me permet de mapper mes reponses JSON (Nous utiliserons MOSHI au lieu de GSON pour retrofit)

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


    //************************************RESPONSE BODY*******************************************
    /*@SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private MyData data;
    @SerializedName("message")
    @Expose
    private Message message;*/
    //************************************END*******************************************



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


    //************************************RESPONSE BODY GETTER AND SETTER*******************************************

    /*public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MyData getData() {
        return data;
    }

    public void setData(MyData data) {
        this.data = data;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }*/


    //************************************END*******************************************
}
