package com.compilador.controller;

import com.compilador.dto.AnalizarRequest;
import com.compilador.dto.TraducirRequest;
import com.compilador.dto.TraducirResponse;
import com.compilador.lexer.Diccionario;
import com.compilador.model.ResultadoAnalisis;
import com.compilador.service.CompiladorService;
import com.compilador.translator.CloudTranslatorAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompiladorController {

    @Autowired
    private CompiladorService compiladorService;

    @Autowired
    private CloudTranslatorAPI cloudTranslatorAPI;

    @PostMapping(value = "/analizar", consumes = "application/json;charset=UTF-8")
    public ResultadoAnalisis analizar(@RequestBody AnalizarRequest body) {
        String texto = body.getTexto();
        boolean usarIA = body.isUsarIA();

        return compiladorService.analizar(texto, usarIA);
    }

    @PostMapping(value = "/traducir", consumes = "application/json;charset=UTF-8")
    public TraducirResponse traducir(@RequestBody TraducirRequest body) {
        return compiladorService.traducirTextoLibre(body);
    }

    @PostMapping(value = "/diccionario/consultar", consumes = "application/json;charset=UTF-8")
    public List<Diccionario.Entrada> consultarDiccionario(@RequestBody TraducirRequest body) {
        return compiladorService.consultarDiccionario(body);
    }

    @GetMapping("/traductor/status")
    public Map<String, Object> estadoTraductor() {
        return Map.of(
                "provider", cloudTranslatorAPI.getProvider(),
                "configured", cloudTranslatorAPI.estaConfigurado());
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok");
    }
}
