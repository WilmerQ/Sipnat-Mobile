/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.wilmer.sat_riomanzanares.modelo;

import java.io.Serializable;

/**
 * clase Usuario
 * </p>
 * contiene la informacion del objeto usuario.
 * </p>
 *
 * @author Windows 8.1
 * @see java.io.Serializable
 */
public class Usuario implements Serializable {

    private Long id;

    /**
     * nombre del usuario
     */
    private String nombreUsuario;
    /**
     * clave
     */
    private String clave;
    /**
     * email
     */
    private String email;
    /**
     * telefono
     */
    private String telefono;

    /**
     * informe de error: usuado para la verificacion de operacion con el servidor.
     */
    private Integer informeDeError;

    /**
     * Gets nombre usuario.
     *
     * @return the nombre usuario
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Sets nombre usuario.
     *
     * @param nombreUsuario the nombre usuario
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Gets clave.
     *
     * @return la clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * Sets clave.
     *
     * @param clave the clave
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets telefono.
     *
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Sets telefono.
     *
     * @param telefono the telefono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Gets informe de error.
     *
     * @return el informe de error
     */
    public Integer getInformeDeError() {
        return informeDeError;
    }

    /**
     * Sets informe de error.
     *
     * @param informeDeError el informe de error
     */
    public void setInformeDeError(Integer informeDeError) {
        this.informeDeError = informeDeError;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

