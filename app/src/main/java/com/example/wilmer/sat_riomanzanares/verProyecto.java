package com.example.wilmer.sat_riomanzanares;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wilmer.sat_riomanzanares.SqLite.parametroBD;
import com.example.wilmer.sat_riomanzanares.modelo.Proyecto;
import com.example.wilmer.sat_riomanzanares.modelo.Sensor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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

/**
 * Clase Ver Proyecto
 * <br>
 * se utiliza para mostrar los sensonres relacionados a un proyecto dentro de un Fragment de un mapa de google Maps.
 *
 * @author Wilmer
 * @see android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
 * @see OnMapReadyCallback
 * @see NavigationView
 * @see android.content.Context
 * @see AppCompatActivity
 */
public class verProyecto extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    MapFragment mapFragment;
    Spinner zona;
    String[] Zonas = {"Zona Afectada", "Zona Localizacion de sonseres"};
    Proyecto proyecto;
    List<Sensor> ListaSensoresXProyecto = new ArrayList<>();
    TextView nombreProyecto;
    ProgressBar bar;
    Handler mHandler = new Handler();
    Handler handler1;
    Switch notificaciones;

    private static final String TAG = "SAT";
    GoogleCloudMessaging gcm;
    String regid;
    String msg;
    Context context;
    //llamando a bd
    parametroBD bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_proyecto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        proyecto = (Proyecto) getIntent().getExtras().getSerializable("proyectoSelecionado");

        bd = new parametroBD(this);
        context = getApplicationContext();
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

        notificaciones = (Switch) findViewById(R.id.switch1);

        notificaciones.setChecked(false);
        notificaciones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Cursor cursor = bd.consultarDispositivo(DameIMEI(context));
                cursor.moveToFirst();
                if (isChecked) {
                    if (isUserRegistered(context)) {
                        Log.d("SAT", "Dispositivo Registrado");
                    } else {
                        Log.d("SAT", "Dispositivo para regiistrar");
                        registerInBackground();
                        //String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/dispositivos/" + cursor.getString(1) + "/" + cursor.getString(0);
                        //new SendGcmToServer().execute(url);
                    }
                    if (checkPlayServices()) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                        regid = getRegistrationId(context);
                        if (regid.isEmpty()) {
                            registerInBackground();
                            //String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/dispositivos/" + cursor.getString(1) + "/" + cursor.getString(0);
                            //new SendGcmToServer().execute(url);
                        }
                    } else {
                        Log.d("SAT", "No valid Google Play Services APK found.");
                    }
                }
                cursor.close();
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
                finish();
            } else if (aBoolean) {
                cambiarEstadoVisual(true);
                actualizarVista(5);
            }
            super.onPostExecute(aBoolean);
        }
    }

    //gcm
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(verProyecto.this);
                        Log.d("SAT", "gcm:--- " + gcm.toString());
                    }
                    regid = gcm.register(Util.SENDER_ID);
                    bd.insertar_dispositivos(DameIMEI(context), regid);
                    msg = "Device registered, registration ID=" + regid;


                    // You should send the registration ID to your server over HTTP,
                    //GoogleCloudMessaging gcm;/ so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    // sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                Log.d("SAT", "MSG:---- " + "" + msg);
                return msg;


            }
        }.execute();

    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.d(TAG, "Saving regId on app version:-- " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Util.PROPERTY_REG_ID, regId);
        editor.putInt(Util.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(verProyecto.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private boolean isUserRegistered(Context context) {
        Cursor cursor = bd.consultarDispositivo(DameIMEI(context));
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return true;
        } else if (cursor.getCount() == 0) {
            Log.d(TAG, "Registration not found.");
            cursor.close();
            return false;
        } else {
            cursor.close();
            return false;
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Util.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(Util.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(Util.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public static String DameIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public class SendGcmToServer extends AsyncTask<String, String, Boolean> {

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
                //               cambiarEstadoVisual(true);
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
            } else if (aBoolean) {
                if (finalStr.toString().equals("Ok")) {
                    mostrarMensaje("Se guardo el dispositivo", Toast.LENGTH_SHORT);
                } else if (finalStr.toString().equals("Error")) {
                    mostrarMensaje("Error al guardar dispositivo en server", Toast.LENGTH_SHORT);
                }
            }
            super.onPostExecute(aBoolean);
        }
    }
}
