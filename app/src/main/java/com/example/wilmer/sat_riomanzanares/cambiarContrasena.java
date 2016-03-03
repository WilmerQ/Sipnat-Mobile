package com.example.wilmer.sat_riomanzanares;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.modelo.Md5;
import com.example.wilmer.sat_riomanzanares.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class cambiarContrasena extends AppCompatActivity {

    EditText actual;
    EditText nueva;
    EditText confirmacion;
    Button Guardar;
    private Handler mHandler = new Handler();
    Usuario usuarioLogueado;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuarioLogueado = (Usuario) getIntent().getExtras().getSerializable("usuarioDatos");

        actual = (EditText) findViewById(R.id.editTexActual);
        nueva = (EditText) findViewById(R.id.editTextNueva);
        confirmacion = (EditText) findViewById(R.id.editTextConfirma);
        Guardar = (Button) findViewById(R.id.guardar);

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
                                try {
                                    ModificarContra(usuarioLogueado, confirmacion.getText().toString().trim());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        mostrarMensaje("" + usuarioLogueado.getNombreUsuario() + "  ERROR DE CAMBIO DE CONTRASEÑA", Toast.LENGTH_LONG);
                    }
                }
            }
        });

    }

    private void ModificarContra(Usuario usuarioLogueado, String nueva) throws Exception {
        StringBuilder finalStr = new StringBuilder();
        URL url = new URL("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/CambioContra/" + usuarioLogueado.getNombreUsuario() + "/" + nueva.trim());

        BufferedReader in;
        Log.d("SAT", "Conectando a: " + url);

        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (Exception e) {
            Log.e("SAT", "Error: " + e.getMessage());
            throw new Exception("SIN CONEXION");
        }
        Log.d("SAT", "Recibiendo Datos");
        String str;
        while ((str = in.readLine()) != null) {
            finalStr.append(str);
            Log.d("SAT", "Recibiendo Datos...");
        }

        in.close();

        Log.d("SAT", "Resultado: " + finalStr.toString());

        Gson gson = new Gson();

        try {
            if (!finalStr.toString().equals("ok")) {
                mostrarMensaje("ERROR ACTULIZANDO DATOS", Toast.LENGTH_LONG);
            } else {
                mostrarMensaje("" + usuarioLogueado.getNombreUsuario() + "  sus datos se han actulizado correctamente", Toast.LENGTH_LONG);
                Intent i = new Intent(this, Loguin.class);
                startActivity(i);

            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(final String mensaje, final int duracion) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mensaje, duracion).show();
            }
        });
    }

}
