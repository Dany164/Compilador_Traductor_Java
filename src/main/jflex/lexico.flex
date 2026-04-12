package com.compilador.lexer;

import com.compilador.parser.sym;
import com.compilador.model.ErrorCompilador;
import java.util.ArrayList;
import java.util.List;

%%

%class Lexer
%unicode
%line
%column
%cup
%public

%{
    public List<ErrorCompilador> erroresLexicos = new ArrayList<>();

    private java_cup.runtime.Symbol puntuacion(int tipo, String val) {
        Token t = new Token(val, nombreTipo(tipo), val,
                            yyline + 1, yycolumn + 1);
        return new java_cup.runtime.Symbol(
            tipo, yyline + 1, yycolumn + 1, t);
    }

    private String nombreTipo(int tipo) {
        if (tipo == sym.PUNTO)         return "PUNTO";
        if (tipo == sym.COMA)          return "COMA";
        if (tipo == sym.INTERROGACION) return "INTERROGACION";
        if (tipo == sym.EXCLAMACION)   return "EXCLAMACION";
        return "PUNTUACION";
    }
%}

/* ════════════════════════════════
   DEFINICIONES
   ════════════════════════════════ */
LETRA     = [:letter:]
PALABRA   = {LETRA}+
DIGITO    = [0-9]
ESPACIO   = [ \t\r\n]+
APOSTROFE = {LETRA}+ "'" {LETRA}+
NUMERO    = {DIGITO}+

%%

/* ── Espacios: ignorar ── */
{ESPACIO}    { /* ignorar */ }

/* ── Signos de puntuación ── */
"."          { return puntuacion(sym.PUNTO,         yytext()); }
","          { return puntuacion(sym.COMA,          yytext()); }
"?"          { return puntuacion(sym.INTERROGACION, yytext()); }
"!"          { return puntuacion(sym.EXCLAMACION,   yytext()); }
";"          { return puntuacion(sym.PUNTO,         yytext()); }

/* ── Contracciones: i'm, don't, i've, etc. ── */
{APOSTROFE}  {
                 String palabra = yytext().toLowerCase();
                 String tipo    = Diccionario.clasificar(palabra);
                 String trad    = Diccionario.traducir(palabra);
                 Token t = new Token(yytext(), "CONTRACCION", trad,
                                     yyline + 1, yycolumn + 1);
                 return new java_cup.runtime.Symbol(
                     sym.CONTRACCION, yyline + 1, yycolumn + 1, t);
             }

/* ── Números: tratados como ARTICULO (determinante numérico) ── */
{NUMERO}     {
                 Token t = new Token(yytext(), "NUMERAL_CARDINAL",
                     yytext(), yyline + 1, yycolumn + 1);
                 return new java_cup.runtime.Symbol(
                     sym.ARTICULO, yyline + 1, yycolumn + 1, t);
             }

/* ════════════════════════════════
   PALABRAS — clasificar por diccionario
   {PALABRA} ya captura letras con tilde gracias a %unicode + [:letter:]
   Si es DESCONOCIDO → SUSTANTIVO por defecto
   ════════════════════════════════ */
{PALABRA}    {
                 String palabra = yytext().toLowerCase();
                 String tipo    = Diccionario.clasificar(palabra);
                 String trad    = Diccionario.traducir(palabra);
                 int    simbol  = Diccionario.simbolo(tipo);

                 if ("DESCONOCIDO".equals(tipo)) {
                     tipo   = "SUSTANTIVO";
                     simbol = sym.SUSTANTIVO;
                     trad   = yytext();
                 }

                 Token t = new Token(yytext(), tipo, trad,
                                     yyline + 1, yycolumn + 1);
                 return new java_cup.runtime.Symbol(
                     simbol, yyline + 1, yycolumn + 1, t);
             }

/* ── Cualquier otro carácter no reconocido: ERROR LÉXICO ── */
[^ \t\r\n]   {
                 erroresLexicos.add(new ErrorCompilador(
                     ErrorCompilador.TipoError.LEXICO,
                     yyline + 1, yycolumn + 1,
                     "Caracter no reconocido: '" + yytext() + "'"
                 ));
             }
