package com.compilador.model;

import com.compilador.lexer.Token;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ResultadoAnalisis {

    @JsonProperty("tokens")
    private List<Token> tokens;

    @JsonProperty("tablaSimbolos")
    private List<TablaSimbolos> tablaSimbolos;

    @JsonProperty("errores")
    private List<ErrorCompilador> errores;

    @JsonProperty("traduccion")
    private String traduccion;

    @JsonProperty("astJson")
    private String astJson;

    @JsonProperty("exitoso")
    private boolean exitoso;
    private boolean usoIA;

    public ResultadoAnalisis(List<Token> tokens,
                             List<TablaSimbolos> tablaSimbolos, List<ErrorCompilador> errores,
                             String traduccion,
                             String astJson,
                             boolean exitoso) {
        this.tokens        = tokens;
        this.tablaSimbolos = tablaSimbolos;
        this.errores       = errores;
        this.traduccion    = traduccion;
        this.astJson       = astJson;
        this.exitoso       = exitoso;
    }

    public List<Token>           getTokens()        { return tokens; }
    public List<TablaSimbolos>   getTablaSimbolos()  { return tablaSimbolos; }
    public List<ErrorCompilador> getErrores()        { return errores; }
    public String                getTraduccion()     { return traduccion; }
    public String                getAstJson()        { return astJson; }
    public boolean               isExitoso()         { return exitoso; }
    public boolean isUsoIA() {
        return usoIA;
    }

    public void setUsoIA(boolean usoIA) {
        this.usoIA = usoIA;
    }
}