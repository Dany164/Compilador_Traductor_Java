package com.compilador.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Traductor usando Cloud Translator API (Azure Translator o similar)
 * Soporta múltiples backends configurables
 */
@Component
public class CloudTranslatorAPI {

    @Value("${translator.api.key:}")
    private String apiKey;

    @Value("${translator.api.region:southcentralus}")
    private String region;

    @Value("${translator.api.endpoint:https://api.cognitive.microsofttranslator.com}")
    private String endpoint;

    @Value("${translator.api.provider:azure}")
    private String provider;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Traduce texto usando Cloud Translator API
     * 
     * @param texto texto a traducir
     * @param desde idioma origen ("en", "es", etc.)
     * @param hacia idioma destino ("en", "es", etc.)
     * @return traducción o null si falla
     */
    public String traducir(String texto, String desde, String hacia) {
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("⚠️ CloudTranslatorAPI: API_KEY no configurada");
            return null;
        }

        try {
            if ("azure".equalsIgnoreCase(provider)) {
                return traducirAzure(texto, desde, hacia);
            } else if ("google".equalsIgnoreCase(provider)) {
                return traducirGoogle(texto, desde, hacia);
            } else {
                System.out.println(" Provider no soportado: " + provider);
                return null;
            }
        } catch (Exception e) {
            System.out.println(" CloudTranslatorAPI error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Traducción usando Azure Cognitive Services Translator
     */
    private String traducirAzure(String texto, String desde, String hacia) throws Exception {
        String url = endpoint + "/translate"
                + "?api-version=3.0"
                + "&from=" + desde
                + "&to=" + hacia;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Ocp-Apim-Subscription-Key", apiKey)
                .header("Ocp-Apim-Subscription-Region", region)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "[{\"Text\":\"" + escaparJson(texto) + "\"}]"))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode root = mapper.readTree(response.body());
            if (root.isArray() && root.size() > 0) {
                JsonNode translations = root.get(0).path("translations");
                if (translations.isArray() && translations.size() > 0) {
                    String traduccion = translations.get(0).path("text").asText(null);
                    return (traduccion != null && !traduccion.isBlank()) ? traduccion : null;
                }
            }
        } else {
            System.out.println("⚠️ Azure Translator status: " + response.statusCode());
            System.out.println("   Respuesta: " + response.body());
        }

        return null;
    }

    /**
     * Traducción usando Google Cloud Translation API (v2)
     */
    private String traducirGoogle(String texto, String desde, String hacia) throws Exception {
        // Usamos GET con URLEncoder para máxima compatibilidad con llaves de API estándar
        String url = "https://translation.googleapis.com/language/translate/v2"
                + "?key=" + apiKey
                + "&source=" + java.net.URLEncoder.encode(desde, StandardCharsets.UTF_8)
                + "&target=" + java.net.URLEncoder.encode(hacia, StandardCharsets.UTF_8)
                + "&q=" + java.net.URLEncoder.encode(texto, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode root = mapper.readTree(response.body());
            JsonNode data = root.path("data");
            if (data.has("translations")) {
                JsonNode translations = data.path("translations");
                if (translations.isArray() && translations.size() > 0) {
                    String traduccion = translations.get(0).path("translatedText").asText(null);
                    return (traduccion != null) ? limpiarHtml(traduccion) : null;
                }
            }
        } else {
            System.out.println("❌ Error Google Translate API (Status " + response.statusCode() + ")");
            System.out.println("   Respuesta: " + response.body());
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

    /**
     * Escapa caracteres especiales para JSON
     */
    private String escaparJson(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Getters y Setters para configuración
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
