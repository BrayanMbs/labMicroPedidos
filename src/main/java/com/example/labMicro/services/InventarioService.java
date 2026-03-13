package com.example.labMicro.services;

import com.example.labMicro.entities.Inventario;
import com.example.labMicro.repositories.InventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public Inventario agregarProducto(Inventario inventario){
        return inventarioRepository.save(inventario);
    }

    public List<Inventario> listar(){
        return inventarioRepository.findAll();
    }

    public Inventario reservarStock(String producto){

        Inventario inventario = inventarioRepository
                .findByProducto(producto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if(inventario.getStock() <= 0){
            throw new RuntimeException("Stock insuficiente");
        }

        inventario.setStock(inventario.getStock() - 1);

        return inventarioRepository.save(inventario);
    }

    public Inventario liberarStock(String producto){

        Inventario inventario = inventarioRepository
                .findByProducto(producto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        inventario.setStock(inventario.getStock() + 1);

        return inventarioRepository.save(inventario);
    }

}