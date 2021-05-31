package com.ezpass.smopaye_mobile;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * class NotifApp qui hérite de la classe Application me permet d'initialiser stetho pour intercepter mes requetes ou encore createNotificationChannel
 * @see NotifApp
 * @see Application
 */
public class NotifApp extends Application {
    public static final String CHANNEL_ID = "exampleChannel";

    //check network
    private static NotifApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        //initInterceptor();

        mInstance = this;
    }


    /**
     * permet de créer une chaine de notification
     */
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

    /**
     * permet d'intercepter les fuites de mémoires dans l'application
     *
     * @see initInterceptor
     */
    private void initInterceptor(){
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

    /**
     *
     * @return mInstance de type NotifApp
     */
    public static synchronized NotifApp getInstance(){
        return mInstance;
    }

    /**
     * permet d'ecouter l'etat de la connexion internet
     *
     * @see setConnectivityListener
     *
     *
     * @param listener
     */
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
