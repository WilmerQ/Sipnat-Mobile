package com.example.wilmer.sat_riomanzanares;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase Registro
 * <br>
 * Clase encargada de recoelectar los datos del usuario y enviarlos al servidor para su registro.
 *
 * @author Wilmer
 * @see android.support.v7.app.AppCompatActivity
 * @see android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
 * @see NavigationView
 */
public class Registro extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    /**
     * EditText donde se digitara el nombre de usuario.
     */
    EditText nombreUsuario;
    /**
     * EditText donde se digitara el correo.
     */
    EditText email;
    /**
     * EditText donde se digitara el numero de tel.
     */
    EditText telefono;
    /**
     * EditText donde se digita la Contraseña
     */
    EditText contrasena;
    /**
     * EditText donde se digitara la confirmacion de la contraseña
     */
    EditText confirmacion;
    /**
     * Button Registrar encargado de realizar la accion..
     */
    Button registrar;
    /**
     * ProgressBar encargada de mostrar el desarrrollo.
     */
    ProgressBar progreso;

    /**
     * Handler utilizado para monstrar un mesnsaje Toast.
     */
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progreso = (ProgressBar) findViewById(R.id.progressBarRegistro);
        progreso.setVisibility(View.INVISIBLE);

        nombreUsuario = (EditText) findViewById(R.id.editTextNombreUsuario);
        email = (EditText) findViewById(R.id.editTextEmail);
        telefono = (EditText) findViewById(R.id.editTextCelular);
        contrasena = (EditText) findViewById(R.id.editTextContrasena);
        confirmacion = (EditText) findViewById(R.id.editTextConfirmacion);
        registrar = (Button) findViewById(R.id.registrar);

        nombreUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("Este campo no se admiten espacios en blanco", Toast.LENGTH_SHORT);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("Este campo no se admiten espacios en blanco", Toast.LENGTH_SHORT);
            }
        });

        contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("Este campo no se admiten espacios en blanco", Toast.LENGTH_SHORT);
            }
        });

        confirmacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("Este campo no se admiten espacios en blanco", Toast.LENGTH_SHORT);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombreUsuario.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue Nombre de Usuario", Toast.LENGTH_SHORT);
                }
                if (email.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue email", Toast.LENGTH_SHORT);
                }
                if (telefono.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue Telefono", Toast.LENGTH_SHORT);
                }
                if (contrasena.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue la Contraseña", Toast.LENGTH_SHORT);
                }
                if (confirmacion.getText().length() == 0) {
                    mostrarMensaje("Error: Agregue la Confirmacion de la Contraseña", Toast.LENGTH_SHORT);
                }
                if (!(confirmacion.getText().toString().equals(contrasena.getText().toString()))) {
                    mostrarMensaje("El campo Contraseña y Confirmacion no coinciden", Toast.LENGTH_SHORT);
                    contrasena.setText("");
                    confirmacion.setText("");
                }

                if ((!(validateEmail(email.getText().toString()))) & email.getText().length() > 0) {
                    mostrarMensaje("El formato de correo es incorecto Ejemplo: ejemplo@correo.com", Toast.LENGTH_LONG);
                    email.setText("");
                } else {
                    if ((confirmacion.getText().toString().length() > 0) && (contrasena.getText().toString().length() > 0) && (telefono.getText().toString().length() > 0) && (email.getText().toString().length() > 0) && (nombreUsuario.getText().toString().length() > 0)) {
                        if (confirmacion.getText().toString().equals(contrasena.getText().toString())) {
                            cambiarEstadoVisual(false);
                            String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Usuario/" + nombreUsuario.getText().toString().trim() + "/" + confirmacion.getText().toString().trim() + "/" + email.getText().toString().trim() + "/" + telefono.getText().toString().trim();
                            new RegistrarUsuarioAsyntask().execute(url);
                        } else {
                            contrasena.setText("");
                            confirmacion.setText("");
                            mostrarMensaje("El campo Contraseña y Confirmacion no coinciden", Toast.LENGTH_SHORT);

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.atrasRegistro) {
            finish();
        } else if (id == R.id.salirRegistro) {
            finishAffinity();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * metodo utilizado para mostrar un toast a traves de un Handler.
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
     * funcion utilizda para validar la estructuta del email ingresado por el usuario.
     * contiene el expresion regular de validar email.
     * @param email
     * @return matcher
     */
    public static boolean validateEmail(String email) {
        String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        // Compiles the given regular expression into a pattern.
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    /**
     * Clase RegistrarUsuarioAsyntask
     * <br>
     *  clase que implementa AsyncTask para enviar toda la informacion al servidor.
     *  @author Wilmer
     *  @see android.os.AsyncTask
     *
     */
    public class RegistrarUsuarioAsyntask extends AsyncTask<String, String, Boolean> {

        HttpURLConnection connection;
        StringBuilder finalStr = new StringBuilder();
        BufferedReader in;

        @Override
        protected void onPreExecute() {
            progreso.setMax(5);
            progreso.setVisibility(View.VISIBLE);
            actualizarVista(1);
            super.onPreExecute();
        }

        @Override
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
            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mostrarMensaje(values[0], Toast.LENGTH_SHORT);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == null) {
                cambiarEstadoVisual(true);
                progreso.setVisibility(View.INVISIBLE);
            } else if (aBoolean) {
                if (finalStr.toString().equals("exite")) {
                    mostrarMensaje("El nombre de usuario Ya Existe", Toast.LENGTH_LONG);
                    contrasena.setText("");
                    confirmacion.setText("");
                    cambiarEstadoVisual(true);
                    progreso.setVisibility(View.INVISIBLE);
                } else if (finalStr.toString().equals("ok")) {
                    nombreUsuario.setText("");
                    email.setText("");
                    telefono.setText("");
                    contrasena.setText("");
                    confirmacion.setText("");
                    cambiarEstadoVisual(true);
                    mostrarMensaje("Registro Exitoso", Toast.LENGTH_LONG);
                    progreso.setVisibility(View.INVISIBLE);
                    finish();
                } else if (finalStr.toString().equals("fail")) {
                    mostrarMensaje("Por Favor Intente mas Tarde", Toast.LENGTH_LONG);
                }
            }
            super.onPostExecute(aBoolean);
        }


    }

    /**
     * metodo encargado para actulizar el progreso de la barra de progreso.
     * @param progress
     */
    private void actualizarVista(final int progress) {
        mHandler.post(new Runnable() {
            public void run() {
                if (progreso != null) {
                    progreso.setProgress(progress);
                    Log.d("SAT", "Progreso " + progress);
                }
            }
        });
    }

    /**
     * metodo encargado de activar o desactivar ciertos elementos de la interfaz visual dependiendo del valor de la entrada del parametro.
     * @param flag
     */
    private void cambiarEstadoVisual(final boolean flag) {
        mHandler.post(new Runnable() {
            public void run() {
                nombreUsuario.setEnabled(flag);
                email.setEnabled(flag);
                telefono.setEnabled(flag);
                contrasena.setEnabled(flag);
                confirmacion.setEnabled(flag);
                registrar.setEnabled(flag);
            }
        });
    }

}
