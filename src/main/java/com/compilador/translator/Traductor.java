package com.compilador.translator;

import com.compilador.ast.NodoAST;
import com.compilador.lexer.Diccionario;
import com.compilador.lexer.Token;
import java.util.*;

public class Traductor {

    private static final Map<String, String> conjugacionesES = new HashMap<>();
    private static final Map<String, String> conjugacionesEN = new HashMap<>();
    private static final Map<String, String> generoSustantivo = new HashMap<>();

    // Todos los subtipos de pronombre
    private static final Set<String> TIPOS_PRONOMBRE = Set.of(
            "PRONOMBRE_PERSONAL", "PRONOMBRE_DEMOSTRATIVO",
            "PRONOMBRE_INTERROGATIVO", "PRONOMBRE", "CONTRACCION");

    // Todos los subtipos de artículo/determinante
    private static final Set<String> TIPOS_ARTICULO = Set.of(
            "ARTICULO_DEFINIDO", "ARTICULO_INDEFINIDO",
            "ARTICULO", "POSESIVO", "DEMOSTRATIVO",
            "NUMERAL_CARDINAL", "NUMERAL_ORDINAL");

    // Puntuación
    private static final Set<String> TIPOS_PUNTUACION = Set.of(
            "PUNTO", "COMA", "INTERROGACION", "EXCLAMACION");

