package com.example.labMicro.controllers;

import com.example.labMicro.entities.Inventario;
import com.example.labMicro.services.InventarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 🔥 LOGS
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping("/agregar")
    public Inventario agregarProducto(@RequestBody Inventario inventario){
        logger.info("Request: Agregar producto -> {}", inventario.getProducto());
        return inventarioService.agregarProducto(inventario);
    }

    @GetMapping("/listar")
    public List<Inventario> listar(){
        logger.info("Request: Listar inventario");
        return inventarioService.listar();
    }

    @PostMapping("/reservar")
    public Inventario reservar(@RequestParam String producto){
        logger.info("Request: Reservar producto -> {}", producto);
        return inventarioService.reservarStock(producto);
    }

    @PostMapping("/liberar")
    public Inventario liberar(@RequestParam String producto){
        logger.info("Request: Liberar producto -> {}", producto);
        return inventarioService.liberarStock(producto);
    }

    @ExceptionHandler(RuntimeException.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> handleException(RuntimeException e) {

        logger.error("Error capturado: {}", e.getMessage());

        return org.springframework.http.ResponseEntity
                .badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
    }
}