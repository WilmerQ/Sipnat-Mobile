package com.example.wilmer.sat_riomanzanares;

import android.annotation.TargetApi;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.modelo.Usuario;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * clase MiPerfil
 * <br>
 * clase encargada de tramitar la visualizacion de los datos personales y la edicion de estos.
 * los EditText que contienen la informacion estan desabilitados, al momento de dar click en el boton modificiar habilitan su edicion.
 *
 * @see android.content.Context
 * @see android.support.v7.app.AppCompatActivity
 * @see NavigationView
 * @see android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
 */
public class MiPerfil extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * EditText con el Correo.
     */
    EditText correo;
    /**
     * EditText que contiene el Telefono del usuario.
     */
    EditText telefono;
    /**
     * el Nombre usuario.
     */
    TextView nombreUsuario;
    /**
     * Button Modificar datos.
     */
    Button modificarDatos;
    /**
     * Button Cambiar contraseña.
     */
    Button cambiarContra;
    private Handler mHandler = new Handler();

    /**
     * The Usuario logueado.
     */
    Usuario usuarioLogueado;
    /**
     * barra de progreso
     */
    ProgressBar progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        correo = (EditText) findViewById(R.id.EditCorreo);
        telefono = (EditText) findViewById(R.id.EditTelefono);
        nombreUsuario = (TextView) findViewById(R.id.TxtNombreUsuario);
        modificarDatos = (Button) findViewById(R.id.BtnActualizarDatos);
        cambiarContra = (Button) findViewById(R.id.BtnCambiarContra);
        progreso = (ProgressBar) findViewById(R.id.progressBarMiPerfil);
        progreso.setMax(3);
        progreso.setProgress(1);
        cambiarEstadoVisualCompleta(false);

        usuarioLogueado = (Usuario) getIntent().getExtras().getSerializable("usuarioDatos");
        new Descargar().execute("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Usuario/" + usuarioLogueado.getId());

        modificarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modificarDatos.getText().equals("Actualizar Datos")) {
                    cambiarEstadoVisual(true);
                    modificarDatos.setText("Guardar");
                } else {
                    if ((!(validateEmail(correo.getText().toString()))) & correo.getText().length() > 0) {
                        mostrarMensaje("El formato de correo es incorecto Ejemplo: ejemplo@correo.com", Toast.LENGTH_SHORT);
                        correo.setText("");
                    }
                    if ((correo.getText().length() > 0) && (telefono.getText().length() > 0)) {
                        String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/CambioEmail/" + usuarioLogueado.getNombreUsuario() + "/" + correo.getText() + "/" + telefono.getText();
                        progreso.setVisibility(View.VISIBLE);
                        progreso.setMax(3);
                        progreso.setProgress(1);
                        new ActualizarDatos().execute(url);
                        cambiarEstadoVisualCompleta(false);
                        modificarDatos.setText("Actualizar Datos");
                        cambiarEstadoVisual(false);
                    }
                    if (!(correo.getText().length() > 0)) {
                        mostrarMensaje("Ingrese Correo", Toast.LENGTH_SHORT);
                    }
                    if (!(telefono.getText().length() > 0)) {
                        mostrarMensaje("Ingrese telefono", Toast.LENGTH_SHORT);
                    }
                }
            }
        });

        cambiarContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), cambiarContrasena.class);
                i.putExtra("usuarioDatos", usuarioLogueado);
                startActivity(i);
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
        getMenuInflater().inflate(R.menu.mi_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.salir) {
            finishAffinity();
        }
        if (id == R.id.atrasMiPerfil) {
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

    /**
     * funcion utilizda para validar la estructuta del email ingresado por el usuario.
     * contiene el expresion regular de validar email.
     *
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

    public class ActualizarDatos extends AsyncTask<String, String, Boolean> {

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
                cambiarEstadoVisualCompleta(true);
                progreso.setVisibility(View.INVISIBLE);
            } else if (aBoolean) {
                if (finalStr.toString().equals("ok")) {
                    cambiarEstadoVisualCompleta(true);
                    cambiarEstadoVisual(false);
                    progreso.setProgress(3);
                    progreso.setVisibility(View.INVISIBLE);
                    mostrarMensaje("Datos Actualizados", Toast.LENGTH_SHORT);
                } else if (finalStr.toString().equals("Error")) {
                    cambiarEstadoVisualCompleta(true);
                    mostrarMensaje("Error, Intente Luego", Toast.LENGTH_SHORT);
                }
            }
            super.onPostExecute(aBoolean);
        }
    }

    private void cambiarEstadoVisual(final boolean flag) {
        mHandler.post(new Runnable() {
            public void run() {
                correo.setEnabled(flag);
                telefono.setEnabled(flag);
            }
        });
    }

    private void cambiarEstadoVisualCompleta(final boolean flag) {
        mHandler.post(new Runnable() {
            public void run() {
                correo.setEnabled(flag);
                telefono.setEnabled(flag);
                nombreUsuario.setEnabled(flag);
                modificarDatos.setEnabled(flag);
                cambiarContra.setEnabled(flag);
            }
        });
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
                mostrarMensaje("Sin Internet", Toast.LENGTH_SHORT);
                finish();
            } else if (aVoid) {
                cambiarEstadoVisualCompleta(true);
                progreso.setProgress(3);
                progreso.setVisibility(View.INVISIBLE);
                nombreUsuario.setText(usuario.getNombreUsuario());
                correo.setText(usuario.getEmail());
                telefono.setText(usuario.getTelefono());
                cambiarEstadoVisual(false);
            }
            super.onPostExecute(aVoid);
        }
    }
}
