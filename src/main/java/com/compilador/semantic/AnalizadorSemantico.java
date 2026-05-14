package com.compilador.semantic;

import com.compilador.lexer.Token;
import com.compilador.model.ErrorCompilador;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AnalizadorSemantico {

    // Grupos de tipos compatibles
    private static final Set<String> TIPOS_PRONOMBRE = Set.of(
            "PRONOMBRE", "PRONOMBRE_PERSONAL",
            "PRONOMBRE_DEMOSTRATIVO", "PRONOMBRE_INTERROGATIVO");
    private static final Set<String> TIPOS_ARTICULO = Set.of(
            "ARTICULO", "ARTICULO_DEFINIDO", "ARTICULO_INDEFINIDO",
            "POSESIVO", "DEMOSTRATIVO",
            "NUMERAL_CARDINAL", "NUMERAL_ORDINAL");
    private static final Set<String> TIPOS_ADJETIVO = Set.of(
            "ADJETIVO", "ADJETIVO_CALIFICATIVO");
    private static final Set<String> TIPOS_ADVERBIO = Set.of(
            "ADVERBIO", "ADVERBIO_TIEMPO", "ADVERBIO_LUGAR",
            "ADVERBIO_MODO", "ADVERBIO_CANTIDAD",
            "ADVERBIO_AFIRMACION", "ADVERBIO_NEGACION", "ADVERBIO_DUDA");
    private static final Set<String> TIPOS_CONJUNCION = Set.of(
            "CONJUNCION", "CONJUNCION_COPULATIVA", "CONJUNCION_ADVERSATIVA",
            "CONJUNCION_DISYUNTIVA", "CONJUNCION_EXPLICATIVA",
            "CONJUNCION_CAUSAL", "CONJUNCION_CONDICIONAL",
            "CONJUNCION_CONCESIVA", "CONJUNCION_COMPARATIVA",
            "CONJUNCION_FINAL", "CONJUNCION_CONSECUTIVA",
            "CONJUNCION_SUSTANTIVA");
    private static final Set<String> TIPOS_EXPRESION = Set.of(
            "EXPRESION", "MODISMO", "ABREVIATURA");
    private static final Set<String> PUEDE_SER_SUJETO = Set.of(
            "PRONOMBRE", "PRONOMBRE_PERSONAL",
            "PRONOMBRE_DEMOSTRATIVO", "PRONOMBRE_INTERROGATIVO",
            "SUSTANTIVO", "CONTRACCION", "EXPRESION", "MODISMO", "ABREVIATURA");
    private static final Set<String> PUEDE_INICIAR_ORACION = Set.of(
            "PRONOMBRE", "PRONOMBRE_PERSONAL", "PRONOMBRE_DEMOSTRATIVO",
            "PRONOMBRE_INTERROGATIVO", "SUSTANTIVO", "CONTRACCION",
            "ARTICULO", "ARTICULO_DEFINIDO", "ARTICULO_INDEFINIDO",
            "POSESIVO", "DEMOSTRATIVO", "NUMERAL_CARDINAL", "NUMERAL_ORDINAL",
            "ADVERBIO", "ADVERBIO_TIEMPO", "ADVERBIO_LUGAR", "ADVERBIO_MODO",
            "ADVERBIO_CANTIDAD", "ADVERBIO_AFIRMACION", "ADVERBIO_NEGACION", "ADVERBIO_DUDA",
            "INTERJECCION", "EXPRESION", "MODISMO", "ABREVIATURA");
    private static final Set<String> PUEDE_SER_OBJETO = Set.of(
            "SUSTANTIVO", "PRONOMBRE", "PRONOMBRE_PERSONAL",
            "PRONOMBRE_DEMOSTRATIVO", "EXPRESION", "MODISMO", "ABREVIATURA");
    private static final Set<String> TIPOS_PUNTUACION = Set.of(
            "PUNTO", "COMA", "INTERROGACION", "EXCLAMACION");

    private List<ErrorCompilador> errores;

    public List<ErrorCompilador> analizar(List<Token> tokens) {
        errores = new ArrayList<>();

        if (tokens == null || tokens.isEmpty()) {
            agregar(1, 1, "La oración está vacía");
            return errores;
        }

        // Filtrar puntuación para el análisis
        List<Token> palabras = tokens.stream()
                .filter(t -> !TIPOS_PUNTUACION.contains(t.getTipo()))
                .toList();

        if (palabras.isEmpty()) {
            agregar(1, 1, "La oración solo contiene signos de puntuación");
            return errores;
        }

        regla1_PrimerTokenEsSujeto(palabras);
        regla2_ExisteVerbo(palabras);
        regla3_OrdenSujetoVerbo(palabras);
        regla5_NoDobleVerboSinConjuncion(palabras);
        regla6_NoDobleSujeto(palabras);
        regla7_PreposicionSeguida(palabras);
        regla8_TokensDesconocidos(palabras);
        regla9_ArticuloSeguido(palabras);
        regla10_ConcordanciaSujetoVerbo(palabras);

        return errores;
    }

    // REGLA 1 — Primer token debe ser sujeto o artículo (estructura SVO o
    // estructura ART+N)
    private void regla1_PrimerTokenEsSujeto(List<Token> palabras) {
        Token primero = palabras.get(0);
        boolean esValido = PUEDE_INICIAR_ORACION.contains(primero.getTipo());
        if (!esValido) {
            agregarToken(primero,
                    "Inicio de oración no permitido para esta gramática, " +
                            "se encontró: " + primero.getTipo() +
                            " '" + primero.getValor() + "'");
        }
    }

    // REGLA 2 — Debe existir al menos un verbo
    private void regla2_ExisteVerbo(List<Token> palabras) {
        // Expresiones cortas como palabras sueltas o frases nominales no requieren verbo.
        if (esExpresionCortaSinVerboValida(palabras)) return;

        boolean tieneVerbo = palabras.stream()
                .anyMatch(t -> t.getTipo().equals("VERBO"));
        if (!tieneVerbo) {
            agregar(1, 1,
                    "La oración no contiene un verbo principal");
        }
    }

    private boolean esExpresionCortaSinVerboValida(List<Token> palabras) {
        if (palabras.size() > 3) {
            return false;
        }
        for (Token t : palabras) {
            if (!(TIPOS_PRONOMBRE.contains(t.getTipo())
                    || TIPOS_ARTICULO.contains(t.getTipo())
                    || TIPOS_ADJETIVO.contains(t.getTipo())
                    || TIPOS_ADVERBIO.contains(t.getTipo())
                    || TIPOS_CONJUNCION.contains(t.getTipo())
                    || TIPOS_EXPRESION.contains(t.getTipo())
                    || t.getTipo().equals("SUSTANTIVO")
                    || t.getTipo().equals("INTERJECCION")
                    || t.getTipo().equals("NUMERAL_CARDINAL")
                    || t.getTipo().equals("NUMERAL_ORDINAL"))) {
                return false;
            }
        }
        return true;
    }

    // REGLA 3 — Verbo después del sujeto
    private void regla3_OrdenSujetoVerbo(List<Token> palabras) {
        int posVerbo = -1;
        int posSujeto = -1;
        for (int i = 0; i < palabras.size(); i++) {
            String tipo = palabras.get(i).getTipo();
            if (PUEDE_SER_SUJETO.contains(tipo) && posSujeto == -1)
                posSujeto = i;
            if (tipo.equals("VERBO") && posVerbo == -1)
                posVerbo = i;
        }
        if (posVerbo != -1 && posSujeto != -1 && posVerbo < posSujeto) {
            agregarToken(palabras.get(posVerbo),
                    "Orden incorrecto: el verbo '" +
                            palabras.get(posVerbo).getValor() +
                            "' aparece antes del sujeto");
        }
    }

    // REGLA 5 — No dos verbos consecutivos EXCEPTO verbo auxiliar +
    // participio/gerundio
    private void regla5_NoDobleVerboSinConjuncion(List<Token> palabras) {
        Set<String> auxiliares = Set.of(
                "have", "has", "had", "is", "am", "are", "was", "were", "be", "been",
                "do", "does", "did", "can", "could", "will", "would", "should", "may", "might",
                "ha", "hemos", "han", "habia", "había", "estoy", "esta", "está", "estan", "están");
        Set<String> participiosFrecuentes = Set.of(
                "found", "been", "played", "written", "seen", "done", "made", "gone",
                "taken", "called", "asked", "worked", "tried", "felt", "left", "kept",
                "begun", "shown", "heard", "told",
                "comido", "bebido", "escrito", "visto", "hecho", "dicho", "encontrado");

        for (int i = 0; i < palabras.size() - 1; i++) {
            Token act = palabras.get(i);
            Token sig = palabras.get(i + 1);
            if (act.getTipo().equals("VERBO") && sig.getTipo().equals("VERBO")) {
                String v1 = act.getValor().toLowerCase();
                String v2 = sig.getValor().toLowerCase();

                boolean esAuxiliar = auxiliares.contains(v1);
                boolean esParticipioOGerundio = participiosFrecuentes.contains(v2)
                        || v2.endsWith("ing")
                        || v2.endsWith("ed")
                        || v2.endsWith("ado")
                        || v2.endsWith("ido");

                if (!(esAuxiliar && esParticipioOGerundio)) {
                    agregarToken(sig,
                            "Dos verbos consecutivos sin conjunción: '" +
                                    act.getValor() + "' y '" + sig.getValor() + "'");
                }
            }
        }
    }

    // REGLA 6 — No dos pronombres consecutivos
    private void regla6_NoDobleSujeto(List<Token> palabras) {
        for (int i = 0; i < palabras.size() - 1; i++) {
            Token act = palabras.get(i);
            Token sig = palabras.get(i + 1);
            if (TIPOS_PRONOMBRE.contains(act.getTipo())
                    && TIPOS_PRONOMBRE.contains(sig.getTipo())) {
                agregarToken(sig,
                        "Dos pronombres consecutivos: '" +
                                act.getValor() + "' y '" + sig.getValor() + "'");
            }
        }
    }

    // REGLA 7 — Preposición debe ir seguida de algo válido (también permite
    // adjetivos)
    private void regla7_PreposicionSeguida(List<Token> palabras) {
        for (int i = 0; i < palabras.size(); i++) {
            Token t = palabras.get(i);
            if (t.getTipo().equals("PREPOSICION")) {
                if (i == palabras.size() - 1) {
                    agregarToken(t,
                            "La preposición '" + t.getValor() +
                                    "' no puede estar al final de la oración");
                } else {
                    Token sig = palabras.get(i + 1);
                    boolean sigValido = PUEDE_SER_OBJETO.contains(sig.getTipo())
                            || TIPOS_ARTICULO.contains(sig.getTipo())
                            || TIPOS_PRONOMBRE.contains(sig.getTipo())
                            || TIPOS_ADJETIVO.contains(sig.getTipo())
                            || sig.getTipo().equals("POSESIVO")
                            || sig.getTipo().equals("CONTRACCION");
                    if (!sigValido) {
                        agregarToken(sig,
                                "Después de la preposición '" + t.getValor() +
                                        "' se esperaba sustantivo, artículo o adjetivo, " +
                                        "se encontró: " + sig.getTipo());
                    }
                }
            }
        }
    }

    // REGLA 8 — Palabras desconocidas
    private void regla8_TokensDesconocidos(List<Token> palabras) {
        for (Token t : palabras) {
            if (t.getTipo().equals("DESCONOCIDO")) {
                agregarToken(t,
                        "Palabra fuera de vocabulario: '" + t.getValor() + "'");
            }
        }
    }

    // REGLA 9 — Artículo debe ir seguido de sustantivo o adjetivo
    private void regla9_ArticuloSeguido(List<Token> palabras) {
        for (int i = 0; i < palabras.size(); i++) {
            Token t = palabras.get(i);
            if (TIPOS_ARTICULO.contains(t.getTipo())) {
                if (i == palabras.size() - 1) {
                    agregarToken(t,
                            "El artículo '" + t.getValor() +
                                    "' no puede estar al final sin un sustantivo");
                } else {
                    Token sig = palabras.get(i + 1);
                    boolean sigValido = sig.getTipo().equals("SUSTANTIVO")
                            || TIPOS_ADJETIVO.contains(sig.getTipo())
                            || TIPOS_PRONOMBRE.contains(sig.getTipo());
                    if (!sigValido) {
                        agregarToken(sig,
                                "Después del artículo '" + t.getValor() +
                                        "' se esperaba sustantivo o adjetivo, " +
                                        "se encontró: " + sig.getTipo() +
                                        " '" + sig.getValor() + "'");
                    }
                }
            }
        }
    }

    // REGLA 10 — Concordancia sujeto-verbo (3ra persona singular EN)
    private void regla10_ConcordanciaSujetoVerbo(List<Token> palabras) {
        String pronombre = null;
        for (Token t : palabras) {
            if (TIPOS_PRONOMBRE.contains(t.getTipo())) {
                pronombre = t.getValor().toLowerCase();
                break;
            }
        }
        if (pronombre == null)
            return;

        Set<String> terceraSingularEN = Set.of("he", "she", "it");
        Set<String> verbosBaseEN = Set.of(
                "eat", "read", "play", "run", "write", "drink", "go",
                "like", "have", "see", "love", "walk", "work", "speak",
                "come", "know", "think", "want", "need", "live",
                "study", "teach", "learn", "buy", "sell", "help",
                "sleep", "wake");

        if (terceraSingularEN.contains(pronombre) && esOracionPosiblementeIngles(palabras)) {
            for (Token t : palabras) {
                if (t.getTipo().equals("VERBO")
                        && verbosBaseEN.contains(t.getValor().toLowerCase())) {
                    agregarToken(t,
                            "Concordancia incorrecta: con '" + pronombre +
                                    "' el verbo debería ser '" +
                                    t.getValor() + "s' en lugar de '" +
                                    t.getValor() + "'");
                }
            }
        }
    }

    private boolean esOracionPosiblementeIngles(List<Token> palabras) {
        Set<String> marcadoresEN = Set.of("the", "a", "an", "in", "on", "at", "with", "from", "to");
        return palabras.stream().map(Token::getValor).map(String::toLowerCase).anyMatch(marcadoresEN::contains);
    }

    // ── Helpers ──────────────────────────────────────
    private void agregarToken(Token t, String desc) {
        errores.add(new ErrorCompilador(
                ErrorCompilador.TipoError.SEMANTICO,
                t.getLinea(), t.getColumna(), desc));
    }

    private void agregar(int linea, int col, String desc) {
        errores.add(new ErrorCompilador(
                ErrorCompilador.TipoError.SEMANTICO, linea, col, desc));
    }

    public boolean sinErrores() {
        return errores.isEmpty();
    }
}
