package com.example.wilmer.sat_riomanzanares;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.modelo.Proyecto;
import com.example.wilmer.sat_riomanzanares.modelo.Usuario;
import com.example.wilmer.sat_riomanzanares.modelo.listas;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class selecionProyecto extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    ListView listaProyectos;
    listas listaActual = new listas();
    descargarProyectoAsynctask proyectoAsynctask = new descargarProyectoAsynctask();
    List<Proyecto> lProyectos = new ArrayList<Proyecto>();

    private Handler mHandler = new Handler();

    GifView gifView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecion_proyecto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gifView = (GifView) findViewById(R.id.gif_view1);
        //habilitarloader(true);
        listaProyectos = (ListView) findViewById(R.id.listProyectos);

        String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/Proyecto/" + getIntent().getExtras().getString("parametro");
        proyectoAsynctask.execute(url);

       /* listaProyectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                Log.d("SAT", "item lista: " + item);
                Proyecto proyectoSelecionado = new Proyecto();

                for (int i = 0; i < listaActual.getStringList().size(); i++) {
                    if (listaActual.getProyectoList().get(i).getNombre().contains(item)) {
                        proyectoSelecionado = listaActual.getProyectoList().get(i);
                    }
                }
                Intent intent = new Intent(getApplicationContext(), verProyecto.class);
                intent.putExtra("proyectoSelecionado", proyectoSelecionado);
                startActivity(intent);
            }
        });*/

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

    private void AntesConectar() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


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

    private void habilitarloader(final boolean habilitar) {
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
    }

    private class descargarProyectoAsynctask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            StringBuilder finalStr = new StringBuilder();
            BufferedReader in = null;
            HttpURLConnection connection;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5 * 1000);
                connection.setReadTimeout(5 * 1000);
                connection.connect();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("SAT", "Recibiendo Datos");
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress("Sin Internet o Excede Limite de tiempo");
            }

            String str;
            try {
                assert in != null;
                while ((str = in.readLine()) != null) {
                    finalStr.append(str);
                    Log.d("SAT", "Recibiendo Datos...");
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("SAT", "Resultado: " + finalStr.toString());

            Type listType = new TypeToken<LinkedList<Proyecto>>() {
            }.getType();
            lProyectos = new Gson().fromJson(finalStr.toString(), listType);

            Log.d("SAT", "Lista Cargandose");
            return finalStr.toString();
        }


        @Override
        protected void onPostExecute(String aVoid) {
            //ArrayAdapter<Proyecto> adapter = new ArrayAdapter<Proyecto>(selecionProyecto.this, android.R.layout.simple_list_item_1, lProyectos);
            //listaProyectos.setAdapter(adapter);

            Log.d("SAT", "imprimir  avoid: " + aVoid);


            super.onPostExecute(aVoid);
        }

    }

}
