package com.example.moluscapp.data;

import java.io.Serializable;

public class TaxonomicRoute implements Serializable {

    public String clase;
    public Float conf_clase;

    public String orden;
    public Float conf_orden;

    public String  familia;
    public Float conf_familia;

    public String genero;
    public Float conf_genero;

    public String especie;
    public Float conf_especie;

    public TaxonomicRoute(String clase, Float conf_clase, String orden, Float conf_orden, String familia, Float conf_familia, String genero, Float conf_genero, String especie, Float conf_especie) {
        this.clase = clase;
        this.conf_clase = conf_clase;
        this.orden = orden;
        this.conf_orden = conf_orden;
        this.familia = familia;
        this.conf_familia = conf_familia;
        this.genero = genero;
        this.conf_genero = conf_genero;
        this.especie = especie;
        this.conf_especie = conf_especie;
    }
}
