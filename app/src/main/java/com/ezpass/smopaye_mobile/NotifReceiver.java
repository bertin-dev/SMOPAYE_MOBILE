package com.ezpass.smopaye_mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationManagerCompat;
import android.widget.Toast;

/**
 *
 * @see NotifReceiver
 * @see BroadcastReceiver
 */
public class NotifReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, R.string.imgClique, Toast.LENGTH_SHORT).show();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(1);
    }
}
