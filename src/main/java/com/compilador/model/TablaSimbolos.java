package com.compilador.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TablaSimbolos {

    @JsonProperty("numero")
    private int numero;

    @JsonProperty("palabra")
    private String palabra;

    @JsonProperty("categoria")
    private String categoria;

    @JsonProperty("subcategoria")
    private String subcategoria;

    @JsonProperty("traduccion")
    private String traduccion;

    @JsonProperty("linea")
    private int linea;

    @JsonProperty("columna")
    private int columna;

    public TablaSimbolos(int numero, String palabra, String categoria,
                         String subcategoria, String traduccion,
                         int linea, int columna) {
        this.numero       = numero;
        this.palabra      = palabra;
        this.categoria    = categoria;
        this.subcategoria = subcategoria;
        this.traduccion   = traduccion;
        this.linea        = linea;
        this.columna      = columna;
    }

    public int    getNumero()       { return numero; }
    public String getPalabra()      { return palabra; }
    public String getCategoria()    { return categoria; }
    public String getSubcategoria() { return subcategoria; }
    public String getTraduccion()   { return traduccion; }
    public int    getLinea()        { return linea; }
    public int    getColumna()      { return columna; }
}