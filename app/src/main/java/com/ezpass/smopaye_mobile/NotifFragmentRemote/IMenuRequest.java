package com.ezpass.smopaye_mobile.NotifFragmentRemote;


import com.ezpass.smopaye_mobile.NotifFragmentModel.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Interface qui permet de lister une d'Items provenant d'une URL
 * @see IMenuRequest
 * {@link getMenuList}
 */
public interface IMenuRequest {
    @GET
    Call<List<Item>> getMenuList(@Url String url);
}
