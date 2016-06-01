/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.wilmer.sat_riomanzanares.modelo;

import java.io.Serializable;
import java.util.Date;


/**
 * @author Windows 8.1
 */

public class Alerta implements Serializable {


    private Proyecto proyecto;

    private String Descripcion;

    private String nivel;

    private Date horaDelDisparo;

    private String codigoColor;

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Date getHoraDelDisparo() {
        return horaDelDisparo;
    }

    public void setHoraDelDisparo(Date horaDelDisparo) {
        this.horaDelDisparo = horaDelDisparo;
    }

    public String getCodigoColor() {
        return codigoColor;
    }

    public void setCodigoColor(String codigoColor) {
        this.codigoColor = codigoColor;
    }
}
