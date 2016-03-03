package com.example.wilmer.sat_riomanzanares;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Registro extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    EditText nombreUsuario;
    EditText email;
    EditText telefono;
    EditText contrasena;
    EditText confirmacion;
    Button registrar;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nombreUsuario = (EditText) findViewById(R.id.editTextNombreUsuario);
        email = (EditText) findViewById(R.id.editTextEmail);
        telefono = (EditText) findViewById(R.id.editTextCelular);
        contrasena = (EditText) findViewById(R.id.editTextContrasena);
        confirmacion = (EditText) findViewById(R.id.editTextConfirmacion);
        registrar = (Button) findViewById(R.id.registrar);

        nombreUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("Este campo no se admiten espacios en blanco", Toast.LENGTH_LONG);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
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

        confirmacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("Este campo no se admiten espacios en blanco", Toast.LENGTH_LONG);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombreUsuario.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue Nombre de Usuario", Toast.LENGTH_LONG);
                }
                if (email.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue email", Toast.LENGTH_LONG);
                }
                if (telefono.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue Telefono", Toast.LENGTH_LONG);
                }
                if (contrasena.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue la Contraseña", Toast.LENGTH_LONG);
                }
                if (confirmacion.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue la Confirmacion de la Contraseña", Toast.LENGTH_LONG);
                }
                if(!(confirmacion.getText().toString().equals(contrasena.getText().toString()))){
                    mostrarMensaje("El campo Contraseña y Confirmacion no coinciden", Toast.LENGTH_LONG);
                }
                if ((confirmacion.getText().toString().length() > 0) && (contrasena.getText().toString().length() > 0) && (telefono.getText().toString().length() > 0) && (email.getText().toString().length() > 0) && (nombreUsuario.getText().toString().length() > 0)) {
                    if (confirmacion.getText().toString().equals(contrasena.getText().toString())) {
                        try {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            RegistrarUsuario(nombreUsuario.getText().toString(), confirmacion.getText().toString(), email.getText().toString(), telefono.getText().toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        mostrarMensaje("El campo Contraseña y Confirmacion no coinciden", Toast.LENGTH_LONG);
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
        getMenuInflater().inflate(R.menu.registro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.salir1) {
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

    private void RegistrarUsuario(String nombreusuario, String Contrasea, String Email, String Telefono) throws Exception {
        StringBuilder finalStr = new StringBuilder();
        URL url = new URL("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Usuario/" + nombreusuario.trim() + "/" + Contrasea.trim() + "/" + Email.trim() + "/" + Telefono.trim());

        BufferedReader in;
        Log.d("SAT", "Conectando a: " + url);

        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (Exception e) {
            e.printStackTrace();
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
            if (finalStr.toString().equals("exite")) {
                mostrarMensaje("El nombre de usuario Ya Existe", Toast.LENGTH_LONG);
                contrasena.setText("");
                confirmacion.setText("");
            } else if (finalStr.toString().equals("ok")) {
                nombreUsuario.setText("");
                email.setText("");
                telefono.setText("");
                contrasena.setText("");
                confirmacion.setText("");
                mostrarMensaje("Registro Exitoso", Toast.LENGTH_LONG);
            } else if (finalStr.toString().equals("fail")) {
                mostrarMensaje("Por Favor Intente mas Tarde", Toast.LENGTH_LONG);
            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

}
