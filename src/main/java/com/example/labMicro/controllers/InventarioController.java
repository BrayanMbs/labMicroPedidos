package com.example.labMicro.controllers;

import com.example.labMicro.entities.Inventario;
import com.example.labMicro.services.InventarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping("/agregar")
    public Inventario agregarProducto(@RequestBody Inventario inventario){
        return inventarioService.agregarProducto(inventario);
    }

    @GetMapping("/listar")
    public List<Inventario> listar(){
        return inventarioService.listar();
    }

    @PostMapping("/reservar")
    public Inventario reservar(@RequestParam String producto){
        return inventarioService.reservarStock(producto);
    }

    @PostMapping("/liberar")
    public Inventario liberar(@RequestParam String producto){
        return inventarioService.liberarStock(producto);
    }

    @ExceptionHandler(RuntimeException.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> handleException(RuntimeException e) {
        return org.springframework.http.ResponseEntity
            .badRequest()
            .body(java.util.Map.of("error", e.getMessage()));
    }
}