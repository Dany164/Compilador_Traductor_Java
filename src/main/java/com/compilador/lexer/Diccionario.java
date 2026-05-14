package com.compilador.lexer;

import com.compilador.parser.sym;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Diccionario {

    private static final Map<String, String[]> mapa = new HashMap<>();
    private static final Map<String, String> expresionCompactaAOriginal = new HashMap<>();
    private static final Map<String, String> traduccionesForzadasES_EN = new HashMap<>();
    private static final Map<String, String> traduccionesForzadasEN_ES = new HashMap<>();
    private static final Pattern TOKEN_TEXTO = Pattern.compile("\\p{L}+(?:['’]\\p{L}+)?|\\d+|[^\\s]");

    private static final Set<String> MARCADORES_EN = Set.of(
            "the", "a", "an", "in", "on", "at", "with", "from", "to", "and", "or", "but",
            "is", "are", "was", "were", "have", "has", "do", "does", "i", "you", "he", "she", "it", "they", "we");
    private static final Set<String> MARCADORES_ES = Set.of(
            "el", "la", "los", "las", "un", "una", "unos", "unas", "en", "con", "de", "por", "para",
            "y", "o", "pero", "es", "son", "fue", "eran", "yo", "tú", "tu", "él", "ella", "ellos", "nosotros",
            "hola", "gracias", "adiós", "adios");

    public static class Entrada {

        private final String palabra;
        private final String normalizada;
        private final String tipo;
        private final String categoria;
        private final String traduccion;
        private final boolean conocida;
        private final String origen;

        public Entrada(
                String palabra,
                String normalizada,
                String tipo,
                String categoria,
                String traduccion,
                boolean conocida,
                String origen) {
            this.palabra = palabra;
            this.normalizada = normalizada;
            this.tipo = tipo;
            this.categoria = categoria;
            this.traduccion = traduccion;
            this.conocida = conocida;
            this.origen = origen;
        }

        public String getPalabra() {
            return palabra;
        }

        public String getNormalizada() {
            return normalizada;
        }

        public String getTipo() {
            return tipo;
        }

        public String getCategoria() {
            return categoria;
        }

        public String getTraduccion() {
            return traduccion;
        }

        public boolean isConocida() {
            return conocida;
        }

        public String getOrigen() {
            return origen;
        }
    }

    static {
        /*
         * ══════════════════════════════════
         * PRONOMBRES PERSONALES EN
         * ══════════════════════════════════
         */
        add("i", "PRONOMBRE_PERSONAL", "yo");
        add("you", "PRONOMBRE_PERSONAL", "tú");
        add("he", "PRONOMBRE_PERSONAL", "él");
        add("she", "PRONOMBRE_PERSONAL", "ella");
        add("it", "PRONOMBRE_PERSONAL", "eso");
        add("we", "PRONOMBRE_PERSONAL", "nosotros");
        add("they", "PRONOMBRE_PERSONAL", "ellos");
        add("me", "PRONOMBRE_PERSONAL", "me");
        add("him", "PRONOMBRE_PERSONAL", "lo");
        add("her", "PRONOMBRE_PERSONAL", "la");
        add("us", "PRONOMBRE_PERSONAL", "nos");
        add("them", "PRONOMBRE_PERSONAL", "los");
        add("one", "PRONOMBRE_PERSONAL", "uno");

        /* ── PRONOMBRES PERSONALES ES ── */
        add("yo", "PRONOMBRE_PERSONAL", "I");
        add("tú", "PRONOMBRE_PERSONAL", "you");
        add("tu", "PRONOMBRE_PERSONAL", "you");
        add("él", "PRONOMBRE_PERSONAL", "he");
        add("ella", "PRONOMBRE_PERSONAL", "she");
        add("nosotros", "PRONOMBRE_PERSONAL", "we");
        add("nosotras", "PRONOMBRE_PERSONAL", "we");
        add("vosotros", "PRONOMBRE_PERSONAL", "you all");
        add("ellos", "PRONOMBRE_PERSONAL", "they");
        add("ellas", "PRONOMBRE_PERSONAL", "they");
        add("usted", "PRONOMBRE_PERSONAL", "you");
        add("ustedes", "PRONOMBRE_PERSONAL", "you all");

        /*
         * ══════════════════════════════════
         * PRONOMBRES DEMOSTRATIVOS
         * ══════════════════════════════════
         */
        add("este", "PRONOMBRE_DEMOSTRATIVO", "this");
        add("esta", "PRONOMBRE_DEMOSTRATIVO", "this");
        add("esto", "PRONOMBRE_DEMOSTRATIVO", "this");
        add("ese", "PRONOMBRE_DEMOSTRATIVO", "that");
        add("esa", "PRONOMBRE_DEMOSTRATIVO", "that");
        add("eso", "PRONOMBRE_DEMOSTRATIVO", "that");
        add("estos", "PRONOMBRE_DEMOSTRATIVO", "these");
        add("estas", "PRONOMBRE_DEMOSTRATIVO", "these");
        add("esos", "PRONOMBRE_DEMOSTRATIVO", "those");
        add("esas", "PRONOMBRE_DEMOSTRATIVO", "those");
        add("aquel", "PRONOMBRE_DEMOSTRATIVO", "that");
        add("aquella", "PRONOMBRE_DEMOSTRATIVO", "that");
        add("aquellos", "PRONOMBRE_DEMOSTRATIVO", "those");
        add("aquellas", "PRONOMBRE_DEMOSTRATIVO", "those");

        /*
         * ══════════════════════════════════
         * PRONOMBRES INTERROGATIVOS
         * (solo los que son genuinamente pronombres)
         * ══════════════════════════════════
         */
        add("what", "PRONOMBRE_INTERROGATIVO", "qué");
        add("who", "PRONOMBRE_INTERROGATIVO", "quién");
        add("which", "PRONOMBRE_INTERROGATIVO", "cuál");
        add("whose", "PRONOMBRE_INTERROGATIVO", "de quién");
        add("whom", "PRONOMBRE_INTERROGATIVO", "a quién");
        add("qué", "PRONOMBRE_INTERROGATIVO", "what");
        add("que", "PRONOMBRE_INTERROGATIVO", "what");
        add("quién", "PRONOMBRE_INTERROGATIVO", "who");
        add("quien", "PRONOMBRE_INTERROGATIVO", "who");
        add("quiénes", "PRONOMBRE_INTERROGATIVO", "who");
        add("quienes", "PRONOMBRE_INTERROGATIVO", "who");
        add("cuál", "PRONOMBRE_INTERROGATIVO", "which");
        add("cual", "PRONOMBRE_INTERROGATIVO", "which");
        add("cuáles", "PRONOMBRE_INTERROGATIVO", "which");
        add("cuales", "PRONOMBRE_INTERROGATIVO", "which");
        add("cuánto", "PRONOMBRE_INTERROGATIVO", "how much");
        add("cuanto", "PRONOMBRE_INTERROGATIVO", "how much");
        add("cuántos", "PRONOMBRE_INTERROGATIVO", "how many");
        add("cuantos", "PRONOMBRE_INTERROGATIVO", "how many");

        /*
         * ══════════════════════════════════
         * ARTÍCULOS DEFINIDOS
         * ══════════════════════════════════
         */
        add("the", "ARTICULO_DEFINIDO", "el/la");
        add("el", "ARTICULO_DEFINIDO", "the");
        add("la", "ARTICULO_DEFINIDO", "the");
        add("los", "ARTICULO_DEFINIDO", "the");
        add("las", "ARTICULO_DEFINIDO", "the");

        /*
         * ══════════════════════════════════
         * ARTÍCULOS INDEFINIDOS
         * ══════════════════════════════════
         */
        add("a", "ARTICULO_INDEFINIDO", "un/una");
        add("an", "ARTICULO_INDEFINIDO", "un/una");
        add("some", "ARTICULO_INDEFINIDO", "unos/unas");
        add("un", "ARTICULO_INDEFINIDO", "a");
        add("una", "ARTICULO_INDEFINIDO", "a");
        add("unos", "ARTICULO_INDEFINIDO", "some");
        add("unas", "ARTICULO_INDEFINIDO", "some");

        /*
         * ══════════════════════════════════
         * POSESIVOS
         * ══════════════════════════════════
         */
        add("my", "POSESIVO", "mi");
        add("your", "POSESIVO", "tu");
        add("his", "POSESIVO", "su");
        add("its", "POSESIVO", "su");
        add("our", "POSESIVO", "nuestro");
        add("their", "POSESIVO", "su");
        add("mine", "POSESIVO", "mío");
        add("yours", "POSESIVO", "tuyo");
        add("hers", "POSESIVO", "suyo");
        add("ours", "POSESIVO", "nuestro");
        add("theirs", "POSESIVO", "suyo");
        add("mi", "POSESIVO", "my");
        add("mis", "POSESIVO", "my");
        add("mío", "POSESIVO", "mine");
        add("mio", "POSESIVO", "mine");
        add("tuyo", "POSESIVO", "yours");
        add("suyo", "POSESIVO", "his/hers");
        add("nuestro", "POSESIVO", "our");
        add("nuestra", "POSESIVO", "our");
        add("nuestros", "POSESIVO", "our");
        add("nuestras", "POSESIVO", "our");
        add("su", "POSESIVO", "his/her/its");
        add("sus", "POSESIVO", "his/her/its");

        /*
         * ══════════════════════════════════
         * DEMOSTRATIVOS (determinantes)
         * ══════════════════════════════════
         */
        add("this", "DEMOSTRATIVO", "este/esta");
        add("that", "DEMOSTRATIVO", "ese/esa");
        add("these", "DEMOSTRATIVO", "estos/estas");
        add("those", "DEMOSTRATIVO", "esos/esas");

        /*
         * ══════════════════════════════════
         * NUMERALES CARDINALES
         * ══════════════════════════════════
         */
        add("one", "NUMERAL_CARDINAL", "uno");
        add("two", "NUMERAL_CARDINAL", "dos");
        add("three", "NUMERAL_CARDINAL", "tres");
        add("four", "NUMERAL_CARDINAL", "cuatro");
        add("five", "NUMERAL_CARDINAL", "cinco");
        add("six", "NUMERAL_CARDINAL", "seis");
        add("seven", "NUMERAL_CARDINAL", "siete");
        add("eight", "NUMERAL_CARDINAL", "ocho");
        add("nine", "NUMERAL_CARDINAL", "nueve");
        add("ten", "NUMERAL_CARDINAL", "diez");
        add("eleven", "NUMERAL_CARDINAL", "once");
        add("twelve", "NUMERAL_CARDINAL", "doce");
        add("twenty", "NUMERAL_CARDINAL", "veinte");
        add("hundred", "NUMERAL_CARDINAL", "cien");
        add("thousand", "NUMERAL_CARDINAL", "mil");
        add("uno", "NUMERAL_CARDINAL", "one");
        add("dos", "NUMERAL_CARDINAL", "two");
        add("tres", "NUMERAL_CARDINAL", "three");
        add("cuatro", "NUMERAL_CARDINAL", "four");
        add("cinco", "NUMERAL_CARDINAL", "five");
        add("seis", "NUMERAL_CARDINAL", "six");
        add("siete", "NUMERAL_CARDINAL", "seven");
        add("ocho", "NUMERAL_CARDINAL", "eight");
        add("nueve", "NUMERAL_CARDINAL", "nine");
        add("diez", "NUMERAL_CARDINAL", "ten");
        add("once", "NUMERAL_CARDINAL", "eleven");
        add("doce", "NUMERAL_CARDINAL", "twelve");
        add("veinte", "NUMERAL_CARDINAL", "twenty");
        add("cien", "NUMERAL_CARDINAL", "hundred");
        add("mil", "NUMERAL_CARDINAL", "thousand");

        /*
         * ══════════════════════════════════
         * NUMERALES ORDINALES
         * ══════════════════════════════════
         */
        add("first", "NUMERAL_ORDINAL", "primero");
        add("second", "NUMERAL_ORDINAL", "segundo");
        add("third", "NUMERAL_ORDINAL", "tercero");
        add("fourth", "NUMERAL_ORDINAL", "cuarto");
        add("fifth", "NUMERAL_ORDINAL", "quinto");
        add("sixth", "NUMERAL_ORDINAL", "sexto");
        add("seventh", "NUMERAL_ORDINAL", "séptimo");
        add("eighth", "NUMERAL_ORDINAL", "octavo");
        add("ninth", "NUMERAL_ORDINAL", "noveno");
        add("tenth", "NUMERAL_ORDINAL", "décimo");
        add("primero", "NUMERAL_ORDINAL", "first");
        add("primera", "NUMERAL_ORDINAL", "first");
        add("segundo", "NUMERAL_ORDINAL", "second");
        add("segunda", "NUMERAL_ORDINAL", "second");
        add("tercero", "NUMERAL_ORDINAL", "third");
        add("tercera", "NUMERAL_ORDINAL", "third");
        add("cuarto", "NUMERAL_ORDINAL", "fourth");
        add("cuarta", "NUMERAL_ORDINAL", "fourth");
        add("quinto", "NUMERAL_ORDINAL", "fifth");
        add("quinta", "NUMERAL_ORDINAL", "fifth");

        /*
         * ══════════════════════════════════
         * VERBOS EN → ES
         * ══════════════════════════════════
         */
        add("eat", "VERBO", "comer");
        add("eats", "VERBO", "come");
        add("ate", "VERBO", "comió");
        add("eaten", "VERBO", "comido");
        add("read", "VERBO", "leer");
        add("reads", "VERBO", "lee");
        add("play", "VERBO", "jugar");
        add("plays", "VERBO", "juega");
        add("played", "VERBO", "jugó");
        add("run", "VERBO", "correr");
        add("runs", "VERBO", "corre");
        add("ran", "VERBO", "corrió");
        add("write", "VERBO", "escribir");
        add("writes", "VERBO", "escribe");
        add("wrote", "VERBO", "escribió");
        add("written", "VERBO", "escrito");
        add("drink", "VERBO", "beber");
        add("drinks", "VERBO", "bebe");
        add("drank", "VERBO", "bebió");
        add("go", "VERBO", "ir");
        add("goes", "VERBO", "va");
        add("went", "VERBO", "fue");
        add("gone", "VERBO", "ido");
        add("like", "VERBO", "gustar");
        add("likes", "VERBO", "le gusta");
        add("liked", "VERBO", "le gustó");
        add("am", "VERBO", "soy/estoy");
        add("is", "VERBO", "es/está");
        add("are", "VERBO", "son/están");
        add("was", "VERBO", "fue/era");
        add("were", "VERBO", "fueron/eran");
        add("been", "VERBO", "sido/estado");
        add("be", "VERBO", "ser/estar");
        add("have", "VERBO", "tener");
        add("has", "VERBO", "tiene");
        add("had", "VERBO", "tuvo");
        add("see", "VERBO", "ver");
        add("sees", "VERBO", "ve");
        add("saw", "VERBO", "vio");
        add("seen", "VERBO", "visto");
        add("walk", "VERBO", "caminar");
        add("walks", "VERBO", "camina");
        add("walked", "VERBO", "caminó");
        add("love", "VERBO", "amar");
        add("loves", "VERBO", "ama");
        add("loved", "VERBO", "amó");
        add("work", "VERBO", "trabajar");
        add("works", "VERBO", "trabaja");
        add("worked", "VERBO", "trabajó");
        add("speak", "VERBO", "hablar");
        add("speaks", "VERBO", "habla");
        add("spoke", "VERBO", "habló");
        add("spoken", "VERBO", "hablado");
        add("come", "VERBO", "venir");
        add("comes", "VERBO", "viene");
        add("came", "VERBO", "vino");
        add("know", "VERBO", "saber");
        add("knows", "VERBO", "sabe");
        add("knew", "VERBO", "supo");
        add("known", "VERBO", "sabido");
        add("think", "VERBO", "pensar");
        add("thinks", "VERBO", "piensa");
        add("thought", "VERBO", "pensó");
        add("want", "VERBO", "querer");
        add("wants", "VERBO", "quiere");
        add("wanted", "VERBO", "quiso");
        add("need", "VERBO", "necesitar");
        add("needs", "VERBO", "necesita");
        add("needed", "VERBO", "necesitó");
        add("live", "VERBO", "vivir");
        add("lives", "VERBO", "vive");
        add("lived", "VERBO", "vivió");
        add("study", "VERBO", "estudiar");
        add("studies", "VERBO", "estudia");
        add("studied", "VERBO", "estudió");
        add("teach", "VERBO", "enseñar");
        add("teaches", "VERBO", "enseña");
        add("taught", "VERBO", "enseñó");
        add("learn", "VERBO", "aprender");
        add("learns", "VERBO", "aprende");
        add("learned", "VERBO", "aprendió");
        add("buy", "VERBO", "comprar");
        add("buys", "VERBO", "compra");
        add("bought", "VERBO", "compró");
        add("sell", "VERBO", "vender");
        add("sells", "VERBO", "vende");
        add("sold", "VERBO", "vendió");
        add("help", "VERBO", "ayudar");
        add("helps", "VERBO", "ayuda");
        add("helped", "VERBO", "ayudó");
        add("sleep", "VERBO", "dormir");
        add("sleeps", "VERBO", "duerme");
        add("slept", "VERBO", "durmió");
        add("wake", "VERBO", "despertar");
        add("wakes", "VERBO", "despierta");
        add("woke", "VERBO", "despertó");
        add("make", "VERBO", "hacer");
        add("makes", "VERBO", "hace");
        add("made", "VERBO", "hizo");
        add("take", "VERBO", "tomar");
        add("takes", "VERBO", "toma");
        add("took", "VERBO", "tomó");
        add("give", "VERBO", "dar");
        add("gives", "VERBO", "da");
        add("gave", "VERBO", "dio");
        add("find", "VERBO", "encontrar");
        add("finds", "VERBO", "encuentra");
        add("found", "VERBO", "encontró");
        add("use", "VERBO", "usar");
        add("uses", "VERBO", "usa");
        add("used", "VERBO", "usó");
        add("get", "VERBO", "obtener");
        add("gets", "VERBO", "obtiene");
        add("got", "VERBO", "obtuvo");
        add("say", "VERBO", "decir");
        add("says", "VERBO", "dice");
        add("said", "VERBO", "dijo");
        add("ask", "VERBO", "preguntar");
        add("asks", "VERBO", "pregunta");
        add("asked", "VERBO", "preguntó");
        add("try", "VERBO", "intentar");
        add("tries", "VERBO", "intenta");
        add("tried", "VERBO", "intentó");
        add("call", "VERBO", "llamar");
        add("calls", "VERBO", "llama");
        add("called", "VERBO", "llamó");
        add("feel", "VERBO", "sentir");
        add("feels", "VERBO", "siente");
        add("felt", "VERBO", "sintió");
        add("can", "VERBO", "poder");
        add("could", "VERBO", "podría");
        add("will", "VERBO", "va a");
        add("would", "VERBO", "haría");
        add("should", "VERBO", "debería");
        add("must", "VERBO", "debe");
        add("may", "VERBO", "puede");
        add("might", "VERBO", "podría");

        /*
         * ══════════════════════════════════
         * VERBOS ES → EN
         * ══════════════════════════════════
         */
        add("comer", "VERBO", "to eat");
        add("como", "VERBO", "eat");
        add("comes", "VERBO", "eat"); // 2da persona
        add("come", "VERBO", "eats"); // 3ra persona ES
        add("comemos", "VERBO", "eat");
        add("comen", "VERBO", "eat");
        add("comió", "VERBO", "ate");
        add("leer", "VERBO", "to read");
        add("leo", "VERBO", "read");
        add("lee", "VERBO", "reads");
        add("leemos", "VERBO", "read");
        add("leen", "VERBO", "read");
        add("leyó", "VERBO", "read");
        add("jugar", "VERBO", "to play");
        add("juego", "VERBO", "play");
        add("juega", "VERBO", "plays");
        add("jugamos", "VERBO", "play");
        add("juegan", "VERBO", "play");
        add("jugó", "VERBO", "played");
        add("correr", "VERBO", "to run");
        add("corro", "VERBO", "run");
        add("corre", "VERBO", "runs");
        add("corremos", "VERBO", "run");
        add("corren", "VERBO", "run");
        add("corrió", "VERBO", "ran");
        add("escribir", "VERBO", "to write");
        add("escribo", "VERBO", "write");
        add("escribe", "VERBO", "writes");
        add("escribimos", "VERBO", "write");
        add("escriben", "VERBO", "write");
        add("escribió", "VERBO", "wrote");
        add("beber", "VERBO", "to drink");
        add("bebo", "VERBO", "drink");
        add("bebe", "VERBO", "drinks");
        add("bebemos", "VERBO", "drink");
        add("beben", "VERBO", "drink");
        add("bebió", "VERBO", "drank");
        add("ir", "VERBO", "to go");
        add("voy", "VERBO", "go");
        add("vas", "VERBO", "go");
        add("va", "VERBO", "goes");
        add("vamos", "VERBO", "go");
        add("van", "VERBO", "go");
        add("fue", "VERBO", "went");
        add("ser", "VERBO", "to be");
        add("estar", "VERBO", "to be");
        add("soy", "VERBO", "am");
        add("eres", "VERBO", "are");
        add("es", "VERBO", "is");
        add("somos", "VERBO", "are");
        add("son", "VERBO", "are");
        add("era", "VERBO", "was");
        add("tener", "VERBO", "to have");
        add("tengo", "VERBO", "have");
        add("tienes", "VERBO", "have");
        add("tiene", "VERBO", "has");
        add("tenemos", "VERBO", "have");
        add("tienen", "VERBO", "have");
        add("tuvo", "VERBO", "had");
        add("ver", "VERBO", "to see");
        add("veo", "VERBO", "see");
        add("ves", "VERBO", "see");
        add("ve", "VERBO", "sees");
        add("vemos", "VERBO", "see");
        add("ven", "VERBO", "see");
        add("vio", "VERBO", "saw");
        add("amar", "VERBO", "to love");
        add("amo", "VERBO", "love");
        add("amas", "VERBO", "love");
        add("ama", "VERBO", "loves");
        add("amamos", "VERBO", "love");
        add("aman", "VERBO", "love");
        add("amó", "VERBO", "loved");
        add("caminar", "VERBO", "to walk");
        add("camino", "VERBO", "walk");
        add("caminas", "VERBO", "walk");
        add("camina", "VERBO", "walks");
        add("caminamos", "VERBO", "walk");
        add("caminan", "VERBO", "walk");
        add("caminó", "VERBO", "walked");
        add("trabajar", "VERBO", "to work");
        add("trabajo", "VERBO", "work");
        add("trabajas", "VERBO", "work");
        add("trabaja", "VERBO", "works");
        add("trabajamos", "VERBO", "work");
        add("trabajan", "VERBO", "work");
        add("trabajó", "VERBO", "worked");
        add("hablar", "VERBO", "to speak");
        add("hablo", "VERBO", "speak");
        add("hablas", "VERBO", "speak");
        add("habla", "VERBO", "speaks");
        add("hablamos", "VERBO", "speak");
        add("hablan", "VERBO", "speak");
        add("habló", "VERBO", "spoke");
        add("saber", "VERBO", "to know");
        add("sé", "VERBO", "know");
        add("sabes", "VERBO", "know");
        add("sabe", "VERBO", "knows");
        add("sabemos", "VERBO", "know");
        add("saben", "VERBO", "know");
        add("supo", "VERBO", "knew");
        add("pensar", "VERBO", "to think");
        add("pienso", "VERBO", "think");
        add("piensas", "VERBO", "think");
        add("piensa", "VERBO", "thinks");
        add("pensamos", "VERBO", "think");
        add("piensan", "VERBO", "think");
        add("pensó", "VERBO", "thought");
        add("querer", "VERBO", "to want");
        add("quiero", "VERBO", "want");
        add("quieres", "VERBO", "want");
        add("quiere", "VERBO", "wants");
        add("queremos", "VERBO", "want");
        add("quieren", "VERBO", "want");
        add("quiso", "VERBO", "wanted");
        add("vivir", "VERBO", "to live");
        add("vivo", "VERBO", "live");
        add("vives", "VERBO", "live");
        add("vive", "VERBO", "lives");
        add("vivimos", "VERBO", "live");
        add("viven", "VERBO", "live");
        add("vivió", "VERBO", "lived");
        add("estudiar", "VERBO", "to study");
        add("estudio", "VERBO", "study");
        add("estudias", "VERBO", "study");
        add("estudia", "VERBO", "studies");
        add("estudiamos", "VERBO", "study");
        add("estudian", "VERBO", "study");
        add("estudió", "VERBO", "studied");
        add("comprar", "VERBO", "to buy");
        add("compro", "VERBO", "buy");
        add("compras", "VERBO", "buy");
        add("compra", "VERBO", "buys");
        add("compramos", "VERBO", "buy");
        add("compran", "VERBO", "buy");
        add("compró", "VERBO", "bought");
        add("dormir", "VERBO", "to sleep");
        add("duermo", "VERBO", "sleep");
        add("duermes", "VERBO", "sleep");
        add("duerme", "VERBO", "sleeps");
        add("dormimos", "VERBO", "sleep");
        add("duermen", "VERBO", "sleep");
        add("durmió", "VERBO", "slept");
        add("ayudar", "VERBO", "to help");
        add("ayudo", "VERBO", "help");
        add("ayudas", "VERBO", "help");
        add("ayuda", "VERBO", "helps");
        add("ayudamos", "VERBO", "help");
        add("ayudan", "VERBO", "help");
        add("ayudó", "VERBO", "helped");
        add("hacer", "VERBO", "to make");
        add("hago", "VERBO", "make");
        add("haces", "VERBO", "make");
        add("hace", "VERBO", "makes");
        add("hacemos", "VERBO", "make");
        add("hacen", "VERBO", "make");
        add("hizo", "VERBO", "made");
        add("tomar", "VERBO", "to take");
        add("tomo", "VERBO", "take");
        add("tomas", "VERBO", "take");
        add("toma", "VERBO", "takes");
        add("tomamos", "VERBO", "take");
        add("toman", "VERBO", "take");
        add("tomó", "VERBO", "took");
        add("dar", "VERBO", "to give");
        add("doy", "VERBO", "give");
        add("das", "VERBO", "give");
        add("da", "VERBO", "gives");
        add("damos", "VERBO", "give");
        add("dan", "VERBO", "give");
        add("dio", "VERBO", "gave");
        add("llamar", "VERBO", "to call");
        add("llamo", "VERBO", "call");
        add("llamas", "VERBO", "call");
        add("llama", "VERBO", "calls");
        add("llamamos", "VERBO", "call");
        add("llaman", "VERBO", "call");
        add("llamó", "VERBO", "called");
        add("sentir", "VERBO", "to feel");
        add("siento", "VERBO", "feel");
        add("sientes", "VERBO", "feel");
        add("siente", "VERBO", "feels");
        add("sentimos", "VERBO", "feel");
        add("sienten", "VERBO", "feel");
        add("sintió", "VERBO", "felt");
        add("poder", "VERBO", "can");
        add("puedo", "VERBO", "can");
        add("puedes", "VERBO", "can");
        add("puede", "VERBO", "can");
        add("podemos", "VERBO", "can");
        add("pueden", "VERBO", "can");
        add("pudo", "VERBO", "could");
        add("necesitar", "VERBO", "to need");
        add("necesito", "VERBO", "need");
        add("necesitas", "VERBO", "need");
        add("necesita", "VERBO", "needs");
        add("necesitamos", "VERBO", "need");
        add("necesitan", "VERBO", "need");
        add("necesitó", "VERBO", "needed");
        add("preguntar", "VERBO", "to ask");
        add("pregunto", "VERBO", "ask");
        add("preguntas", "VERBO", "ask");
        add("pregunta", "VERBO", "asks");
        add("preguntamos", "VERBO", "ask");
        add("preguntan", "VERBO", "ask");
        add("preguntó", "VERBO", "asked");
        add("intentar", "VERBO", "to try");
        add("intento", "VERBO", "try");
        add("intentas", "VERBO", "try");
        add("intenta", "VERBO", "tries");
        add("intentamos", "VERBO", "try");
        add("intentan", "VERBO", "try");
        add("intentó", "VERBO", "tried");
        add("venir", "VERBO", "to come");
        add("vengo", "VERBO", "come");
        add("vienes", "VERBO", "come");
        add("viene", "VERBO", "comes");
        add("venimos", "VERBO", "come");
        add("vienen", "VERBO", "come");
        add("vino", "VERBO", "came");
        add("encontrar", "VERBO", "to find");
        add("encuentro", "VERBO", "find");
        add("encuentras", "VERBO", "find");
        add("encuentra", "VERBO", "finds");
        add("encontramos", "VERBO", "find");
        add("encuentran", "VERBO", "find");
        add("encontró", "VERBO", "found");
        add("usar", "VERBO", "to use");
        add("uso", "VERBO", "use");
        add("usas", "VERBO", "use");
        add("usa", "VERBO", "uses");
        add("usamos", "VERBO", "use");
        add("usan", "VERBO", "use");
        add("usó", "VERBO", "used");
        add("decir", "VERBO", "to say");
        add("digo", "VERBO", "say");
        add("dices", "VERBO", "say");
        add("dice", "VERBO", "says");
        add("decimos", "VERBO", "say");
        add("dicen", "VERBO", "say");
        add("dijo", "VERBO", "said");

        /*
         * ══════════════════════════════════
         * SUSTANTIVOS EN → ES
         * ══════════════════════════════════
         */
        add("apple", "SUSTANTIVO", "manzana");
        add("book", "SUSTANTIVO", "libro");
        add("soccer", "SUSTANTIVO", "fútbol");
        add("football", "SUSTANTIVO", "fútbol");
        add("water", "SUSTANTIVO", "agua");
        add("house", "SUSTANTIVO", "casa");
        add("home", "SUSTANTIVO", "hogar");
        add("dog", "SUSTANTIVO", "perro");
        add("cat", "SUSTANTIVO", "gato");
        add("car", "SUSTANTIVO", "carro");
        add("school", "SUSTANTIVO", "escuela");
        add("market", "SUSTANTIVO", "mercado");
        add("food", "SUSTANTIVO", "comida");
        add("ball", "SUSTANTIVO", "pelota");
        add("man", "SUSTANTIVO", "hombre");
        add("woman", "SUSTANTIVO", "mujer");
        add("child", "SUSTANTIVO", "niño");
        add("children", "SUSTANTIVO", "niños");
        add("city", "SUSTANTIVO", "ciudad");
        add("friend", "SUSTANTIVO", "amigo");
        add("teacher", "SUSTANTIVO", "maestro");
        add("student", "SUSTANTIVO", "estudiante");
        add("family", "SUSTANTIVO", "familia");
        add("mother", "SUSTANTIVO", "madre");
        add("father", "SUSTANTIVO", "padre");
        add("brother", "SUSTANTIVO", "hermano");
        add("sister", "SUSTANTIVO", "hermana");
        add("day", "SUSTANTIVO", "día");
        add("night", "SUSTANTIVO", "noche");
        add("morning", "SUSTANTIVO", "mañana");
        add("afternoon", "SUSTANTIVO", "tarde");
        add("time", "SUSTANTIVO", "tiempo");
        add("money", "SUSTANTIVO", "dinero");
        add("life", "SUSTANTIVO", "vida");
        add("world", "SUSTANTIVO", "mundo");
        add("country", "SUSTANTIVO", "país");
        add("music", "SUSTANTIVO", "música");
        add("park", "SUSTANTIVO", "parque");
        add("store", "SUSTANTIVO", "tienda");
        add("hospital", "SUSTANTIVO", "hospital");
        add("church", "SUSTANTIVO", "iglesia");
        add("street", "SUSTANTIVO", "calle");
        add("road", "SUSTANTIVO", "camino");
        add("river", "SUSTANTIVO", "río");
        add("mountain", "SUSTANTIVO", "montaña");
        add("beach", "SUSTANTIVO", "playa");
        add("sun", "SUSTANTIVO", "sol");
        add("moon", "SUSTANTIVO", "luna");
        add("star", "SUSTANTIVO", "estrella");
        add("tree", "SUSTANTIVO", "árbol");
        add("flower", "SUSTANTIVO", "flor");
        add("bird", "SUSTANTIVO", "pájaro");
        add("fish", "SUSTANTIVO", "pez");
        add("horse", "SUSTANTIVO", "caballo");
        add("cow", "SUSTANTIVO", "vaca");
        add("bread", "SUSTANTIVO", "pan");
        add("milk", "SUSTANTIVO", "leche");
        add("coffee", "SUSTANTIVO", "café");
        add("tea", "SUSTANTIVO", "té");
        add("rice", "SUSTANTIVO", "arroz");
        add("meat", "SUSTANTIVO", "carne");
        add("paper", "SUSTANTIVO", "papel");
        add("pen", "SUSTANTIVO", "pluma");
        add("chair", "SUSTANTIVO", "silla");
        add("table", "SUSTANTIVO", "mesa");
        add("door", "SUSTANTIVO", "puerta");
        add("window", "SUSTANTIVO", "ventana");
        add("phone", "SUSTANTIVO", "teléfono");
        add("computer", "SUSTANTIVO", "computadora");
        add("language", "SUSTANTIVO", "idioma");
        add("word", "SUSTANTIVO", "palabra");
        add("sentence", "SUSTANTIVO", "oración");
        add("letter", "SUSTANTIVO", "carta/letra");
        add("number", "SUSTANTIVO", "número");
        add("name", "SUSTANTIVO", "nombre");
        add("place", "SUSTANTIVO", "lugar");
        add("thing", "SUSTANTIVO", "cosa");
        add("person", "SUSTANTIVO", "persona");
        add("people", "SUSTANTIVO", "personas");
        add("game", "SUSTANTIVO", "juego");
        add("team", "SUSTANTIVO", "equipo");
        add("year", "SUSTANTIVO", "año");
        add("month", "SUSTANTIVO", "mes");
        add("week", "SUSTANTIVO", "semana");
        add("hour", "SUSTANTIVO", "hora");
        add("minute", "SUSTANTIVO", "minuto");

        /*
         * ══════════════════════════════════
         * SUSTANTIVOS ES → EN
         * ══════════════════════════════════
         */
        add("manzana", "SUSTANTIVO", "apple");
        add("libro", "SUSTANTIVO", "book");
        add("fútbol", "SUSTANTIVO", "soccer");
        add("futbol", "SUSTANTIVO", "soccer");
        add("agua", "SUSTANTIVO", "water");
        add("casa", "SUSTANTIVO", "house");
        add("hogar", "SUSTANTIVO", "home");
        add("perro", "SUSTANTIVO", "dog");
        add("gato", "SUSTANTIVO", "cat");
        add("carro", "SUSTANTIVO", "car");
        add("escuela", "SUSTANTIVO", "school");
        add("colegio", "SUSTANTIVO", "school");
        add("mercado", "SUSTANTIVO", "market");
        add("comida", "SUSTANTIVO", "food");
        add("pelota", "SUSTANTIVO", "ball");
        add("hombre", "SUSTANTIVO", "man");
        add("mujer", "SUSTANTIVO", "woman");
        add("niño", "SUSTANTIVO", "child");
        add("niña", "SUSTANTIVO", "child");
        add("niños", "SUSTANTIVO", "children");
        add("ciudad", "SUSTANTIVO", "city");
        add("amigo", "SUSTANTIVO", "friend");
        add("amiga", "SUSTANTIVO", "friend");
        add("maestro", "SUSTANTIVO", "teacher");
        add("maestra", "SUSTANTIVO", "teacher");
        add("estudiante", "SUSTANTIVO", "student");
        add("alumno", "SUSTANTIVO", "student");
        add("familia", "SUSTANTIVO", "family");
        add("madre", "SUSTANTIVO", "mother");
        add("mamá", "SUSTANTIVO", "mother");
        add("mama", "SUSTANTIVO", "mother");
        add("padre", "SUSTANTIVO", "father");
        add("papá", "SUSTANTIVO", "father");
        add("papa", "SUSTANTIVO", "father");
        add("hermano", "SUSTANTIVO", "brother");
        add("hermana", "SUSTANTIVO", "sister");
        add("día", "SUSTANTIVO", "day");
        add("dia", "SUSTANTIVO", "day");
        add("noche", "SUSTANTIVO", "night");
        add("mañana", "SUSTANTIVO", "morning");
        add("manana", "SUSTANTIVO", "morning");
        add("tarde", "SUSTANTIVO", "afternoon");
        add("tiempo", "SUSTANTIVO", "time");
        add("dinero", "SUSTANTIVO", "money");
        add("vida", "SUSTANTIVO", "life");
        add("mundo", "SUSTANTIVO", "world");
        add("país", "SUSTANTIVO", "country");
        add("pais", "SUSTANTIVO", "country");
        add("música", "SUSTANTIVO", "music");
        add("musica", "SUSTANTIVO", "music");
        add("parque", "SUSTANTIVO", "park");
        add("tienda", "SUSTANTIVO", "store");
        add("hospital", "SUSTANTIVO", "hospital");
        add("iglesia", "SUSTANTIVO", "church");
        add("calle", "SUSTANTIVO", "street");
        add("camino", "SUSTANTIVO", "road");
        add("río", "SUSTANTIVO", "river");
        add("rio", "SUSTANTIVO", "river");
        add("montaña", "SUSTANTIVO", "mountain");
        add("montana", "SUSTANTIVO", "mountain");
        add("playa", "SUSTANTIVO", "beach");
        add("sol", "SUSTANTIVO", "sun");
        add("luna", "SUSTANTIVO", "moon");
        add("estrella", "SUSTANTIVO", "star");
        add("árbol", "SUSTANTIVO", "tree");
        add("arbol", "SUSTANTIVO", "tree");
        add("flor", "SUSTANTIVO", "flower");
        add("pájaro", "SUSTANTIVO", "bird");
        add("pajaro", "SUSTANTIVO", "bird");
        add("pez", "SUSTANTIVO", "fish");
        add("caballo", "SUSTANTIVO", "horse");
        add("vaca", "SUSTANTIVO", "cow");
        add("pan", "SUSTANTIVO", "bread");
        add("leche", "SUSTANTIVO", "milk");
        add("café", "SUSTANTIVO", "coffee");
        add("cafe", "SUSTANTIVO", "coffee");
        add("arroz", "SUSTANTIVO", "rice");
        add("carne", "SUSTANTIVO", "meat");
        add("papel", "SUSTANTIVO", "paper");
        add("pluma", "SUSTANTIVO", "pen");
        add("silla", "SUSTANTIVO", "chair");
        add("mesa", "SUSTANTIVO", "table");
        add("puerta", "SUSTANTIVO", "door");
        add("ventana", "SUSTANTIVO", "window");
        add("teléfono", "SUSTANTIVO", "phone");
        add("telefono", "SUSTANTIVO", "phone");
        add("computadora", "SUSTANTIVO", "computer");
        add("idioma", "SUSTANTIVO", "language");
        add("lengua", "SUSTANTIVO", "language");
        add("palabra", "SUSTANTIVO", "word");
        add("oración", "SUSTANTIVO", "sentence");
        add("oracion", "SUSTANTIVO", "sentence");
        add("número", "SUSTANTIVO", "number");
        add("numero", "SUSTANTIVO", "number");
        add("nombre", "SUSTANTIVO", "name");
        add("lugar", "SUSTANTIVO", "place");
        add("cosa", "SUSTANTIVO", "thing");
        add("persona", "SUSTANTIVO", "person");
        add("personas", "SUSTANTIVO", "people");
        // 'juego' es VERBO (yo juego = I play). 'game' en EN ya mapea a su propio sustantivo.
        add("equipo", "SUSTANTIVO", "team");
        add("año", "SUSTANTIVO", "year");
        add("anio", "SUSTANTIVO", "year");
        add("mes", "SUSTANTIVO", "month");
        add("semana", "SUSTANTIVO", "week");
        add("hora", "SUSTANTIVO", "hour");
        add("minuto", "SUSTANTIVO", "minute");

        /*
         * ══════════════════════════════════
         * ADJETIVOS CALIFICATIVOS EN → ES
         * ══════════════════════════════════
         */
        add("big", "ADJETIVO_CALIFICATIVO", "grande");
        add("large", "ADJETIVO_CALIFICATIVO", "grande");
        add("small", "ADJETIVO_CALIFICATIVO", "pequeño");
        add("little", "ADJETIVO_CALIFICATIVO", "pequeño");
        add("fast", "ADJETIVO_CALIFICATIVO", "rápido");
        add("quick", "ADJETIVO_CALIFICATIVO", "rápido");
        add("slow", "ADJETIVO_CALIFICATIVO", "lento");
        add("good", "ADJETIVO_CALIFICATIVO", "bueno");
        add("great", "ADJETIVO_CALIFICATIVO", "genial");
        add("bad", "ADJETIVO_CALIFICATIVO", "malo");
        add("happy", "ADJETIVO_CALIFICATIVO", "feliz");
        add("sad", "ADJETIVO_CALIFICATIVO", "triste");
        add("new", "ADJETIVO_CALIFICATIVO", "nuevo");
        add("old", "ADJETIVO_CALIFICATIVO", "viejo");
        add("hot", "ADJETIVO_CALIFICATIVO", "caliente");
        add("cold", "ADJETIVO_CALIFICATIVO", "frío");
        add("frio", "ADJETIVO_CALIFICATIVO", "cold");
        add("beautiful", "ADJETIVO_CALIFICATIVO", "hermoso");
        add("pretty", "ADJETIVO_CALIFICATIVO", "bonito");
        add("ugly", "ADJETIVO_CALIFICATIVO", "feo");
        add("tall", "ADJETIVO_CALIFICATIVO", "alto");
        add("short", "ADJETIVO_CALIFICATIVO", "bajo");
        add("long", "ADJETIVO_CALIFICATIVO", "largo");
        add("young", "ADJETIVO_CALIFICATIVO", "joven");
        add("strong", "ADJETIVO_CALIFICATIVO", "fuerte");
        add("weak", "ADJETIVO_CALIFICATIVO", "débil");
        add("rich", "ADJETIVO_CALIFICATIVO", "rico");
        add("poor", "ADJETIVO_CALIFICATIVO", "pobre");
        add("smart", "ADJETIVO_CALIFICATIVO", "inteligente");
        add("intelligent", "ADJETIVO_CALIFICATIVO", "inteligente");
        add("funny", "ADJETIVO_CALIFICATIVO", "gracioso");
        add("kind", "ADJETIVO_CALIFICATIVO", "amable");
        add("brave", "ADJETIVO_CALIFICATIVO", "valiente");
        add("clean", "ADJETIVO_CALIFICATIVO", "limpio");
        add("dirty", "ADJETIVO_CALIFICATIVO", "sucio");
        add("dark", "ADJETIVO_CALIFICATIVO", "oscuro");
        add("light", "ADJETIVO_CALIFICATIVO", "claro");
        add("hard", "ADJETIVO_CALIFICATIVO", "duro");
        add("soft", "ADJETIVO_CALIFICATIVO", "suave");
        add("heavy", "ADJETIVO_CALIFICATIVO", "pesado");
        add("thin", "ADJETIVO_CALIFICATIVO", "delgado");
        add("fat", "ADJETIVO_CALIFICATIVO", "gordo");
        add("empty", "ADJETIVO_CALIFICATIVO", "vacío");
        add("full", "ADJETIVO_CALIFICATIVO", "lleno");
        add("open", "ADJETIVO_CALIFICATIVO", "abierto");
        add("closed", "ADJETIVO_CALIFICATIVO", "cerrado");
        add("free", "ADJETIVO_CALIFICATIVO", "libre");
        add("important", "ADJETIVO_CALIFICATIVO", "importante");
        add("different", "ADJETIVO_CALIFICATIVO", "diferente");
        add("possible", "ADJETIVO_CALIFICATIVO", "posible");

        /* ── ADJETIVOS CALIFICATIVOS ES → EN ── */
        add("grande", "ADJETIVO_CALIFICATIVO", "big");
        add("pequeño", "ADJETIVO_CALIFICATIVO", "small");
        add("pequeña", "ADJETIVO_CALIFICATIVO", "small");
        add("rápido", "ADJETIVO_CALIFICATIVO", "fast");
        add("rapido", "ADJETIVO_CALIFICATIVO", "fast");
        add("lento", "ADJETIVO_CALIFICATIVO", "slow");
        add("bueno", "ADJETIVO_CALIFICATIVO", "good");
        add("buena", "ADJETIVO_CALIFICATIVO", "good");
        add("malo", "ADJETIVO_CALIFICATIVO", "bad");
        add("mala", "ADJETIVO_CALIFICATIVO", "bad");
        add("feliz", "ADJETIVO_CALIFICATIVO", "happy");
        add("triste", "ADJETIVO_CALIFICATIVO", "sad");
        add("nuevo", "ADJETIVO_CALIFICATIVO", "new");
        add("nueva", "ADJETIVO_CALIFICATIVO", "new");
        add("viejo", "ADJETIVO_CALIFICATIVO", "old");
        add("vieja", "ADJETIVO_CALIFICATIVO", "old");
        add("caliente", "ADJETIVO_CALIFICATIVO", "hot");
        add("hermoso", "ADJETIVO_CALIFICATIVO", "beautiful");
        add("hermosa", "ADJETIVO_CALIFICATIVO", "beautiful");
        add("bonito", "ADJETIVO_CALIFICATIVO", "pretty");
        add("bonita", "ADJETIVO_CALIFICATIVO", "pretty");
        add("feo", "ADJETIVO_CALIFICATIVO", "ugly");
        add("fea", "ADJETIVO_CALIFICATIVO", "ugly");
        add("alto", "ADJETIVO_CALIFICATIVO", "tall");
        add("alta", "ADJETIVO_CALIFICATIVO", "tall");
        add("bajo", "ADJETIVO_CALIFICATIVO", "short");
        add("baja", "ADJETIVO_CALIFICATIVO", "short");
        add("largo", "ADJETIVO_CALIFICATIVO", "long");
        add("joven", "ADJETIVO_CALIFICATIVO", "young");
        add("fuerte", "ADJETIVO_CALIFICATIVO", "strong");
        add("débil", "ADJETIVO_CALIFICATIVO", "weak");
        add("debil", "ADJETIVO_CALIFICATIVO", "weak");
        add("rico", "ADJETIVO_CALIFICATIVO", "rich");
        add("rica", "ADJETIVO_CALIFICATIVO", "rich");
        add("pobre", "ADJETIVO_CALIFICATIVO", "poor");
        add("inteligente", "ADJETIVO_CALIFICATIVO", "smart");
        add("gracioso", "ADJETIVO_CALIFICATIVO", "funny");
        add("graciosa", "ADJETIVO_CALIFICATIVO", "funny");
        add("amable", "ADJETIVO_CALIFICATIVO", "kind");
        add("valiente", "ADJETIVO_CALIFICATIVO", "brave");
        add("limpio", "ADJETIVO_CALIFICATIVO", "clean");
        add("limpia", "ADJETIVO_CALIFICATIVO", "clean");
        add("sucio", "ADJETIVO_CALIFICATIVO", "dirty");
        add("sucia", "ADJETIVO_CALIFICATIVO", "dirty");
        add("oscuro", "ADJETIVO_CALIFICATIVO", "dark");
        add("oscura", "ADJETIVO_CALIFICATIVO", "dark");
        add("duro", "ADJETIVO_CALIFICATIVO", "hard");
        add("suave", "ADJETIVO_CALIFICATIVO", "soft");
        add("pesado", "ADJETIVO_CALIFICATIVO", "heavy");
        add("pesada", "ADJETIVO_CALIFICATIVO", "heavy");
        add("delgado", "ADJETIVO_CALIFICATIVO", "thin");
        add("delgada", "ADJETIVO_CALIFICATIVO", "thin");
        add("gordo", "ADJETIVO_CALIFICATIVO", "fat");
        add("gorda", "ADJETIVO_CALIFICATIVO", "fat");
        add("vacío", "ADJETIVO_CALIFICATIVO", "empty");
        add("vacio", "ADJETIVO_CALIFICATIVO", "empty");
        add("lleno", "ADJETIVO_CALIFICATIVO", "full");
        add("llena", "ADJETIVO_CALIFICATIVO", "full");
        add("libre", "ADJETIVO_CALIFICATIVO", "free");
        add("importante", "ADJETIVO_CALIFICATIVO", "important");
        add("diferente", "ADJETIVO_CALIFICATIVO", "different");
        add("posible", "ADJETIVO_CALIFICATIVO", "possible");

        /*
         * ══════════════════════════════════
         * ADVERBIOS DE TIEMPO
         * ══════════════════════════════════
         */
        add("today", "ADVERBIO_TIEMPO", "hoy");
        add("yesterday", "ADVERBIO_TIEMPO", "ayer");
        add("tomorrow", "ADVERBIO_TIEMPO", "mañana");
        add("now", "ADVERBIO_TIEMPO", "ahora");
        add("always", "ADVERBIO_TIEMPO", "siempre");
        add("never", "ADVERBIO_TIEMPO", "nunca");
        add("often", "ADVERBIO_TIEMPO", "frecuentemente");
        add("sometimes", "ADVERBIO_TIEMPO", "a veces");
        add("soon", "ADVERBIO_TIEMPO", "pronto");
        add("already", "ADVERBIO_TIEMPO", "ya");
        add("still", "ADVERBIO_TIEMPO", "todavía");
        add("again", "ADVERBIO_TIEMPO", "de nuevo");
        add("lately", "ADVERBIO_TIEMPO", "últimamente");
        add("recently", "ADVERBIO_TIEMPO", "recientemente");
        add("finally", "ADVERBIO_TIEMPO", "finalmente");
        add("hoy", "ADVERBIO_TIEMPO", "today");
        add("ayer", "ADVERBIO_TIEMPO", "yesterday");
        add("ahora", "ADVERBIO_TIEMPO", "now");
        add("siempre", "ADVERBIO_TIEMPO", "always");
        add("nunca", "ADVERBIO_TIEMPO", "never");
        add("jamás", "ADVERBIO_TIEMPO", "never");
        add("jamas", "ADVERBIO_TIEMPO", "never");
        add("pronto", "ADVERBIO_TIEMPO", "soon");
        add("todavía", "ADVERBIO_TIEMPO", "still");
        add("todavia", "ADVERBIO_TIEMPO", "still");
        add("ya", "ADVERBIO_TIEMPO", "already");
        add("después", "ADVERBIO_TIEMPO", "after");
        add("despues", "ADVERBIO_TIEMPO", "after");
        add("antes", "ADVERBIO_TIEMPO", "before");
        add("luego", "ADVERBIO_TIEMPO", "then");
        add("finalmente", "ADVERBIO_TIEMPO", "finally");
        add("recientemente", "ADVERBIO_TIEMPO", "recently");

        /*
         * ══════════════════════════════════
         * ADVERBIOS DE LUGAR
         * ══════════════════════════════════
         */
        add("here", "ADVERBIO_LUGAR", "aquí");
        add("there", "ADVERBIO_LUGAR", "allí");
        add("where", "ADVERBIO_LUGAR", "dónde");
        add("everywhere", "ADVERBIO_LUGAR", "en todas partes");
        add("nowhere", "ADVERBIO_LUGAR", "en ningún lugar");
        add("somewhere", "ADVERBIO_LUGAR", "en algún lugar");
        add("inside", "ADVERBIO_LUGAR", "adentro");
        add("outside", "ADVERBIO_LUGAR", "afuera");
        add("up", "ADVERBIO_LUGAR", "arriba");
        add("down", "ADVERBIO_LUGAR", "abajo");
        add("far", "ADVERBIO_LUGAR", "lejos");
        add("near", "ADVERBIO_LUGAR", "cerca");
        add("aquí", "ADVERBIO_LUGAR", "here");
        add("aqui", "ADVERBIO_LUGAR", "here");
        add("allí", "ADVERBIO_LUGAR", "there");
        add("alli", "ADVERBIO_LUGAR", "there");
        add("allá", "ADVERBIO_LUGAR", "there");
        add("alla", "ADVERBIO_LUGAR", "there");
        add("dónde", "ADVERBIO_LUGAR", "where");
        add("donde", "ADVERBIO_LUGAR", "where");
        add("adentro", "ADVERBIO_LUGAR", "inside");
        add("afuera", "ADVERBIO_LUGAR", "outside");
        add("arriba", "ADVERBIO_LUGAR", "up");
        add("abajo", "ADVERBIO_LUGAR", "down");
        add("lejos", "ADVERBIO_LUGAR", "far");
        add("cerca", "ADVERBIO_LUGAR", "near");

        /*
         * ══════════════════════════════════
         * ADVERBIOS DE MODO
         * ══════════════════════════════════
         */
        add("quickly", "ADVERBIO_MODO", "rápidamente");
        add("slowly", "ADVERBIO_MODO", "lentamente");
        add("well", "ADVERBIO_MODO", "bien");
        add("badly", "ADVERBIO_MODO", "mal");
        add("carefully", "ADVERBIO_MODO", "cuidadosamente");
        add("easily", "ADVERBIO_MODO", "fácilmente");
        add("quietly", "ADVERBIO_MODO", "silenciosamente");
        add("loudly", "ADVERBIO_MODO", "ruidosamente");
        add("together", "ADVERBIO_MODO", "juntos");
        add("alone", "ADVERBIO_MODO", "solo");
        add("hard", "ADVERBIO_MODO", "duro");
        add("how", "ADVERBIO_MODO", "cómo");
        add("rápidamente", "ADVERBIO_MODO", "quickly");
        add("rapidamente", "ADVERBIO_MODO", "quickly");
        add("lentamente", "ADVERBIO_MODO", "slowly");
        add("bien", "ADVERBIO_MODO", "well");
        add("mal", "ADVERBIO_MODO", "badly");
        add("cuidadosamente", "ADVERBIO_MODO", "carefully");
        add("fácilmente", "ADVERBIO_MODO", "easily");
        add("facilmente", "ADVERBIO_MODO", "easily");
        add("juntos", "ADVERBIO_MODO", "together");
        add("juntas", "ADVERBIO_MODO", "together");
        add("solo", "ADVERBIO_MODO", "alone");
        add("sola", "ADVERBIO_MODO", "alone");
        add("cómo", "ADVERBIO_MODO", "how");
        add("asi", "ADVERBIO_MODO", "like this");
        add("así", "ADVERBIO_MODO", "like this");

        /*
         * ══════════════════════════════════
         * ADVERBIOS DE CANTIDAD
         * ══════════════════════════════════
         */
        add("very", "ADVERBIO_CANTIDAD", "muy");
        add("too", "ADVERBIO_CANTIDAD", "demasiado");
        add("enough", "ADVERBIO_CANTIDAD", "suficiente");
        add("almost", "ADVERBIO_CANTIDAD", "casi");
        add("quite", "ADVERBIO_CANTIDAD", "bastante");
        add("much", "ADVERBIO_CANTIDAD", "mucho");
        add("more", "ADVERBIO_CANTIDAD", "más");
        add("less", "ADVERBIO_CANTIDAD", "menos");
        add("many", "ADVERBIO_CANTIDAD", "muchos");
        add("few", "ADVERBIO_CANTIDAD", "pocos");
        add("any", "ADVERBIO_CANTIDAD", "cualquier");
        add("all", "ADVERBIO_CANTIDAD", "todo");
        add("both", "ADVERBIO_CANTIDAD", "ambos");
        add("muy", "ADVERBIO_CANTIDAD", "very");
        add("demasiado", "ADVERBIO_CANTIDAD", "too");
        add("demasiada", "ADVERBIO_CANTIDAD", "too");
        add("bastante", "ADVERBIO_CANTIDAD", "quite");
        add("mucho", "ADVERBIO_CANTIDAD", "much");
        add("mucha", "ADVERBIO_CANTIDAD", "much");
        add("muchos", "ADVERBIO_CANTIDAD", "many");
        add("muchas", "ADVERBIO_CANTIDAD", "many");
        add("poco", "ADVERBIO_CANTIDAD", "little");
        add("poca", "ADVERBIO_CANTIDAD", "little");
        add("pocos", "ADVERBIO_CANTIDAD", "few");
        add("pocas", "ADVERBIO_CANTIDAD", "few");
        add("casi", "ADVERBIO_CANTIDAD", "almost");
        add("más", "ADVERBIO_CANTIDAD", "more");
        add("mas", "ADVERBIO_CANTIDAD", "more");
        add("menos", "ADVERBIO_CANTIDAD", "less");
        add("todo", "ADVERBIO_CANTIDAD", "all");
        add("toda", "ADVERBIO_CANTIDAD", "all");
        add("todos", "ADVERBIO_CANTIDAD", "all");
        add("todas", "ADVERBIO_CANTIDAD", "all");
        add("ambos", "ADVERBIO_CANTIDAD", "both");
        add("ambas", "ADVERBIO_CANTIDAD", "both");

        /*
         * ══════════════════════════════════
         * ADVERBIOS DE AFIRMACIÓN
         * ══════════════════════════════════
         */
        add("yes", "ADVERBIO_AFIRMACION", "sí");
        add("indeed", "ADVERBIO_AFIRMACION", "efectivamente");
        add("certainly", "ADVERBIO_AFIRMACION", "ciertamente");
        add("definitely", "ADVERBIO_AFIRMACION", "definitivamente");
        add("absolutely", "ADVERBIO_AFIRMACION", "absolutamente");
        add("of course", "ADVERBIO_AFIRMACION", "por supuesto");
        add("sí", "ADVERBIO_AFIRMACION", "yes");
        add("si", "ADVERBIO_AFIRMACION", "yes");
        add("claro", "ADVERBIO_AFIRMACION", "of course");
        add("ciertamente", "ADVERBIO_AFIRMACION", "certainly");
        add("efectivamente", "ADVERBIO_AFIRMACION", "indeed");
        add("definitivamente", "ADVERBIO_AFIRMACION", "definitely");
        add("exactamente", "ADVERBIO_AFIRMACION", "exactly");
        add("exacto", "ADVERBIO_AFIRMACION", "exactly");

        /*
         * ══════════════════════════════════
         * ADVERBIOS DE NEGACIÓN
         * ══════════════════════════════════
         */
        add("not", "ADVERBIO_NEGACION", "no");
        add("no", "ADVERBIO_NEGACION", "no");
        add("neither", "ADVERBIO_NEGACION", "tampoco");
        add("nor", "ADVERBIO_NEGACION", "ni");
        add("never", "ADVERBIO_NEGACION", "nunca");
        add("nothing", "ADVERBIO_NEGACION", "nada");
        add("nobody", "ADVERBIO_NEGACION", "nadie");
        add("nowhere", "ADVERBIO_NEGACION", "en ningún lugar");
        add("tampoco", "ADVERBIO_NEGACION", "neither");
        add("nada", "ADVERBIO_NEGACION", "nothing");
        add("nadie", "ADVERBIO_NEGACION", "nobody");

        /*
         * ══════════════════════════════════
         * ADVERBIOS DE DUDA
         * ══════════════════════════════════
         */
        add("maybe", "ADVERBIO_DUDA", "quizás");
        add("perhaps", "ADVERBIO_DUDA", "tal vez");
        add("probably", "ADVERBIO_DUDA", "probablemente");
        add("possibly", "ADVERBIO_DUDA", "posiblemente");
        add("apparently", "ADVERBIO_DUDA", "aparentemente");
        add("why", "ADVERBIO_DUDA", "por qué");
        add("when", "ADVERBIO_TIEMPO", "cuándo");
        add("quizás", "ADVERBIO_DUDA", "maybe");
        add("quizas", "ADVERBIO_DUDA", "maybe");
        add("tal vez", "ADVERBIO_DUDA", "perhaps");
        add("probablemente", "ADVERBIO_DUDA", "probably");
        add("posiblemente", "ADVERBIO_DUDA", "possibly");
        add("acaso", "ADVERBIO_DUDA", "perhaps");
        add("cuándo", "ADVERBIO_TIEMPO", "when");
        add("cuando", "ADVERBIO_TIEMPO", "when");
        add("aparentemente", "ADVERBIO_DUDA", "apparently");

        /*
         * ══════════════════════════════════
         * PREPOSICIONES
         * ══════════════════════════════════
         */
        add("to", "PREPOSICION", "a");
        add("in", "PREPOSICION", "en");
        add("on", "PREPOSICION", "sobre");
        add("at", "PREPOSICION", "en/a");
        add("of", "PREPOSICION", "de");
        add("for", "PREPOSICION", "para");
        add("with", "PREPOSICION", "con");
        add("from", "PREPOSICION", "desde");
        add("by", "PREPOSICION", "por");
        add("about", "PREPOSICION", "sobre");
        add("between", "PREPOSICION", "entre");
        add("without", "PREPOSICION", "sin");
        add("under", "PREPOSICION", "bajo");
        add("over", "PREPOSICION", "sobre");
        add("after", "PREPOSICION", "después de");
        add("before", "PREPOSICION", "antes de");
        add("during", "PREPOSICION", "durante");
        add("through", "PREPOSICION", "a través de");
        add("into", "PREPOSICION", "dentro de");
        add("onto", "PREPOSICION", "sobre");
        add("upon", "PREPOSICION", "sobre");
        add("against", "PREPOSICION", "contra");
        add("among", "PREPOSICION", "entre");
        add("within", "PREPOSICION", "dentro de");
        add("behind", "PREPOSICION", "detrás de");
        add("beside", "PREPOSICION", "al lado de");
        add("beyond", "PREPOSICION", "más allá de");
        add("despite", "PREPOSICION", "a pesar de");
        add("except", "PREPOSICION", "excepto");
        add("toward", "PREPOSICION", "hacia");
        add("towards", "PREPOSICION", "hacia");
        add("until", "PREPOSICION", "hasta");
        // EN: "to" como preposición (infinitive marker y direccional)
        add("to", "PREPOSICION", "a/hacia");
        add("de", "PREPOSICION", "of/from");
        add("en", "PREPOSICION", "in/on");
        add("para", "PREPOSICION", "for/to");
        add("con", "PREPOSICION", "with");
        add("por", "PREPOSICION", "for/by");
        add("sin", "PREPOSICION", "without");
        add("sobre", "PREPOSICION", "on/about");
        add("entre", "PREPOSICION", "between/among");
        add("bajo", "PREPOSICION", "under");
        add("desde", "PREPOSICION", "from/since");
        add("hasta", "PREPOSICION", "until/to");
        add("hacia", "PREPOSICION", "toward");
        add("durante", "PREPOSICION", "during");
        add("según", "PREPOSICION", "according to");
        add("segun", "PREPOSICION", "according to");
        add("ante", "PREPOSICION", "before/in front of");
        add("contra", "PREPOSICION", "against");
        add("tras", "PREPOSICION", "after/behind");

        /*
         * ══════════════════════════════════
         * CONJUNCIONES COORDINANTES
         * ══════════════════════════════════
         */
        // Copulativas
        add("and", "CONJUNCION_COPULATIVA", "y");
        add("y", "CONJUNCION_COPULATIVA", "and");
        add("e", "CONJUNCION_COPULATIVA", "and");
        add("nor", "CONJUNCION_COPULATIVA", "ni");
        add("ni", "CONJUNCION_COPULATIVA", "nor");
        // Adversativas
        add("but", "CONJUNCION_ADVERSATIVA", "pero");
        add("however", "CONJUNCION_ADVERSATIVA", "sin embargo");
        add("yet", "CONJUNCION_ADVERSATIVA", "sin embargo");
        add("nevertheless", "CONJUNCION_ADVERSATIVA", "no obstante");
        add("although", "CONJUNCION_ADVERSATIVA", "aunque");
        add("though", "CONJUNCION_ADVERSATIVA", "aunque");
        add("pero", "CONJUNCION_ADVERSATIVA", "but");
        add("sino", "CONJUNCION_ADVERSATIVA", "but rather");
        add("aunque", "CONJUNCION_ADVERSATIVA", "although");
        add("sin embargo", "CONJUNCION_ADVERSATIVA", "however");
        add("no obstante", "CONJUNCION_ADVERSATIVA", "nevertheless");
        // Disyuntivas
        add("or", "CONJUNCION_DISYUNTIVA", "o");
        add("either", "CONJUNCION_DISYUNTIVA", "o");
        add("o", "CONJUNCION_DISYUNTIVA", "or");
        add("u", "CONJUNCION_DISYUNTIVA", "or");
        add("ya sea", "CONJUNCION_DISYUNTIVA", "either");
        // Distributivas
        add("both", "CONJUNCION_DISTRIBUTIVA", "tanto...como");
        add("whether", "CONJUNCION_DISTRIBUTIVA", "ya sea");
        add("tanto", "CONJUNCION_DISTRIBUTIVA", "both");
        // Explicativas
        add("namely", "CONJUNCION_EXPLICATIVA", "a saber");
        add("osea", "CONJUNCION_EXPLICATIVA", "that is");
        add("o sea", "CONJUNCION_EXPLICATIVA", "that is");
        add("es decir", "CONJUNCION_EXPLICATIVA", "that is");

        /*
         * ══════════════════════════════════
         * CONJUNCIONES SUBORDINANTES
         * ══════════════════════════════════
         */
        // Causales
        add("because", "CONJUNCION_CAUSAL", "porque");
        add("since", "CONJUNCION_CAUSAL", "ya que");
        add("as", "CONJUNCION_CAUSAL", "ya que");
        add("porque", "CONJUNCION_CAUSAL", "because");
        add("pues", "CONJUNCION_CAUSAL", "since");
        add("ya que", "CONJUNCION_CAUSAL", "since");
        add("dado que", "CONJUNCION_CAUSAL", "given that");
        add("puesto que", "CONJUNCION_CAUSAL", "since");
        // Condicionales
        add("if", "CONJUNCION_CONDICIONAL", "si");
        add("unless", "CONJUNCION_CONDICIONAL", "a menos que");
        add("provided", "CONJUNCION_CONDICIONAL", "siempre que");
        add("a menos que", "CONJUNCION_CONDICIONAL", "unless");
        add("siempre que", "CONJUNCION_CONDICIONAL", "as long as");
        add("con tal que", "CONJUNCION_CONDICIONAL", "provided that");
        // Concesivas
        add("even though", "CONJUNCION_CONCESIVA", "aunque");
        add("even if", "CONJUNCION_CONCESIVA", "aunque");
        add("aun cuando", "CONJUNCION_CONCESIVA", "even if");
        add("a pesar de", "CONJUNCION_CONCESIVA", "despite");
        add("por más que", "CONJUNCION_CONCESIVA", "no matter how");
        // Comparativas
        add("than", "CONJUNCION_COMPARATIVA", "que");
        add("as...as", "CONJUNCION_COMPARATIVA", "tan...como");
        add("tan", "CONJUNCION_COMPARATIVA", "as");
        add("que", "CONJUNCION_COMPARATIVA", "than/that");
        // Finales
        add("so that", "CONJUNCION_FINAL", "para que");
        add("in order to", "CONJUNCION_FINAL", "para");
        add("para que", "CONJUNCION_FINAL", "so that");
        add("a fin de", "CONJUNCION_FINAL", "in order to");
        // Consecutivas
        add("so", "CONJUNCION_CONSECUTIVA", "entonces");
        add("therefore", "CONJUNCION_CONSECUTIVA", "por lo tanto");
        add("thus", "CONJUNCION_CONSECUTIVA", "así pues");
        add("hence", "CONJUNCION_CONSECUTIVA", "por ende");
        add("entonces", "CONJUNCION_CONSECUTIVA", "then/so");
        add("por lo tanto", "CONJUNCION_CONSECUTIVA", "therefore");
        add("por ende", "CONJUNCION_CONSECUTIVA", "hence");
        add("así pues", "CONJUNCION_CONSECUTIVA", "thus");
        add("por eso", "CONJUNCION_CONSECUTIVA", "that is why");
        // Sustantivas
        add("that", "CONJUNCION_SUSTANTIVA", "que");
        add("whether", "CONJUNCION_SUSTANTIVA", "si");
        add("if", "CONJUNCION_SUSTANTIVA", "si");

        /*
         * ══════════════════════════════════
         * INTERJECCIONES
         * ══════════════════════════════════
         */
        add("oh", "INTERJECCION", "oh");
        add("wow", "INTERJECCION", "vaya");
        add("hey", "INTERJECCION", "oye");
        add("ouch", "INTERJECCION", "ay");
        add("oops", "INTERJECCION", "ups");
        add("bravo", "INTERJECCION", "bravo");
        add("please", "INTERJECCION", "por favor");
        add("thanks", "INTERJECCION", "gracias");
        add("thank you", "INTERJECCION", "gracias");
        add("sorry", "INTERJECCION", "lo siento");
        add("excuse me", "INTERJECCION", "con permiso");
        add("hello", "INTERJECCION", "hola");
        add("hi", "INTERJECCION", "hola");
        add("goodbye", "INTERJECCION", "adiós");
        add("bye", "INTERJECCION", "adiós");
        add("ok", "INTERJECCION", "está bien");
        add("okay", "INTERJECCION", "está bien");
        add("ay", "INTERJECCION", "ouch");
        add("oye", "INTERJECCION", "hey");
        add("vaya", "INTERJECCION", "wow");
        add("ojalá", "INTERJECCION", "hopefully");
        add("ojala", "INTERJECCION", "hopefully");
        add("gracias", "INTERJECCION", "thanks");
        add("perdón", "INTERJECCION", "sorry");
        add("perdon", "INTERJECCION", "sorry");
        add("por favor", "INTERJECCION", "please");
        add("hola", "INTERJECCION", "hello");
        add("adiós", "INTERJECCION", "goodbye");
        add("adios", "INTERJECCION", "goodbye");
        add("ups", "INTERJECCION", "oops");
        add("basta", "INTERJECCION", "enough");
        add("bravo", "INTERJECCION", "bravo");

        /*
         * ══════════════════════════════════
         * CONTRACCIONES EN
         * ══════════════════════════════════
         */
        add("i'm", "CONTRACCION", "yo soy/estoy");
        add("i've", "CONTRACCION", "yo he");
        add("i'll", "CONTRACCION", "yo voy a");
        add("i'd", "CONTRACCION", "yo quisiera");
        add("you're", "CONTRACCION", "tú eres/estás");
        add("you've", "CONTRACCION", "tú has");
        add("you'll", "CONTRACCION", "tú vas a");
        add("he's", "CONTRACCION", "él es/está");
        add("he'll", "CONTRACCION", "él va a");
        add("she's", "CONTRACCION", "ella es/está");
        add("she'll", "CONTRACCION", "ella va a");
        add("it's", "CONTRACCION", "es/está");
        add("we're", "CONTRACCION", "nosotros somos/estamos");
        add("we've", "CONTRACCION", "nosotros hemos");
        add("we'll", "CONTRACCION", "nosotros vamos a");
        add("they're", "CONTRACCION", "ellos son/están");
        add("they've", "CONTRACCION", "ellos han");
        add("they'll", "CONTRACCION", "ellos van a");
        add("that's", "CONTRACCION", "eso es");
        add("there's", "CONTRACCION", "hay");
        add("there're", "CONTRACCION", "hay");
        add("here's", "CONTRACCION", "aquí está");
        add("don't", "CONTRACCION", "no");
        add("doesn't", "CONTRACCION", "no");
        add("didn't", "CONTRACCION", "no hizo");
        add("isn't", "CONTRACCION", "no es");
        add("aren't", "CONTRACCION", "no son");
        add("wasn't", "CONTRACCION", "no fue");
        add("weren't", "CONTRACCION", "no fueron");
        add("can't", "CONTRACCION", "no puedo");
        add("cannot", "CONTRACCION", "no puedo");
        add("won't", "CONTRACCION", "no voy a");
        add("wouldn't", "CONTRACCION", "no haría");
        add("shouldn't", "CONTRACCION", "no debería");
        add("couldn't", "CONTRACCION", "no podía");
        add("haven't", "CONTRACCION", "no he");
        add("hasn't", "CONTRACCION", "no ha");
        add("hadn't", "CONTRACCION", "no había");
        add("what's", "CONTRACCION", "qué es");
        add("who's", "CONTRACCION", "quién es");
        add("how's", "CONTRACCION", "cómo está");
        add("let's", "CONTRACCION", "vamos a");

        /*
         * ══════════════════════════════════
         * CONTRACCIONES ES
         * ══════════════════════════════════
         */
        add("al", "CONTRACCION", "to the");
        add("del", "CONTRACCION", "of the");

        /*
         * ══════════════════════════════════
         * PARTICIPIOS PASADOS Y GERUNDIOS
         * ══════════════════════════════════
         */
        // Gerun dios
        add("writing", "VERBO", "escribiendo");
        add("reading", "VERBO", "leyendo");
        add("playing", "VERBO", "jugando");
        add("running", "VERBO", "corriendo");
        add("eating", "VERBO", "comiendo");
        add("drinking", "VERBO", "bebiendo");
        add("walking", "VERBO", "caminando");
        add("working", "VERBO", "trabajando");
        add("studying", "VERBO", "estudiando");
        add("talking", "VERBO", "hablando");
        add("living", "VERBO", "viviendo");
        add("coming", "VERBO", "viniendo");
        add("going", "VERBO", "yendo");
        add("staying", "VERBO", "quedando");
        add("sitting", "VERBO", "sentándose");
        add("standing", "VERBO", "de pie");
        add("buying", "VERBO", "comprando");
        add("selling", "VERBO", "vendiendo");
        add("finding", "VERBO", "encontrando");
        add("looking", "VERBO", "mirando");
        add("asking", "VERBO", "preguntando");
        add("helping", "VERBO", "ayudando");
        add("knowing", "VERBO", "sabiendo");
        // Participios pasados FUTUROS
        add("found", "VERBO", "encontrado");
        add("seen", "VERBO", "visto");
        add("said", "VERBO", "dicho");
        add("given", "VERBO", "dado");
        add("made", "VERBO", "hecho");
        add("gone", "VERBO", "ido");
        add("taken", "VERBO", "tomado");
        add("called", "VERBO", "llamado");
        add("asked", "VERBO", "preguntado");
        add("worked", "VERBO", "trabajado");
        add("tried", "VERBO", "intentado");
        add("felt", "VERBO", "sentido");
        add("became", "VERBO", "se convirtió");
        add("left", "VERBO", "dejado");
        add("put", "VERBO", "puesto");
        add("meant", "VERBO", "significado");
        add("kept", "VERBO", "mantenido");
        add("let", "VERBO", "dejado");
        add("begun", "VERBO", "comenzado");
        add("seemed", "VERBO", "parecía");
        add("helped", "VERBO", "ayudado");
        add("talked", "VERBO", "hablado");
        add("turned", "VERBO", "girado");
        add("started", "VERBO", "comenzado");
        add("showed", "VERBO", "mostrado");
        add("heard", "VERBO", "oído");
        add("played", "VERBO", "jugado");
        add("followed", "VERBO", "seguido");
        add("let", "VERBO", "dejado");
        add("ended", "VERBO", "terminado");
        add("told", "VERBO", "contado");

        // Participios españoles
        add("encontrado", "VERBO", "found");
        add("encontrada", "VERBO", "found");
        add("visto", "VERBO", "seen");
        add("vista", "VERBO", "seen");
        add("hecho", "VERBO", "made/done");
        add("hecha", "VERBO", "made/done");
        add("dicho", "VERBO", "said");
        add("dicha", "VERBO", "said");
        add("dado", "VERBO", "given");
        add("dada", "VERBO", "given");
        add("ido", "VERBO", "gone");
        add("tomado", "VERBO", "taken");
        add("tomada", "VERBO", "taken");
        add("llamado", "VERBO", "called");
        add("llamada", "VERBO", "called");
        add("escrito", "VERBO", "written");
        add("escrita", "VERBO", "written");
        add("comido", "VERBO", "eaten");
        add("bebido", "VERBO", "drunk");
        add("corrido", "VERBO", "run");

        /*
         * ══════════════════════════════════
         * ADVERBIOS DE CANTIDAD Y MODO
         * ══════════════════════════════════
         */
        add("very", "ADVERBIO_CANTIDAD", "muy");
        add("too", "ADVERBIO_CANTIDAD", "demasiado");
        add("so", "ADVERBIO_CANTIDAD", "tan");
        add("much", "ADVERBIO_CANTIDAD", "mucho");
        add("many", "ADVERBIO_CANTIDAD", "muchos");
        add("little", "ADVERBIO_CANTIDAD", "poco");
        add("few", "ADVERBIO_CANTIDAD", "pocos");
        add("more", "ADVERBIO_CANTIDAD", "más");
        add("less", "ADVERBIO_CANTIDAD", "menos");
        add("most", "ADVERBIO_CANTIDAD", "la mayoría");
        add("least", "ADVERBIO_CANTIDAD", "lo menos");
        add("enough", "ADVERBIO_CANTIDAD", "suficiente");
        // Modo
        add("well", "ADVERBIO_MODO", "bien");
        add("badly", "ADVERBIO_MODO", "mal");
        add("easily", "ADVERBIO_MODO", "fácilmente");
        add("quickly", "ADVERBIO_MODO", "rápidamente");
        add("slowly", "ADVERBIO_MODO", "lentamente");
        add("carefully", "ADVERBIO_MODO", "cuidadosamente");
        add("exactly", "ADVERBIO_MODO", "exactamente");
        add("perfectly", "ADVERBIO_MODO", "perfectamente");
        add("mostly", "ADVERBIO_MODO", "principalmente");
        add("simply", "ADVERBIO_MODO", "simplemente");
        add("only", "ADVERBIO_MODO", "solo/solo");
        add("just", "ADVERBIO_MODO", "justo");

        // Español - adverbios de cantidad y modo
        add("muy", "ADVERBIO_CANTIDAD", "very");
        add("demasiado", "ADVERBIO_CANTIDAD", "too");
        add("tan", "ADVERBIO_CANTIDAD", "so");
        add("mucho", "ADVERBIO_CANTIDAD", "much");
        add("muchos", "ADVERBIO_CANTIDAD", "many");
        add("pocas", "ADVERBIO_CANTIDAD", "few");
        add("pocas", "ADVERBIO_CANTIDAD", "few");
        add("poco", "ADVERBIO_CANTIDAD", "little");
        add("más", "ADVERBIO_CANTIDAD", "more");
        add("menos", "ADVERBIO_CANTIDAD", "less");
        add("bien", "ADVERBIO_MODO", "well");
        add("mal", "ADVERBIO_MODO", "badly");
        add("fácilmente", "ADVERBIO_MODO", "easily");
        add("facilmente", "ADVERBIO_MODO", "easily");
        add("rápidamente", "ADVERBIO_MODO", "quickly");
        add("rapidamente", "ADVERBIO_MODO", "quickly");
        add("lentamente", "ADVERBIO_MODO", "slowly");
        add("cuidadosamente", "ADVERBIO_MODO", "carefully");
        add("perfectamente", "ADVERBIO_MODO", "perfectly");
        add("principalmente", "ADVERBIO_MODO", "mostly");
        add("simplemente", "ADVERBIO_MODO", "simply");
        add("solo", "ADVERBIO_MODO", "only");
        add("solamente", "ADVERBIO_MODO", "only");

        /*
         * ══════════════════════════════════
         * MODALES Y AUXILIARES ADICIONALES
         * ══════════════════════════════════
         */
        add("will", "VERBO", "va a");
        add("would", "VERBO", "haría");
        add("shall", "VERBO", "debo");
        add("should", "VERBO", "debería");
        add("must", "VERBO", "debe");
        add("could", "VERBO", "podría");
        add("can", "VERBO", "puede");
        add("may", "VERBO", "puede");
        add("might", "VERBO", "podría");
        add("ought", "VERBO", "debería");
        add("dare", "VERBO", "atrever");
        add("need", "VERBO", "necesitar");
        add("used to", "VERBO", "solía");
        // Español
        add("será", "VERBO", "will be");
        add("sería", "VERBO", "would be");
        add("debo", "VERBO", "should");
        add("debe", "VERBO", "must");
        add("puede", "VERBO", "may/can");
        add("podría", "VERBO", "could");
        add("podrá", "VERBO", "will be able");

        /*
         * ══════════════════════════════════
         * VERBOS ESPAÑOLES ADICIONALES
         * (CONJUGACIONES FALTANTES)
         * ══════════════════════════════════
         */
        add("quería", "VERBO", "wanted");
        add("querían", "VERBO", "wanted");
        add("querías", "VERBO", "wanted");
        add("podía", "VERBO", "could");
        add("podían", "VERBO", "could");
        add("podías", "VERBO", "could");
        add("estaba", "VERBO", "was");
        add("estaban", "VERBO", "were");
        add("estabas", "VERBO", "were");
        add("estoy", "VERBO", "am");
        add("estás", "VERBO", "are");
        add("estas", "VERBO", "are");
        add("está", "VERBO", "is");
        add("estamos", "VERBO", "are");
        add("están", "VERBO", "are");
        add("iba", "VERBO", "was going");
        add("ibas", "VERBO", "were going");
        add("iban", "VERBO", "were going");
        add("íbamos", "VERBO", "were going");
        add("ibamos", "VERBO", "were going");
        // Fix: 'he' eliminado aquí para no sobreescribir el PRONOMBRE_PERSONAL 'he' (inglés)
        // El auxiliar español 'he' se deduce del contexto cuando 'hemos/han' también están presentes.
        add("has", "VERBO", "has");
        add("hemos", "VERBO", "have");
        add("han", "VERBO", "have");
        add("he comido", "VERBO", "have eaten");
        add("hemos comido", "VERBO", "have eaten");
        add("han comido", "VERBO", "have eaten");
        add("ha comido", "VERBO", "has eaten");
        add("había", "VERBO", "had");
        add("habían", "VERBO", "had");
        add("había comido", "VERBO", "had eaten");
        add("habían comido", "VERBO", "had eaten");
        add("habian", "VERBO", "had");
        add("comido", "VERBO", "eaten");

        /*
         * ══════════════════════════════════
         * ADJETIVOS ADICIONALES Y CON GÉNERO
         * ══════════════════════════════════
         */
        // Más adjetivos en inglés
        add("wet", "ADJETIVO_CALIFICATIVO", "mojado");
        add("dry", "ADJETIVO_CALIFICATIVO", "seco");
        add("warm", "ADJETIVO_CALIFICATIVO", "cálido");
        add("cool", "ADJETIVO_CALIFICATIVO", "fresco");
        add("bright", "ADJETIVO_CALIFICATIVO", "brillante");
        add("dark", "ADJETIVO_CALIFICATIVO", "oscuro");
        add("loud", "ADJETIVO_CALIFICATIVO", "ruidoso");
        add("quiet", "ADJETIVO_CALIFICATIVO", "silencioso");
        add("sick", "ADJETIVO_CALIFICATIVO", "enfermo");
        add("healthy", "ADJETIVO_CALIFICATIVO", "sano");
        add("fair", "ADJETIVO_CALIFICATIVO", "justo");
        add("sweet", "ADJETIVO_CALIFICATIVO", "dulce");
        add("bitter", "ADJETIVO_CALIFICATIVO", "amargo");
        add("salty", "ADJETIVO_CALIFICATIVO", "salado");
        add("sour", "ADJETIVO_CALIFICATIVO", "agrio");
        add("sharp", "ADJETIVO_CALIFICATIVO", "afilado");
        add("dull", "ADJETIVO_CALIFICATIVO", "opaco");
        add("smooth", "ADJETIVO_CALIFICATIVO", "suave");
        add("rough", "ADJETIVO_CALIFICATIVO", "áspero");
        add("rough", "ADJETIVO_CALIFICATIVO", "aspero");
        add("modern", "ADJETIVO_CALIFICATIVO", "moderno");
        add("ancient", "ADJETIVO_CALIFICATIVO", "antiguo");
        add("normal", "ADJETIVO_CALIFICATIVO", "normal");
        add("strange", "ADJETIVO_CALIFICATIVO", "extraño");
        add("excited", "ADJETIVO_CALIFICATIVO", "emocionado");
        add("quiet", "ADJETIVO_CALIFICATIVO", "tranquilo");
        add("calm", "ADJETIVO_CALIFICATIVO", "tranquilo");
        add("angry", "ADJETIVO_CALIFICATIVO", "enojado");
        add("lazy", "ADJETIVO_CALIFICATIVO", "perezoso");
        add("diligent", "ADJETIVO_CALIFICATIVO", "diligente");
        add("honest", "ADJETIVO_CALIFICATIVO", "honesto");
        add("proud", "ADJETIVO_CALIFICATIVO", "orgulloso");
        add("ashamed", "ADJETIVO_CALIFICATIVO", "avergonzado");

        // Adjetivos españoles CON GÉNERO (para concordancia)
        add("mojada", "ADJETIVO_CALIFICATIVO", "wet");
        add("mojado", "ADJETIVO_CALIFICATIVO", "wet");
        add("mojados", "ADJETIVO_CALIFICATIVO", "wet");
        add("mojadas", "ADJETIVO_CALIFICATIVO", "wet");
        add("seco", "ADJETIVO_CALIFICATIVO", "dry");
        add("seca", "ADJETIVO_CALIFICATIVO", "dry");
        add("secos", "ADJETIVO_CALIFICATIVO", "dry");
        add("secas", "ADJETIVO_CALIFICATIVO", "dry");
        add("cálido", "ADJETIVO_CALIFICATIVO", "warm");
        add("calido", "ADJETIVO_CALIFICATIVO", "warm");
        add("cálida", "ADJETIVO_CALIFICATIVO", "warm");
        add("calida", "ADJETIVO_CALIFICATIVO", "warm");
        add("fresco", "ADJETIVO_CALIFICATIVO", "cool");
        add("fresca", "ADJETIVO_CALIFICATIVO", "cool");
        add("frescos", "ADJETIVO_CALIFICATIVO", "cool");
        add("frescas", "ADJETIVO_CALIFICATIVO", "cool");
        add("fría", "ADJETIVO_CALIFICATIVO", "cold");
        add("frio", "ADJETIVO_CALIFICATIVO", "cold");
        add("frío", "ADJETIVO_CALIFICATIVO", "cold");
        add("fría", "ADJETIVO_CALIFICATIVO", "cold");
        add("fríos", "ADJETIVO_CALIFICATIVO", "cold");
        add("frios", "ADJETIVO_CALIFICATIVO", "cold");
        add("frías", "ADJETIVO_CALIFICATIVO", "cold");
        add("frías", "ADJETIVO_CALIFICATIVO", "cold");
        add("brillante", "ADJETIVO_CALIFICATIVO", "bright");
        add("brillantes", "ADJETIVO_CALIFICATIVO", "bright");
        add("oscura", "ADJETIVO_CALIFICATIVO", "dark");
        add("oscuro", "ADJETIVO_CALIFICATIVO", "dark");
        add("oscuros", "ADJETIVO_CALIFICATIVO", "dark");
        add("oscuras", "ADJETIVO_CALIFICATIVO", "dark");
        add("ruidoso", "ADJETIVO_CALIFICATIVO", "loud");
        add("ruidosa", "ADJETIVO_CALIFICATIVO", "loud");
        add("ruidosos", "ADJETIVO_CALIFICATIVO", "loud");
        add("ruidosas", "ADJETIVO_CALIFICATIVO", "loud");
        add("silencioso", "ADJETIVO_CALIFICATIVO", "quiet");
        add("silenciosa", "ADJETIVO_CALIFICATIVO", "quiet");
        add("silenciosos", "ADJETIVO_CALIFICATIVO", "quiet");
        add("silenciosas", "ADJETIVO_CALIFICATIVO", "quiet");
        add("tranquilo", "ADJETIVO_CALIFICATIVO", "calm");
        add("tranquila", "ADJETIVO_CALIFICATIVO", "calm");
        add("tranquilos", "ADJETIVO_CALIFICATIVO", "calm");
        add("tranquilas", "ADJETIVO_CALIFICATIVO", "calm");
        add("enojado", "ADJETIVO_CALIFICATIVO", "angry");
        add("enojada", "ADJETIVO_CALIFICATIVO", "angry");
        add("enojados", "ADJETIVO_CALIFICATIVO", "angry");
        add("enojadas", "ADJETIVO_CALIFICATIVO", "angry");
        add("enojado", "ADJETIVO_CALIFICATIVO", "angry");
        add("perezoso", "ADJETIVO_CALIFICATIVO", "lazy");
        add("perezosa", "ADJETIVO_CALIFICATIVO", "lazy");
        add("perezosos", "ADJETIVO_CALIFICATIVO", "lazy");
        add("perezosas", "ADJETIVO_CALIFICATIVO", "lazy");
        add("diligente", "ADJETIVO_CALIFICATIVO", "diligent");
        add("diligentes", "ADJETIVO_CALIFICATIVO", "diligent");
        add("honesto", "ADJETIVO_CALIFICATIVO", "honest");
        add("honesta", "ADJETIVO_CALIFICATIVO", "honest");
        add("honestos", "ADJETIVO_CALIFICATIVO", "honest");
        add("honestas", "ADJETIVO_CALIFICATIVO", "honest");
        add("orgulloso", "ADJETIVO_CALIFICATIVO", "proud");
        add("orgullosa", "ADJETIVO_CALIFICATIVO", "proud");
        add("orgullosos", "ADJETIVO_CALIFICATIVO", "proud");
        add("orgullosas", "ADJETIVO_CALIFICATIVO", "proud");
        add("avergonzado", "ADJETIVO_CALIFICATIVO", "ashamed");
        add("avergonzada", "ADJETIVO_CALIFICATIVO", "ashamed");
        add("avergonzados", "ADJETIVO_CALIFICATIVO", "ashamed");
        add("avergonzadas", "ADJETIVO_CALIFICATIVO", "ashamed");
        add("buen", "ADJETIVO_CALIFICATIVO", "good");
        add("mal", "ADJETIVO_CALIFICATIVO", "bad");
        add("nueva", "ADJETIVO_CALIFICATIVO", "new");
        add("nuevos", "ADJETIVO_CALIFICATIVO", "new");
        add("nuevas", "ADJETIVO_CALIFICATIVO", "new");
        add("vieja", "ADJETIVO_CALIFICATIVO", "old");
        add("viejos", "ADJETIVO_CALIFICATIVO", "old");
        add("viejas", "ADJETIVO_CALIFICATIVO", "old");

        /*
         * ══════════════════════════════════
         * SUSTANTIVOS ADICIONALES
         * ══════════════════════════════════
         */
        add("friend", "SUSTANTIVO", "amigo");
        add("english", "ADJETIVO_CALIFICATIVO", "ingles");
        add("grammar", "SUSTANTIVO", "gramatica");
        add("library", "SUSTANTIVO", "biblioteca");
        add("mountain", "SUSTANTIVO", "montaña");
        add("beach", "SUSTANTIVO", "playa");
        add("ocean", "SUSTANTIVO", "océano");
        add("lake", "SUSTANTIVO", "lago");
        add("island", "SUSTANTIVO", "isla");
        add("forest", "SUSTANTIVO", "bosque");
        add("desert", "SUSTANTIVO", "desierto");
        add("book", "SUSTANTIVO", "libro");
        add("newspaper", "SUSTANTIVO", "periódico");
        add("magazine", "SUSTANTIVO", "revista");
        add("movie", "SUSTANTIVO", "película");
        add("music", "SUSTANTIVO", "música");
        add("song", "SUSTANTIVO", "canción");
        add("dance", "SUSTANTIVO", "baile");
        add("sport", "SUSTANTIVO", "deporte");
        add("game", "SUSTANTIVO", "juego");
        add("toy", "SUSTANTIVO", "juguete");
        add("dress", "SUSTANTIVO", "vestido");
        add("shirt", "SUSTANTIVO", "camisa");
        add("pants", "SUSTANTIVO", "pantalones");
        add("shoe", "SUSTANTIVO", "zapato");
        add("coat", "SUSTANTIVO", "abrigo");
        add("hat", "SUSTANTIVO", "sombrero");
        add("bag", "SUSTANTIVO", "bolsa");
        add("wallet", "SUSTANTIVO", "cartera");
        add("watch", "SUSTANTIVO", "reloj");
        add("ring", "SUSTANTIVO", "anillo");
        add("necklace", "SUSTANTIVO", "collar");
        add("bracelet", "SUSTANTIVO", "pulsera");
        add("eye", "SUSTANTIVO", "ojo");
        add("ear", "SUSTANTIVO", "oído");
        add("nose", "SUSTANTIVO", "nariz");
        add("mouth", "SUSTANTIVO", "boca");
        add("tooth", "SUSTANTIVO", "diente");
        add("hand", "SUSTANTIVO", "mano");
        add("foot", "SUSTANTIVO", "pie");
        add("arm", "SUSTANTIVO", "brazo");
        add("leg", "SUSTANTIVO", "pierna");
        add("head", "SUSTANTIVO", "cabeza");
        add("heart", "SUSTANTIVO", "corazón");
        add("blood", "SUSTANTIVO", "sangre");
        add("bone", "SUSTANTIVO", "hueso");
        add("skin", "SUSTANTIVO", "piel");
        add("hair", "SUSTANTIVO", "cabello");
        add("button", "SUSTANTIVO", "botón");
        add("key", "SUSTANTIVO", "llave");
        add("lock", "SUSTANTIVO", "cerradura");

        /*
         * ══════════════════════════════════
         * SUSTANTIVOS ESPAÑOLES ADICIONALES
         * ══════════════════════════════════
         */
        add("semana", "SUSTANTIVO", "week");
        add("fin de semana", "SUSTANTIVO", "weekend");
        add("amiga", "SUSTANTIVO", "friend");
        add("biblioteca", "SUSTANTIVO", "library");
        add("película", "SUSTANTIVO", "movie");
        add("canción", "SUSTANTIVO", "song");
        add("baile", "SUSTANTIVO", "dance");
        add("deporte", "SUSTANTIVO", "sport");
        add("juguete", "SUSTANTIVO", "toy");
        add("vestido", "SUSTANTIVO", "dress");
        add("camisa", "SUSTANTIVO", "shirt");
        add("pantalones", "SUSTANTIVO", "pants");
        add("zapato", "SUSTANTIVO", "shoe");
        add("abrigo", "SUSTANTIVO", "coat");
        add("sombrero", "SUSTANTIVO", "hat");
        add("bolsa", "SUSTANTIVO", "bag");
        add("reloj", "SUSTANTIVO", "watch");
        add("anillo", "SUSTANTIVO", "ring");
        add("collar", "SUSTANTIVO", "necklace");
        add("pulsera", "SUSTANTIVO", "bracelet");
        add("ojo", "SUSTANTIVO", "eye");
        add("oído", "SUSTANTIVO", "ear");
        add("nariz", "SUSTANTIVO", "nose");
        add("boca", "SUSTANTIVO", "mouth");
        add("diente", "SUSTANTIVO", "tooth");
        add("mano", "SUSTANTIVO", "hand");
        add("pie", "SUSTANTIVO", "foot");
        add("brazo", "SUSTANTIVO", "arm");
        add("pierna", "SUSTANTIVO", "leg");
        add("cabeza", "SUSTANTIVO", "head");
        add("corazón", "SUSTANTIVO", "heart");
        add("corazon", "SUSTANTIVO", "heart");
        add("sangre", "SUSTANTIVO", "blood");
        add("hueso", "SUSTANTIVO", "bone");
        add("piel", "SUSTANTIVO", "skin");
        add("cabello", "SUSTANTIVO", "hair");
        add("botón", "SUSTANTIVO", "button");
        add("boton", "SUSTANTIVO", "button");
        add("llave", "SUSTANTIVO", "key");
        add("cerradura", "SUSTANTIVO", "lock");
        add("océano", "SUSTANTIVO", "ocean");
        add("oceano", "SUSTANTIVO", "ocean");
        add("lago", "SUSTANTIVO", "lake");
        add("isla", "SUSTANTIVO", "island");
        add("bosque", "SUSTANTIVO", "forest");
        add("desierto", "SUSTANTIVO", "desert");
        add("periódico", "SUSTANTIVO", "newspaper");
        add("periodico", "SUSTANTIVO", "newspaper");
        add("revista", "SUSTANTIVO", "magazine");

        cargarVocabularioLocalAmpliado();

        // Traducciones forzadas para términos frecuentes y ambiguos.
        traduccionesForzadasES_EN.put("hola", "hello");
        traduccionesForzadasES_EN.put("adiós", "goodbye");
        traduccionesForzadasES_EN.put("adios", "goodbye");
        traduccionesForzadasES_EN.put("gracias", "thanks");
        traduccionesForzadasES_EN.put("porfavor", "please");
        traduccionesForzadasES_EN.put("por favor", "please");

        traduccionesForzadasEN_ES.put("hello", "hola");
        traduccionesForzadasEN_ES.put("hi", "hola");
        traduccionesForzadasEN_ES.put("goodbye", "adiós");
        traduccionesForzadasEN_ES.put("thanks", "gracias");
        traduccionesForzadasEN_ES.put("thankyou", "gracias");
        traduccionesForzadasEN_ES.put("thank you", "gracias");
    }

    private static void add(String palabra, String tipo, String traduccion) {
        String clave = palabra.toLowerCase();
        mapa.put(clave, new String[] { tipo, traduccion });

        if (clave.contains(" ")) {
            expresionCompactaAOriginal.put(clave.replace(" ", ""), clave);
        }
    }

    private static void addBidireccionales(String tipo, String[][] pares) {
        for (String[] par : pares) {
            add(par[0], tipo, par[1]);
            add(par[1], tipo, par[0]);
        }
    }

    private static void cargarVocabularioLocalAmpliado() {
        /*
         * Refuerzo del diccionario local para cubrir palabras de uso academico,
         * tecnologico y cotidiano sin salir de esta misma clase.
         */
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"computer", "computadora"},
                {"laptop", "computadora portatil"},
                {"keyboard", "teclado"},
                {"mouse", "raton"},
                {"screen", "pantalla"},
                {"monitor", "monitor"},
                {"printer", "impresora"},
                {"camera", "camara"},
                {"microphone", "microfono"},
                {"speaker", "bocina"},
                {"volume", "volumen"},
                {"file", "archivo"},
                {"folder", "carpeta"},
                {"document", "documento"},
                {"image", "imagen"},
                {"video", "video"},
                {"audio", "audio"},
                {"network", "red"},
                {"internet", "internet"},
                {"website", "sitio web"},
                {"web page", "pagina web"},
                {"server", "servidor"},
                {"client", "cliente"},
                {"database", "base de datos"},
                {"data base", "base de datos"},
                {"source code", "codigo fuente"},
                {"code", "codigo"},
                {"program", "programa"},
                {"application", "aplicacion"},
                {"app", "aplicacion"},
                {"software", "software"},
                {"hardware", "hardware"},
                {"system", "sistema"},
                {"user", "usuario"},
                {"password", "contrasena"},
                {"account", "cuenta"},
                {"project", "proyecto"},
                {"task", "tarea"},
                {"error", "error"},
                {"result", "resultado"},
                {"translation", "traduccion"},
                {"translator", "traductor"},
                {"dictionary", "diccionario"},
                {"language", "idioma"},
                {"word", "palabra"},
                {"sentence", "oracion"},
                {"paragraph", "parrafo"},
                {"text", "texto"},
                {"grammar rule", "regla gramatical"},
                {"pronunciation", "pronunciacion"},
                {"voice", "voz"},
                {"command", "comando"},
                {"school", "escuela"},
                {"university", "universidad"},
                {"class", "clase"},
                {"course", "curso"},
                {"teacher", "maestro"},
                {"professor", "profesor"},
                {"student", "estudiante"},
                {"homework", "tarea"},
                {"exam", "examen"},
                {"grade", "calificacion"},
                {"notebook", "cuaderno"},
                {"pencil", "lapiz"},
                {"pen", "lapicero"},
                {"desk", "escritorio"},
                {"board", "pizarra"},
                {"lesson", "leccion"},
                {"question", "pregunta"},
                {"answer", "respuesta"},
                {"explanation", "explicacion"},
                {"company", "empresa"},
                {"customer", "cliente"},
                {"product", "producto"},
                {"price", "precio"},
                {"sale", "venta"},
                {"purchase", "compra"},
                {"store", "tienda"},
                {"market", "mercado"},
                {"invoice", "factura"},
                {"payment", "pago"},
                {"money", "dinero"},
                {"bank", "banco"},
                {"job", "trabajo"},
                {"meeting", "reunion"},
                {"report", "reporte"},
                {"schedule", "horario"},
                {"house", "casa"},
                {"room", "habitacion"},
                {"kitchen", "cocina"},
                {"bathroom", "bano"},
                {"door", "puerta"},
                {"window", "ventana"},
                {"table", "mesa"},
                {"chair", "silla"},
                {"bed", "cama"},
                {"car", "carro"},
                {"bus", "bus"},
                {"street", "calle"},
                {"city", "ciudad"},
                {"country", "pais"},
                {"family", "familia"},
                {"mother", "madre"},
                {"father", "padre"},
                {"brother", "hermano"},
                {"sister", "hermana"},
                {"child", "nino"},
                {"food", "comida"},
                {"water", "agua"},
                {"coffee", "cafe"},
                {"tea", "te"},
                {"bread", "pan"},
                {"rice", "arroz"},
                {"meat", "carne"},
                {"chicken", "pollo"},
                {"fish", "pescado"},
                {"fruit", "fruta"},
                {"apple", "manzana"},
                {"banana", "banano"},
                {"vegetable", "vegetal"},
                {"milk", "leche"},
                {"sugar", "azucar"},
                {"salt", "sal"},
                {"breakfast", "desayuno"},
                {"lunch", "almuerzo"},
                {"dinner", "cena"},
                {"doctor", "doctor"},
                {"hospital", "hospital"},
                {"medicine", "medicina"},
                {"pain", "dolor"},
                {"fever", "fiebre"},
                {"cough", "tos"},
                {"headache", "dolor de cabeza"},
                {"airport", "aeropuerto"},
                {"hotel", "hotel"},
                {"ticket", "boleto"},
                {"trip", "viaje"},
                {"map", "mapa"},
                {"time", "tiempo"},
                {"hour", "hora"},
                {"minute", "minuto"},
                {"second", "segundo"},
                {"day", "dia"},
                {"week", "semana"},
                {"month", "mes"},
                {"year", "ano"},
                {"morning", "manana"},
                {"afternoon", "tarde"},
                {"night", "noche"},
                {"weather", "clima"},
                {"rain", "lluvia"},
                {"sun", "sol"},
                {"cloud", "nube"},
                {"wind", "viento"},
                {"heat", "calor"},
                {"cold", "frio"},
                {"frontend", "frontend"},
                {"backend", "backend"},
                {"interface", "interfaz"},
                {"user interface", "interfaz de usuario"},
                {"artificial intelligence", "inteligencia artificial"},
                {"machine learning", "aprendizaje automatico"},
                {"spring boot", "spring boot"},
                {"react", "react"},
                {"browser", "navegador"},
                {"deployment", "despliegue"},
                {"hosting", "alojamiento"},
                {"domain", "dominio"},
                {"port", "puerto"},
                {"request", "solicitud"},
                {"response", "respuesta"},
                {"connection", "conexion"},
                {"permission", "permiso"},
                {"status", "estado"},
                {"configuration", "configuracion"},
                {"environment", "entorno"},
                {"key", "clave"},
                {"api key", "clave api"},
                {"token", "token"},
                {"security", "seguridad"},
                {"version", "version"}
        });

        addBidireccionales("SUSTANTIVO", new String[][] {
                {"science", "ciencia"},
                {"technology", "tecnologia"},
                {"mathematics", "matematicas"},
                {"math", "matematica"},
                {"history", "historia"},
                {"geography", "geografia"},
                {"biology", "biologia"},
                {"chemistry", "quimica"},
                {"physics", "fisica"},
                {"literature", "literatura"},
                {"english", "ingles"},
                {"spanish", "espanol"},
                {"reading", "lectura"},
                {"writing", "escritura"},
                {"listening", "escucha"},
                {"speaking", "habla"},
                {"research", "investigacion"},
                {"definition", "definicion"},
                {"meaning", "significado"},
                {"example", "ejemplo"},
                {"exercise", "ejercicio"},
                {"practice", "practica"},
                {"topic", "tema"},
                {"subject", "materia"},
                {"rule", "regla"},
                {"tense", "tiempo verbal"},
                {"present tense", "presente"},
                {"past tense", "pasado"},
                {"future tense", "futuro"},
                {"verb tense", "tiempo verbal"},
                {"noun", "sustantivo"},
                {"verb", "verbo"},
                {"adjective", "adjetivo"},
                {"adverb", "adverbio"},
                {"pronoun", "pronombre"},
                {"preposition", "preposicion"},
                {"conjunction", "conjuncion"},
                {"article", "articulo"},
                {"synonym", "sinonimo"},
                {"antonym", "antonimo"}
        });

        addBidireccionales("ADJETIVO_CALIFICATIVO", new String[][] {
                {"important", "importante"},
                {"available", "disponible"},
                {"ready", "listo"},
                {"free", "gratis"},
                {"busy", "ocupado"},
                {"possible", "posible"},
                {"impossible", "imposible"},
                {"easy", "facil"},
                {"difficult", "dificil"},
                {"correct", "correcto"},
                {"incorrect", "incorrecto"},
                {"complete", "completo"},
                {"incomplete", "incompleto"},
                {"active", "activo"},
                {"inactive", "inactivo"},
                {"public", "publico"},
                {"private", "privado"},
                {"secure", "seguro"},
                {"insecure", "inseguro"},
                {"local", "local"},
                {"global", "global"},
                {"fast", "rapido"},
                {"slow", "lento"},
                {"clean", "limpio"},
                {"dirty", "sucio"},
                {"full", "lleno"},
                {"empty", "vacio"},
                {"open", "abierto"},
                {"closed", "cerrado"},
                {"strong", "fuerte"},
                {"weak", "debil"},
                {"useful", "util"},
                {"necessary", "necesario"},
                {"professional", "profesional"},
                {"academic", "academico"},
                {"modern", "moderno"},
                {"simple", "simple"},
                {"complex", "complejo"},
                {"automatic", "automatico"},
                {"manual", "manual"},
                {"digital", "digital"},
                {"visual", "visual"},
                {"functional", "funcional"},
                {"responsive", "adaptable"},
                {"stable", "estable"},
                {"experimental", "experimental"},
                {"native", "nativo"},
                {"external", "externo"},
                {"internal", "interno"},
                {"personal", "personal"},
                {"general", "general"},
                {"specific", "especifico"},
                {"main", "principal"},
                {"secondary", "secundario"}
        });

        addBidireccionales("VERBO", new String[][] {
                {"open", "abrir"},
                {"close", "cerrar"},
                {"start", "iniciar"},
                {"stop", "detener"},
                {"save", "guardar"},
                {"load", "cargar"},
                {"copy", "copiar"},
                {"paste", "pegar"},
                {"delete", "eliminar"},
                {"clean", "limpiar"},
                {"search", "buscar"},
                {"translate", "traducir"},
                {"analyze", "analizar"},
                {"detect", "detectar"},
                {"listen", "escuchar"},
                {"speak", "hablar"},
                {"write", "escribir"},
                {"read", "leer"},
                {"send", "enviar"},
                {"receive", "recibir"},
                {"create", "crear"},
                {"edit", "editar"},
                {"update", "actualizar"},
                {"upload", "subir"},
                {"download", "descargar"},
                {"connect", "conectar"},
                {"disconnect", "desconectar"},
                {"fix", "arreglar"},
                {"repair", "reparar"},
                {"improve", "mejorar"},
                {"verify", "verificar"},
                {"test", "probar"},
                {"build", "compilar"},
                {"run", "ejecutar"},
                {"deploy", "desplegar"},
                {"show", "mostrar"},
                {"hide", "ocultar"},
                {"choose", "elegir"},
                {"select", "seleccionar"},
                {"change", "cambiar"},
                {"switch", "intercambiar"},
                {"move", "mover"},
                {"return", "regresar"},
                {"wait", "esperar"},
                {"continue", "continuar"},
                {"finish", "terminar"},
                {"begin", "empezar"},
                {"learn", "aprender"},
                {"teach", "ensenar"},
                {"study", "estudiar"},
                {"work", "trabajar"},
                {"buy", "comprar"},
                {"sell", "vender"},
                {"pay", "pagar"},
                {"ask", "preguntar"},
                {"answer", "responder"},
                {"understand", "entender"},
                {"remember", "recordar"},
                {"forget", "olvidar"},
                {"need", "necesitar"},
                {"want", "querer"},
                {"use", "usar"},
                {"help", "ayudar"}
        });

        addBidireccionales("ADVERBIO_TIEMPO", new String[][] {
                {"now", "ahora"},
                {"today", "hoy"},
                {"yesterday", "ayer"},
                {"tomorrow", "manana"},
                {"later", "despues"},
                {"soon", "pronto"},
                {"early", "temprano"},
                {"late", "tarde"},
                {"always", "siempre"},
                {"never", "nunca"},
                {"sometimes", "a veces"},
                {"usually", "usualmente"},
                {"currently", "actualmente"},
                {"already", "ya"},
                {"still", "todavia"}
        });

        addBidireccionales("ADVERBIO_LUGAR", new String[][] {
                {"here", "aqui"},
                {"there", "alli"},
                {"inside", "adentro"},
                {"outside", "afuera"},
                {"above", "arriba"},
                {"below", "abajo"},
                {"near", "cerca"},
                {"far", "lejos"},
                {"left", "izquierda"},
                {"right", "derecha"},
                {"forward", "adelante"},
                {"back", "atras"}
        });

        addBidireccionales("ADVERBIO_MODO", new String[][] {
                {"well", "bien"},
                {"badly", "mal"},
                {"quickly", "rapidamente"},
                {"slowly", "lentamente"},
                {"correctly", "correctamente"},
                {"professionally", "profesionalmente"},
                {"automatically", "automaticamente"},
                {"manually", "manualmente"},
                {"locally", "localmente"},
                {"directly", "directamente"},
                {"carefully", "cuidadosamente"},
                {"clearly", "claramente"},
                {"easily", "facilmente"},
                {"completely", "completamente"}
        });

        addBidireccionales("EXPRESION", new String[][] {
                {"good morning", "buenos dias"},
                {"good afternoon", "buenas tardes"},
                {"good night", "buenas noches"},
                {"how are you", "como estas"},
                {"where are you from", "de donde eres"},
                {"what is your name", "como te llamas"},
                {"my name is", "mi nombre es"},
                {"thank you very much", "muchas gracias"},
                {"you are welcome", "de nada"},
                {"see you later", "hasta luego"},
                {"see you tomorrow", "hasta manana"},
                {"what time is it", "que hora es"},
                {"i do not understand", "no entiendo"},
                {"i need help", "necesito ayuda"},
                {"please translate", "por favor traduce"},
                {"local translator", "traductor local"},
                {"academic translator", "traductor academico"},
                {"english language", "idioma ingles"},
                {"spanish language", "idioma espanol"},
                {"backend not available", "backend no disponible"},
                {"connection refused", "conexion rechazada"},
                {"network error", "error de red"},
                {"source language", "idioma origen"},
                {"target language", "idioma destino"},
                {"copy translation", "copiar traduccion"},
                {"listen pronunciation", "escuchar pronunciacion"}
        });

        add("translated", "VERBO", "tradujo");
        add("translating", "VERBO", "traduciendo");
        add("translates", "VERBO", "traduce");
        add("traduzco", "VERBO", "translate");
        add("traduces", "VERBO", "translate");
        add("traduce", "VERBO", "translates");
        add("traducimos", "VERBO", "translate");
        add("traducen", "VERBO", "translate");
        add("traducido", "VERBO", "translated");
        add("copied", "VERBO", "copio");
        add("copying", "VERBO", "copiando");
        add("copies", "VERBO", "copia");
        add("copia", "VERBO", "copies");
        add("copiado", "VERBO", "copied");
        add("loaded", "VERBO", "cargo");
        add("loading", "VERBO", "cargando");
        add("loads", "VERBO", "carga");
        add("carga", "VERBO", "loads");
        add("cargado", "VERBO", "loaded");
        add("deleted", "VERBO", "elimino");
        add("deleting", "VERBO", "eliminando");
        add("deletes", "VERBO", "elimina");
        add("elimina", "VERBO", "deletes");
        add("eliminado", "VERBO", "deleted");
        add("listened", "VERBO", "escucho");
        add("listening", "VERBO", "escuchando");
        add("listens", "VERBO", "escucha");
        add("escucha", "VERBO", "listens");
        add("escuchado", "VERBO", "listened");
        add("spoken", "VERBO", "hablado");
        add("speaking", "VERBO", "hablando");
        add("speaks", "VERBO", "habla");
        add("habla", "VERBO", "speaks");
        add("written", "VERBO", "escrito");
        add("writing", "VERBO", "escribiendo");
        add("writes", "VERBO", "escribe");
        add("escribe", "VERBO", "writes");
        add("opened", "VERBO", "abrio");
        add("opening", "VERBO", "abriendo");
        add("opens", "VERBO", "abre");
        add("abre", "VERBO", "opens");
        add("closed", "VERBO", "cerro");
        add("closing", "VERBO", "cerrando");
        add("closes", "VERBO", "cierra");
        add("cierra", "VERBO", "closes");
        add("started", "VERBO", "inicio");
        add("starting", "VERBO", "iniciando");
        add("starts", "VERBO", "inicia");
        add("inicia", "VERBO", "starts");
        add("running", "VERBO", "ejecutando");
        add("runs", "VERBO", "ejecuta");
        add("ejecuta", "VERBO", "runs");
        add("verified", "VERBO", "verifico");
        add("verifying", "VERBO", "verificando");
        add("verifies", "VERBO", "verifica");
        add("verifica", "VERBO", "verifies");
        add("improved", "VERBO", "mejoro");
        add("improving", "VERBO", "mejorando");
        add("improves", "VERBO", "mejora");
        add("mejora", "VERBO", "improves");
        add("needed", "VERBO", "necesito");
        add("needing", "VERBO", "necesitando");
        add("needs", "VERBO", "necesita");
        add("necesita", "VERBO", "needs");

        cargarVocabularioGramaticalProfesional();
        cargarVocabularioCienciasYProfesiones();
        cargarVocabularioTecnologiaComputacion();
        cargarVocabularioCotidianoYRelaciones();
        cargarExpresionesIdiomaticasYContextuales();
        cargarVariacionesFrecuentes();
        cargarVocabularioServiciosEmergenciasYGobierno();
        cargarVocabularioComercioLogisticaYAtencion();
        cargarVocabularioDatosNubeDevOpsEIA();
        cargarVocabularioSaludCuerpoYEmociones();
        cargarVocabularioEducacionInvestigacionYEscritura();
    }

    private static void cargarVocabularioGramaticalProfesional() {
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"proper noun", "sustantivo propio"},
                {"common noun", "sustantivo comun"},
                {"auxiliary verb", "verbo auxiliar"},
                {"modal verb", "verbo modal"},
                {"phrasal verb", "verbo compuesto"},
                {"regular verb", "verbo regular"},
                {"irregular verb", "verbo irregular"},
                {"possessive pronoun", "pronombre posesivo"},
                {"demonstrative pronoun", "pronombre demostrativo"},
                {"reflexive pronoun", "pronombre reflexivo"},
                {"interrogative pronoun", "pronombre interrogativo"},
                {"definite article", "articulo definido"},
                {"indefinite article", "articulo indefinido"},
                {"determiner", "determinante"},
                {"quantifier", "cuantificador"},
                {"logical connector", "conector logico"},
                {"grammar connector", "conector gramatical"},
                {"idiom", "modismo"},
                {"formal phrase", "frase formal"},
                {"informal phrase", "frase informal"},
                {"courtesy expression", "expresion de cortesia"},
                {"affirmative sentence", "oracion afirmativa"},
                {"negative sentence", "oracion negativa"},
                {"interrogative sentence", "oracion interrogativa"},
                {"active voice", "voz activa"},
                {"passive voice", "voz pasiva"},
                {"conditional sentence", "oracion condicional"},
                {"comparative", "comparativo"},
                {"superlative", "superlativo"},
                {"singular", "singular"},
                {"plural", "plural"},
                {"masculine gender", "genero masculino"},
                {"feminine gender", "genero femenino"},
                {"agreement", "concordancia"},
                {"spelling rule", "regla ortografica"},
                {"punctuation rule", "regla de puntuacion"},
                {"capital letter", "mayuscula"},
                {"lowercase letter", "minuscula"},
                {"contraction", "contraccion"},
                {"abbreviation", "abreviatura"},
                {"prefix", "prefijo"},
                {"suffix", "sufijo"},
                {"homonym", "homonimo"},
                {"compound word", "palabra compuesta"},
                {"contextual translation", "traduccion contextual"},
                {"grammar correction", "correccion gramatical"},
                {"spelling correction", "correccion ortografica"},
                {"semantic validation", "validacion semantica"},
                {"context interpretation", "interpretacion contextual"}
        });

        addBidireccionales("PRONOMBRE_PERSONAL", new String[][] {
                {"myself", "yo mismo"},
                {"yourself", "tu mismo"},
                {"himself", "el mismo"},
                {"herself", "ella misma"},
                {"itself", "si mismo"},
                {"ourselves", "nosotros mismos"},
                {"yourselves", "ustedes mismos"},
                {"themselves", "ellos mismos"}
        });

        addBidireccionales("ADVERBIO_NEGACION", new String[][] {
                {"not", "no"},
                {"no longer", "ya no"},
                {"neither", "tampoco"},
                {"nor", "ni"}
        });

        addBidireccionales("ADVERBIO_CANTIDAD", new String[][] {
                {"much", "mucho"},
                {"many", "muchos"},
                {"few", "pocos"},
                {"little", "poco"},
                {"enough", "suficiente"},
                {"too much", "demasiado"},
                {"almost", "casi"},
                {"approximately", "aproximadamente"},
                {"exactly", "exactamente"}
        });

        addBidireccionales("CONJUNCION_CONSECUTIVA", new String[][] {
                {"therefore", "por lo tanto"},
                {"so", "asi que"},
                {"thus", "por consiguiente"},
                {"consequently", "en consecuencia"}
        });

        addBidireccionales("CONJUNCION_CONDICIONAL", new String[][] {
                {"if", "si"},
                {"unless", "a menos que"},
                {"provided that", "siempre que"},
                {"as long as", "siempre y cuando"}
        });

        addBidireccionales("CONJUNCION_ADVERSATIVA", new String[][] {
                {"however", "sin embargo"},
                {"nevertheless", "no obstante"},
                {"although", "aunque"},
                {"even though", "aunque"}
        });
    }

    private static void cargarVocabularioCienciasYProfesiones() {
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"nursing", "enfermeria"},
                {"psychology", "psicologia"},
                {"law", "derecho"},
                {"economics", "economia"},
                {"finance", "finanzas"},
                {"accounting", "contabilidad"},
                {"administration", "administracion"},
                {"marketing", "mercadeo"},
                {"business", "negocios"},
                {"human resources", "recursos humanos"},
                {"engineering", "ingenieria"},
                {"architecture", "arquitectura"},
                {"construction", "construccion"},
                {"electronics", "electronica"},
                {"electricity", "electricidad"},
                {"mechanics", "mecanica"},
                {"automotive", "automotriz"},
                {"aviation", "aviacion"},
                {"gastronomy", "gastronomia"},
                {"agriculture", "agricultura"},
                {"veterinary medicine", "veterinaria"},
                {"sports", "deportes"},
                {"gym", "gimnasio"},
                {"soccer", "futbol"},
                {"video game", "videojuego"},
                {"music", "musica"},
                {"movie", "pelicula"},
                {"series", "serie"},
                {"art", "arte"},
                {"politics", "politica"},
                {"religion", "religion"},
                {"social media", "redes sociales"},
                {"communication", "comunicacion"},
                {"news", "noticias"},
                {"travel", "viaje"},
                {"tourism", "turismo"},
                {"transportation", "transporte"},
                {"shopping", "compras"},
                {"fashion", "moda"},
                {"beauty", "belleza"},
                {"health", "salud"},
                {"emotion", "emocion"},
                {"relationship", "relacion"},
                {"office", "oficina"},
                {"customer service", "atencion al cliente"},
                {"contract", "contrato"},
                {"budget", "presupuesto"},
                {"profit", "ganancia"},
                {"loss", "perdida"},
                {"tax", "impuesto"},
                {"investment", "inversion"},
                {"loan", "prestamo"},
                {"salary", "salario"},
                {"resume", "curriculum"},
                {"interview", "entrevista"},
                {"manager", "gerente"},
                {"employee", "empleado"},
                {"team", "equipo"},
                {"goal", "meta"},
                {"strategy", "estrategia"},
                {"campaign", "campana"},
                {"patient", "paciente"},
                {"nurse", "enfermero"},
                {"treatment", "tratamiento"},
                {"diagnosis", "diagnostico"},
                {"symptom", "sintoma"},
                {"therapy", "terapia"},
                {"anxiety", "ansiedad"},
                {"stress", "estres"},
                {"evidence", "evidencia"},
                {"judge", "juez"},
                {"lawyer", "abogado"},
                {"court", "tribunal"},
                {"case", "caso"},
                {"rights", "derechos"},
                {"responsibility", "responsabilidad"},
                {"formula", "formula"},
                {"equation", "ecuacion"},
                {"variable", "variable"},
                {"constant", "constante"},
                {"cell", "celula"},
                {"molecule", "molecula"},
                {"atom", "atomo"},
                {"energy", "energia"},
                {"force", "fuerza"},
                {"mass", "masa"},
                {"speed", "velocidad"},
                {"temperature", "temperatura"}
        });

        addBidireccionales("ADJETIVO_CALIFICATIVO", new String[][] {
                {"financial", "financiero"},
                {"legal", "legal"},
                {"medical", "medico"},
                {"academic", "academico"},
                {"scientific", "cientifico"},
                {"professional", "profesional"},
                {"technical", "tecnico"},
                {"commercial", "comercial"},
                {"administrative", "administrativo"},
                {"human", "humano"},
                {"economic", "economico"},
                {"political", "politico"},
                {"cultural", "cultural"},
                {"historical", "historico"},
                {"geographical", "geografico"}
        });
    }

    private static void cargarVocabularioTecnologiaComputacion() {
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"cybersecurity", "seguridad informatica"},
                {"computer security", "seguridad informatica"},
                {"networking", "redes"},
                {"router", "enrutador"},
                {"switch device", "conmutador"},
                {"firewall", "cortafuegos"},
                {"encryption", "cifrado"},
                {"authentication", "autenticacion"},
                {"authorization", "autorizacion"},
                {"session", "sesion"},
                {"cookie", "cookie"},
                {"cache", "cache"},
                {"endpoint", "endpoint"},
                {"api", "api"},
                {"framework", "framework"},
                {"library", "libreria"},
                {"package", "paquete"},
                {"module", "modulo"},
                {"component", "componente"},
                {"function", "funcion"},
                {"method", "metodo"},
                {"object", "objeto"},
                {"property", "propiedad"},
                {"attribute", "atributo"},
                {"parameter", "parametro"},
                {"argument", "argumento"},
                {"array", "arreglo"},
                {"list", "lista"},
                {"queue", "cola"},
                {"stack", "pila"},
                {"tree", "arbol"},
                {"syntax tree", "arbol sintactico"},
                {"abstract syntax tree", "arbol sintactico abstracto"},
                {"token list", "lista de tokens"},
                {"lexer", "analizador lexico"},
                {"parser", "analizador sintactico"},
                {"semantic analyzer", "analizador semantico"},
                {"compiler", "compilador"},
                {"interpreter", "interprete"},
                {"algorithm", "algoritmo"},
                {"data structure", "estructura de datos"},
                {"variable declaration", "declaracion de variable"},
                {"assignment", "asignacion"},
                {"loop", "ciclo"},
                {"condition", "condicion"},
                {"exception", "excepcion"},
                {"bug", "error"},
                {"feature", "funcionalidad"},
                {"repository", "repositorio"},
                {"branch", "rama"},
                {"commit", "commit"},
                {"pull request", "solicitud de cambios"},
                {"merge", "fusion"},
                {"deployment pipeline", "pipeline de despliegue"},
                {"build process", "proceso de compilacion"},
                {"frontend application", "aplicacion frontend"},
                {"backend service", "servicio backend"},
                {"mobile development", "desarrollo movil"},
                {"web development", "desarrollo web"},
                {"html", "html"},
                {"css", "css"},
                {"javascript", "javascript"},
                {"java", "java"},
                {"python", "python"},
                {"php", "php"},
                {"sql", "sql"},
                {"linux", "linux"},
                {"windows", "windows"},
                {"android", "android"},
                {"github", "github"},
                {"git", "git"},
                {"render", "render"},
                {"vercel", "vercel"},
                {"cloud", "nube"}
        });

        addBidireccionales("VERBO", new String[][] {
                {"compile", "compilar"},
                {"execute", "ejecutar"},
                {"debug", "depurar"},
                {"install", "instalar"},
                {"configure", "configurar"},
                {"authenticate", "autenticar"},
                {"authorize", "autorizar"},
                {"encrypt", "cifrar"},
                {"decrypt", "descifrar"},
                {"validate", "validar"},
                {"parse", "analizar sintacticamente"},
                {"tokenize", "tokenizar"},
                {"render", "renderizar"},
                {"refresh", "actualizar"},
                {"redirect", "redirigir"},
                {"request", "solicitar"},
                {"respond", "responder"},
                {"commit", "confirmar"},
                {"merge", "fusionar"},
                {"clone", "clonar"},
                {"push", "subir"},
                {"pull", "descargar cambios"}
        });
    }

    private static void cargarVocabularioCotidianoYRelaciones() {
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"friendship", "amistad"},
                {"partner", "pareja"},
                {"boyfriend", "novio"},
                {"girlfriend", "novia"},
                {"husband", "esposo"},
                {"wife", "esposa"},
                {"marriage", "matrimonio"},
                {"wedding", "boda"},
                {"date", "cita"},
                {"hug", "abrazo"},
                {"kiss", "beso"},
                {"romance", "romance"},
                {"affection", "afecto"},
                {"trust", "confianza"},
                {"respect", "respeto"},
                {"support", "apoyo"},
                {"feeling", "sentimiento"},
                {"happiness", "felicidad"},
                {"sadness", "tristeza"},
                {"hope", "esperanza"},
                {"dream", "sueno"},
                {"life", "vida"},
                {"memory", "recuerdo"},
                {"smile", "sonrisa"},
                {"conversation", "conversacion"},
                {"message", "mensaje"},
                {"call", "llamada"},
                {"family member", "familiar"},
                {"neighbor", "vecino"},
                {"community", "comunidad"},
                {"apartment", "apartamento"},
                {"garden", "jardin"},
                {"garage", "garaje"},
                {"restaurant", "restaurante"},
                {"menu", "menu"},
                {"recipe", "receta"},
                {"ingredient", "ingrediente"},
                {"break", "descanso"},
                {"vacation", "vacaciones"},
                {"beach", "playa"},
                {"mountain", "montana"},
                {"river", "rio"},
                {"station", "estacion"},
                {"train", "tren"},
                {"subway", "metro"},
                {"taxi", "taxi"},
                {"flight", "vuelo"},
                {"passport", "pasaporte"},
                {"suitcase", "maleta"}
        });

        add("amor", "SUSTANTIVO", "love");
        add("amores", "SUSTANTIVO", "loves");
        add("cariño", "SUSTANTIVO", "darling");
        add("carino", "SUSTANTIVO", "darling");
        add("querido", "ADJETIVO_CALIFICATIVO", "dear");
        add("querida", "ADJETIVO_CALIFICATIVO", "dear");
        add("enamorado", "ADJETIVO_CALIFICATIVO", "in love");
        add("enamorada", "ADJETIVO_CALIFICATIVO", "in love");
        add("romantico", "ADJETIVO_CALIFICATIVO", "romantic");
        add("romantica", "ADJETIVO_CALIFICATIVO", "romantic");
        add("tierno", "ADJETIVO_CALIFICATIVO", "tender");
        add("tierna", "ADJETIVO_CALIFICATIVO", "tender");
        add("fiel", "ADJETIVO_CALIFICATIVO", "faithful");
        add("beloved", "ADJETIVO_CALIFICATIVO", "amado");
        add("romantic", "ADJETIVO_CALIFICATIVO", "romantico");
        add("tender", "ADJETIVO_CALIFICATIVO", "tierno");
        add("faithful", "ADJETIVO_CALIFICATIVO", "fiel");

        addBidireccionales("VERBO", new String[][] {
                {"like", "gustar"},
                {"care", "cuidar"},
                {"trust", "confiar"},
                {"respect", "respetar"},
                {"support", "apoyar"},
                {"miss", "extrañar"},
                {"hug", "abrazar"},
                {"kiss", "besar"},
                {"smile", "sonreir"},
                {"feel", "sentir"},
                {"forgive", "perdonar"},
                {"promise", "prometer"},
                {"celebrate", "celebrar"},
                {"invite", "invitar"},
                {"visit", "visitar"}
        });

        add("loved", "VERBO", "amo");
        add("loving", "VERBO", "amando");
        add("liked", "VERBO", "gusto");
        add("liking", "VERBO", "gustando");
        add("missed", "VERBO", "extrano");
        add("missing", "VERBO", "extranando");
        add("hugged", "VERBO", "abrazo");
        add("hugging", "VERBO", "abrazando");
        add("kissed", "VERBO", "beso");
        add("kissing", "VERBO", "besando");
    }

    private static void cargarExpresionesIdiomaticasYContextuales() {
        addBidireccionales("EXPRESION", new String[][] {
                {"hello love", "hola amor"},
                {"hi love", "hola amor"},
                {"my love", "mi amor"},
                {"dear friend", "querido amigo"},
                {"dear customer", "estimado cliente"},
                {"best regards", "saludos cordiales"},
                {"kind regards", "atentamente"},
                {"to whom it may concern", "a quien corresponda"},
                {"i would like", "me gustaria"},
                {"could you please", "podrias por favor"},
                {"thank you for your help", "gracias por tu ayuda"},
                {"sorry for the inconvenience", "disculpa las molestias"},
                {"as soon as possible", "lo antes posible"},
                {"in other words", "en otras palabras"},
                {"on the other hand", "por otro lado"},
                {"in addition", "ademas"},
                {"for example", "por ejemplo"},
                {"in conclusion", "en conclusion"},
                {"according to", "de acuerdo con"},
                {"due to", "debido a"},
                {"because of", "a causa de"},
                {"in order to", "con el fin de"},
                {"instead of", "en lugar de"},
                {"right now", "ahora mismo"},
                {"last week", "la semana pasada"},
                {"next week", "la proxima semana"},
                {"last year", "el ano pasado"},
                {"next year", "el proximo ano"},
                {"log in", "iniciar sesion"},
                {"sign in", "iniciar sesion"},
                {"sign up", "registrarse"},
                {"log out", "cerrar sesion"},
                {"turn on", "encender"},
                {"turn off", "apagar"},
                {"set up", "configurar"},
                {"look up", "buscar"},
                {"fill out", "llenar"},
                {"find out", "averiguar"},
                {"carry out", "llevar a cabo"},
                {"break down", "descomponer"},
                {"back up", "respaldar"},
                {"speed up", "acelerar"},
                {"slow down", "reducir velocidad"},
                {"try again", "intentar de nuevo"},
                {"make sure", "asegurarse"},
                {"keep going", "continuar"},
                {"let me know", "avisame"},
                {"i love you", "te amo"},
                {"i miss you", "te extraño"},
                {"i like you", "me gustas"},
                {"you are my love", "eres mi amor"},
                {"good luck", "buena suerte"},
                {"take care", "cuidate"},
                {"have a nice day", "que tengas buen dia"}
        });
        add("hola amor", "EXPRESION", "hello love");
        add("hello love", "EXPRESION", "hola amor");

        addBidireccionales("ABREVIATURA", new String[][] {
                {"mr", "senor"},
                {"mrs", "senora"},
                {"ms", "senorita"},
                {"dr", "doctor"},
                {"prof", "profesor"},
                {"etc", "etcetera"},
                {"id", "identificador"},
                {"url", "direccion web"},
                {"ui", "interfaz de usuario"},
                {"ux", "experiencia de usuario"},
                {"db", "base de datos"},
                {"os", "sistema operativo"},
                {"cpu", "procesador"},
                {"ram", "memoria ram"},
                {"ip", "direccion ip"}
        });
    }

    private static void cargarVariacionesFrecuentes() {
        add("people", "SUSTANTIVO", "personas");
        add("men", "SUSTANTIVO", "hombres");
        add("women", "SUSTANTIVO", "mujeres");
        add("feet", "SUSTANTIVO", "pies");
        add("teeth", "SUSTANTIVO", "dientes");
        add("data", "SUSTANTIVO", "datos");
        add("criteria", "SUSTANTIVO", "criterios");
        add("analyses", "SUSTANTIVO", "analisis");

        add("better", "ADJETIVO_CALIFICATIVO", "mejor");
        add("best", "ADJETIVO_CALIFICATIVO", "mejor");
        add("worse", "ADJETIVO_CALIFICATIVO", "peor");
        add("worst", "ADJETIVO_CALIFICATIVO", "peor");
        add("more", "ADVERBIO_CANTIDAD", "mas");
        add("most", "ADVERBIO_CANTIDAD", "mas");
        add("less", "ADVERBIO_CANTIDAD", "menos");
        add("least", "ADVERBIO_CANTIDAD", "menos");

        add("am", "VERBO", "soy");
        add("is", "VERBO", "es");
        add("are", "VERBO", "son");
        add("was", "VERBO", "fue");
        add("were", "VERBO", "fueron");
        add("been", "VERBO", "sido");
        add("being", "VERBO", "siendo");
        add("do", "VERBO", "hacer");
        add("does", "VERBO", "hace");
        add("did", "VERBO", "hizo");
        add("done", "VERBO", "hecho");
        add("have", "VERBO", "tener");
        add("has", "VERBO", "tiene");
        add("had", "VERBO", "tuvo");
        add("can", "VERBO", "poder");
        add("could", "VERBO", "podria");
        add("may", "VERBO", "puede");
        add("might", "VERBO", "podria");
        add("must", "VERBO", "debe");
        add("should", "VERBO", "deberia");
        add("would", "VERBO", "haria");
        add("will", "VERBO", "hara");

        add("soy", "VERBO", "am");
        add("eres", "VERBO", "are");
        add("es", "VERBO", "is");
        add("somos", "VERBO", "are");
        add("son", "VERBO", "are");
        add("fui", "VERBO", "was");
        add("fue", "VERBO", "was");
        add("fueron", "VERBO", "were");
        add("hacer", "VERBO", "do");
        add("hago", "VERBO", "do");
        add("hace", "VERBO", "does");
        add("hizo", "VERBO", "did");
        add("hecho", "VERBO", "done");
        add("tener", "VERBO", "have");
        add("tengo", "VERBO", "have");
        add("tiene", "VERBO", "has");
        add("tuvo", "VERBO", "had");
    }

    private static void cargarVocabularioServiciosEmergenciasYGobierno() {
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"emergency", "emergencia"},
                {"police", "policia"},
                {"firefighter", "bombero"},
                {"ambulance", "ambulancia"},
                {"clinic", "clinica"},
                {"pharmacy", "farmacia"},
                {"appointment", "cita"},
                {"identification", "identificacion"},
                {"license", "licencia"},
                {"permit", "permiso"},
                {"certificate", "certificado"},
                {"birth certificate", "acta de nacimiento"},
                {"address", "direccion"},
                {"phone number", "numero de telefono"},
                {"email address", "correo electronico"},
                {"municipality", "municipalidad"},
                {"government", "gobierno"},
                {"ministry", "ministerio"},
                {"public service", "servicio publico"},
                {"citizen", "ciudadano"},
                {"community service", "servicio comunitario"},
                {"procedure", "tramite"},
                {"form", "formulario"},
                {"requirement", "requisito"},
                {"deadline", "fecha limite"},
                {"complaint", "queja"},
                {"claim", "reclamo"},
                {"report incident", "reporte de incidente"},
                {"safety", "seguridad"},
                {"risk", "riesgo"},
                {"accident", "accidente"},
                {"injury", "lesion"},
                {"warning", "advertencia"},
                {"evacuation", "evacuacion"},
                {"shelter", "refugio"}
        });

        addBidireccionales("VERBO", new String[][] {
                {"report", "reportar"},
                {"request", "solicitar"},
                {"register", "registrar"},
                {"renew", "renovar"},
                {"approve", "aprobar"},
                {"reject", "rechazar"},
                {"notify", "notificar"},
                {"protect", "proteger"},
                {"rescue", "rescatar"},
                {"evacuate", "evacuar"},
                {"prevent", "prevenir"},
                {"assist", "asistir"}
        });
    }

    private static void cargarVocabularioComercioLogisticaYAtencion() {
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"customer support", "soporte al cliente"},
                {"technical support", "soporte tecnico"},
                {"order", "pedido"},
                {"cart", "carrito"},
                {"checkout", "pago final"},
                {"shipping", "envio"},
                {"delivery", "entrega"},
                {"shipment", "envio"},
                {"tracking number", "numero de seguimiento"},
                {"warehouse", "bodega"},
                {"inventory", "inventario"},
                {"supplier", "proveedor"},
                {"vendor", "vendedor"},
                {"wholesale", "mayoreo"},
                {"retail", "venta minorista"},
                {"discount", "descuento"},
                {"coupon", "cupon"},
                {"refund", "reembolso"},
                {"return policy", "politica de devolucion"},
                {"warranty", "garantia"},
                {"receipt", "recibo"},
                {"quote", "cotizacion"},
                {"quotation", "cotizacion"},
                {"purchase order", "orden de compra"},
                {"stock", "existencias"},
                {"availability", "disponibilidad"},
                {"branch office", "sucursal"},
                {"cash register", "caja registradora"},
                {"cashier", "cajero"},
                {"payment method", "metodo de pago"},
                {"credit card", "tarjeta de credito"},
                {"debit card", "tarjeta de debito"},
                {"bank transfer", "transferencia bancaria"},
                {"installment", "cuota"},
                {"subscription", "suscripcion"},
                {"membership", "membresia"}
        });

        addBidireccionales("VERBO", new String[][] {
                {"order", "pedir"},
                {"ship", "enviar"},
                {"deliver", "entregar"},
                {"track", "rastrear"},
                {"refund", "reembolsar"},
                {"return item", "devolver producto"},
                {"quote", "cotizar"},
                {"charge", "cobrar"},
                {"bill", "facturar"},
                {"subscribe", "suscribirse"},
                {"cancel", "cancelar"},
                {"confirm", "confirmar"}
        });

        addBidireccionales("EXPRESION", new String[][] {
                {"how can i help you", "en que puedo ayudarte"},
                {"your order is ready", "tu pedido esta listo"},
                {"payment was successful", "el pago fue exitoso"},
                {"payment failed", "el pago fallo"},
                {"out of stock", "agotado"},
                {"in stock", "disponible"},
                {"free shipping", "envio gratis"},
                {"estimated delivery", "entrega estimada"},
                {"customer service", "servicio al cliente"}
        });
    }

    private static void cargarVocabularioDatosNubeDevOpsEIA() {
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"cloud computing", "computacion en la nube"},
                {"virtual machine", "maquina virtual"},
                {"container", "contenedor"},
                {"docker image", "imagen docker"},
                {"kubernetes", "kubernetes"},
                {"cluster", "cluster"},
                {"load balancer", "balanceador de carga"},
                {"scalability", "escalabilidad"},
                {"latency", "latencia"},
                {"throughput", "rendimiento"},
                {"uptime", "tiempo activo"},
                {"downtime", "tiempo fuera de servicio"},
                {"monitoring", "monitoreo"},
                {"logging", "registro de eventos"},
                {"metric", "metrica"},
                {"alert", "alerta"},
                {"incident", "incidente"},
                {"backup", "respaldo"},
                {"restore", "restauracion"},
                {"migration", "migracion"},
                {"schema", "esquema"},
                {"query", "consulta"},
                {"index", "indice"},
                {"transaction", "transaccion"},
                {"constraint", "restriccion"},
                {"primary key", "clave primaria"},
                {"foreign key", "clave foranea"},
                {"dataset", "conjunto de datos"},
                {"data model", "modelo de datos"},
                {"neural network", "red neuronal"},
                {"training", "entrenamiento"},
                {"inference", "inferencia"},
                {"prediction", "prediccion"},
                {"classification", "clasificacion"},
                {"regression", "regresion"},
                {"natural language processing", "procesamiento de lenguaje natural"},
                {"prompt", "instruccion"},
                {"model", "modelo"},
                {"embedding", "representacion vectorial"},
                {"accuracy", "precision"},
                {"bias", "sesgo"},
                {"privacy", "privacidad"}
        });

        addBidireccionales("VERBO", new String[][] {
                {"scale", "escalar"},
                {"monitor", "monitorear"},
                {"log", "registrar"},
                {"backup", "respaldar"},
                {"restore", "restaurar"},
                {"migrate", "migrar"},
                {"index", "indexar"},
                {"query", "consultar"},
                {"train", "entrenar"},
                {"predict", "predecir"},
                {"classify", "clasificar"},
                {"optimize", "optimizar"},
                {"cache", "almacenar en cache"},
                {"deploy automatically", "desplegar automaticamente"}
        });

        addBidireccionales("ADJETIVO_CALIFICATIVO", new String[][] {
                {"scalable", "escalable"},
                {"available", "disponible"},
                {"reliable", "confiable"},
                {"observable", "observable"},
                {"encrypted", "cifrado"},
                {"authenticated", "autenticado"},
                {"authorized", "autorizado"},
                {"optimized", "optimizado"},
                {"predictive", "predictivo"},
                {"generative", "generativo"}
        });
    }

    private static void cargarVocabularioSaludCuerpoYEmociones() {
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"body", "cuerpo"},
                {"brain", "cerebro"},
                {"lung", "pulmon"},
                {"stomach", "estomago"},
                {"liver", "higado"},
                {"kidney", "rinon"},
                {"muscle", "musculo"},
                {"joint", "articulacion"},
                {"allergy", "alergia"},
                {"infection", "infeccion"},
                {"wound", "herida"},
                {"burn", "quemadura"},
                {"blood pressure", "presion arterial"},
                {"heart rate", "ritmo cardiaco"},
                {"breathing", "respiracion"},
                {"nutrition", "nutricion"},
                {"exercise", "ejercicio"},
                {"sleep", "sueño"},
                {"rest", "descanso"},
                {"depression", "depresion"},
                {"fear", "miedo"},
                {"anger", "enojo"},
                {"joy", "alegria"},
                {"calm", "calma"},
                {"confidence", "confianza"},
                {"motivation", "motivacion"},
                {"self esteem", "autoestima"},
                {"relationship problem", "problema de relacion"}
        });

        addBidireccionales("ADJETIVO_CALIFICATIVO", new String[][] {
                {"healthy", "saludable"},
                {"sick", "enfermo"},
                {"tired", "cansado"},
                {"worried", "preocupado"},
                {"calm", "tranquilo"},
                {"happy", "feliz"},
                {"sad", "triste"},
                {"angry", "enojado"},
                {"afraid", "asustado"},
                {"motivated", "motivado"},
                {"confident", "confiado"},
                {"comfortable", "comodo"},
                {"uncomfortable", "incomodo"}
        });

        addBidireccionales("VERBO", new String[][] {
                {"breathe", "respirar"},
                {"heal", "sanar"},
                {"recover", "recuperarse"},
                {"rest", "descansar"},
                {"sleep", "dormir"},
                {"exercise", "ejercitarse"},
                {"worry", "preocuparse"},
                {"relax", "relajarse"},
                {"motivate", "motivar"}
        });
    }

    private static void cargarVocabularioEducacionInvestigacionYEscritura() {
        addBidireccionales("SUSTANTIVO", new String[][] {
                {"assignment", "asignacion"},
                {"essay", "ensayo"},
                {"thesis", "tesis"},
                {"abstract", "resumen"},
                {"introduction", "introduccion"},
                {"conclusion", "conclusion"},
                {"reference", "referencia"},
                {"bibliography", "bibliografia"},
                {"citation", "cita bibliografica"},
                {"source", "fuente"},
                {"hypothesis", "hipotesis"},
                {"methodology", "metodologia"},
                {"result", "resultado"},
                {"discussion", "discusion"},
                {"analysis", "analisis"},
                {"argument", "argumento"},
                {"evidence", "evidencia"},
                {"paragraph structure", "estructura de parrafo"},
                {"main idea", "idea principal"},
                {"supporting idea", "idea secundaria"},
                {"draft", "borrador"},
                {"revision", "revision"},
                {"rubric", "rubrica"},
                {"presentation", "presentacion"},
                {"slide", "diapositiva"},
                {"workshop", "taller"},
                {"laboratory", "laboratorio"},
                {"experiment", "experimento"},
                {"observation", "observacion"},
                {"survey", "encuesta"},
                {"sample", "muestra"},
                {"population", "poblacion"}
        });

        addBidireccionales("VERBO", new String[][] {
                {"summarize", "resumir"},
                {"explain", "explicar"},
                {"describe", "describir"},
                {"compare", "comparar"},
                {"contrast", "contrastar"},
                {"evaluate", "evaluar"},
                {"argue", "argumentar"},
                {"cite", "citar"},
                {"reference", "referenciar"},
                {"revise", "revisar"},
                {"proofread", "corregir"},
                {"present", "presentar"},
                {"observe", "observar"},
                {"measure", "medir"},
                {"calculate", "calcular"}
        });

        addBidireccionales("EXPRESION", new String[][] {
                {"the purpose of this study", "el proposito de este estudio"},
                {"the results show", "los resultados muestran"},
                {"according to the author", "segun el autor"},
                {"this essay argues that", "este ensayo argumenta que"},
                {"further research is needed", "se necesita mas investigacion"},
                {"in summary", "en resumen"},
                {"to sum up", "para resumir"}
        });
    }

    public static String clasificar(String palabra) {
        String clave = normalizarClave(palabra);
        String[] e = mapa.get(clave);
        return e != null ? e[0] : inferirTipo(clave);
    }

    public static String traducir(String palabra) {
        String clave = normalizarClave(palabra);
        String[] e = mapa.get(clave);
        String resultado = e != null ? e[1] : palabra;
        // Bug #5: Limpiar traducciones con / tomando el primer elemento
        if (resultado != null && resultado.contains("/")) {
            resultado = resultado.split("/")[0];
        }
        return resultado;
    }

    public static String traducirSegunIdioma(String palabra, String idiomaOrigen) {
        String clave = normalizarClave(palabra);
        String forzada = obtenerTraduccionForzada(clave, idiomaOrigen);
        if (forzada != null) {
            return forzada;
        }
        return traducir(clave);
    }

    public static Entrada consultar(String palabra, String idiomaOrigen) {
        String original = palabra == null ? "" : palabra.trim();
        String clave = normalizarClave(limpiarPalabra(original));
        String[] entrada = mapa.get(clave);
        String forzada = obtenerTraduccionForzada(clave, idiomaOrigen);
        boolean tieneForzada = forzada != null;
        boolean conocida = entrada != null || tieneForzada;
        String tipo = entrada != null ? entrada[0] : inferirTipo(clave);
        String traduccion;

        if (tieneForzada) {
            traduccion = forzada;
        } else if (entrada != null) {
            traduccion = limpiarAlternativas(entrada[1]);
        } else {
            String derivada = traducirFormaDerivada(clave, idiomaOrigen);
            traduccion = derivada != null ? derivada : original;
        }

        return new Entrada(
                original,
                clave,
                tipo,
                categoria(tipo),
                traduccion,
                conocida,
                entrada != null ? "DICCIONARIO" : (tieneForzada ? "FORZADA" : "INFERIDA"));
    }

    public static List<Entrada> consultarTexto(String texto, String idiomaOrigen) {
        List<Entrada> entradas = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            return entradas;
        }

        Matcher matcher = TOKEN_TEXTO.matcher(normalizarExpresiones(texto));
        while (matcher.find()) {
            String token = matcher.group();
            if (token.matches("\\p{L}+(?:['’]\\p{L}+)?|\\d+")) {
                entradas.add(consultar(token, idiomaOrigen));
            }
        }
        return entradas;
    }

    public static String traducirTexto(String texto, String idiomaOrigen) {
        if (texto == null || texto.isBlank()) {
            return "";
        }

        StringBuilder resultado = new StringBuilder();
        Matcher matcher = TOKEN_TEXTO.matcher(normalizarExpresiones(texto));

        while (matcher.find()) {
            String token = matcher.group();
            String salida = token;
            if (token.matches("\\p{L}+(?:['’]\\p{L}+)?|\\d+")) {
                Entrada entrada = consultar(token, idiomaOrigen);
                salida = entrada.getTraduccion();
            }

            if (resultado.length() == 0) {
                resultado.append(capitalizarSiAplica(salida, token));
            } else if (esPuntuacionAdherida(salida)) {
                resultado.append(salida);
            } else {
                resultado.append(" ").append(salida);
            }
        }

        return resultado.toString().trim();
    }

    public static String categoria(String tipo) {
        if (tipo == null || tipo.isBlank()) return "OTRO";
        if (tipo.startsWith("PRONOMBRE")) return "PRONOMBRE";
        if (tipo.startsWith("VERBO")) return "VERBO";
        if (tipo.startsWith("SUSTANTIVO")) return "SUSTANTIVO";
        if (tipo.startsWith("ADJETIVO")) return "ADJETIVO";
        if (tipo.startsWith("ADVERBIO")) return "ADVERBIO";
        if (tipo.startsWith("ARTICULO")) return "ARTICULO";
        if (tipo.startsWith("CONJUNCION")) return "CONJUNCION";
        if (tipo.startsWith("PREPOSICION")) return "PREPOSICION";
        if (tipo.startsWith("NUMERAL")) return "NUMERAL";
        if (tipo.equals("POSESIVO") || tipo.equals("DEMOSTRATIVO")) return "DETERMINANTE";
        if (tipo.equals("CONTRACCION")) return "CONTRACCION";
        if (tipo.equals("INTERJECCION")) return "INTERJECCION";
        if (tipo.equals("EXPRESION") || tipo.equals("MODISMO")) return "EXPRESION";
        if (tipo.equals("ABREVIATURA")) return "ABREVIATURA";
        return "OTRO";
    }

    public static String normalizarExpresiones(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }

        String resultado = texto;
        List<String> expresiones = expresionCompactaAOriginal.values().stream()
                .distinct()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .toList();

        for (String expresion : expresiones) {
            resultado = reemplazarExpresionPreservandoMayuscula(resultado, expresion);
        }
        return resultado;
    }

    private static String reemplazarExpresionPreservandoMayuscula(String texto, String expresion) {
        String compacta = expresion.replace(" ", "");
        Pattern patron = Pattern.compile("(?i)(?<!\\p{L})" + Pattern.quote(expresion) + "(?!\\p{L})");
        Matcher matcher = patron.matcher(texto);
        StringBuffer resultado = new StringBuffer();

        while (matcher.find()) {
            String encontrada = matcher.group();
            String reemplazo = Character.isUpperCase(encontrada.charAt(0))
                    ? Character.toUpperCase(compacta.charAt(0)) + compacta.substring(1)
                    : compacta;
            matcher.appendReplacement(resultado, Matcher.quoteReplacement(reemplazo));
        }

        matcher.appendTail(resultado);
        return resultado.toString();
    }

    public static String detectarIdioma(String texto) {
        if (texto == null || texto.isBlank()) {
            return "en";
        }

        String limpio = texto.toLowerCase().replaceAll("[^\\p{L}\\s]", " ");
        String[] palabras = limpio.trim().split("\\s+");
        int en = 0;
        int es = 0;
        for (String p : palabras) {
            if (MARCADORES_EN.contains(p)) {
                en++;
            }
            if (MARCADORES_ES.contains(p)) {
                es++;
            }
        }
        return es > en ? "es" : "en";
    }

    private static String obtenerTraduccionForzada(String clave, String idiomaOrigen) {
        if ("es".equals(idiomaOrigen)) {
            return traduccionesForzadasES_EN.get(clave);
        }
        if ("en".equals(idiomaOrigen)) {
            return traduccionesForzadasEN_ES.get(clave);
        }
        return null;
    }

    private static String limpiarPalabra(String palabra) {
        if (palabra == null) {
            return "";
        }
        return palabra
                .replaceAll("^[^\\p{L}\\d]+", "")
                .replaceAll("[^\\p{L}\\d]+$", "")
                .toLowerCase();
    }

    private static String limpiarAlternativas(String traduccion) {
        if (traduccion == null) {
            return "";
        }
        return traduccion.contains("/") ? traduccion.split("/")[0] : traduccion;
    }

    private static String inferirTipo(String clave) {
        if (clave == null || clave.isBlank()) {
            return "DESCONOCIDO";
        }
        if (clave.matches("\\d+")) {
            return "NUMERAL_CARDINAL";
        }
        if (clave.matches(".*(ing|ed|en)$")) {
            return "VERBO";
        }
        if (clave.matches(".*ly$")) {
            return "ADVERBIO_MODO";
        }
        if (clave.matches(".*(able|ible|ous|ful|less|ive|al|ic|ish)$")) {
            return "ADJETIVO_CALIFICATIVO";
        }
        return "SUSTANTIVO";
    }

    private static String traducirFormaDerivada(String clave, String idiomaOrigen) {
        if (!"en".equals(idiomaOrigen) || clave == null || clave.isBlank()) {
            return null;
        }

        if (clave.endsWith("ing") && clave.length() > 4) {
            String base = resolverBaseIngles(clave.substring(0, clave.length() - 3));
            String[] entradaBase = mapa.get(base);
            if (entradaBase != null && entradaBase[0].startsWith("VERBO")) {
                return gerundioEspanol(limpiarAlternativas(entradaBase[1]));
            }
        }

        if (clave.endsWith("ed") && clave.length() > 3) {
            String base = resolverBaseIngles(clave.substring(0, clave.length() - 2));
            String[] entradaBase = mapa.get(base);
            if (entradaBase != null && entradaBase[0].startsWith("VERBO")) {
                return pasadoSimpleEspanol(limpiarAlternativas(entradaBase[1]));
            }
        }

        if (clave.endsWith("s") && clave.length() > 2) {
            String singular = clave.substring(0, clave.length() - 1);
            String[] entradaSingular = mapa.get(singular);
            if (entradaSingular != null && entradaSingular[0].startsWith("SUSTANTIVO")) {
                return pluralEspanol(limpiarAlternativas(entradaSingular[1]));
            }
        }

        return null;
    }

    private static String resolverBaseIngles(String base) {
        if (mapa.containsKey(base)) {
            return base;
        }
        if (base.endsWith("i")) {
            String conY = base.substring(0, base.length() - 1) + "y";
            if (mapa.containsKey(conY)) {
                return conY;
            }
        }
        if (base.length() > 2) {
            String sinDobleConsonante = base.substring(0, base.length() - 1);
            if (mapa.containsKey(sinDobleConsonante)) {
                return sinDobleConsonante;
            }
        }
        if (mapa.containsKey(base + "e")) {
            return base + "e";
        }
        return base;
    }

    private static String gerundioEspanol(String infinitivo) {
        if (infinitivo == null || infinitivo.isBlank()) {
            return infinitivo;
        }
        if (infinitivo.endsWith("ar")) {
            return infinitivo.substring(0, infinitivo.length() - 2) + "ando";
        }
        if (infinitivo.endsWith("er") || infinitivo.endsWith("ir")) {
            return infinitivo.substring(0, infinitivo.length() - 2) + "iendo";
        }
        return infinitivo;
    }

    private static String pasadoSimpleEspanol(String infinitivo) {
        if (infinitivo == null || infinitivo.isBlank()) {
            return infinitivo;
        }
        if (infinitivo.endsWith("ar")) {
            return infinitivo.substring(0, infinitivo.length() - 2) + "o";
        }
        if (infinitivo.endsWith("er") || infinitivo.endsWith("ir")) {
            return infinitivo.substring(0, infinitivo.length() - 2) + "io";
        }
        return infinitivo;
    }

    private static String pluralEspanol(String singular) {
        if (singular == null || singular.isBlank()) {
            return singular;
        }
        char ultima = singular.charAt(singular.length() - 1);
        if ("aeiou".indexOf(Character.toLowerCase(ultima)) >= 0) {
            return singular + "s";
        }
        return singular + "es";
    }

    private static boolean esPuntuacionAdherida(String token) {
        return token.matches("[.,;:!?)]");
    }

    private static String capitalizarSiAplica(String salida, String tokenOriginal) {
        if (salida == null || salida.isBlank() || tokenOriginal == null || tokenOriginal.isBlank()) {
            return salida;
        }
        if (Character.isUpperCase(tokenOriginal.charAt(0))) {
            return Character.toUpperCase(salida.charAt(0)) + salida.substring(1);
        }
        return salida;
    }

    private static String normalizarClave(String palabra) {
        String clave = palabra == null ? "" : palabra.trim().toLowerCase();
        String original = expresionCompactaAOriginal.get(clave);
        return original != null ? original : clave;
    }

    public static int simbolo(String tipo) {
        if (tipo == null) {
            return sym.SUSTANTIVO;
        }

        switch (tipo) {
            case "PRONOMBRE_PERSONAL":
            case "PRONOMBRE_DEMOSTRATIVO":
            case "PRONOMBRE_INTERROGATIVO":
                return sym.PRONOMBRE;
            case "VERBO":
            case "PHRASAL_VERB":
                return sym.VERBO;
            case "SUSTANTIVO":
            case "EXPRESION":
            case "MODISMO":
            case "ABREVIATURA":
                return sym.SUSTANTIVO;
            case "ARTICULO_DEFINIDO":
            case "ARTICULO_INDEFINIDO":
            case "POSESIVO":
            case "DEMOSTRATIVO":
            case "NUMERAL_CARDINAL":
            case "NUMERAL_ORDINAL":
                return sym.ARTICULO;
            case "ADJETIVO_CALIFICATIVO":
                return sym.ADJETIVO;
            case "ADVERBIO_TIEMPO":
            case "ADVERBIO_LUGAR":
            case "ADVERBIO_MODO":
            case "ADVERBIO_CANTIDAD":
            case "ADVERBIO_AFIRMACION":
            case "ADVERBIO_NEGACION":
            case "ADVERBIO_DUDA":
                return sym.ADVERBIO;
            case "PREPOSICION":
                return sym.PREPOSICION;
            case "CONJUNCION_COPULATIVA":
            case "CONJUNCION_ADVERSATIVA":
            case "CONJUNCION_DISYUNTIVA":
            case "CONJUNCION_DISTRIBUTIVA":
            case "CONJUNCION_EXPLICATIVA":
            case "CONJUNCION_CAUSAL":
            case "CONJUNCION_CONDICIONAL":
            case "CONJUNCION_CONCESIVA":
            case "CONJUNCION_COMPARATIVA":
            case "CONJUNCION_FINAL":
            case "CONJUNCION_CONSECUTIVA":
            case "CONJUNCION_SUSTANTIVA":
                return sym.CONJUNCION;
            case "CONTRACCION":
                return sym.CONTRACCION;
            default:
                return sym.SUSTANTIVO;
        }
    }
}

