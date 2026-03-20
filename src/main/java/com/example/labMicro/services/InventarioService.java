package com.example.labMicro.services;

import com.example.labMicro.entities.Inventario;
import com.example.labMicro.repositories.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 🔥 LOGS
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InventarioService {

    private static final Logger logger = LoggerFactory.getLogger(InventarioService.class);

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    // 📌 AGREGAR PRODUCTO
    public Inventario agregarProducto(Inventario inventario){
        Inventario guardado = inventarioRepository.save(inventario);

        logger.info("Producto agregado | Nombre: {} | Stock inicial: {} | Precio: {}",
                guardado.getProducto(),
                guardado.getStock(),
                guardado.getPrecio());

        return guardado;
    }

    // 📌 LISTAR
    public List<Inventario> listar(){
        logger.info("Listado de inventario consultado");
        return inventarioRepository.findAll();
    }

    // 🛒 RESERVAR STOCK (CON CANTIDAD Y ROLLBACK)
    @Transactional
    public Inventario reservarStock(String producto, int cantidad){

        Inventario inventario = inventarioRepository
                .findByProducto(producto)
                .orElseThrow(() -> {
                    logger.error("Producto no encontrado: {}", producto);
                    return new RuntimeException("Producto no encontrado");
                });

        int stockAntes = inventario.getStock();

        // 🚨 VALIDACIÓN
        if(stockAntes < cantidad){
            logger.warn("Stock insuficiente | Producto: {} | Disponible: {} | Solicitado: {}",
                    producto, stockAntes, cantidad);

            throw new RuntimeException("Stock insuficiente");
        }

        // 🔻 DESCUENTO
        inventario.setStock(stockAntes - cantidad);

        Inventario actualizado = inventarioRepository.save(inventario);

        logger.info("Compra realizada | Producto: {} | Cantidad: {} | Stock antes: {} | Stock actual: {}",
                producto,
                cantidad,
                stockAntes,
                actualizado.getStock());

        return actualizado;
    }

    // 🔁 LIBERAR STOCK
    @Transactional
    public Inventario liberarStock(String producto, int cantidad){

        Inventario inventario = inventarioRepository
                .findByProducto(producto)
                .orElseThrow(() -> {
                    logger.error("Producto no encontrado: {}", producto);
                    return new RuntimeException("Producto no encontrado");
                });

        int stockAntes = inventario.getStock();

        inventario.setStock(stockAntes);

        Inventario actualizado = inventarioRepository.save(inventario);

        logger.info("Stock liberado | Producto: {} | Cantidad: {} | Stock antes: {} | Stock actual: {}",
                producto,
                cantidad,
                stockAntes,
                actualizado.getStock());

        return actualizado;
    }
}