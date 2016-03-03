package com.example.wilmer.sat_riomanzanares;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidplot.Plot;
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
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Wilmer on 2/02/2016.
 */
public class Pop extends Activity {


    Sensor sensor;
    TextView id;
    TextView tipo;
    ImageButton cerrar;
    private XYPlot mySimpleXYPlot;
    List<Dato> datos = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;


        sensor = (Sensor) getIntent().getExtras().getSerializable("sensorSeleccionado");
        id = (TextView) findViewById(R.id.textView21);
        tipo = (TextView) findViewById(R.id.textView23);

        id.setText("" + sensor.getId().toString());
        tipo.setText(sensor.getNombreTipoSensor());

        Log.d("SAT", "poppu " + sensor.getIdTipoSensor() + sensor.getId() + sensor.getLatitud() + sensor.getLongitud());
        getWindow().setLayout((int) (width * .85), (int) (height * .90));

        try {
            obtenerDato();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int i = 0;
        Number[] datoXsensor = new Number[0];
        Number[] fechas = new Number[0];
        while (datos.size() <= i) {
            datoXsensor[i] = Integer.parseInt(datos.get(i).getDato());
            fechas[i] = datos.get(i).getFechaRecolecion().getTime();
        }


        mySimpleXYPlot = (XYPlot) findViewById(R.id.myXYPlot);
        //Number[] numSightings = {5, 8, 9, 2, 5};
        /*Number[] years = {
                978307200,  // 2001
                1009843200, // 2002
                1041379200, // 2003
                1072915200, // 2004
                1104537600  // 2005
        };*/
        // create our series from our array of nums:
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(fechas),
                Arrays.asList(datoXsensor),
                "Sensor #" + sensor.getNombreTipoSensor());

        mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        mySimpleXYPlot.getGraphWidget().getDomainGridLinePaint().setColor(Color.BLACK);
        //mySimpleXYPlot.getGraphWidget().getDomainGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        //mySimpleXYPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        //mySimpleXYPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        mySimpleXYPlot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        mySimpleXYPlot.getBorderPaint().setStrokeWidth(1);
        mySimpleXYPlot.getBorderPaint().setAntiAlias(false);
        mySimpleXYPlot.getBorderPaint().setColor(Color.WHITE);

        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        //LineAndPointFormatter series1Format = new LineAndPointFormatter();

        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));

        LineAndPointFormatter formatter = new LineAndPointFormatter();
        formatter.setFillPaint(lineFill);
        mySimpleXYPlot.getGraphWidget().setPaddingRight(2);
        mySimpleXYPlot.addSeries(series2, formatter);

        // draw a domain tick for each year:
        mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, fechas.length);

        // customize our domain/range labels
        mySimpleXYPlot.setDomainLabel("Date");
        mySimpleXYPlot.setRangeLabel("DATOS");

        // get rid of decimal points in our range labels:
        mySimpleXYPlot.setRangeValueFormat(new DecimalFormat("0"));

        mySimpleXYPlot.setDomainValueFormat(new Format() {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
                long timestamp = ((Number) object).longValue() * 1000;
                Date date = new Date(timestamp);
                return dateFormat.format(date, buffer, field);
            }

            @Override
            public Object parseObject(String string, ParsePosition position) {
                return null;
            }
        });

       // mySimpleXYPlot.setMarkupEnabled(false);


    }


    public void obtenerDato() throws Exception {
        StringBuilder finalStr = new StringBuilder();
        // String nombre = getIntent().getExtras().getString("parametro");
        //og.d("SAT", "nombreusuario" + nombre);
        URL url = new URL("http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/sipnat/webresources/dato/" + 10);
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
        Type listType = new TypeToken<LinkedList<Dato>>() {
        }.getType();
        datos = new Gson().fromJson(finalStr.toString(), listType);
    }
}
