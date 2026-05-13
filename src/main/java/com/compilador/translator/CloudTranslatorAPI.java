package com.compilador.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Traductor usando APIs cloud configurables.
 * La llave se lee desde application.properties o variables de entorno.
 */
@Component
public class CloudTranslatorAPI {

    @Value("${translator.api.key:}")
    private String apiKey;

    @Value("${translator.api.region:southcentralus}")
    private String region;

    @Value("${translator.api.endpoint:https://api.cognitive.microsofttranslator.com}")
    private String endpoint;

    @Value("${translator.api.provider:google}")
    private String provider;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public boolean estaConfigurado() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String getProvider() {
        return provider;
    }

    public String traducir(String texto, String desde, String hacia) {
        if (!estaConfigurado()) {
            System.out.println("CloudTranslatorAPI: API key no configurada");
            return null;
        }

        if (texto == null || texto.isBlank()) {
            return null;
        }

        try {
            if ("google".equalsIgnoreCase(provider)) {
                return traducirGoogle(texto, desde, hacia);
            }
            if ("azure".equalsIgnoreCase(provider)) {
                return traducirAzure(texto, desde, hacia);
            }

            System.out.println("CloudTranslatorAPI: provider no soportado: " + provider);
            return null;
        } catch (Exception e) {
            System.out.println("CloudTranslatorAPI error: " + e.getMessage());
            return null;
        }
    }

    public String traducirBidireccional(String texto) {
        String desde = resolverIdiomaOrigen(texto, null);
        String hacia = resolverIdiomaDestino(desde, null);

        System.out.println("CloudTranslatorAPI: desde=" + desde + " hacia=" + hacia);
        return traducir(texto, desde, hacia);
    }

    public String traducirFlexible(String texto, String desde, String hacia) {
        String idiomaOrigen = resolverIdiomaOrigen(texto, desde);
        String idiomaDestino = resolverIdiomaDestino(idiomaOrigen, hacia);

        System.out.println("CloudTranslatorAPI: desde=" + idiomaOrigen + " hacia=" + idiomaDestino);
        return traducir(texto, idiomaOrigen, idiomaDestino);
    }

    public String resolverIdiomaOrigen(String texto, String desde) {
        if (desde != null && !desde.isBlank() && !"auto".equalsIgnoreCase(desde)) {
            return normalizarIdiomaSoportado(desde);
        }

        String idiomaDetectado = detectarIdioma(texto);
        if (idiomaDetectado == null || idiomaDetectado.isBlank()) {
            idiomaDetectado = detectarIdiomaBasico(texto);
        }

        return normalizarIdiomaSoportado(idiomaDetectado);
    }

    public String resolverIdiomaDestino(String desde, String hacia) {
        if (hacia != null && !hacia.isBlank() && !"auto".equalsIgnoreCase(hacia)) {
            return normalizarIdiomaSoportado(hacia);
        }

        return "es".equalsIgnoreCase(desde) ? "en" : "es";
    }

    public String detectarIdioma(String texto) {
        if (!estaConfigurado() || texto == null || texto.isBlank()) {
            return null;
        }

        if (!"google".equalsIgnoreCase(provider)) {
            return detectarIdiomaBasico(texto);
        }

        try {
            String url = "https://translation.googleapis.com/language/translate/v2/detect"
                    + "?key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            Map<String, Object> payload = new HashMap<>();
            payload.put("q", List.of(texto));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            mapper.writeValueAsString(payload),
                            StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() == 200) {
                JsonNode detections = mapper.readTree(response.body()).path("data").path("detections");
                if (detections.isArray() && detections.size() > 0) {
                    JsonNode primeraLista = detections.get(0);
                    if (primeraLista.isArray() && primeraLista.size() > 0) {
                        String language = primeraLista.get(0).path("language").asText(null);
                        return language != null && !language.isBlank() ? language : null;
                    }
                }
            } else {
                System.out.println("Error Google Detect API (status " + response.statusCode() + ")");
                System.out.println("Respuesta: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("CloudTranslatorAPI deteccion error: " + e.getMessage());
        }

        return null;
    }

    private String traducirGoogle(String texto, String desde, String hacia) throws Exception {
        String url = "https://translation.googleapis.com/language/translate/v2"
                + "?key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

        Map<String, Object> payload = new HashMap<>();
        payload.put("q", List.of(texto));
        payload.put("source", desde);
        payload.put("target", hacia);
        payload.put("format", "text");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(payload),
                        StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            JsonNode root = mapper.readTree(response.body());
            JsonNode translations = root.path("data").path("translations");
            if (translations.isArray() && translations.size() > 0) {
                String traduccion = translations.get(0).path("translatedText").asText(null);
                return traduccion != null ? limpiarHtml(traduccion) : null;
            }
        } else {
            System.out.println("Error Google Translate API (status " + response.statusCode() + ")");
            System.out.println("Respuesta: " + response.body());
        }

        return null;
    }

    private String traducirAzure(String texto, String desde, String hacia) throws Exception {
        String url = endpoint + "/translate"
                + "?api-version=3.0"
                + "&from=" + URLEncoder.encode(desde, StandardCharsets.UTF_8)
                + "&to=" + URLEncoder.encode(hacia, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Ocp-Apim-Subscription-Key", apiKey)
                .header("Ocp-Apim-Subscription-Region", region)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "[{\"Text\":\"" + escaparJson(texto) + "\"}]",
                        StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            JsonNode root = mapper.readTree(response.body());
            if (root.isArray() && root.size() > 0) {
                JsonNode translations = root.get(0).path("translations");
                if (translations.isArray() && translations.size() > 0) {
                    String traduccion = translations.get(0).path("text").asText(null);
                    return traduccion != null && !traduccion.isBlank() ? traduccion : null;
                }
            }
        } else {
            System.out.println("Azure Translator status: " + response.statusCode());
            System.out.println("Respuesta: " + response.body());
        }

        return null;
    }

    private String limpiarHtml(String texto) {
        return texto.replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }

    private String escaparJson(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String detectarIdiomaBasico(String texto) {
        String limpio = texto == null ? "" : texto.toLowerCase();
        if (limpio.matches(".*[\\u00e1\\u00e9\\u00ed\\u00f3\\u00fa\\u00f1\\u00fc\\u00c1\\u00c9\\u00cd\\u00d3\\u00da\\u00d1\\u00dc].*")) {
            return "es";
        }
        if (limpio.matches(".*\\b(el|la|los|las|un|una|unos|unas|de|para|por|con|que|estoy|esta|est\\u00e1|son|fue)\\b.*")) {
            return "es";
        }
        return "en";
    }

    private String normalizarIdiomaSoportado(String idioma) {
        if (idioma == null || idioma.isBlank()) {
            return "en";
        }

        String normalizado = idioma.trim().toLowerCase();
        if (normalizado.startsWith("es")) {
            return "es";
        }
        return "en";
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