    static {
        /*
         * ══════════════════════════════════
         * CONJUGACIONES EN → ES
         * "pronombre_en|verbo_base" → conjugado_es
         * ══════════════════════════════════
         */
        // eat
        c(conjugacionesES, "i|eat", "como");
        c(conjugacionesES, "you|eat", "comes");
        c(conjugacionesES, "he|eat", "come");
        c(conjugacionesES, "she|eat", "come");
        c(conjugacionesES, "it|eat", "come");
        c(conjugacionesES, "we|eat", "comemos");
        c(conjugacionesES, "they|eat", "comen");
        // eats (3ra persona ya conjugada)
        c(conjugacionesES, "he|eats", "come");
        c(conjugacionesES, "she|eats", "come");
        c(conjugacionesES, "it|eats", "come");
        // play
        c(conjugacionesES, "i|play", "juego");
        c(conjugacionesES, "you|play", "juegas");
        c(conjugacionesES, "he|play", "juega");
        c(conjugacionesES, "she|play", "juega");
        c(conjugacionesES, "it|play", "juega");
        c(conjugacionesES, "we|play", "jugamos");
        c(conjugacionesES, "they|play", "juegan");
        c(conjugacionesES, "he|plays", "juega");
        c(conjugacionesES, "she|plays", "juega");
        // read
        c(conjugacionesES, "i|read", "leo");
        c(conjugacionesES, "you|read", "lees");
        c(conjugacionesES, "he|read", "lee");
        c(conjugacionesES, "she|read", "lee");
        c(conjugacionesES, "we|read", "leemos");
        c(conjugacionesES, "they|read", "leen");
        c(conjugacionesES, "he|reads", "lee");
        c(conjugacionesES, "she|reads", "lee");
        // run
        c(conjugacionesES, "i|run", "corro");
        c(conjugacionesES, "you|run", "corres");
        c(conjugacionesES, "he|run", "corre");
        c(conjugacionesES, "she|run", "corre");
        c(conjugacionesES, "we|run", "corremos");
        c(conjugacionesES, "they|run", "corren");
        c(conjugacionesES, "he|runs", "corre");
        c(conjugacionesES, "she|runs", "corre");
        // write
        c(conjugacionesES, "i|write", "escribo");
        c(conjugacionesES, "you|write", "escribes");
        c(conjugacionesES, "he|write", "escribe");
        c(conjugacionesES, "she|write", "escribe");
        c(conjugacionesES, "we|write", "escribimos");
        c(conjugacionesES, "they|write", "escriben");
        c(conjugacionesES, "he|writes", "escribe");
        c(conjugacionesES, "she|writes", "escribe");
        // drink
        c(conjugacionesES, "i|drink", "bebo");
        c(conjugacionesES, "you|drink", "bebes");
        c(conjugacionesES, "he|drink", "bebe");
        c(conjugacionesES, "she|drink", "bebe");
        c(conjugacionesES, "we|drink", "bebemos");
        c(conjugacionesES, "they|drink", "beben");
        c(conjugacionesES, "he|drinks", "bebe");
        c(conjugacionesES, "she|drinks", "bebe");
        // go
        c(conjugacionesES, "i|go", "voy");
        c(conjugacionesES, "you|go", "vas");
        c(conjugacionesES, "he|go", "va");
        c(conjugacionesES, "she|go", "va");
        c(conjugacionesES, "we|go", "vamos");
        c(conjugacionesES, "they|go", "van");
        c(conjugacionesES, "he|goes", "va");
        c(conjugacionesES, "she|goes", "va");
        // like
        c(conjugacionesES, "i|like", "me gusta");
        c(conjugacionesES, "you|like", "te gusta");
        c(conjugacionesES, "he|like", "le gusta");
        c(conjugacionesES, "she|like", "le gusta");
        c(conjugacionesES, "we|like", "nos gusta");
        c(conjugacionesES, "they|like", "les gusta");
        c(conjugacionesES, "he|likes", "le gusta");
        c(conjugacionesES, "she|likes", "le gusta");
        // is/are/was/were
        c(conjugacionesES, "i|am", "soy");
        c(conjugacionesES, "i|is", "soy");
        c(conjugacionesES, "he|is", "es");
        c(conjugacionesES, "she|is", "es");
        c(conjugacionesES, "it|is", "es");
        c(conjugacionesES, "you|are", "eres");
        c(conjugacionesES, "we|are", "somos");
        c(conjugacionesES, "they|are", "son");
        c(conjugacionesES, "i|was", "fui");
        c(conjugacionesES, "he|was", "fue");
        c(conjugacionesES, "she|was", "fue");
        c(conjugacionesES, "you|were", "fuiste");
        c(conjugacionesES, "we|were", "fuimos");
        c(conjugacionesES, "they|were", "fueron");
        // have/has
        c(conjugacionesES, "i|have", "tengo");
        c(conjugacionesES, "you|have", "tienes");
        c(conjugacionesES, "he|has", "tiene");
        c(conjugacionesES, "she|has", "tiene");
        c(conjugacionesES, "we|have", "tenemos");
        c(conjugacionesES, "they|have", "tienen");
        // see
        c(conjugacionesES, "i|see", "veo");
        c(conjugacionesES, "you|see", "ves");
        c(conjugacionesES, "he|see", "ve");
        c(conjugacionesES, "she|see", "ve");
        c(conjugacionesES, "we|see", "vemos");
        c(conjugacionesES, "they|see", "ven");
        c(conjugacionesES, "he|sees", "ve");
        c(conjugacionesES, "she|sees", "ve");
        // love
        c(conjugacionesES, "i|love", "amo");
        c(conjugacionesES, "you|love", "amas");
        c(conjugacionesES, "he|love", "ama");
        c(conjugacionesES, "she|love", "ama");
        c(conjugacionesES, "we|love", "amamos");
        c(conjugacionesES, "they|love", "aman");
        c(conjugacionesES, "he|loves", "ama");
        c(conjugacionesES, "she|loves", "ama");
        // walk
        c(conjugacionesES, "i|walk", "camino");
        c(conjugacionesES, "you|walk", "caminas");
        c(conjugacionesES, "he|walk", "camina");
        c(conjugacionesES, "she|walk", "camina");
        c(conjugacionesES, "we|walk", "caminamos");
        c(conjugacionesES, "they|walk", "caminan");
        c(conjugacionesES, "he|walks", "camina");
        c(conjugacionesES, "she|walks", "camina");
        // work
        c(conjugacionesES, "i|work", "trabajo");
        c(conjugacionesES, "you|work", "trabajas");
        c(conjugacionesES, "he|work", "trabaja");
        c(conjugacionesES, "she|work", "trabaja");
        c(conjugacionesES, "we|work", "trabajamos");
        c(conjugacionesES, "they|work", "trabajan");
        c(conjugacionesES, "he|works", "trabaja");
        c(conjugacionesES, "she|works", "trabaja");
        // speak
        c(conjugacionesES, "i|speak", "hablo");
        c(conjugacionesES, "you|speak", "hablas");
        c(conjugacionesES, "he|speak", "habla");
        c(conjugacionesES, "she|speak", "habla");
        c(conjugacionesES, "we|speak", "hablamos");
        c(conjugacionesES, "they|speak", "hablan");
        c(conjugacionesES, "he|speaks", "habla");
        c(conjugacionesES, "she|speaks", "habla");
        // know
        c(conjugacionesES, "i|know", "sé");
        c(conjugacionesES, "you|know", "sabes");
        c(conjugacionesES, "he|know", "sabe");
        c(conjugacionesES, "she|know", "sabe");
        c(conjugacionesES, "we|know", "sabemos");
        c(conjugacionesES, "they|know", "saben");
        c(conjugacionesES, "he|knows", "sabe");
        c(conjugacionesES, "she|knows", "sabe");
        // think
        c(conjugacionesES, "i|think", "pienso");
        c(conjugacionesES, "you|think", "piensas");
        c(conjugacionesES, "he|think", "piensa");
        c(conjugacionesES, "she|think", "piensa");
        c(conjugacionesES, "we|think", "pensamos");
        c(conjugacionesES, "they|think", "piensan");
        c(conjugacionesES, "he|thinks", "piensa");
        c(conjugacionesES, "she|thinks", "piensa");
        // want
        c(conjugacionesES, "i|want", "quiero");
        c(conjugacionesES, "you|want", "quieres");
        c(conjugacionesES, "he|want", "quiere");
        c(conjugacionesES, "she|want", "quiere");
        c(conjugacionesES, "we|want", "queremos");
        c(conjugacionesES, "they|want", "quieren");
        c(conjugacionesES, "he|wants", "quiere");
        c(conjugacionesES, "she|wants", "quiere");
        // need
        c(conjugacionesES, "i|need", "necesito");
        c(conjugacionesES, "you|need", "necesitas");
        c(conjugacionesES, "he|need", "necesita");
        c(conjugacionesES, "she|need", "necesita");
        c(conjugacionesES, "we|need", "necesitamos");
        c(conjugacionesES, "they|need", "necesitan");
        c(conjugacionesES, "he|needs", "necesita");
        c(conjugacionesES, "she|needs", "necesita");
        // live
        c(conjugacionesES, "i|live", "vivo");
        c(conjugacionesES, "you|live", "vives");
        c(conjugacionesES, "he|live", "vive");
        c(conjugacionesES, "she|live", "vive");
        c(conjugacionesES, "we|live", "vivimos");
        c(conjugacionesES, "they|live", "viven");
        c(conjugacionesES, "he|lives", "vive");
        c(conjugacionesES, "she|lives", "vive");
        // study
        c(conjugacionesES, "i|study", "estudio");
        c(conjugacionesES, "you|study", "estudias");
        c(conjugacionesES, "he|study", "estudia");
        c(conjugacionesES, "she|study", "estudia");
        c(conjugacionesES, "we|study", "estudiamos");
        c(conjugacionesES, "they|study", "estudian");
        c(conjugacionesES, "he|studies", "estudia");
        c(conjugacionesES, "she|studies", "estudia");
        // come
        c(conjugacionesES, "i|come", "vengo");
        c(conjugacionesES, "you|come", "vienes");
        c(conjugacionesES, "he|come", "viene");
        c(conjugacionesES, "she|come", "viene");
        c(conjugacionesES, "we|come", "venimos");
        c(conjugacionesES, "they|come", "vienen");
        c(conjugacionesES, "he|comes", "viene");
        c(conjugacionesES, "she|comes", "viene");
        // make
        c(conjugacionesES, "i|make", "hago");
        c(conjugacionesES, "you|make", "haces");
        c(conjugacionesES, "he|make", "hace");
        c(conjugacionesES, "she|make", "hace");
        c(conjugacionesES, "we|make", "hacemos");
        c(conjugacionesES, "they|make", "hacen");
        c(conjugacionesES, "he|makes", "hace");
        c(conjugacionesES, "she|makes", "hace");
        // sleep
        c(conjugacionesES, "i|sleep", "duermo");
        c(conjugacionesES, "you|sleep", "duermes");
        c(conjugacionesES, "he|sleep", "duerme");
        c(conjugacionesES, "she|sleep", "duerme");
        c(conjugacionesES, "we|sleep", "dormimos");
        c(conjugacionesES, "they|sleep", "duermen");
        c(conjugacionesES, "he|sleeps", "duerme");
        c(conjugacionesES, "she|sleeps", "duerme");
        // help
        c(conjugacionesES, "i|help", "ayudo");
        c(conjugacionesES, "you|help", "ayudas");
        c(conjugacionesES, "he|help", "ayuda");
        c(conjugacionesES, "she|help", "ayuda");
        c(conjugacionesES, "we|help", "ayudamos");
        c(conjugacionesES, "they|help", "ayudan");
        c(conjugacionesES, "he|helps", "ayuda");
        c(conjugacionesES, "she|helps", "ayuda");
        // buy
        c(conjugacionesES, "i|buy", "compro");
        c(conjugacionesES, "you|buy", "compras");
        c(conjugacionesES, "he|buy", "compra");
        c(conjugacionesES, "she|buy", "compra");
        c(conjugacionesES, "we|buy", "compramos");
        c(conjugacionesES, "they|buy", "compran");
        c(conjugacionesES, "he|buys", "compra");
        c(conjugacionesES, "she|buys", "compra");
        // feel
        c(conjugacionesES, "i|feel", "siento");
        c(conjugacionesES, "you|feel", "sientes");
        c(conjugacionesES, "he|feel", "siente");
        c(conjugacionesES, "she|feel", "siente");
        c(conjugacionesES, "we|feel", "sentimos");
        c(conjugacionesES, "they|feel", "sienten");
        c(conjugacionesES, "he|feels", "siente");
        c(conjugacionesES, "she|feels", "siente");
        // can/could
        c(conjugacionesES, "i|can", "puedo");
        c(conjugacionesES, "you|can", "puedes");
        c(conjugacionesES, "he|can", "puede");
        c(conjugacionesES, "she|can", "puede");
        c(conjugacionesES, "we|can", "podemos");
        c(conjugacionesES, "they|can", "pueden");
        c(conjugacionesES, "i|could", "podría");
        c(conjugacionesES, "you|could", "podrías");
        c(conjugacionesES, "he|could", "podría");
        c(conjugacionesES, "she|could", "podría");
        // will/would
        c(conjugacionesES, "i|will", "voy a");
        c(conjugacionesES, "you|will", "vas a");
        c(conjugacionesES, "he|will", "va a");
        c(conjugacionesES, "she|will", "va a");
        c(conjugacionesES, "we|will", "vamos a");
        c(conjugacionesES, "they|will", "van a");
        // say
        c(conjugacionesES, "i|say", "digo");
        c(conjugacionesES, "you|say", "dices");
        c(conjugacionesES, "he|say", "dice");
        c(conjugacionesES, "she|say", "dice");
        c(conjugacionesES, "we|say", "decimos");
        c(conjugacionesES, "they|say", "dicen");
        c(conjugacionesES, "he|says", "dice");
        c(conjugacionesES, "she|says", "dice");
        // teach
        c(conjugacionesES, "i|teach", "enseño");
        c(conjugacionesES, "you|teach", "enseñas");
        c(conjugacionesES, "he|teach", "enseña");
        c(conjugacionesES, "she|teach", "enseña");
        c(conjugacionesES, "we|teach", "enseñamos");
        c(conjugacionesES, "they|teach", "enseñan");
        c(conjugacionesES, "he|teaches", "enseña");
        c(conjugacionesES, "she|teaches", "enseña");
        // learn
        c(conjugacionesES, "i|learn", "aprendo");
        c(conjugacionesES, "you|learn", "aprendes");
        c(conjugacionesES, "he|learn", "aprende");
        c(conjugacionesES, "she|learn", "aprende");
        c(conjugacionesES, "we|learn", "aprendemos");
        c(conjugacionesES, "they|learn", "aprenden");
        c(conjugacionesES, "he|learns", "aprende");
        c(conjugacionesES, "she|learns", "aprende");

        /*
         * ══════════════════════════════════
         * CONJUGACIONES ES → EN
         * "pronombre_es|verbo_conjugado_es" → verbo_en
         * ══════════════════════════════════
         */
        // comer
        c(conjugacionesEN, "yo|como", "eat");
        c(conjugacionesEN, "tú|comes", "eat");
        c(conjugacionesEN, "tu|comes", "eat");
        c(conjugacionesEN, "él|come", "eats");
        c(conjugacionesEN, "ella|come", "eats");
        c(conjugacionesEN, "nosotros|comemos", "eat");
        c(conjugacionesEN, "ellos|comen", "eat");
        c(conjugacionesEN, "ellas|comen", "eat");
        // jugar
        c(conjugacionesEN, "yo|juego", "play");
        c(conjugacionesEN, "tú|juegas", "play");
        c(conjugacionesEN, "tu|juegas", "play");
        c(conjugacionesEN, "él|juega", "plays");
        c(conjugacionesEN, "ella|juega", "plays");
        c(conjugacionesEN, "nosotros|jugamos", "play");
        c(conjugacionesEN, "ellos|juegan", "play");
        c(conjugacionesEN, "ellas|juegan", "play");
        // leer
        c(conjugacionesEN, "yo|leo", "read");
        c(conjugacionesEN, "tú|lees", "read");
        c(conjugacionesEN, "tu|lees", "read");
        c(conjugacionesEN, "él|lee", "reads");
        c(conjugacionesEN, "ella|lee", "reads");
        c(conjugacionesEN, "nosotros|leemos", "read");
        c(conjugacionesEN, "ellos|leen", "read");
        // correr
        c(conjugacionesEN, "yo|corro", "run");
        c(conjugacionesEN, "tú|corres", "run");
        c(conjugacionesEN, "tu|corres", "run");
        c(conjugacionesEN, "él|corre", "runs");
        c(conjugacionesEN, "ella|corre", "runs");
        c(conjugacionesEN, "nosotros|corremos", "run");
        c(conjugacionesEN, "ellos|corren", "run");
        // escribir
        c(conjugacionesEN, "yo|escribo", "write");
        c(conjugacionesEN, "tú|escribes", "write");
        c(conjugacionesEN, "tu|escribes", "write");
        c(conjugacionesEN, "él|escribe", "writes");
        c(conjugacionesEN, "ella|escribe", "writes");
        c(conjugacionesEN, "nosotros|escribimos", "write");
        c(conjugacionesEN, "ellos|escriben", "write");
        // beber
        c(conjugacionesEN, "yo|bebo", "drink");
        c(conjugacionesEN, "tú|bebes", "drink");
        c(conjugacionesEN, "tu|bebes", "drink");
        c(conjugacionesEN, "él|bebe", "drinks");
        c(conjugacionesEN, "ella|bebe", "drinks");
        c(conjugacionesEN, "nosotros|bebemos", "drink");
        c(conjugacionesEN, "ellos|beben", "drink");
        // ir
        c(conjugacionesEN, "yo|voy", "go");
        c(conjugacionesEN, "tú|vas", "go");
        c(conjugacionesEN, "tu|vas", "go");
        c(conjugacionesEN, "él|va", "goes");
        c(conjugacionesEN, "ella|va", "goes");
        c(conjugacionesEN, "nosotros|vamos", "go");
        c(conjugacionesEN, "ellos|van", "go");
        // ser/estar
        c(conjugacionesEN, "yo|soy", "am");
        c(conjugacionesEN, "tú|eres", "are");
        c(conjugacionesEN, "tu|eres", "are");
        c(conjugacionesEN, "él|es", "is");
        c(conjugacionesEN, "ella|es", "is");
        c(conjugacionesEN, "nosotros|somos", "are");
        c(conjugacionesEN, "ellos|son", "are");
        c(conjugacionesEN, "ellas|son", "are");
        // tener
        c(conjugacionesEN, "yo|tengo", "have");
        c(conjugacionesEN, "tú|tienes", "have");
        c(conjugacionesEN, "tu|tienes", "have");
        c(conjugacionesEN, "él|tiene", "has");
        c(conjugacionesEN, "ella|tiene", "has");
        c(conjugacionesEN, "nosotros|tenemos", "have");
        c(conjugacionesEN, "ellos|tienen", "have");
        // ver
        c(conjugacionesEN, "yo|veo", "see");
        c(conjugacionesEN, "tú|ves", "see");
        c(conjugacionesEN, "tu|ves", "see");
        c(conjugacionesEN, "él|ve", "sees");
        c(conjugacionesEN, "ella|ve", "sees");
        c(conjugacionesEN, "nosotros|vemos", "see");
        c(conjugacionesEN, "ellos|ven", "see");
        // amar
        c(conjugacionesEN, "yo|amo", "love");
        c(conjugacionesEN, "tú|amas", "love");
        c(conjugacionesEN, "tu|amas", "love");
        c(conjugacionesEN, "él|ama", "loves");
        c(conjugacionesEN, "ella|ama", "loves");
        c(conjugacionesEN, "nosotros|amamos", "love");
        c(conjugacionesEN, "ellos|aman", "love");
        // caminar
        c(conjugacionesEN, "yo|camino", "walk");
        c(conjugacionesEN, "tú|caminas", "walk");
        c(conjugacionesEN, "tu|caminas", "walk");
        c(conjugacionesEN, "él|camina", "walks");
        c(conjugacionesEN, "ella|camina", "walks");
        c(conjugacionesEN, "nosotros|caminamos", "walk");
        c(conjugacionesEN, "ellos|caminan", "walk");
        // trabajar
        c(conjugacionesEN, "yo|trabajo", "work");
        c(conjugacionesEN, "tú|trabajas", "work");
        c(conjugacionesEN, "tu|trabajas", "work");
        c(conjugacionesEN, "él|trabaja", "works");
        c(conjugacionesEN, "ella|trabaja", "works");
        c(conjugacionesEN, "nosotros|trabajamos", "work");
        c(conjugacionesEN, "ellos|trabajan", "work");
        // hablar
        c(conjugacionesEN, "yo|hablo", "speak");
        c(conjugacionesEN, "tú|hablas", "speak");
        c(conjugacionesEN, "tu|hablas", "speak");
        c(conjugacionesEN, "él|habla", "speaks");
        c(conjugacionesEN, "ella|habla", "speaks");
        c(conjugacionesEN, "nosotros|hablamos", "speak");
        c(conjugacionesEN, "ellos|hablan", "speak");
        // saber
        c(conjugacionesEN, "yo|sé", "know");
        c(conjugacionesEN, "tú|sabes", "know");
        c(conjugacionesEN, "tu|sabes", "know");
        c(conjugacionesEN, "él|sabe", "knows");
        c(conjugacionesEN, "ella|sabe", "knows");
        c(conjugacionesEN, "nosotros|sabemos", "know");
        c(conjugacionesEN, "ellos|saben", "know");
        // pensar
        c(conjugacionesEN, "yo|pienso", "think");
        c(conjugacionesEN, "tú|piensas", "think");
        c(conjugacionesEN, "tu|piensas", "think");
        c(conjugacionesEN, "él|piensa", "thinks");
        c(conjugacionesEN, "ella|piensa", "thinks");
        c(conjugacionesEN, "nosotros|pensamos", "think");
        c(conjugacionesEN, "ellos|piensan", "think");
        // querer
        c(conjugacionesEN, "yo|quiero", "want");
        c(conjugacionesEN, "tú|quieres", "want");
        c(conjugacionesEN, "tu|quieres", "want");
        c(conjugacionesEN, "él|quiere", "wants");
        c(conjugacionesEN, "ella|quiere", "wants");
        c(conjugacionesEN, "nosotros|queremos", "want");
        c(conjugacionesEN, "ellos|quieren", "want");
        // vivir
        c(conjugacionesEN, "yo|vivo", "live");
        c(conjugacionesEN, "tú|vives", "live");
        c(conjugacionesEN, "tu|vives", "live");
        c(conjugacionesEN, "él|vive", "lives");
        c(conjugacionesEN, "ella|vive", "lives");
        c(conjugacionesEN, "nosotros|vivimos", "live");
        c(conjugacionesEN, "ellos|viven", "live");
        // estudiar
        c(conjugacionesEN, "yo|estudio", "study");
        c(conjugacionesEN, "tú|estudias", "study");
        c(conjugacionesEN, "tu|estudias", "study");
        c(conjugacionesEN, "él|estudia", "studies");
        c(conjugacionesEN, "ella|estudia", "studies");
        c(conjugacionesEN, "nosotros|estudiamos", "study");
        c(conjugacionesEN, "ellos|estudian", "study");
        // comprar
        c(conjugacionesEN, "yo|compro", "buy");
        c(conjugacionesEN, "tú|compras", "buy");
        c(conjugacionesEN, "tu|compras", "buy");
        c(conjugacionesEN, "él|compra", "buys");
        c(conjugacionesEN, "ella|compra", "buys");
        c(conjugacionesEN, "nosotros|compramos", "buy");
        c(conjugacionesEN, "ellos|compran", "buy");
        // dormir
        c(conjugacionesEN, "yo|duermo", "sleep");
        c(conjugacionesEN, "tú|duermes", "sleep");
        c(conjugacionesEN, "tu|duermes", "sleep");
        c(conjugacionesEN, "él|duerme", "sleeps");
        c(conjugacionesEN, "ella|duerme", "sleeps");
        c(conjugacionesEN, "nosotros|dormimos", "sleep");
        c(conjugacionesEN, "ellos|duermen", "sleep");
        // ayudar
        c(conjugacionesEN, "yo|ayudo", "help");
        c(conjugacionesEN, "tú|ayudas", "help");
        c(conjugacionesEN, "tu|ayudas", "help");
        c(conjugacionesEN, "él|ayuda", "helps");
        c(conjugacionesEN, "ella|ayuda", "helps");
        c(conjugacionesEN, "nosotros|ayudamos", "help");
        c(conjugacionesEN, "ellos|ayudan", "help");
        // hacer
        c(conjugacionesEN, "yo|hago", "make");
        c(conjugacionesEN, "tú|haces", "make");
        c(conjugacionesEN, "tu|haces", "make");
        c(conjugacionesEN, "él|hace", "makes");
        c(conjugacionesEN, "ella|hace", "makes");
        c(conjugacionesEN, "nosotros|hacemos", "make");
        c(conjugacionesEN, "ellos|hacen", "make");
        // poder
        c(conjugacionesEN, "yo|puedo", "can");
        c(conjugacionesEN, "tú|puedes", "can");
        c(conjugacionesEN, "tu|puedes", "can");
        c(conjugacionesEN, "él|puede", "can");
        c(conjugacionesEN, "ella|puede", "can");
        c(conjugacionesEN, "nosotros|podemos", "can");
        c(conjugacionesEN, "ellos|pueden", "can");
        // decir
        c(conjugacionesEN, "yo|digo", "say");
        c(conjugacionesEN, "tú|dices", "say");
        c(conjugacionesEN, "tu|dices", "say");
        c(conjugacionesEN, "él|dice", "says");
        c(conjugacionesEN, "ella|dice", "says");
        c(conjugacionesEN, "nosotros|decimos", "say");
        c(conjugacionesEN, "ellos|dicen", "say");
        // sentir
        c(conjugacionesEN, "yo|siento", "feel");
        c(conjugacionesEN, "tú|sientes", "feel");
        c(conjugacionesEN, "tu|sientes", "feel");
        c(conjugacionesEN, "él|siente", "feels");
        c(conjugacionesEN, "ella|siente", "feels");
        c(conjugacionesEN, "nosotros|sentimos", "feel");
        c(conjugacionesEN, "ellos|sienten", "feel");
        // necesitar
        c(conjugacionesEN, "yo|necesito", "need");
        c(conjugacionesEN, "tú|necesitas", "need");
        c(conjugacionesEN, "tu|necesitas", "need");
        c(conjugacionesEN, "él|necesita", "needs");
        c(conjugacionesEN, "ella|necesita", "needs");
        c(conjugacionesEN, "nosotros|necesitamos", "need");
        c(conjugacionesEN, "ellos|necesitan", "need");
        // llamar
        c(conjugacionesEN, "yo|llamo", "call");
        c(conjugacionesEN, "tú|llamas", "call");
        c(conjugacionesEN, "tu|llamas", "call");
        c(conjugacionesEN, "él|llama", "calls");
        c(conjugacionesEN, "ella|llama", "calls");
        c(conjugacionesEN, "nosotros|llamamos", "call");
        c(conjugacionesEN, "ellos|llaman", "call");
        // venir
        c(conjugacionesEN, "yo|vengo", "come");
        c(conjugacionesEN, "tú|vienes", "come");
        c(conjugacionesEN, "tu|vienes", "come");
        c(conjugacionesEN, "él|viene", "comes");
        c(conjugacionesEN, "ella|viene", "comes");
        c(conjugacionesEN, "nosotros|venimos", "come");
        c(conjugacionesEN, "ellos|vienen", "come");

        /*
         * ══════════════════════════════════
         * GÉNERO DE SUSTANTIVOS
         * (clave = sustantivo en idioma destino)
         * ══════════════════════════════════
         */
        // Masculinos
        g("libro", "M");
        g("perro", "M");
        g("gato", "M");
        g("carro", "M");
        g("mercado", "M");
        g("amigo", "M");
        g("fútbol", "M");
        g("futbol", "M");
        g("hombre", "M");
        g("niño", "M");
        g("día", "M");
        g("dia", "M");
        g("dinero", "M");
        g("mundo", "M");
        g("parque", "M");
        g("sol", "M");
        g("árbol", "M");
        g("arbol", "M");
        g("pájaro", "M");
        g("pez", "M");
        g("caballo", "M");
        g("pan", "M");
        g("café", "M");
        g("cafe", "M");
        g("arroz", "M");
        g("papel", "M");
        g("juego", "M");
        g("equipo", "M");
        g("año", "M");
        g("mes", "M");
        g("minuto", "M");
        g("número", "M");
        g("numero", "M");
        g("nombre", "M");
        g("lugar", "M");
        g("tiempo", "M");
        g("hospital", "M");
        g("río", "M");
        g("rio", "M");
        g("hermano", "M");
        g("padre", "M");
        g("papá", "M");
        g("hogar", "M");
        // Femeninos
        g("manzana", "F");
        g("casa", "F");
        g("escuela", "F");
        g("ciudad", "F");
        g("comida", "F");
        g("pelota", "F");
        g("mujer", "F");
        g("agua", "F");
        g("noche", "F");
        g("mañana", "F");
        g("manana", "F");
        g("tarde", "F");
        g("música", "F");
        g("musica", "F");
        g("tienda", "F");
        g("iglesia", "F");
        g("calle", "F");
        g("montaña", "F");
        g("montana", "F");
        g("playa", "F");
        g("luna", "F");
        g("estrella", "F");
        g("flor", "F");
        g("vaca", "F");
        g("leche", "F");
        g("carne", "F");
        g("silla", "F");
        g("mesa", "F");
        g("puerta", "F");
        g("ventana", "F");
        g("semana", "F");
        g("hora", "F");
        g("vida", "F");
        g("familia", "F");
        g("madre", "F");
        g("mamá", "F");
        g("hermana", "F");
        g("persona", "F");
        g("palabra", "F");
        g("oración", "F");
        g("oracion", "F");
    }

