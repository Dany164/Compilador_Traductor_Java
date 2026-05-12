package com.compilador.service;

import com.compilador.ast.NodoAST;
import com.compilador.lexer.Diccionario;
import com.compilador.lexer.Lexer;
import com.compilador.lexer.Token;
import com.compilador.model.ErrorCompilador;
import com.compilador.model.ResultadoAnalisis;
import com.compilador.model.TablaSimbolos;
import com.compilador.parser.Parser;
import com.compilador.semantic.AnalizadorSemantico;
import com.compilador.translator.Traductor;
import com.compilador.translator.CloudTranslatorAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CompiladorService {

    @Autowired
    private CloudTranslatorAPI cloudTranslatorAPI;

    // ════════════════════════════════════════════════════════
    // Método auxiliar: crea un Reader UTF-8 desde un String.
    // Garantiza que JFlex reciba siempre un flujo UTF-8 limpio,
    // independientemente de cómo llegó el String al servicio.
    // ════════════════════════════════════════════════════════
    private Reader toUtf8Reader(String texto) {
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        return new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8);
    }

    public ResultadoAnalisis analizar(String texto, boolean usarIA) {

        List<Token> todosTokens        = new ArrayList<>();
        List<ErrorCompilador> todosErrores = new ArrayList<>();
        StringBuilder traduccionFinal  = new StringBuilder();
        boolean resultado_usaIA        = false;
        String astJson                 = null;

        String[] oraciones = texto.split("(?<=[.!?])\\s*");
        if (oraciones.length == 0 ||
                (oraciones.length == 1 && !texto.matches(".*[.!?].*"))) {
            oraciones = new String[]{ texto.trim() };
        }

        for (int idx = 0; idx < oraciones.length; idx++) {
            String oracion = oraciones[idx].trim();
            if (oracion.isEmpty()) continue;

            // Agregar punto final si falta
            char ultimo = oracion.charAt(oracion.length() - 1);
            if (ultimo != '.' && ultimo != '?' && ultimo != '!' && ultimo != ';') {
                oracion = oracion + ".";
                System.out.println("   [AUTO] Punto agregado: \"" + oracion + "\"");
            }

            System.out.println("\n── Oración " + (idx + 1) + ": \"" + oracion + "\"");

            List<Token> tokOracion             = new ArrayList<>();
            List<ErrorCompilador> errOracion   = new ArrayList<>();

            // ════════════════════════════════════════
            // FASE 1 — LÉXICO
            // Usamos toUtf8Reader() para garantizar que JFlex
            // reciba el texto como UTF-8 en todos los casos,
            // incluyendo palabras con tildes (á é í ó ú ñ ü).
            // ════════════════════════════════════════
            Lexer lexer = new Lexer(toUtf8Reader(oracion));

            try {
                java_cup.runtime.Symbol sym;
                while ((sym = lexer.next_token()).sym != com.compilador.parser.sym.EOF) {
                    if (sym.value instanceof Token t) {
                        tokOracion.add(t);
                    }
                }
            } catch (java.io.IOException e) {
                errOracion.add(new ErrorCompilador(
                        ErrorCompilador.TipoError.LEXICO, idx + 1, 0,
                        "Error de E/S en oración " + (idx + 1) + ": " + e.getMessage()));
            } catch (Exception e) {
                errOracion.add(new ErrorCompilador(
                        ErrorCompilador.TipoError.LEXICO, idx + 1, 0,
                        "Error inesperado en léxico (Oración " + (idx + 1) + "): " + e.getMessage()));
            }

            errOracion.addAll(lexer.erroresLexicos);

            System.out.println("   Léxico: " + tokOracion.size()
                    + " tokens, " + lexer.erroresLexicos.size() + " errores");

            // ─────────────────────────────────────────────
            // TABLA DE TOKENS
            // ─────────────────────────────────────────────
            if (!tokOracion.isEmpty()) {
                System.out.println("\n   ╔════════════════════════════════════════════════════════════════════╗");
                System.out.println("   ║ TABLA DE TOKENS CON TRADUCCIONES                                   ║");
                System.out.println("   ╠════╦═══════════════╦═══════════════════════╦════════════════════╣");
                System.out.println("   ║ #  ║ PALABRA       ║ TIPO                  ║ TRADUCCIÓN         ║");
                System.out.println("   ╠════╬═══════════════╬═══════════════════════╬════════════════════╣");
                int num = 1;
                for (Token t : tokOracion) {
                    String palabra    = String.format("%-13s", t.getValor());
                    String tipo       = String.format("%-21s", t.getTipo());
                    String traduccion = String.format("%-18s", t.getTraduccion());
                    System.out.println(String.format("   ║ %-2d ║ %s ║ %s ║ %s ║",
                            num++, palabra, tipo, traduccion));
                }
                System.out.println("   ╚════╩═══════════════╩═══════════════════════╩════════════════════╝\n");
            }

            // ════════════════════════════════════════
            // FASE 2 — SINTÁCTICO
            // También usa toUtf8Reader() para coherencia.
            // StringReader opera sobre char[] internos de Java
            // (UTF-16), lo que puede propagar codificaciones
            // incorrectas si el String llegó mal al servicio.
            // ════════════════════════════════════════
            Lexer lexer2 = new Lexer(toUtf8Reader(oracion));
            Parser parser = new Parser(lexer2);
            try {
                parser.parse();
            } catch (Exception e) {
                parser.erroresSintacticos.add("Error sintáctico: " + e.getMessage());
            }
            for (String msg : parser.erroresSintacticos) {
                errOracion.add(new ErrorCompilador(
                        ErrorCompilador.TipoError.SINTACTICO, idx + 1, 0,
                        "Oración " + (idx + 1) + ": " + msg));
            }
            System.out.println("   Sintáctico: " + parser.erroresSintacticos.size() + " errores");

            // ════════════════════════════════════════
            // FASE 3 — SEMÁNTICO
            // ════════════════════════════════════════
            AnalizadorSemantico sem = new AnalizadorSemantico();
            List<ErrorCompilador> errSem = sem.analizar(tokOracion);
            errOracion.addAll(errSem);
            System.out.println("   Semántico: " + errSem.size() + " errores");

            // ════════════════════════════════════════
            // FASE 4 — TRADUCCIÓN
            // Bug #3: Tolerancia de errores sintácticos
            // Si hay ≤ N errores sintácticos PERO 0 léxicos y 0 semánticos, permitir traducción
            // ════════════════════════════════════════
            int erroresLexicos = (int) errOracion.stream()
                    .filter(e -> e.getTipo() == ErrorCompilador.TipoError.LEXICO)
                    .count();
            int erroresSemanticos = (int) errOracion.stream()
                    .filter(e -> e.getTipo() == ErrorCompilador.TipoError.SEMANTICO)
                    .count();
            
            boolean puedeTraducir = (erroresLexicos == 0 && erroresSemanticos == 0);
            
            if (puedeTraducir) {
                String trad = new Traductor().traducir(tokOracion);
                boolean esTradLocal = trad != null && !trad.isBlank() && !trad.startsWith("[");

                System.out.println("   [DEBUG] trad local: '" + trad + "'");
                // Si el usuario activó la IA, le damos prioridad sobre la local
                if (usarIA) {
                    System.out.println("   [IA] Forzando uso de IA por petición del usuario...");
                    String tradIA = intentarTraduccionIA(oracion, tokOracion);
                    if (tradIA != null) {
                        trad = tradIA;
                        resultado_usaIA = true;
                        esTradLocal = false; // La IA tomó el control
                    }
                } else if (!esTradLocal && usarIA) {
                    // Fallback (redundante ahora, pero seguro)
                    String tradIA = intentarTraduccionIA(oracion, tokOracion);
                    if (tradIA != null) {
                        trad = tradIA;
                        resultado_usaIA = true;
                        esTradLocal = false;
                    }
                }

                if (traduccionFinal.length() > 0) traduccionFinal.append(" ");
                traduccionFinal.append(trad != null ? trad : oracion);
                System.out.println("    Traducción: " + trad);

                if (astJson == null) {
                    astJson = construirAst(tokOracion);
                    System.out.println("    AST generado");
                }
                
                // Si llegamos aquí con una traducción válida (local o IA), limpiamos los errores sintácticos 
                // para que el frontend no lo marque como "exitoso = false" y oculte la traducción.
                if (esTradLocal || resultado_usaIA) {
                    errOracion.removeIf(e -> e.getTipo() == ErrorCompilador.TipoError.SINTACTICO);
                }

            } else if (usarIA) {
                System.out.println("   [IA] Oración con errores, intentando IA...");
                String tradIA = intentarTraduccionIA(oracion, tokOracion);

                if (traduccionFinal.length() > 0) traduccionFinal.append(" ");

                if (tradIA != null) {
                    traduccionFinal.append(tradIA);
                    resultado_usaIA = true;
                    System.out.println("    Traducción IA exitosa: " + tradIA);
                    // Si la IA lo salvó, limpiamos errores para que sea exitoso
                    errOracion.clear();
                } else {
                    traduccionFinal.append("[Error en oración ").append(idx + 1).append("]");
                    System.out.println("    IA también falló");
                }

            } else {
                if (traduccionFinal.length() > 0) traduccionFinal.append(" ");
                traduccionFinal.append("[Error en oración ").append(idx + 1).append("]");
                System.out.println("    " + errOracion.size() + " errores, IA desactivada");
            }

            todosTokens.addAll(tokOracion);
            todosErrores.addAll(errOracion);
        }

        boolean exitoso  = todosErrores.isEmpty();
        String tradFinal = exitoso ? traduccionFinal.toString() : null;


        List<TablaSimbolos> tablaSimbolos = generarTablaSimbolos(todosTokens);

        System.out.println("\n══ RESPUESTA ══");
        System.out.println("exitoso:    " + exitoso);
        System.out.println("usoIA:      " + resultado_usaIA);
        System.out.println("traduccion: " + tradFinal);

        ResultadoAnalisis resultado = new ResultadoAnalisis(
                todosTokens, tablaSimbolos, todosErrores,
                tradFinal, astJson, exitoso);
        resultado.setUsoIA(resultado_usaIA);
        return resultado;
    }

    // ════════════════════════════════════════
    // Helper: detecta idioma y llama a Cloud Translator API
    // ════════════════════════════════════════
    private String intentarTraduccionIA(String oracion, List<Token> tokOracion) {
        try {
            String idioma = detectarIdioma(tokOracion, oracion);
            String desde  = idioma.equals("en") ? "en" : "es";
            String hacia  = idioma.equals("en") ? "es" : "en";

            System.out.println("   [Cloud API] idiomaDetectado=" + idioma);
            System.out.println("   [Cloud API] desde=" + desde + " hacia=" + hacia);

            return cloudTranslatorAPI.traducir(oracion, desde, hacia);
        } catch (Exception e) {
            return null;
        }
    }

    // ════════════════════════════════════════
    // Detector de idioma por heurística de tokens
    // ════════════════════════════════════════
    private String detectarIdioma(List<Token> tokens, String texto) {
        int ingles  = 0;
        int espanol = 0;

        for (Token t : tokens) {
            String palabra = t.getValor().toLowerCase();

            if (Set.of("the","this","that","is","are","was","were","be",
                            "have","has","do","to","of","in","on","by")
                    .contains(palabra)) ingles++;

            if (Set.of("el","la","los","las","un","una","es","son",
                            "ser","estar","de","en","por","para")
                    .contains(palabra)) espanol++;
        }

        if (ingles == 0 && espanol == 0) {
            return texto.matches(".*[áéíóúñÁÉÍÓÚÑ].*") ? "es" : "en";
        }

        return ingles >= espanol ? "en" : "es";
    }

    private boolean esTokenIngles(String valor) {
        return Set.of("the","a","an","is","are","was","were","i","he","she",
                        "we","they","you","it","have","has","do","does")
                .contains(valor.toLowerCase());
    }

    // ════════════════════════════════════════
    // Construcción del AST
    // ════════════════════════════════════════
    private String construirAst(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) return null;

        List<Token> palabras = tokens.stream()
                .filter(t -> !esPuntuacion(t.getTipo()))
                .toList();

        if (palabras.isEmpty()) return null;

        int posVerbo = -1;
        for (int i = 0; i < palabras.size(); i++) {
            if (palabras.get(i).getTipo().equals("VERBO")) {
                posVerbo = i;
                break;
            }
        }

        if (posVerbo == -1) {
            NodoAST raiz = new NodoAST("ORACION");
            for (Token t : palabras)
                raiz.getHijos().add(new NodoAST(t.getTipo(), t.getValor()));
            return raiz.toJson();
        }

        NodoAST sujeto    = construirFraseNominal("SUJETO", palabras.subList(0, posVerbo));
        NodoAST verboNodo = new NodoAST("VERBO", palabras.get(posVerbo).getValor());
        List<Token> resto = new ArrayList<>(palabras.subList(posVerbo + 1, palabras.size()));

        NodoAST predicado;
        if (resto.isEmpty()) {
            predicado = new NodoAST("PREDICADO", verboNodo);
        } else {
            int posPrep = -1;
            for (int i = 0; i < resto.size(); i++) {
                if (resto.get(i).getTipo().equals("PREPOSICION")) { posPrep = i; break; }
            }
            if (posPrep == -1) {
                predicado = new NodoAST("PREDICADO", verboNodo,
                        construirFraseNominal("OBJETO_DIRECTO", resto));
            } else if (posPrep == 0) {
                predicado = new NodoAST("PREDICADO", verboNodo, construirComplemento(resto));
            } else {
                predicado = new NodoAST("PREDICADO", verboNodo,
                        construirFraseNominal("OBJETO_DIRECTO", resto.subList(0, posPrep)),
                        construirComplemento(resto.subList(posPrep, resto.size())));
            }
        }

        return new NodoAST("ORACION", sujeto, predicado).toJson();
    }

    private NodoAST construirFraseNominal(String etiqueta, List<Token> tokens) {
        if (tokens.isEmpty()) return new NodoAST(etiqueta, "");
        if (tokens.size() == 1) {
            Token t = tokens.get(0);
            return new NodoAST(etiqueta, new NodoAST(t.getTipo(), t.getValor()));
        }
        NodoAST frase = new NodoAST("FRASE_NOMINAL");
        for (Token t : tokens)
            frase.getHijos().add(new NodoAST(t.getTipo(), t.getValor()));
        return new NodoAST(etiqueta, frase);
    }

    private NodoAST construirComplemento(List<Token> tokens) {
        if (tokens.isEmpty()) return new NodoAST("COMPLEMENTO", "");
        NodoAST comp = new NodoAST("COMPLEMENTO");
        for (Token t : tokens)
            comp.getHijos().add(new NodoAST(t.getTipo(), t.getValor()));
        return comp;
    }

    private boolean esPuntuacion(String tipo) {
        return tipo.equals("PUNTO") || tipo.equals("COMA")
                || tipo.equals("INTERROGACION") || tipo.equals("EXCLAMACION");
    }

    // ════════════════════════════════════════
    // Tabla de símbolos
    // ════════════════════════════════════════
    private List<TablaSimbolos> generarTablaSimbolos(List<Token> tokens) {
        List<TablaSimbolos> tabla = new ArrayList<>();
        int numero = 1;
        for (Token t : tokens) {
            String tipoCompleto = Diccionario.clasificar(t.getValor().toLowerCase());
            String categoria    = obtenerCategoria(tipoCompleto);
            tabla.add(new TablaSimbolos(
                    numero++, t.getValor(), categoria, tipoCompleto,
                    t.getTraduccion(), t.getLinea(), t.getColumna()));
        }
        return tabla;
    }

    private String obtenerCategoria(String tipo) {
        if (tipo.startsWith("PRONOMBRE"))   return "PRONOMBRE";
        if (tipo.startsWith("VERBO"))       return "VERBO";
        if (tipo.startsWith("SUSTANTIVO"))  return "SUSTANTIVO";
        if (tipo.startsWith("ADJETIVO"))    return "ADJETIVO";
        if (tipo.startsWith("ADVERBIO"))    return "ADVERBIO";
        if (tipo.startsWith("ARTICULO"))    return "ARTICULO";
        if (tipo.startsWith("CONJUNCION"))  return "CONJUNCION";
        if (tipo.startsWith("PREPOSICION")) return "PREPOSICION";
        if (tipo.startsWith("NUMERAL"))     return "NUMERAL";
        if (tipo.equals("POSESIVO"))        return "DETERMINANTE";
        if (tipo.equals("DEMOSTRATIVO"))    return "DETERMINANTE";
        if (tipo.equals("CONTRACCION"))     return "CONTRACCION";
        if (tipo.equals("INTERJECCION"))    return "INTERJECCION";
        return "OTRO";
    }
}
