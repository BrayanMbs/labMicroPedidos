package com.example.labMicro.services;

import com.example.labMicro.entities.Inventario;
import com.example.labMicro.repositories.InventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InventarioService {

    private static final Logger logger = LoggerFactory.getLogger(InventarioService.class);

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }


    public Inventario agregarProducto(Inventario inventario){
        Inventario guardado = inventarioRepository.save(inventario);

        logger.info("Producto agregado | Nombre: {} | Stock inicial: {} | Precio: {}",
                guardado.getProducto(),
                guardado.getStock(),
                guardado.getPrecio());

        return guardado;
    }


    public List<Inventario> listar(){
        logger.info("Listado de inventario consultado");
        return inventarioRepository.findAll();
    }


    public Inventario reservarStock(String producto){

        Inventario inventario = inventarioRepository
                .findByProducto(producto)
                .orElseThrow(() -> {
                    logger.error("Producto no encontrado: {}", producto);
                    return new RuntimeException("Producto no encontrado");
                });

        int stockAntes = inventario.getStock();

        if(stockAntes <= 0){
            logger.warn("Intento de compra sin stock | Producto: {}", producto);
            throw new RuntimeException("Stock insuficiente");
        }

        inventario.setStock(stockAntes - 1);

        Inventario actualizado = inventarioRepository.save(inventario);

        logger.info("Compra realizada | Producto: {} | Stock antes: {} | Stock actual: {}",
                producto,
                stockAntes,
                actualizado.getStock());

        return actualizado;
    }


    public Inventario liberarStock(String producto){

        Inventario inventario = inventarioRepository
                .findByProducto(producto)
                .orElseThrow(() -> {
                    logger.error("Producto no encontrado: {}", producto);
                    return new RuntimeException("Producto no encontrado");
                });

        int stockAntes = inventario.getStock();

        inventario.setStock(stockAntes + 1);

        Inventario actualizado = inventarioRepository.save(inventario);

        logger.info("Stock liberado | Producto: {} | Stock antes: {} | Stock actual: {}",
                producto,
                stockAntes,
                actualizado.getStock());

        return actualizado;
    }

}