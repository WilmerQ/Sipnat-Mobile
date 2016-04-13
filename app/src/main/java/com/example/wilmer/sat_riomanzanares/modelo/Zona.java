/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.wilmer.sat_riomanzanares.modelo;

import java.util.List;

/**
 * @author Windows 8.1
 */
public class Zona {

    private String nombre;
    private List<DetalleZona> detalleZonas;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<DetalleZona> getDetalleZonas() {
        return detalleZonas;
    }

    public void setDetalleZonas(List<DetalleZona> detalleZonas) {
        this.detalleZonas = detalleZonas;
    }


}
