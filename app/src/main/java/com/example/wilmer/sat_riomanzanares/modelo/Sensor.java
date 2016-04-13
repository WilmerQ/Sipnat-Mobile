/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.wilmer.sat_riomanzanares.modelo;

import java.io.Serializable;

/**
 * clase Sensor
 * </p>
 * contiene la informacion del objeto Sensor.
 * Sensor: Dispositivo de medicion que trasmite informacion de variables ambientales.
 * @author Wilmer
 * @see java.io.Serializable
 */
public class Sensor implements Serializable{

    /**
     * id del sensor
     */
    private Long id;
    /**
     * atributo de la ubicacion del sensor: latitud
     */
    private String latitud;
    /**
     * atributo de la ubicacion del sensor: longitud
     */
    private String longitud;
    /**
     * nombre de la categoria a la cual pertenece el sensor
     */
    private String nombreTipoSensor;
    /**
     * id de la categoria del sensor
     */
    private Long idTipoSensor;

    /**
     * Gets id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id el id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets latitud.
     *
     * @return la latitud
     */
    public String getLatitud() {
        return latitud;
    }

    /**
     * Sets latitud.
     *
     * @param latitud la latitud
     */
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    /**
     * Gets longitud.
     *
     * @return la longitud
     */
    public String getLongitud() {
        return longitud;
    }

    /**
     * Sets longitud.
     *
     * @param longitud la longitud
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    /**
     * Gets id tipo sensor.
     *
     * @return el id tipo sensor
     */
    public Long getIdTipoSensor() {
        return idTipoSensor;
    }

    /**
     * Sets id tipo sensor.
     *
     * @param idTipoSensor el id tipo sensor
     */
    public void setIdTipoSensor(Long idTipoSensor) {
        this.idTipoSensor = idTipoSensor;
    }

    /**
     * Gets nombre tipo sensor.
     *
     * @return el nombre tipo sensor
     */
    public String getNombreTipoSensor() {
        return nombreTipoSensor;
    }

    /**
     * Sets nombre tipo sensor.
     *
     * @param nombreTipoSensor el nombre tipo sensor
     */
    public void setNombreTipoSensor(String nombreTipoSensor) {
        this.nombreTipoSensor = nombreTipoSensor;
    }
}
