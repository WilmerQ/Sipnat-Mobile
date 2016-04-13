/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.wilmer.sat_riomanzanares.modelo;

import java.io.Serializable;

/**
 * Clase proyecto
 * </p>
 * contiene la informacion del objeto proyecto.
 *
 * @author Wilmer
 * @see java.io.Serializable
 */
public class Proyecto implements Serializable {

    /**
     * id del objeto
     */
    private Long id;
    /**
     * nombre del proyecto
     */
    private String nombre;
    /**
     * descripcion de un proyecto
     */
    private String descripcion;
    /**
     * usuario Creacion del proyecto
     */
    private String usuarioCreacion;

    /**
     * getId retorna el id del objeto
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * setId permite modicar el id del objeto.
     * @param id el id del proyecto
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets nombre
     * @return el nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets nombre.
     * @param nombre el nombre del proyecto
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Gets descripcion.
     * @return la descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Sets descripcion.
     * @param descripcion la descripcion del proyecto
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Gets usuario creacion.
     * @return usuario creacion
     */
    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    /**
     * Sets usuario creacion.
     * @param usuarioCreacion el usuario creacion
     */
    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }
}
