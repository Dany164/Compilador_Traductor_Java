package com.compilador.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Wrapper para TraductorIA que delega a CloudTranslatorAPI
 * Mantiene compatibilidad con el código existente
 */
@Component
public class TraductorIA {

        @Autowired
        private CloudTranslatorAPI cloudTranslator;

        /**
         * Traduce texto usando Cloud Translator API
         * 
         * @param texto texto a traducir
         * @param desde "en" o "es"
         * @param hacia "en" o "es"
         * @return traducción o null si falla
         */
        public String traducir(String texto, String desde, String hacia) {
                if (cloudTranslator == null) {
                        System.out.println(" CloudTranslatorAPI no está disponible");
                        return null;
                }
                return cloudTranslator.traducir(texto, desde, hacia);
        }
}