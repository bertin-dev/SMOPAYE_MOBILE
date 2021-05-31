package com.ezpass.smopaye_mobile.RemoteFragments;

import com.ezpass.smopaye_mobile.RemoteNotifications.MyResponse;
import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Structure du model de la classe APIService
 * il contient la clé d'authorisation de l'application
 *
 * @see APIService
 */
public interface APIService {

    @Headers({"Authorization: key=AAAAeZCcmGU:APA91bE1sASgnUZn2Sn980h9q-ElhSCWrHwqFFKuu88rcRIcS6IVSsAbqPwavSgxXQf4P3zvx-XRAxoHBbogjRNBc-Fx0qEKUgfFMS1cN_4BH17YRBfHaKOFeD16qPKJYSz-t8lkpKVW",
            "Content-Type:application/json"})

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
