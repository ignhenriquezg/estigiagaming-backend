package com.estigiagaming.backend.controller;

import com.estigiagaming.backend.model.Boleta;
import com.estigiagaming.backend.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/boletas")
@Tag(name = "Boletas", description = "Registro y consulta de órdenes de compra")
public class BoletaController {

    private final BoletaService boletaService;

    public BoletaController(BoletaService boletaService) {
        this.boletaService = boletaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las boletas (Administrador y Vendedor)")
    public ResponseEntity<List<Boleta>> listar() {
        return ResponseEntity.ok(boletaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una boleta por ID (Administrador y Vendedor)")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(boletaService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva compra desde el checkout")
    public ResponseEntity<?> crear(@RequestBody Boleta boleta) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(boletaService.crearBoleta(boleta));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}