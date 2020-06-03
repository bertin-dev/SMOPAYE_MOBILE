package com.ezpass.smopaye_mobile.web_service;

import com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB.Response_Status;
import com.ezpass.smopaye_mobile.Profil_user.Card;
import com.ezpass.smopaye_mobile.Profil_user.Categorie;
import com.ezpass.smopaye_mobile.Profil_user.DataUser;
import com.ezpass.smopaye_mobile.web_service_access.AccessToken;
import com.ezpass.smopaye_mobile.web_service_response.AllMyResponse;
import com.ezpass.smopaye_mobile.web_service_response.HomeResponse;
import com.ezpass.smopaye_mobile.web_service_response.Recharge.RechargeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    /*----------------------------------------------------------SMOPAYE MOBILE-------------------------------------------------------*/
    /* Register */
    @POST("api/auth/register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("lastname") String lastname,
                               @Field("firstname") String firstname,
                               @Field("gender") String gender,
                               @Field("phone") String phone,
                               @Field("address") String address,
                               @Field("category_id") String category_id,
                               @Field("role_id") String role_id,
                               @Field("cni") String cni,
                               @Field("card_number") String card_number);


    /* Update Profil */
    @PUT("api/user/{id}")
    @FormUrlEncoded
    Call<AllMyResponse> update_profil(@Field("lastname") String lastname,
                               @Field("firstname") String firstname,
                               @Field("gender") String gender,
                               @Field("phone") String phone,
                               @Field("address") String address,
                               @Field("category_id") String category_id,
                               @Field("role_id") String role_id,
                               @Field("cni") String cni,
                               @Field("card_number") String card_number,
                               @Path("id") String id);

    /*auto register */
    @POST("api/auth/autoregister")
    @FormUrlEncoded
    Call<AllMyResponse> autoregister(@Field("firstname") String firstname,
                                   @Field("lastname") String lastname,
                                   @Field("gender") String gender,
                                   @Field("address") String address,
                                   @Field("category_id") String category_id,
                                   @Field("created_by") String created_by,
                                   @Field("role_id") String role_id,
                                   @Field("cni") String cni,
                                   @Field("phone") String phone,
                                   @Field("nom_img_recto") String nom_img_recto,
                                   @Field("nom_img_verso") String nom_img_verso);

    /* Login */
    @POST("api/auth/login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("phone") String phone,
                            @Field("password") String password,
                            @Field("secret") String secret);


    /* Update Account */
    /*@PUT("api/update/{phone_number}")
    @FormUrlEncoded
    Call<AllMyResponse> update_account(@Path("phone_number") int phone_number,
                                     @Field("password") String password,
                                     @Field("ancien_password") String ancien_password);*/
    @POST("api/resetPassword")
    @FormUrlEncoded
    Call<AllMyResponse> update_account(@Field("oldPassword") String oldPassword,
                                     @Field("newPassword") String newPassword);


    /* Refresh Token*/
    @POST("api/refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);


    /* profil user for MainActivity*/
    @GET("api/user/profile/{phone_number}")
    Call<DataUser> profil(@Path("phone_number") String phone_number);


    /* Reinitialiser mot de passe*/
    @POST("api/renitializePassword")
    @FormUrlEncoded
    Call<AllMyResponse> reset_password(@Field("phone") String phone,
                                       @Field("cni") String cni);


    /* Transfert, Payer Facture, QR CODE, RETRAIT_SMOPAYE*/
    @POST("api/card/transaction/payment")
    @FormUrlEncoded
    Call<HomeResponse> transaction(@Field("amount") float amount,
                                   @Field("code_number_sender") String code_number_sender,
                                   @Field("code_number_receiver") String code_number_receiver,
                                   @Field("transaction_type") String transaction_type);


    /* Abonnement*/
    @POST("api/takeSubscription/{card_id}")
    @FormUrlEncoded
    Call<AllMyResponse> abonnement(@Path("card_id") int card_id,
                                 @Field("type") String type);


    /* Retrait Smopaye*/
    @POST("api/card/{card_id}/retrait")
    @FormUrlEncoded
    Call<AllMyResponse> retrait_accepteur(@Field("withDrawalAmount") Float withDrawalAmount,
                                        @Field("phoneNumber") String phoneNumber,
                                        @Path("card_id") String card_id);


    /* Recharge step 1*/
    @POST("api/account/{account_number}/recharge")
    @FormUrlEncoded
    Call<RechargeResponse> recharge_step1(@Path("account_number") String account_number,
                                          @Field("amount") Float amount,
                                          @Field("phoneNumber") String phoneNumber);

    /* Recharge step 2*/
    @POST("api/account/{account_number}/checkpayment")
    @FormUrlEncoded
    Call<AllMyResponse> recharge_step2(@Path("account_number") String account_number,
                                          @Field("paymentId") String paymentId);

    /* Retrait Accepteur*/
    /*@POST("api/card/{card_id}/retrait")
    @FormUrlEncoded
    Call<AccessToken> retrait_accepteur(@Field("withDrawalAmount") String withDrawalAmount,
                                        @Field("phoneNumber") String phoneNumber,
                                        @Field("phone") String phone);*/


    /* Consult Account */
    @GET("api/card/{card_id}/{typeSolde}")
    Call<AllMyResponse> consult_account(@Path("card_id") String card_id,
                                        @Path("typeSolde") String typeSolde);

    /* Telecollecte  */
    @POST("api/transaction/remote")
    @FormUrlEncoded
    Call<AccessToken> telecollecte(@Field("amount") int amount,
                                   @Field("code_number_receiver") String code_number_receiver,
                                   @Field("serial_number_device") String serial_number_device);


    /* Debit Card */
    @POST("api/transaction/debit")
    @FormUrlEncoded
    Call<AccessToken> debit_card(@Field("amount") int amount,
                                 @Field("code_number_sender") String code_number_sender,
                                 @Field("serial_number_device") String serial_number_device);


    /* Details Card */
    @GET("api/card/{id}")
    Call<Card> mycards(@Path("id") int id);


    /* Activer et désactiver la carte */
    @POST("api/card/{card_id}/activation")
    @FormUrlEncoded
    Call<AccessToken> etat_carte(@Path("card_id") String card_id,
                                 @Field("card_state") String card_state);


    /* Categories  */
    @GET("api/categorierole")
    Call<List<Categorie>> getAllCategories();
    /*----------------------------------------------------------END SMOPAYE MOBILE-------------------------------------------------------*/



    /*----------------------------------------------------------CARD SMOPAYE-------------------------------------------------------*/
    /* Create Card */
    @POST("api/card")
    @FormUrlEncoded
    Call<AllMyResponse> createCard(@Field("code_number") String code_number,
                                   @Field("serial_number") String serial_number,
                                   @Field("end_date") String end_date,
                                   @Field("user_created") int user_created);

    /* Lister all Card */
    @GET("api/card")
    Call<Response_Status> findAllCards();


    /* update Card */
    @PUT("api/card/{card_id}")
    @FormUrlEncoded
    Call<AllMyResponse> updateCard(@Path("card_id") int card_id,
                                  @Field("code_number") String code_number,
                                  @Field("serial_number") String serial_number,
                                  @Field("end_date") String end_date,
                                  @Field("user_created") int user_created);


    /* delete Card */
    @DELETE("api/card/{card_id}")
    Call<AllMyResponse> deleteCard(@Path("card_id") int card_id);


    /*----------------------------------------------------------END CARD SMOPAYE-------------------------------------------------------*/

}
