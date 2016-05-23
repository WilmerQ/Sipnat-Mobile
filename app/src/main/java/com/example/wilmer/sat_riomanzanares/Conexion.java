package com.example.wilmer.sat_riomanzanares;

/**
 * Clase Conexion
 * <br>
 * clase con la informacion de la conexion realiza por la aplicacion.
 *
 * @author Wilmer
 */
public class Conexion {

    /**
     * variable que contiene la ip o direccion para conectar.
     */
    public static final String localhost = "54.165.89.80";
    //public static final String localhost = "90.1.1.2";

    /**
     * contiene el puerto de entrada para la conexion.
     */
    public static final String puerto = "8080";


    /**
     * Gets localhost.
     *
     * @return the localhost
     */
    public static String getLocalhost() {
        return localhost;
    }

    /**
     * Gets puerto.
     *
     * @return the puerto
     */
    public static String getPuerto() {
        return puerto;
    }

}
