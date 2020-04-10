package com.ezpass.smopaye_mobile.web_service;

import com.ezpass.smopaye_mobile.web_service_access.AccessToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("nom") String nom, @Field("prenom") String prenom, @Field("tel") String tel, @Field("cni") String cni, @Field("adresse") String adresse, @Field("numcarte") String numcarte);

   @POST("login")
   @FormUrlEncoded
    Call<AccessToken> login(@Field("telephone") String telephone, @Field("password") String password);

}
