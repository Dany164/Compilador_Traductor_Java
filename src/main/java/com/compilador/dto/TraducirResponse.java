package com.compilador.dto;

import com.compilador.lexer.Diccionario;
import java.util.List;

public class TraducirResponse {

    private boolean exitoso;
    private String textoOriginal;
    private String traduccion;
    private String traduccionDiccionario;
    private String idiomaOrigen;
    private String idiomaDestino;
    private String provider;
    private String mensaje;
    private List<Diccionario.Entrada> diccionario;

    public TraducirResponse() {
    }

    public TraducirResponse(
            boolean exitoso,
            String textoOriginal,
            String traduccion,
            String idiomaOrigen,
            String idiomaDestino,
            String provider,
            String mensaje) {
        this(exitoso, textoOriginal, traduccion, null, idiomaOrigen, idiomaDestino, provider, mensaje, List.of());
    }

    public TraducirResponse(
            boolean exitoso,
            String textoOriginal,
            String traduccion,
            String traduccionDiccionario,
            String idiomaOrigen,
            String idiomaDestino,
            String provider,
            String mensaje,
            List<Diccionario.Entrada> diccionario) {
        this.exitoso = exitoso;
        this.textoOriginal = textoOriginal;
        this.traduccion = traduccion;
        this.traduccionDiccionario = traduccionDiccionario;
        this.idiomaOrigen = idiomaOrigen;
        this.idiomaDestino = idiomaDestino;
        this.provider = provider;
        this.mensaje = mensaje;
        this.diccionario = diccionario;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getTextoOriginal() {
        return textoOriginal;
    }

    public String getTraduccion() {
        return traduccion;
    }

    public String getTraduccionDiccionario() {
        return traduccionDiccionario;
    }

    public String getIdiomaOrigen() {
        return idiomaOrigen;
    }

    public String getIdiomaDestino() {
        return idiomaDestino;
    }

    public String getProvider() {
        return provider;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<Diccionario.Entrada> getDiccionario() {
        return diccionario;
    }
}
