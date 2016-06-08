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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.SqLite.parametroBD;
import com.example.wilmer.sat_riomanzanares.modelo.Proyecto;
import com.example.wilmer.sat_riomanzanares.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Clase seleccionProyecto
 * <br>
 * clase donde podra seleccionar un item(Proyecto) de una ListView(listaProyectos)
 *
 * @author Wilmer
 * @see android.content.Context
 * @see android.support.v7.app.AppCompatActivity
 * @see android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
 */
public class selecionProyecto extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    /**
     * listaProyectos es un ListView que mostrara los proyectos.
     */
    ListView listaProyectos;
    /**
     * lProyectos es una List<Proyecto> que almacenas los proyectos
     */
    List<Proyecto> lProyectos = new ArrayList<>();
    /**
     * List<String> es una lista de String utilizada de interfaz
     */
    List<String> listaProyectosTemp = new ArrayList<>();
    /**
     * handler
     */
    Handler mHandler = new Handler();
    /**
     * ProgressBar barra de progreso utilizado para mostrar la carga
     */
    ProgressBar bar;

    ListView listaProyectosActivos;
    List<String> listaProyectosActivosTemp = new ArrayList<>();
    List<Proyecto> lProyectosActivos = new ArrayList<>();
    //llamando a bd
    parametroBD bd;

    TextView textAlertaActivadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecion_proyecto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listaProyectos = (ListView) findViewById(R.id.listProyectos);
        bar = (ProgressBar) findViewById(R.id.progressBarSeleccionProyecto);
        bar.setVisibility(View.VISIBLE);
        bar.setMax(5);
        bd = new parametroBD(this);
        listaProyectosActivos = (ListView) findViewById(R.id.listViewProyectosActivos);
        textAlertaActivadas = (TextView) findViewById(R.id.textView11);

        String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Proyecto/" + getIntent().getExtras().getString("parametro");
        new DescargarProyectos().execute(url);

        listaProyectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                Log.d("SAT", "item lista: " + item);
                Proyecto proyectoSelecionado = null;

                for (int i = 0; i < listaProyectosTemp.size(); i++) {
                    if (lProyectos.get(i).getNombre().contains(item)) {
                        proyectoSelecionado = lProyectos.get(i);
                    }
                }
                if (proyectoSelecionado != null) {
                    Intent intent = new Intent(getBaseContext(), verProyecto.class);
                    intent.putExtra("proyectoSelecionado", proyectoSelecionado);
                    intent.putExtra("usuarioParaVerProyecto", (Usuario) getIntent().getExtras().getSerializable("usuarioDatos"));
                    startActivity(intent);
                } else {
                    mostrarMensaje("No hay Conexion a internet", Toast.LENGTH_LONG);
                }

            }
        });

        listaProyectosActivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Log.d("SAT", "item lista: " + item);
                Proyecto proyectoSelecionado = null;

                for (int i = 0; i < listaProyectosActivosTemp.size(); i++) {
                    if (lProyectosActivos.get(i).getNombre().contains(item)) {
                        proyectoSelecionado = lProyectosActivos.get(i);
                    }
                }
                if (proyectoSelecionado != null) {
                    Intent intent = new Intent(getBaseContext(), verProyecto.class);
                    intent.putExtra("proyectoSelecionado", proyectoSelecionado);
                    intent.putExtra("usuarioParaVerProyecto", (Usuario) getIntent().getExtras().getSerializable("usuarioDatos"));
                    startActivity(intent);
                } else {
                    mostrarMensaje("No hay Conexion a internet", Toast.LENGTH_LONG);
                }
            }
        });

        if (bd.consultarAlertasActivas().isEmpty()) {
            textAlertaActivadas.setVisibility(View.INVISIBLE);
            listaProyectosActivos.setVisibility(View.INVISIBLE);
        } else {
            Log.d("SAT", "proyectos con alertas activas");
            Gson g = new GsonBuilder().create();
            Log.d("SAT", "gson" + g.toJson(bd.consultarAlertasActivas()));
            String url1 = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Proyecto/" + g.toJson(bd.consultarAlertasActivas()) + "/2";
            new DescargarProyectosActivos().execute(url1);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mostrarMensaje("te vas a salir", Toast.LENGTH_SHORT);
        finishAffinity();
        return super.onKeyDown(keyCode, event);

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
        getMenuInflater().inflate(R.menu.selecion_proyecto, menu);
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

        if (id == R.id.Mi_perfil) {
            Usuario usuario = (Usuario) getIntent().getExtras().getSerializable("usuarioDatos");
            Intent i = new Intent(this, MiPerfil.class);
            i.putExtra("usuarioDatos", usuario);
            startActivity(i);
        } else if (id == R.id.salir) {

            finishAffinity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * metodo utilizado para mostrar un mensaje en pantalla
     * utilizando un elemento Toast
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
     * metodo utilizado para activar o desactivar ciertos elementos de la view depende del valor del parmetro.
     *
     * @param flag
     */
    private void cambiarEstadoVisual(final boolean flag) {
        mHandler.post(new Runnable() {
            public void run() {
                listaProyectos.setEnabled(flag);
            }
        });
    }

    /**
     * metodo encargado de actualizar la barra de progreso bar
     *
     * @param progress
     */
    private void actualizarVista(final int progress) {
        mHandler.post(new Runnable() {
            public void run() {
                if (bar != null) {
                    bar.setProgress(progress);
                    Log.d("SAT", "Progreso " + progress);
                    if (bar.getMax() == bar.getProgress()) {
                        bar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }


    /**
     * DescargarProyectos
     * <br>
     * clase extendida de AsyncTask encargada de conectar al servidor y descargar la informacion requerida.
     *
     * @see android.os.AsyncTask
     */
    public class DescargarProyectos extends AsyncTask<String, String, Boolean> {


        HttpURLConnection connection;
        StringBuilder finalStr = new StringBuilder();
        BufferedReader in;

        @Override
        protected void onPreExecute() {
            actualizarVista(1);
            super.onPreExecute();
        }

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
                actualizarVista(2);
            } catch (Exception e) {
                publishProgress("sin Internet");
                Log.e("SAT", "Error: " + e.getMessage());
                cambiarEstadoVisual(true);
                return null;
            }

            try {
                //publishProgress("Recibiendo Proyectos");
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
            actualizarVista(3);
            Type listType = new TypeToken<LinkedList<Proyecto>>() {
            }.getType();
            lProyectos = new Gson().fromJson(finalStr.toString(), listType);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!(aBoolean == null)) {
                if (aBoolean) {
                    for (int i = 0; i < lProyectos.size(); i++) {
                        listaProyectosTemp.add(i, lProyectos.get(i).getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, listaProyectosTemp);
                    listaProyectos.setAdapter(adapter);
                    cambiarEstadoVisual(true);
                    actualizarVista(5);
                } else {
                    mostrarMensaje("Error de conexion - Verifique la conexion", Toast.LENGTH_LONG);
                }
            } else {
                mostrarMensaje("Error de conexion - Verifique la conexion", Toast.LENGTH_LONG);
                cambiarEstadoVisual(true);
            }
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mostrarMensaje(values[0], Toast.LENGTH_SHORT);
            super.onProgressUpdate(values);
        }


    }

    public class DescargarProyectosActivos extends AsyncTask<String, String, Boolean> {


        HttpURLConnection connection;
        StringBuilder finalStr = new StringBuilder();
        BufferedReader in;

        @Override
        protected void onPreExecute() {
            //actualizarVista(1);
            super.onPreExecute();
        }

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
                //  actualizarVista(2);
            } catch (Exception e) {
                publishProgress("sin Internet");
                Log.e("SAT", "Error: " + e.getMessage());
                //cambiarEstadoVisual(true);
                return null;
            }

            try {
                //publishProgress("Recibiendo Proyectos");
                Log.d("SAT", "Recibiendo Datos");
                String str;
                while ((str = in.readLine()) != null) {
                    finalStr.append(str);
                    Log.d("SAT", "Recibiendo Datos...");
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                //cambiarEstadoVisual(true);
                return null;
            }

            Log.d("SAT", "Resultado: " + finalStr.toString());
            //actualizarVista(3);
            Type listType = new TypeToken<LinkedList<Proyecto>>() {
            }.getType();
            lProyectosActivos = new Gson().fromJson(finalStr.toString(), listType);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!(aBoolean == null)) {
                if (aBoolean) {
                    for (int i = 0; i < lProyectosActivos.size(); i++) {
                        listaProyectosActivosTemp.add(i, lProyectosActivos.get(i).getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, listaProyectosActivosTemp);
                    listaProyectosActivos.setAdapter(adapter);
                    //      cambiarEstadoVisual(true);
                    //    actualizarVista(5);
                } else {
                    mostrarMensaje("Error de conexion - Verifique la conexion", Toast.LENGTH_LONG);
                }
            } else {
                mostrarMensaje("Error de conexion - Verifique la conexion", Toast.LENGTH_LONG);
                // cambiarEstadoVisual(true);
            }
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mostrarMensaje(values[0], Toast.LENGTH_SHORT);
            super.onProgressUpdate(values);
        }


    }

}