    private static void c(Map<String, String> map, String k, String v) {
        map.put(k.toLowerCase(), v);
    }

    private static void g(String sust, String genero) {
        generoSustantivo.put(sust.toLowerCase(), genero);
    }

    // ═══════════════════════════════════════════
    public String traducir(List<Token> tokens) {
        return traducir(tokens, "en");
    }

    public String traducir(List<Token> tokens, String idiomaOrigen) {
        StringBuilder sb = new StringBuilder();
        String pronombre = detectarPronombre(tokens);

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            String tipo = t.getTipo();
            String trad;

            // Puntuación — pegar sin espacio
            if (TIPOS_PUNTUACION.contains(tipo)) {
                sb.append(t.getValor());
                continue;
            }

            // Verbo → conjugar Y APLICAR CONTEXTO
            if (tipo.equals("VERBO") && pronombre != null) {
                String clave = pronombre + "|" + t.getValor().toLowerCase();
                String conjES = conjugacionesES.get(clave);
                String conjEN = conjugacionesEN.get(clave);

                if (conjES != null)
                    trad = conjES;
                else if (conjEN != null)
                    trad = conjEN;
                else
                    trad = t.getTraduccion();

                // ═════════════════════════════════════════════
                // CONTEXTO 1: Verbo "are/is" + preposición de lugar → "estar", no "ser"
                // Ejemplo: "The water is in the lake" → "El agua está en el lago"
                // ═════════════════════════════════════════════
                if ((t.getValor().toLowerCase().equals("is") ||
                        t.getValor().toLowerCase().equals("are") ||
                        t.getValor().toLowerCase().equals("am")) &&
                        i + 1 < tokens.size()) {

                    Token siguiente = tokens.get(i + 1);
                    if (siguiente.getTipo().equals("PREPOSICION")) {
                        // Usar "estar" en lugar de "ser"
                        String prep = siguiente.getValor().toLowerCase();
                        if (prep.equals("in") || prep.equals("on") || prep.equals("at") ||
                                prep.equals("under") || prep.equals("over") || prep.equals("behind")) {
                            // Cambiar la traducción a "estar"
                            if (pronombre.equals("i"))
                                trad = "estoy";
                            else if (pronombre.equals("you"))
                                trad = "estás";
                            else if (pronombre.equals("he") || pronombre.equals("she"))
                                trad = "está";
                            else if (pronombre.equals("it"))
                                trad = "está";
                            else if (pronombre.equals("we"))
                                trad = "estamos";
                            else if (pronombre.equals("they"))
                                trad = "están";
                        }
                    }
                }

                // ═════════════════════════════════════════════
                // CONTEXTO 2: Verbo "was/were" + preposición → "estaba/estaban", no "era/eran"
                // ═════════════════════════════════════════════
                if ((t.getValor().toLowerCase().equals("was") ||
                        t.getValor().toLowerCase().equals("were")) &&
                        i + 1 < tokens.size()) {

                    Token siguiente = tokens.get(i + 1);
                    if (siguiente.getTipo().equals("PREPOSICION")) {
                        String prep = siguiente.getValor().toLowerCase();
                        if (prep.equals("in") || prep.equals("on") || prep.equals("at") ||
                                prep.equals("under") || prep.equals("over") || prep.equals("behind")) {
                            if (pronombre.equals("i") || pronombre.equals("he") || pronombre.equals("she")
                                    || pronombre.equals("it")) {
                                trad = "estaba";
                            } else {
                                trad = "estaban";
                            }
                        }
                    }
                }

                // ═════════════════════════════════════════════
                // CONTEXTO 3: "have/has" + participio → verbo compuesto presente perfecto
                // Ejemplo: "have found" → "han encontrado"
                // ═════════════════════════════════════════════
                if ((t.getValor().toLowerCase().equals("have") ||
                        t.getValor().toLowerCase().equals("has")) &&
                        i + 1 < tokens.size()) {

                    Token siguiente = tokens.get(i + 1);
                    // Si el siguiente es un verbo (potencialmente un participio)
                    if (siguiente.getTipo().equals("VERBO")) {
                        String verbSig = siguiente.getValor().toLowerCase();
                        // Si es un participio como "found", "written", "eaten"
                        if (esParticipio(verbSig)) {
                            if (pronombre.equals("i") || pronombre.equals("we"))
                                trad = "hemos";
                            else if (pronombre.equals("you") || pronombre.equals("they"))
                                trad = "han";
                            else if (pronombre.equals("he") || pronombre.equals("she") || pronombre.equals("it"))
                                trad = "ha";
                        }
                    }
                }

                // Artículo → concordancia de género
            } else if (TIPOS_ARTICULO.contains(tipo)) {
                String sustTrad = sustantivoCercano(tokens, i);
                trad = traducirArticulo(
                        t.getValor().toLowerCase(), sustTrad);

                // ═════════════════════════════════════════════
                // ADJETIVOS: Aplicar concordancia de género y posición
                // ═════════════════════════════════════════════
            } else if (tipo.equals("ADJETIVO_CALIFICATIVO")) {
                trad = t.getTraduccion();

                // Detectar género del sustantivo cercano
                String sustTrad = sustantivoCercano(tokens, i);
                String genero = "M";
                if (sustTrad != null) {
                    genero = generoSustantivo.getOrDefault(sustTrad, "M");
                }

                // Aplicar concordancia de género al adjetivo
                trad = aplicarConcordanciaAdjetivo(trad, genero);

                // ═════════════════════════════════════════════
                // APÓCOPE: "bueno" → "buen", "malo" → "mal", "grande" → "gran"
                // (antes de sustantivo masculino)
                // ═════════════════════════════════════════════
                if (i + 1 < tokens.size()) {
                    Token sigToken = tokens.get(i + 1);
                    if (sigToken.getTipo().equals("SUSTANTIVO")) {
                        if (trad.equalsIgnoreCase("bueno") && genero.equals("M")) {
                            trad = "buen";
                        } else if (trad.equalsIgnoreCase("malo") && genero.equals("M")) {
                            trad = "mal";
                        } else if (trad.equalsIgnoreCase("grande")) {
                            trad = "gran";
                        } else if (trad.equalsIgnoreCase("primero") && genero.equals("M")) {
                            trad = "primer";
                        } else if (trad.equalsIgnoreCase("tercero") && genero.equals("M")) {
                            trad = "tercer";
                        }
                    }
                }

                // Todo lo demás → diccionario
            } else {
                trad = Diccionario.traducirSegunIdioma(t.getValor(), idiomaOrigen);
            }

            if (trad == null || trad.isEmpty())
                trad = t.getValor();

            // Espacio + capitalizar primera palabra
            if (sb.length() == 0) {
                trad = Character.toUpperCase(trad.charAt(0))
                        + trad.substring(1);
            } else {
                sb.append(" ");
            }

            sb.append(trad);
        }

