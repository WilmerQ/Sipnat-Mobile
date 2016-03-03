/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.wilmer.sat_riomanzanares.modelo;

import java.io.Serializable;

/**
 * @author Windows 8.1
 */

public class Usuario implements Serializable {

    private String nombreUsuario;
    private String clave;
    private String email;
    private String telefono;

    private Integer informeDeError;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getInformeDeError() {
        return informeDeError;
    }

    public void setInformeDeError(Integer informeDeError) {
        this.informeDeError = informeDeError;
    }
}
