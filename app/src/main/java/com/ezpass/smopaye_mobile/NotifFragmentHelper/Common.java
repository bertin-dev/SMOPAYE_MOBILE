package com.ezpass.smopaye_mobile.NotifFragmentHelper;


import com.ezpass.smopaye_mobile.NotifFragmentRemote.IMenuRequest;
import com.ezpass.smopaye_mobile.NotifFragmentRemote.RetrofitClient;

public class Common {

    public static IMenuRequest getMenuRequest(){
        return RetrofitClient.getClient("https://api.androidhive.info/").create(IMenuRequest.class);
    }
}
