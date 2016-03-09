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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.modelo.Proyecto;
import com.example.wilmer.sat_riomanzanares.modelo.Usuario;
import com.google.gson.Gson;
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

public class selecionProyecto extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    ListView listaProyectos;
    List<Proyecto> lProyectos = new ArrayList<>();
    List<String> listaProyectosTemp = new ArrayList<>();
    Handler mHandler = new Handler();
    ProgressBar bar;


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
                    startActivity(intent);
                } else {
                    mostrarMensaje("No hay Conexion a internet", Toast.LENGTH_LONG);
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
        } else if (id == R.id.Info) {

        } else if (id == R.id.salir) {

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

   /*public void obtenerProyectos() throws Exception {
        StringBuilder finalStr = new StringBuilder();
        String nombre = getIntent().getExtras().getString("parametro");
        Log.d("SAT", "nombreusuario" + nombre);
        URL url = new URL("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Proyecto/" + nombre);
        BufferedReader in;
        Log.d("SAT", "Conectando a: " + url);
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (Exception e) {
            Log.e("SAT", "Error: " + e.getMessage());
            habilitarloader(false);
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
        Type listType = new TypeToken<LinkedList<Proyecto>>() {
        }.getType();
        lProyectos = new Gson().fromJson(finalStr.toString(), listType);
        habilitarloader(false);
    }*/

    private void mostrarMensaje(final String mensaje, final int duracion) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mensaje, duracion).show();
            }
        });
    }

    private void cambiarEstadoVisual(final boolean flag) {
        mHandler.post(new Runnable() {
            public void run() {
                listaProyectos.setEnabled(flag);
            }
        });
    }

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
                publishProgress("Recibiendo Proyectos");
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
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mostrarMensaje(values[0], Toast.LENGTH_LONG);
            super.onProgressUpdate(values);
        }


    }

}
