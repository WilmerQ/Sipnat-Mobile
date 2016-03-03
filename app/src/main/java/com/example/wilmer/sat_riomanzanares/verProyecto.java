package com.example.wilmer.sat_riomanzanares;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.wilmer.sat_riomanzanares.modelo.Proyecto;
import com.example.wilmer.sat_riomanzanares.modelo.Sensor;
import com.google.android.gms.maps.CameraUpdate;
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
import java.util.LinkedList;
import java.util.List;

public class verProyecto extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    MapFragment mapFragment;
    Spinner zona;
    String[] Zonas = {"Zona Afectada", "Zona Localizacion de sonseres"};
    Proyecto proyecto;
    List<Sensor> ListaSensoresXProyecto;
    TextView nombreProyecto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_proyecto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        proyecto = (Proyecto) getIntent().getExtras().getSerializable("proyectoSelecionado");
        nombreProyecto = (TextView) findViewById(R.id.textView13);
        nombreProyecto.setText(proyecto.getNombre());

        zona = (Spinner) findViewById(R.id.spinnerZonas);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, R.layout.spinner_personalizado_item, Zonas);
        zona.setAdapter(adaptador);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentVerMapa);
        mapFragment.getMapAsync(this);


        try {
            obtenerCensorProyecto(proyecto);
        } catch (Exception e) {
            e.printStackTrace();
        }


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


    public void obtenerCensorProyecto(Proyecto proyecto) throws Exception {
        StringBuilder finalStr = new StringBuilder();
        URL url = new URL("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/SensorMovil/" + proyecto.getId());
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
        Type listType = new TypeToken<LinkedList<Sensor>>() {
        }.getType();
        ListaSensoresXProyecto = new Gson().fromJson(finalStr.toString(), listType);
        Log.d("SAT", "ListaSensoresXProyecto: " + ListaSensoresXProyecto.size());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


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
                    LatLng temp = new LatLng(Double.parseDouble(s.getLatitud()),Double.parseDouble(s.getLongitud()));
                    if (temp.equals(marker.getPosition())){
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


    }

    private Bitmap DownloadImage(String imageHttpAddress) {
        URL imageUrl = null;
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

    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog pDialog;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(verProyecto.this);
            pDialog.setMessage("Cargando Imagen...");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.i("doInBackground ", "Entra en doInBackground");
            String url = params[0];
            Bitmap imagen = DownloadImage(url);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            // img.setImageBitmap(result);
            pDialog.dismiss();
        }

    }

}
