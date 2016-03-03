package com.example.wilmer.sat_riomanzanares.modelo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wilmer on 2/03/2016.
 */
public class listas implements Serializable {

    List<Proyecto>  proyectoList;
    List<String> stringList;

    public List<Proyecto> getProyectoList() {
        return proyectoList;
    }

    public void setProyectoList(List<Proyecto> proyectoList) {
        this.proyectoList = proyectoList;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
}
