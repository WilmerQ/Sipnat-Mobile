package com.example.wilmer.sat_riomanzanares;

import android.annotation.TargetApi;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuarioLogueado = (Usuario) getIntent().getExtras().getSerializable("usuarioDatos");

        correo = (EditText) findViewById(R.id.EditCorreo);
        telefono = (EditText) findViewById(R.id.EditTelefono);
        nombreUsuario = (TextView) findViewById(R.id.TxtNombreUsuario);
        modificarDatos = (Button) findViewById(R.id.BtnActualizarDatos);

        nombreUsuario.setText(usuarioLogueado.getNombreUsuario());
        correo.setEnabled(false);
        correo.setText(usuarioLogueado.getEmail());
        telefono.setEnabled(false);
        telefono.setText(usuarioLogueado.getTelefono());
        cambiarContra = (Button) findViewById(R.id.BtnCambiarContra);

        modificarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modificarDatos.getText().equals("Actualizar Datos")) {
                    correo.setEnabled(true);
                    telefono.setEnabled(true);
                    modificarDatos.setText("Guardar");
                } else {
                    if ((!correo.getText().equals(usuarioLogueado.getEmail())) || (!telefono.getText().equals(usuarioLogueado.getTelefono()))) {
                        Log.d("SAT", "modificar datos");
                        try {
                            ModificarDato(usuarioLogueado, "" + correo.getText(), "" + telefono.getText());
                            modificarDatos.setText("Actualizar Datos");
                            correo.setEnabled(false);
                            telefono.setEnabled(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    /**
     * Modificar Dato
     * <br>
     * metodo encargado de recoger los datos editados desde la aplicacion y guardar los cambios hasta el seridor a traves del conmuso de un web service.
     *
     * @param usuarioLogueado
     * @param correo
     * @param telefono
     * @throws Exception
     */
    private void ModificarDato(Usuario usuarioLogueado, String correo, String telefono) throws Exception {
        StringBuilder finalStr = new StringBuilder();
        URL url = new URL("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/CambioEmail/" + usuarioLogueado.getNombreUsuario() + "/" + correo + "/" + telefono);
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
