package com.example.wilmer.sat_riomanzanares;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Loguin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText nombreUsuario;
    private EditText contrasena;
    private Button btnEntrar;
    private Handler mHandler = new Handler(), mHandler1 = new Handler();
    ProgressBar progreso;


    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loguin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nombreUsuario = (EditText) findViewById(R.id.nombreUsuario);
        contrasena = (EditText) findViewById(R.id.password);
        btnEntrar = (Button) findViewById(R.id.btnEntrar);
        progreso = (ProgressBar) findViewById(R.id.progressBar);
        progreso.setVisibility(View.INVISIBLE);
        i = new Intent(Loguin.this, selecionProyecto.class);

        nombreUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("Este campo no se admiten espacios en blanco", Toast.LENGTH_LONG);
            }
        });

        contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("Este campo no se admiten espacios en blanco", Toast.LENGTH_LONG);
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombreUsuario.getText().length() == 0) {
                    mostrarMensaje("ERROR: ingrese nombre de usuario", Toast.LENGTH_LONG);
                }

                if (contrasena.getText().length() == 0) {
                    mostrarMensaje("ERROR: ingrese la contraseña", Toast.LENGTH_LONG);
                }

                if ((nombreUsuario.getText().length() != 0) && (contrasena.getText().length() != 0)) {

                    String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Usuario/" + nombreUsuario.getText().toString() + "/" + contrasena.getText().toString();
                    cambiarEstadoVisual(false);
                    new Descargar().execute(url);
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loguin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.registrar) {

            Intent intent = new Intent(Loguin.this, Registro.class);
            startActivity(intent);
        } else if (id == R.id.Info) {

        } else if (id == R.id.salir) {

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mostrarMensaje(final String mensaje, final int duracion) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), mensaje, duracion).show();
            }
        });
    }

    private void cambiarEstadoVisual(final boolean flag) {
        mHandler.post(new Runnable() {
            public void run() {
                nombreUsuario.setEnabled(flag);
                contrasena.setEnabled(flag);
                btnEntrar.setEnabled(flag);
            }
        });
    }

    private void actualizarVista(final int progress) {
        mHandler1.post(new Runnable() {
            public void run() {
                if (progreso != null) {
                    progreso.setProgress(progress);
                    Log.d("SAT", "Progreso " + progress);
                }
            }
        });
    }

    private void setProgresoIndeterminado(final boolean flag) {
        mHandler.post(new Runnable() {
            public void run() {
                progreso.setIndeterminate(flag);
            }
        });
    }

    public class Descargar extends AsyncTask<String, String, Boolean> {

        HttpURLConnection connection;
        StringBuilder finalStr = new StringBuilder();
        BufferedReader in;
        Gson gson = new Gson();
        Usuario usuario;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso.setMax(100);
            progreso.setVisibility(View.VISIBLE);
            actualizarVista(0);
        }

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
                publishProgress("sin Internet");
                Log.e("SAT", "Error: " + e.getMessage());
                cambiarEstadoVisual(true);
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
                cambiarEstadoVisual(true);
                return null;
            }

            Log.d("SAT", "Resultado: " + finalStr.toString());
            actualizarVista(50);


            usuario = gson.fromJson(finalStr.toString(), Usuario.class);
            if (usuario.getInformeDeError() == 1) {
                publishProgress("ERROR De Autenticacion");
                return false;
            } else {
                publishProgress("Bienvenido " + usuario.getNombreUsuario());
                return true;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (!(values == null)) {
                mostrarMensaje(" " + values[0], Toast.LENGTH_LONG);
                actualizarVista(progreso.getProgress() + 10);
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            try {
                if (aVoid == null) {
                    cambiarEstadoVisual(true);
                    progreso.setVisibility(View.INVISIBLE);
                } else if (!aVoid) {
                    contrasena.setText("");
                    cambiarEstadoVisual(true);
                    progreso.setVisibility(View.INVISIBLE);

                } else {
                    nombreUsuario.setText("");
                    contrasena.setText("");
                    cambiarEstadoVisual(true);
                    i.putExtra("parametro", usuario.getNombreUsuario());
                    i.putExtra("usuarioDatos", usuario);
                    progreso.setProgress(100);
                    startActivity(i);
                    finish();
                }
            } catch (JsonSyntaxException e) {
                cambiarEstadoVisual(true);
                progreso.setVisibility(View.INVISIBLE);
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);
        }
    }


}
