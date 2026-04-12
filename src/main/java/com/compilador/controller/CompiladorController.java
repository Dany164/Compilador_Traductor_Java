package com.compilador.controller;

import com.compilador.dto.AnalizarRequest;
import com.compilador.model.ResultadoAnalisis;
import com.compilador.service.CompiladorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CompiladorController {

    @Autowired
    private CompiladorService compiladorService;

    @PostMapping(value = "/analizar", consumes = "application/json;charset=UTF-8")
    public ResultadoAnalisis analizar(@RequestBody AnalizarRequest body) {

        String texto = body.getTexto();
        boolean usarIA = body.isUsarIA();

        // 🔍 DEBUG (puedes quitar luego)
        System.out.println("TEXTO RECIBIDO: " + texto);

        return compiladorService.analizar(texto, usarIA);
    }
}