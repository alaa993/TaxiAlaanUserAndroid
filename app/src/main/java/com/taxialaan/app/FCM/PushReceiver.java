package com.taxialaan.app.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.taxialaan.app.Activities.MainActivity;
import com.taxialaan.app.R;
import com.taxialaan.app.Utils.Utils;
import me.pushy.sdk.Pushy;
import static com.facebook.FacebookSdk.getApplicationContext;


public class PushReceiver extends BroadcastReceiver {

    String channelId = "fcm_default_channel22";
    MediaPlayer mPlayer;
    int data;
    String message;

    @Override
    public void onReceive(Context context, Intent intent) {

        message = intent.getStringExtra("message");
        data = intent.getIntExtra("data",0);
        sendNotification(message, context);

    }

    private void sendNotification(String messageBody, Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setContentTitle(Utils.getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder));


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Pushy.setNotificationChannel(notificationBuilder, context);
        mPlayer = MediaPlayer.create(context, R.raw.alert);
        mPlayer.start();

        notificationManager.notify(1, notificationBuilder.build());
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            return R.drawable.notification_white;
        } else {
            return R.mipmap.ic_launcher;
        }
    }
}

