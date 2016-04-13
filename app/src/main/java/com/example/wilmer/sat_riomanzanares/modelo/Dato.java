/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.wilmer.sat_riomanzanares.modelo;


import java.io.Serializable;
import java.util.Date;

/**
 * clase Dato
 * </p>
 * contiene la informacion del objeto dato.
 * </p>
 * Dato: es llamado dato aquella informacion recolectada por un sensor por ejemplo la temperatura en un punto.
 *
 * @author Wilmer
 * @see java.io.Serializable
 */
public class Dato implements Serializable {

    /**
     * Fecha de Recoleccion
     */
    private Date fechaRecolecion;
    /**
     * hora de la recoleccion
     */
    private String horaRecolecion;
    /**
     * dato recolectado.
     */
    private String dato;
    /**
     * Sensor que recolecto el dato
     */
    private Sensor sensor;

    /**
     * Gets fecha recolecion.
     *
     * @return la fecha recolecion
     */
    public Date getFechaRecolecion() {
        return fechaRecolecion;
    }

    /**
     * Sets fecha recolecion.
     *
     * @param fechaRecolecion la fecha recolecion
     */
    public void setFechaRecolecion(Date fechaRecolecion) {
        this.fechaRecolecion = fechaRecolecion;
    }

    /**
     * Gets hora recolecion.
     *
     * @return la hora recolecion
     */
    public String getHoraRecolecion() {
        return horaRecolecion;
    }

    /**
     * Sets hora recolecion.
     *
     * @param horaRecolecion la hora recolecion
     */
    public void setHoraRecolecion(String horaRecolecion) {
        this.horaRecolecion = horaRecolecion;
    }

    /**
     * Gets dato.
     *
     * @return el dato
     */
    public String getDato() {
        return dato;
    }

    /**
     * Sets dato.
     *
     * @param dato el dato
     */
    public void setDato(String dato) {
        this.dato = dato;
    }

    /**
     * Gets sensor.
     *
     * @return el sensor
     */
    public Sensor getSensor() {
        return sensor;
    }

    /**
     * Sets sensor.
     *
     * @param sensor el sensor
     */
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

}
