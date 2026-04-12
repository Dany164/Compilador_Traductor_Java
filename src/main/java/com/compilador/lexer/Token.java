package com.compilador.lexer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

    @JsonProperty("valor")
    private String valor;

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("traduccion")
    private String traduccion;

    @JsonProperty("linea")
    private int linea;

    @JsonProperty("columna")
    private int columna;

    public Token(String valor, String tipo, String traduccion,
                 int linea, int columna) {
        this.valor      = valor;
        this.tipo       = tipo;
        this.traduccion = traduccion;
        this.linea      = linea;
        this.columna    = columna;
    }

    public String getValor()      { return valor; }
    public String getTipo()       { return tipo; }
    public String getTraduccion() { return traduccion; }
    public int    getLinea()      { return linea; }
    public int    getColumna()    { return columna; }
}