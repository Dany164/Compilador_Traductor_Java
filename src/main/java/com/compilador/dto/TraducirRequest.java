package com.compilador.dto;

public class TraducirRequest {

    private String texto;
    private String desde;
    private String hacia;
    private Boolean usarIA;

    public TraducirRequest() {
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHacia() {
        return hacia;
    }

    public void setHacia(String hacia) {
        this.hacia = hacia;
    }

    public boolean isUsarIA() {
        return usarIA == null || usarIA;
    }

    public void setUsarIA(Boolean usarIA) {
        this.usarIA = usarIA;
    }
}
