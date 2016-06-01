package com.example.wilmer.sat_riomanzanares;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wilmer.sat_riomanzanares.SqLite.parametroBD;
import com.example.wilmer.sat_riomanzanares.modelo.Alerta;
import com.example.wilmer.sat_riomanzanares.modelo.Usuario;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by Wilmer on 29/03/2016.
 */
public class PopAlerta extends Activity {
    Vibrator vibrator;
    MediaPlayer mediaPlayer;

    TextView nomnreProyecto;
    TextView nivelAlerta;
    TextView descripcion;
    TextView FechayHora;
    TextView cuentaAtras;

    Alerta alerta;

    CountDownTimer timer;
    private NotificationManager mNotificationManager;
    android.support.v4.app.NotificationCompat.Builder builder;
    public static final int NOTIFICATION_ID = 1;

    //llamando a bd
    parametroBD bd;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_alerta);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 200, 0};
        vibrator.vibrate(pattern, 0);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarma);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        getWindow().setLayout((int) (width * .70), (int) (height * .60));

        nomnreProyecto = (TextView) findViewById(R.id.textViewNombreProyecto);
        nivelAlerta = (TextView) findViewById(R.id.textViewNivelAlerta);
        descripcion = (TextView) findViewById(R.id.textViewDescripcion);
        FechayHora = (TextView) findViewById(R.id.FechaAlerta);
        cuentaAtras = (TextView) findViewById(R.id.textView27);
        RelativeLayout root = (RelativeLayout) findViewById(R.id.XmlPopAlerta);

        alerta = (Alerta) getIntent().getExtras().getSerializable("alerta");


        assert alerta != null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        nomnreProyecto.setText(alerta.getProyecto().getNombre());
        nivelAlerta.setText(alerta.getNivel());
        descripcion.setText(alerta.getDescripcion());
        FechayHora.setText(dateFormat.format(alerta.getHoraDelDisparo()));
        root.setBackgroundColor(Color.parseColor("" + alerta.getCodigoColor()));

        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("SAT", "cuenta atras:" + millisUntilFinished);
                cuentaAtras.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                vibrator.cancel();
                mediaPlayer.stop();
                finish();
            }
        }.start();

        bd = new parametroBD(this);
        Cursor cursor = bd.consultarUsuario();
        cursor.moveToFirst();
        Log.d("SAT", "cursor: " + cursor.getString(0));
        new Descargar().execute("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Usuario/" + cursor.getString(0));

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        vibrator.cancel();
        mediaPlayer.stop();

        Log.d("SAT", "ontouch: " + event.toString());
        return super.onTouchEvent(event);
    }

    public class Descargar extends AsyncTask<String, String, Boolean> {

        /**
         * HttpURLConnection objeto connection que establece la comunicacion.
         */
        HttpURLConnection connection;
        /**
         * StringBuilder encargada de obtener la cadena de texto descargada desde la comunicacion.
         */
        StringBuilder finalStr = new StringBuilder();
        /**
         * BufferedReader informacion recibida sin ningun tipo de formato
         */
        BufferedReader in;
        /**
         * The Gson.
         */
        Gson gson = new Gson();
        /**
         * The Usuario.
         */
        Usuario usuario;

        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5 * 1000);
                connection.setReadTimeout(10 * 1000);
                connection.connect();
                Log.d("SAT", "Conectando a: " + url);
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (Exception e) {

                Log.e("SAT", "Error: " + e.getMessage());
                return null;
            }

            try {
                Log.d("SAT", "Recibiendo Datos");
                String str;
                while ((str = in.readLine()) != null) {
                    finalStr.append(str);
                    Log.d("SAT", "Recibiendo Datos...");
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            Log.d("SAT", "Resultado: " + finalStr.toString());
            usuario = gson.fromJson(finalStr.toString(), Usuario.class);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            if (aVoid == null) {
                //mostrarMensaje("Sin Internet", Toast.LENGTH_SHORT);
                finish();
            } else if (aVoid) {
                //cambiarEstadoVisualCompleta(true);
                //progreso.setProgress(3);
                //progreso.setVisibility(View.INVISIBLE);
                //nombreUsuario.setText(usuario.getNombreUsuario());
                //correo.setText(usuario.getEmail());
                //telefono.setText(usuario.getTelefono());
                //cambiarEstadoVisual(false);


                Intent intent1 = new Intent(PopAlerta.this, verProyecto.class);
                intent1.putExtra("usuarioParaVerProyecto", usuario);
                intent1.putExtra("proyectoSelecionado", alerta.getProyecto());
                // intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                PendingIntent pi = PendingIntent.getActivity(PopAlerta.this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
                //PendingIntent contentIntent = PendingIntent.getActivity(PopAlerta.this, 0, intent1, 0);

                android.support.v4.app.NotificationCompat.Builder mBuilder =
                        new android.support.v4.app.NotificationCompat.Builder(PopAlerta.this)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icononoti))
                                .setSmallIcon(R.mipmap.icono2)
                                .setContentTitle("Sipnat - Alerta")
                                .setContentText("" + alerta.getNivel())
                                .setContentIntent(pi);

                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
            super.onPostExecute(aVoid);
        }
    }
}
