package com.example.wilmer.sat_riomanzanares;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.wilmer.sat_riomanzanares.modelo.Alerta;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

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
                sendNotification(recieved_message);

                //Log.d("SAT", "message recieved: " + recieved_message);
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

        Gson gson = new Gson();
        Alerta alerta = gson.fromJson(msg, Alerta.class);

        Intent intent = new Intent(this, PopAlerta.class);
        intent.putExtra("alerta", alerta);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
