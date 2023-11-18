package com.ezpass.smopaye_mobile.web_service;

import com.ezpass.smopaye_mobile.BuildConfig;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * cette classe me permet de faire des appels réseau et intercepte les requêtes
 *
 * @see RetrofitBuilder
 */
public class RetrofitBuilder {
    //New Domaine
    private static final String BASE_URL = "https://wbser.cm.21052112.smopaye.cm/public/";

    //Old Domaine
    //private static final String BASE_URL = "https://webservice.domaineteste.space.smopaye.fr/public/";

    private final static OkHttpClient client = buildClient();
    private final static Retrofit retrofit = buildRetrofit(client);

    /**
     * cette methode permet d'intercepter la requête, ajouter les Headers correspondant ensuite de renvoyer à nouveau la requête
     * @return une instance de OkHttpClient
     */
    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        Request.Builder builder = request.newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Connection", "close");

                        request = builder.build();

                        return chain.proceed(request);

                    }
                });
        //cette condition permet de verifier si l'on est en mode debug. si oui alors builder.addNetworkInterceptor(new StethoInterceptor());
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        return builder.build();
    }


    /**
     * permet de créer une instance de retrofit qui prend en paramètre le client
     * @param client
     * @return
     */
    private static Retrofit buildRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }


    public static <T> T createService(Class<T> service) {
        return retrofit.create(service);
    }

    /**
     * méthode permettant d'accéder aux ressources privées afin d'obtenir les authorizations
     *
     * @param service
     * @param tokenManager
     * @param <T>
     * @return une classe de type T
     */
    public static <T> T createServiceWithAuth(Class<T> service, final TokenManager tokenManager) {

        OkHttpClient newClient = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                if (tokenManager.getToken().getAccessToken() != null) {
                    builder.addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());
                }
                request = builder.build();
                return chain.proceed(request);
            }
        }).authenticator(CustomAuthenticator.getInstance(tokenManager)).build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }


}
