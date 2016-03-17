package com.example.wilmer.sat_riomanzanares;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.example.wilmer.sat_riomanzanares.modelo.Dato;
import com.example.wilmer.sat_riomanzanares.modelo.Sensor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;


public class Pop extends Activity {


    Sensor sensor;
    TextView id;
    TextView tipo;
    private XYPlot mySimpleXYPlot;
    List<Dato> datos = new ArrayList<>();
    Handler mHandler = new Handler(Looper.myLooper());
    ProgressBar bar;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        bar = (ProgressBar) findViewById(R.id.progressBarPopWindow);
        bar.setVisibility(View.VISIBLE);
        bar.setMax(5);

        sensor = (Sensor) getIntent().getExtras().getSerializable("sensorSeleccionado");
        id = (TextView) findViewById(R.id.textView21);
        tipo = (TextView) findViewById(R.id.textView23);

        id.setText("" + sensor.getId().toString());
        tipo.setText(sensor.getNombreTipoSensor());

        String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/dato/" + sensor.getId();

        Log.d("SAT", "poppu " + sensor.getIdTipoSensor() + sensor.getId() + sensor.getLatitud() + sensor.getLongitud());
        getWindow().setLayout((width * 1), (height * 1));


        new DescargarDatosXSensor().execute(url);
    }

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
                id.setEnabled(flag);
                tipo.setEnabled(flag);
                mySimpleXYPlot.setEnabled(flag);
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

    public class DescargarDatosXSensor extends AsyncTask<String, String, Boolean> {

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
                //cambiarEstadoVisual(true);
                return null;
            }

            try {
                publishProgress("Recibiendo Datos");
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
            Type listType = new TypeToken<LinkedList<Dato>>() {
            }.getType();
            datos = new Gson().fromJson(finalStr.toString(), listType);
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
                mostrarMensaje("Sin Internet", Toast.LENGTH_SHORT);
                finish();
            } else if (aBoolean) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                int year = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                Calendar calendar1 = new GregorianCalendar(year, mes, dia);
                Calendar tope = new GregorianCalendar(year, mes, dia + 1);
                List<Dato> datosGraficar = new ArrayList<>();

                for (Dato d : datos) {
                    if ((d.getFechaRecolecion().getTime() > calendar1.getTimeInMillis()) && (d.getFechaRecolecion().getTime() < tope.getTimeInMillis())) {
                        Log.d("SAT", "las fechas de hoy: " + d.getDato() + "   ---- " + d.getFechaRecolecion());
                        datosGraficar.add(d);
                    }
                }

                Number[] horas = new Number[datosGraficar.size()];
                Number[] dato = new Number[datosGraficar.size()];

                int i = 0;
                for (Dato d : datosGraficar) {
                    horas[i] = d.getFechaRecolecion().getTime();
                    dato[i] = Integer.parseInt(d.getDato());
                    Log.d("SAT", "tiempo: --------" + horas[i]);
                    Log.d("SAT", "dato: --------" + dato[i]);
                    i++;
                }

                graficar(horas, dato);


            }
            actualizarVista(5);
            super.onPostExecute(aBoolean);
        }
    }

    private void graficar(final Number[] horas, final Number[] dato) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                mySimpleXYPlot = (XYPlot) findViewById(R.id.myXYPlot);

                // create our series from our array of nums:
                XYSeries series2 = new SimpleXYSeries(
                        Arrays.asList(horas),
                        Arrays.asList(dato),
                        "Datos del Sensor" + sensor.getId());

                mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
                mySimpleXYPlot.getGraphWidget().getDomainGridLinePaint().setColor(Color.BLACK);
                mySimpleXYPlot.getGraphWidget().getDomainGridLinePaint().
                        setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
                mySimpleXYPlot.getGraphWidget().getRangeGridLinePaint().setColor(Color.BLACK);
                mySimpleXYPlot.getGraphWidget().getRangeGridLinePaint().
                        setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
                mySimpleXYPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
                mySimpleXYPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

                // Create a getFormatter to use for drawing a series using LineAndPointRenderer:
                LineAndPointFormatter series1Format = new LineAndPointFormatter(
                        Color.rgb(0, 100, 0),                   // line color
                        Color.rgb(0, 100, 0),                   // point color
                        Color.rgb(100, 200, 0), null);                // fill color


                // setup our line fill paint to be a slightly transparent gradient:
                Paint lineFill = new Paint();
                lineFill.setAlpha(200);

                // ugly usage of LinearGradient. unfortunately there's no way to determine the actual size of
                // a View from within onCreate.  one alternative is to specify a dimension in resources
                // and use that accordingly.  at least then the values can be customized for the device type and orientation.
                lineFill.setShader(new LinearGradient(0, 0, 200, 200, Color.WHITE, Color.GREEN, Shader.TileMode.CLAMP));

                LineAndPointFormatter formatter =
                        new LineAndPointFormatter(Color.rgb(0, 0, 0), Color.BLUE, Color.RED, null);
                formatter.setFillPaint(lineFill);
                mySimpleXYPlot.getGraphWidget().setPaddingRight(2);
                mySimpleXYPlot.addSeries(series2, formatter);

                // draw a domain tick for each year:
                mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, horas.length);

                // customize our domain/range labels
                mySimpleXYPlot.setDomainLabel("Hora");
                mySimpleXYPlot.setRangeLabel("Dato Recolectado");

                // get rid of decimal points in our range labels:
                mySimpleXYPlot.setRangeValueFormat(new DecimalFormat("0"));

                mySimpleXYPlot.setDomainValueFormat(new Format() {

                    // create a simple date format that draws on the year portion of our timestamp.
                    // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
                    // for a full description of SimpleDateFormat.
                    private SimpleDateFormat dateFormat = new SimpleDateFormat(" aaaa - MM - DD HH : mm");

                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

                        // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
                        // we multiply our timestamp by 1000:
                        long timestamp = ((Number) obj).longValue() * 1000;
                        Date date = new Date(timestamp);
                        return dateFormat.format(date, toAppendTo, pos);
                    }

                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return null;

                    }
                });


                mySimpleXYPlot.redraw();
            }
        });
    }

}
