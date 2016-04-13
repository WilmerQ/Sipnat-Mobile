package com.example.wilmer.sat_riomanzanares;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Clase GcmIntentService
 * <br>
 * se encarga de activar cuando la clase GcmBroadcastReceiver se lo indica
 * y despues de esto envia una notificacion.
 *
 * @author Wilmer
 * @see android.app.IntentService
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    android.support.v4.app.NotificationCompat.Builder builder;
    String TAG = "SAT";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            Log.d("SAT", "Extras: " + extras.toString());
            for (String s : extras.keySet()) {
                Log.d("SAT", "Extras:describe " + s);
                Log.d("SAT", "Extras:contenido " + extras.getString(s));
            }

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String recieved_message = intent.getStringExtra("message");
                sendNotification("message recieved :" + recieved_message);
                // alertaDialogo("message recieved :" + recieved_message);
                Log.d("SAT", "message recieved: " + recieved_message);
                Intent sendIntent = new Intent("message_recieved");
                sendIntent.putExtra("message", recieved_message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * sendNotification
     * <br>
     * recibe un objeto y lo tramita para ser mostrado en la zona de notificacion
     * llama una nueva actividad PopAlerta
     *
     * @param msg
     */
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        /*PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, verProyecto.class), 0);*/

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_salir)
                        .setContentTitle("SAT - Alerta")
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        //mBuilder.setContentIntent(contentIntent);
        Intent intent = new Intent(this, PopAlerta.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        //  PendingIntent.FLAG_ONE_SHOT);
        startActivity(intent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
