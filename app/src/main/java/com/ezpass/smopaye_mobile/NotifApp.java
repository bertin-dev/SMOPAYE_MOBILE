package com.ezpass.smopaye_mobile;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

public class NotifApp extends Application {
    public static final String CHANNEL_ID = "exampleChannel";

    //check network
    private static NotifApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        initInterceptor();

        mInstance = this;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void  initInterceptor(){
        //initialisation de stetho pour l'interption des requêtes HTTP
        Stetho.initializeWithDefaults(this);

        //initialisation de LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }

    //check network
    public static synchronized NotifApp getInstance(){
        return mInstance;
    }
    //check network
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
