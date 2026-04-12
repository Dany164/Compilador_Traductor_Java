package com.compilador.ast;

import java.util.ArrayList;
import java.util.List;

public class NodoAST {
    private String        tipo;
    private String        valor;
    private List<NodoAST> hijos;

    /* Constructor para nodos internos */
    public NodoAST(String tipo, Object... elementos) {
        this.tipo  = tipo;
        this.hijos = new ArrayList<>();
        for (Object e : elementos) {
            if (e instanceof NodoAST nodo) {
                hijos.add(nodo);
            } else if (e instanceof String s) {
                if (s != null && !s.isEmpty()) {
                    hijos.add(new NodoAST("HOJA", s));
                }
            } else if (e != null) {
                hijos.add(new NodoAST("HOJA", e.toString()));
            }
        }
    }

    /* Constructor hoja */
    public NodoAST(String tipo, String valor) {
        this.tipo  = tipo;
        this.valor = valor != null ? valor : "";
        this.hijos = new ArrayList<>();
    }

    public String        getTipo()  { return tipo; }
    public String        getValor() { return valor; }
    public List<NodoAST> getHijos() { return hijos; }
    public boolean       esHoja()   { return hijos.isEmpty(); }

    /* ── Serialización JSON para el frontend ── */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"tipo\":\"").append(escapar(tipo)).append("\"");

        if (esHoja() && valor != null && !valor.isEmpty()) {
            sb.append(",\"valor\":\"").append(escapar(valor)).append("\"");
        }

        if (!hijos.isEmpty()) {
            sb.append(",\"hijos\":[");
            for (int i = 0; i < hijos.size(); i++) {
                sb.append(hijos.get(i).toJson());
                if (i < hijos.size() - 1) sb.append(",");
            }
            sb.append("]");
        }

        sb.append("}");
        return sb.toString();
    }

    /* ── Impresión en consola para debug ── */
    public void imprimir(String indent) {
        if (esHoja()) {
            System.out.println(indent + "[" + tipo + "]"
                    + (valor != null ? " = \"" + valor + "\"" : ""));
        } else {
            System.out.println(indent + "<" + tipo + ">");
            for (NodoAST h : hijos) h.imprimir(indent + "  ");
        }
    }

    private String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}