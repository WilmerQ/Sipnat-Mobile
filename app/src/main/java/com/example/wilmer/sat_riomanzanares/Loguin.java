package com.example.wilmer.sat_riomanzanares;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Loguin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static Usuario usuario;
    private EditText nombreUsuario;
    private EditText contrasena;
    private Button btnEntrar;
    //private Handler mHandler1;
    private Handler mHandler = new Handler();
    //GifView gifView;
    HttpURLConnection connection;
    Thread threadSecundario;
    ProgressDialog pd;
    private ProgressBar progreso;


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
        // gifView = (GifView) findViewById(R.id.gif_view);
        progreso = (ProgressBar) findViewById(R.id.progressBar);

        progreso.setMax(100);
        progreso.setVisibility(View.INVISIBLE);
        //habilitarloader(false);
        i = new Intent(Loguin.this, Registro.class);

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
                    threadSecundario = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cambiarEstadoVisual(false);
                                DescargarDatos(nombreUsuario.getText().toString(), contrasena.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    threadSecundario.start();
                    while (progreso.getProgress() < progreso.getMax()) {
                        Log.d("SAT", "Esperando.");
                        if (!(usuario == null)) {
                            i.putExtra("parametro", usuario.getNombreUsuario());
                            i.putExtra("usuarioDatos", usuario);
                            startActivity(i);
                            break;
                        }
                    }
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
                Toast.makeText(getApplicationContext(), mensaje, duracion).show();
            }
        });
    }

    public void DescargarDatos(String usr, String pass) throws Exception {

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        Thread.sleep(5000);
        StringBuilder finalStr = new StringBuilder();
        BufferedReader in;
        URL url = new URL("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Usuario/" + usr + "/" + pass);
        try {

            actualizarVista(20);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();
            Log.d("SAT", "Conectando a: " + url);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        } catch (Exception e) {
            mostrarMensaje("Sin Conexion  a internet", Toast.LENGTH_LONG);
            //habilitarloader(false);
            Log.e("SAT", "Error: " + e.getMessage());
            cambiarEstadoVisual(true);
            throw new Exception("SIN CONEXION");
        }
        Log.d("SAT", "Recibiendo Datos");
        String str;
        while ((str = in.readLine()) != null) {
            finalStr.append(str);
            actualizarVista(progreso.getProgress() + 10);
            Log.d("SAT", "Recibiendo Datos...");
        }
        in.close();
        Log.d("SAT", "Resultado: " + finalStr.toString());
        actualizarVista(100);
        Gson gson = new Gson();

        try {
            usuario = gson.fromJson(finalStr.toString(), Usuario.class);
            if (usuario.getInformeDeError() == 1) {
                mostrarMensaje("ERROR DE AUTENTICACION", Toast.LENGTH_LONG);
                //habilitarloader(false);
                contrasena.setText("");
                cambiarEstadoVisual(true);

            } else {
                mostrarMensaje("Bienvenido " + usuario.getNombreUsuario(), Toast.LENGTH_LONG);
                nombreUsuario.setText("");
                contrasena.setText("");
                //  habilitarloader(false);
                cambiarEstadoVisual(true);
                btnEntrar.setClickable(true);


            }
        } catch (JsonSyntaxException e) {
            //habilitarloader(false);
            cambiarEstadoVisual(true);
            e.printStackTrace();
        }
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

    private void intent(final Usuario usuario) {

    }

   /* private void habilitarloader(final boolean habilitar) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (habilitar) {
                    gifView.setVisibility(View.VISIBLE);
                    //loader.setVisibility(View.VISIBLE);
                } else {
                    gifView.setVisibility(View.INVISIBLE);
                    //  loader.setVisibility(View.INVISIBLE);
                }
            }
        });
    }*/

    private void actualizarVista(final int progress) {
        if (progreso.getVisibility() == View.INVISIBLE) {
            progreso.setVisibility(View.VISIBLE);
        }
        mHandler.post(new Runnable() {
            public void run() {
                if (progreso != null) {
                    progreso.setProgress(progress);
                    Log.d("SAT", "Progreso " + progress);
                    Log.d("SAT", "visibilidad " + progreso.getVisibility());
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


}
