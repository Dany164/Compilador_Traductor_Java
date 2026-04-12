package com.compilador.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorCompilador {

    public enum TipoError { LEXICO, SINTACTICO, SEMANTICO }

    @JsonProperty("tipo")
    private TipoError tipo;

    @JsonProperty("linea")
    private int linea;

    @JsonProperty("columna")
    private int columna;

    @JsonProperty("descripcion")
    private String descripcion;

    public ErrorCompilador(TipoError tipo, int linea,
                           int columna, String descripcion) {
        this.tipo        = tipo;
        this.linea       = linea;
        this.columna     = columna;
        this.descripcion = descripcion;
    }

    public TipoError getTipo()        { return tipo; }
    public int       getLinea()       { return linea; }
    public int       getColumna()     { return columna; }
    public String    getDescripcion() { return descripcion; }
}