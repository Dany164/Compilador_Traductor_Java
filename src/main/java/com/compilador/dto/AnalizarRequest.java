package com.compilador.dto;

public class AnalizarRequest {

    private String texto;
    private boolean usarIA;

    public AnalizarRequest() {}

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isUsarIA() {
        return usarIA;
    }

    public void setUsarIA(boolean usarIA) {
        this.usarIA = usarIA;
    }
}