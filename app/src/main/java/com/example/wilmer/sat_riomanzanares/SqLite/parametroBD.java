package com.example.wilmer.sat_riomanzanares.SqLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Wilmer on 27/02/2015.
 */


public class parametroBD {

    private funcionesBD funcionesBD;
    private SQLiteDatabase db;

    public parametroBD(Context context) {
        funcionesBD = new funcionesBD(context);
        db = funcionesBD.getWritableDatabase();
    }

    //insertar parametros

    //crear las tablas
    public static final String crear_tabla_dispositivos = "CREATE TABLE dispositivos (" +
            "imei TEXT NOT NULL ," +
            "tokengoogle TEXT NOT NULL ," +
            "idproyecto TEXT NOT NULL " +
            ");";

    /*alter table ejmplo
    public static final String actualizar_gps = "ALTER TABLE gps ADD COLUMN ruta INTEGER; update gps set ruta = 0";*/

    //ContentValues
    private ContentValues generarContentValues_Dispositivos(String imei, String tokengoogle, Long idProyecto) {
        ContentValues valores = new ContentValues();
        valores.put("imei", imei);
        valores.put("tokengoogle", tokengoogle);
        valores.put("idproyecto", idProyecto.toString());
        return valores;
    }

    //insert dispositivos
    public long insertar_dispositivos(String imei, String tokengoogle, Long idProyecto) {
        return db.insert("dispositivos", null, generarContentValues_Dispositivos(imei, tokengoogle, idProyecto));
    }

    public Cursor consultarDispositivo(String imei) {
        Cursor cursor = db.rawQuery("SELECT * FROM dispositivos where imei=" + imei, null);
        return cursor;
    }


    public Cursor consultarProyecto(String idproyecto, String imei) {
        Cursor cursor = db.rawQuery("SELECT * FROM dispositivos where idproyecto=" + idproyecto + " and imei=" + imei, null);
        return cursor;
    }

    public void EliminarDispositvo(String idproyecto){
        int a = db.delete("dispositivos","idproyecto="+idproyecto,null);
    }


}



