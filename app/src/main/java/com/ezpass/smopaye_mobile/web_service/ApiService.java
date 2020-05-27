package com.ezpass.smopaye_mobile.web_service;

import com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB.Response_Status;
import com.ezpass.smopaye_mobile.Profil_user.Card;
import com.ezpass.smopaye_mobile.Profil_user.Categorie;
import com.ezpass.smopaye_mobile.Profil_user.DataUser;
import com.ezpass.smopaye_mobile.web_service_access.AccessToken;

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
    @POST("api/register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("lastname") String lastname,
                               @Field("firstname") String firstname,
                               @Field("phone") String phone,
                               @Field("cni") String cni,
                               @Field("address") String address,
                               @Field("gender") String gender,
                               @Field("role_id") String role_id,
                               @Field("category_id") String category_id,
                               @Field("card_number") String card_number,
                               @Field("abonnement") String abonnement);

    /*auto register */
    @POST("api/autoregister")
    @FormUrlEncoded
    Call<AccessToken> autoregister(@Field("firstname") String firstname,
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
    @POST("oauth/token")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("grant_type") String grant_type,
                            @Field("client_id") String client_id,
                            @Field("client_secret") String client_secret,
                            @Field("username") String username,
                            @Field("password") String password,
                            @Field("scope") String scope);


    /* Update Account */
    @PUT("api/update/{phone_number}")
    @FormUrlEncoded
    Call<AccessToken> update_account(@Path("phone_number") int phone_number,
                                     @Field("password") String password,
                                     @Field("ancien_password") String ancien_password);


    /* Refresh Token*/
    @POST("api/refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);


    /* profil user for MainActivity*/
    @GET("api/userprofile/{phone_number}")
    Call<DataUser> profil(@Path("phone_number") String phone_number);


    /* Reinitialiser mot de passe*/
    @POST("api/reset_password")
    @FormUrlEncoded
    Call<AccessToken> reset_password(@Field("telephone") String telephone,
                                     @Field("piece_justificative") String pj);


    /* Transfert, Payer Facture, QR CODE*/
    @POST("api/transaction/payment")
    @FormUrlEncoded
    Call<AccessToken> transaction(@Field("amount") int amount,
                                  @Field("transaction_type") String transaction_type,
                                  @Field("code_number_sender") String code_number_sender,
                                  @Field("code_number_receiver") String code_number_receiver);


    /* Abonnement*/
    @POST("api/takeSubscription/{card_id}")
    @FormUrlEncoded
    Call<AccessToken> abonnement(@Path("card_id") int card_id,
                                 @Field("type") String type);


    /* Retrait Smopaye*/
    @POST("api/card/{card_id}/retrait")
    @FormUrlEncoded
    Call<AccessToken> retrait_smopaye(@Field("withDrawalAmount") int withDrawalAmount,
                                      @Path("card_id") String card_id,
                                      @Field("phoneNumber") String phoneNumber);


    /* Retrait Accepteur*/
    @POST("api/retrait_accepteur")
    @FormUrlEncoded
    Call<AccessToken> retrait_accepteur(@Field("card_number") String card_number,
                                        @Field("amount") String amount,
                                        @Field("phone") String phone);


    /* Consult Account */
    @POST("api/card/{card_id}/toggleUnityDeposit")
    @FormUrlEncoded
    Call<AccessToken> consult_account(@Path("card_id") String card_id,
                                      @Field("password") String password,
                                      @Field("typeSolde") String typeSolde,
                                      @Field("phone") String phone);

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

    /* Recharge*/
    @POST("api/card/{card_id}/recharge")
    @FormUrlEncoded
    Call<AccessToken> recharge(@Path("card_id") String card_id,
                               @Field("rechargeAmount") int rechargeAmount,
                               @Field("phoneNumber") String phoneNumber);


    /* Details Card */
    @GET("api/card/{id}")
    Call<Card> mycards(@Path("id") int id);


    /* Activer et désactiver la carte */
    @POST("api/card/{card_id}/activation")
    @FormUrlEncoded
    Call<AccessToken> etat_carte(@Path("card_id") String card_id,
                                 @Field("card_state") String card_state);


    /* Categories  */
    @GET("api/categorie")
    Call<List<Categorie>> getAllCategories();
    /*----------------------------------------------------------END SMOPAYE MOBILE-------------------------------------------------------*/



    /*----------------------------------------------------------CARD SMOPAYE-------------------------------------------------------*/
    /* Create Card */
    @POST("api/card")
    @FormUrlEncoded
    Call<AccessToken> createCard(@Field("code_number") String code_number,
                                   @Field("serial_number") String serial_number,
                                   @Field("end_date") String end_date,
                                   @Field("user_created") int user_created);

    /* Lister all Card */
    @GET("api/card")
    Call<Response_Status> findAllCards();


    /* update Card */
    @PUT("api/card/{card_id}")
    @FormUrlEncoded
    Call<AccessToken> updateCard(@Path("card_id") int card_id,
                                  @Field("code_number") String code_number,
                                  @Field("serial_number") String serial_number,
                                  @Field("end_date") String end_date,
                                  @Field("user_created") int user_created);


    /* delete Card */
    @DELETE("api/card/{card_id}")
    Call<AccessToken> deleteCard(@Path("card_id") int card_id);


    /*----------------------------------------------------------END CARD SMOPAYE-------------------------------------------------------*/

}