        return sb.toString();
    }

    public String traducirDesdeAst(NodoAST raiz, String idiomaOrigen) {
        if (raiz == null) {
            return "";
        }

        List<String> hojas = new ArrayList<>();
        recolectarHojas(raiz, hojas);
        if (hojas.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String hoja : hojas) {
            if (hoja == null || hoja.isBlank()) {
                continue;
            }

            if (esPuntuacionLexema(hoja)) {
                sb.append(hoja);
                continue;
            }

            String traducida = Diccionario.traducirSegunIdioma(hoja, idiomaOrigen);
            if (traducida == null || traducida.isBlank()) {
                traducida = hoja;
            }

            if (sb.length() == 0) {
                traducida = Character.toUpperCase(traducida.charAt(0)) + traducida.substring(1);
            } else if (!sb.toString().endsWith(" ")) {
                sb.append(" ");
            }
            sb.append(traducida);
        }
        return sb.toString()
                .replace(" ,", ",")
                .replace(" .", ".")
                .replace(" !", "!")
                .replace(" ?", "?")
                .trim();
    }

    private void recolectarHojas(NodoAST nodo, List<String> hojas) {
        if (nodo == null) {
            return;
        }
        if (nodo.esHoja()) {
            if (nodo.getValor() != null && !nodo.getValor().isBlank()) {
                hojas.add(nodo.getValor());
            }
            return;
        }
        for (NodoAST h : nodo.getHijos()) {
            recolectarHojas(h, hojas);
        }
    }

    private boolean esPuntuacionLexema(String lexema) {
        return lexema.equals(".") || lexema.equals(",") || lexema.equals("!") || lexema.equals("?");
    }

    // ═════════════════════════════════════════════
    // Helper: Detecta si una palabra es un participio
    // ═════════════════════════════════════════════
    private boolean esParticipio(String palabra) {
        Set<String> participios = Set.of(
                "found", "seen", "written", "said", "given", "made", "gone",
                "taken", "called", "asked", "worked", "tried", "felt", "left",
                "put", "meant", "kept", "let", "begun", "seemed", "helped",
                "talked", "turned", "started", "showed", "heard", "played",
                "followed", "ended", "told", "eaten", "drunk",
                "run", "comido", "visto", "hecho", "escrito");
        return participios.contains(palabra.toLowerCase());
    }

    // ═════════════════════════════════════════════
    // Helper: Aplica concordancia de género a adjetivos españoles
    // ═════════════════════════════════════════════
    private String aplicarConcordanciaAdjetivo(String adjetivo, String genero) {
        if (adjetivo == null)
            return adjetivo;

        adjetivo = adjetivo.toLowerCase();

        // Si es femenino, cambiar -o a -a, o agregar -a
        if (genero.equals("F")) {
            if (adjetivo.endsWith("o")) {
                return adjetivo.substring(0, adjetivo.length() - 1) + "a";
            } else if (!adjetivo.endsWith("a") && !adjetivo.endsWith("e")) {
                return adjetivo + "a";
            }
        }

        return adjetivo;
    }

    // Detecta pronombre sujeto de la oración
    private String detectarPronombre(List<Token> tokens) {
        for (Token t : tokens) {
            if (TIPOS_PRONOMBRE.contains(t.getTipo())) {
                return t.getValor().toLowerCase();
            }
        }
        return null;
    }

    // Sustantivo más cercano hacia la derecha (para artículo)
    private String sustantivoCercano(List<Token> tokens, int desde) {
        for (int j = desde + 1; j < tokens.size(); j++) {
            if (tokens.get(j).getTipo().equals("SUSTANTIVO")) {
                return tokens.get(j).getTraduccion().toLowerCase();
            }
        }
        return null;
    }

    // Concordancia de género en artículos
    private String traducirArticulo(String articulo, String sustTrad) {
        String g = "M";
        if (sustTrad != null)
            g = generoSustantivo.getOrDefault(sustTrad, "M");

        return switch (articulo) {
            case "the" -> g.equals("F") ? "la" : "el";
            case "a", "an" -> g.equals("F") ? "una" : "un";
            case "some" -> g.equals("F") ? "unas" : "unos";
            case "el", "un" -> "the";
            case "la", "una" -> "the";
            case "los", "unos" -> "the";
            case "las", "unas" -> "the";
            default -> articulo;
        };
    }
}
