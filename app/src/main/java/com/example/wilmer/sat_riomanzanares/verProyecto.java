package com.example.wilmer.sat_riomanzanares;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
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
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.modelo.Proyecto;
import com.example.wilmer.sat_riomanzanares.modelo.Sensor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class verProyecto extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    MapFragment mapFragment;
    Spinner zona;
    String[] Zonas = {"Zona Afectada", "Zona Localizacion de sonseres"};
    Proyecto proyecto;
    List<Sensor> ListaSensoresXProyecto = new ArrayList<>();
    TextView nombreProyecto;
    ProgressBar bar;
    Handler mHandler = new Handler();
    Handler handler1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_proyecto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        proyecto = (Proyecto) getIntent().getExtras().getSerializable("proyectoSelecionado");

        handler1 = new Handler(Looper.getMainLooper());
        nombreProyecto = (TextView) findViewById(R.id.textView13);
        nombreProyecto.setText(proyecto.getNombre());
        bar = (ProgressBar) findViewById(R.id.progressBarVerProyecto);
        bar.setVisibility(View.VISIBLE);
        bar.setMax(10);
        bar.setProgress(1);
        zona = (Spinner) findViewById(R.id.spinnerZonas);
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, R.layout.spinner_personalizado_item, Zonas);
        zona.setAdapter(adaptador);

        cambiarEstadoVisual(false);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentVerMapa);

        String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/SensorMovil/" + proyecto.getId();
        new DescargarSensorXProyecto().execute(url);


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
        getMenuInflater().inflate(R.menu.ver_proyecto, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (!(ListaSensoresXProyecto.size() == 0)) {
            actualizarVista(10);
            for (Sensor s : ListaSensoresXProyecto) {
                Log.d("SAT", "Lat Lon" + s.getLatitud() + "   " + s.getLongitud());
                Double lat = Double.parseDouble(s.getLatitud());
                Double lon = Double.parseDouble(s.getLongitud());
                LatLng latLng1 = new LatLng(lat, lon);
                Bitmap bitmap = DownloadImage("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/imagenServlet?id=" + s.getIdTipoSensor());
                Bitmap temp = Bitmap.createScaledBitmap(bitmap, 128, 128, false);
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(temp);

                MarkerOptions markerOptions = new MarkerOptions().position(latLng1).icon(descriptor);
                googleMap.addMarker(markerOptions);
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(ListaSensoresXProyecto.get(0).getLatitud()),
                    Double.parseDouble(ListaSensoresXProyecto.get(0).getLongitud())), 13));

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    for (Sensor s : ListaSensoresXProyecto) {
                        LatLng temp = new LatLng(Double.parseDouble(s.getLatitud()), Double.parseDouble(s.getLongitud()));
                        if (temp.equals(marker.getPosition())) {
                            Intent i = new Intent(getApplicationContext(), Pop.class);
                            i.putExtra("sensorSeleccionado", s);
                            startActivity(i);
                            return true;
                        }
                    }
                    Log.d("SAT", "entrada a setOnMarkerClickListener");


                    return false;
                }
            });
        } else {
            Log.d("SAT", "esta aqui");
        }
    }

    private Bitmap DownloadImage(String imageHttpAddress) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL imageUrl;
        Bitmap imagen = null;
        try {
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagen = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return imagen;
    }

    private void cambiarEstadoVisual(final boolean flag) {
        mHandler.post(new Runnable() {
            public void run() {
                nombreProyecto.setEnabled(flag);
                zona.setEnabled(flag);
                if (flag) {
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            mapFragment.getMapAsync(verProyecto.this);
                        }
                    });

                } else {
                    mapFragment.onStart();
                }

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

    private void mostrarMensaje(final String mensaje, final int duracion) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mensaje, duracion).show();
            }
        });
    }

    public class DescargarSensorXProyecto extends AsyncTask<String, String, Boolean> {

        HttpURLConnection connection;
        StringBuilder finalStr = new StringBuilder();
        BufferedReader in;

        @Override
        protected void onPreExecute() {
            actualizarVista(2);
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
                //               cambiarEstadoVisual(true);
                return null;
            }

            try {
                publishProgress("Recibiendo Sensores del proyecto");
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
            actualizarVista(3);
            Type listType = new TypeToken<LinkedList<Sensor>>() {
            }.getType();
            ListaSensoresXProyecto = new Gson().fromJson(finalStr.toString(), listType);
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
                mostrarMensaje("Error de conexion - Verifique la conexion", Toast.LENGTH_LONG);
            } else if (aBoolean) {
                cambiarEstadoVisual(true);
                actualizarVista(5);

            }
            super.onPostExecute(aBoolean);
        }
    }
}
