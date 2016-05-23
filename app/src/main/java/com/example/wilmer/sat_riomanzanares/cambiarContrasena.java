package com.example.wilmer.sat_riomanzanares;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.modelo.Md5;
import com.example.wilmer.sat_riomanzanares.modelo.Usuario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Clase cambiar contraseña
 * </p>
 * permite realizar todas las operaciones para cambiar la contraseña del usuario.
 * <br>
 * caso de uso:
 *
 * @author Wilmer
 * @see android.support.v7.app.AppCompatActivity
 */
public class cambiarContrasena extends AppCompatActivity {

    /**
     * EditText donde el usuario digita la contraseña actual
     */
    EditText actual;
    /**
     * EditText donde el usuario digita la contraseña nueva
     */
    EditText nueva;
    /**
     * EditText donde el usuario digita la confirmacion de la contraseña
     */
    EditText confirmacion;
    /**
     * Button Guardar
     */
    Button Guardar;
    /**
     * Handler
     */
    private Handler mHandler = new Handler();
    /**
     * Objeto tipo usuario, el usuario logueado actualmente en la aplicacion.
     */
    Usuario usuarioLogueado;

    ProgressBar progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * asignar a usuario logueado un objeto obtenido por medio de intent.
         */
        usuarioLogueado = (Usuario) getIntent().getExtras().getSerializable("usuarioDatos");

        /**
         * se conecta la variable actual con elemento correspondiente en el layout
         */
        actual = (EditText) findViewById(R.id.editTexActual);
        /**
         * se conecta la variable nueva con elemento correspondiente en el layout
         */
        nueva = (EditText) findViewById(R.id.editTextNueva);
        /**
         * se conecta la variable confirmacion con elemento correspondiente en el layout
         */
        confirmacion = (EditText) findViewById(R.id.editTextConfirma);
        /**
         * se conecta la variable guardar con elemento correspondiente en el layout
         */
        Guardar = (Button) findViewById(R.id.guardar);

        progreso = (ProgressBar) findViewById(R.id.progressBarCambiarContrasea);

        progreso.setVisibility(View.INVISIBLE);
        progreso.setMax(3);
        /**
         * Guardar.setOnClickListener
         * evento del boton guardar donde se realizara:
         *     1. los siguientes campos contengas datos: nueva, actual,confirmacion.
         *     2. nueva contraseña debe coincidir con la contraseña actual del usuario
         *     3. se valida que el campo nueva y confirmacion sean identicos.
         *     4. se invoca el metodo ModificarContra como parametro el objeto usuario logueado y confirmacion.
         */
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((actual.getText().length() > 0) && (nueva.getText().length() > 0) && (confirmacion.getText().length() > 0)) {
                    Log.d("SAT", "nueva: " + nueva.getText());
                    Log.d("SAT", "confimacion: " + confirmacion.getText());
                    if (nueva.getText().toString().trim().equals(confirmacion.getText().toString().trim())) {
                        String convertida = Md5.getEncoddedString(actual.getText().toString().trim());
                        if (convertida != null) {
                            Log.d("SAT", "convertida: " + convertida);
                            Log.d("SAT", "actual: " + usuarioLogueado.getClave());
                            if (convertida.trim().equals(usuarioLogueado.getClave().trim())) {
                                progreso.setVisibility(View.VISIBLE);
                                progreso.setProgress(1);
                                new CambiarContrasenaAsynt().execute("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/CambioContra/" + usuarioLogueado.getNombreUsuario() + "/" + nueva.getText().toString().trim());
                            } else {
                                mostrarMensaje("Contraseña Actual incorrecta", Toast.LENGTH_SHORT);
                                actual.setText("");
                            }
                        }
                    } else {
                        mostrarMensaje("" + usuarioLogueado.getNombreUsuario() + "  ERROR DE CAMBIO DE CONTRASEÑA", Toast.LENGTH_LONG);
                    }
                }
            }
        });

    }


    /**
     * mostrarMensaje:
     * <br>
     * metodo en el cual a traves del metodo post de un Handler (mHandler) se muestra un toast en pantalla.
     *
     * @param mensaje
     * @param duracion
     */
    private void mostrarMensaje(final String mensaje, final int duracion) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mensaje, duracion).show();
            }
        });
    }

    /**
     * ModificarContra
     * <br>
     * metodo que recibe el usuario actual y la nueva contraseña y envia la informacion al servidor para que realice el cambio de contraseña
     *
     * @throws Exception
     */
    public class CambiarContrasenaAsynt extends AsyncTask<String, String, Boolean> {

        HttpURLConnection connection;
        StringBuilder finalStr = new StringBuilder();
        BufferedReader in;

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10 * 1000);
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
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == null) {
                mostrarMensaje("Error de conexion - Verifique la conexion", Toast.LENGTH_SHORT);
                progreso.setVisibility(View.INVISIBLE);
            } else if (aBoolean) {
                if (finalStr.toString().equals("ok")) {
                    progreso.setProgress(3);
                    progreso.setVisibility(View.INVISIBLE);
                    mostrarMensaje("Contraseña Modificada", Toast.LENGTH_SHORT);
                    finish();
                } else if (finalStr.toString().equals("Error")) {
                    mostrarMensaje("Error, Intente Luego", Toast.LENGTH_SHORT);
                }
            }
            super.onPostExecute(aBoolean);
        }
    }
}
