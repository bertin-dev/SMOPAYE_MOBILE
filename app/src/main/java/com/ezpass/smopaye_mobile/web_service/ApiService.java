package com.ezpass.smopaye_mobile.web_service;

import com.ezpass.smopaye_mobile.web_service_access.AccessToken;
import com.ezpass.smopaye_mobile.web_service_access.ResponseUser;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("nom") String nom,
                               @Field("prenom") String prenom,
                               @Field("tel") String tel,
                               @Field("cni") String cni,
                               @Field("adresse") String adresse,
                               @Field("numcarte") String numcarte);

   @POST("login")
   @FormUrlEncoded
    Call<AccessToken> login(@Field("telephone") String telephone,
                            @Field("password") String password);


    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);


    @GET("response_user")
    Call<ResponseUser> response_user();


    @POST("reset_password")
    @FormUrlEncoded
    Call<AccessToken>reset_password(@Field("telephone") String telephone, @Field("piece_justificative") String pj);



}
