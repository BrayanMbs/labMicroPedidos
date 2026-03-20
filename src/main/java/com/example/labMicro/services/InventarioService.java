package com.example.labMicro.services;

import com.example.labMicro.entities.Inventario;
import com.example.labMicro.repositories.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 🔥 Librerías para el Log
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InventarioService {

    // Definimos el logger para rastrear qué pasa en el microservicio
    private static final Logger logger = LoggerFactory.getLogger(InventarioService.class);

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    /**
     * 📌 AGREGAR PRODUCTO
     * Guarda un nuevo artículo y registra cuánto stock inicial tiene.
     */
    public Inventario agregarProducto(Inventario inventario){
        Inventario guardado = inventarioRepository.save(inventario);

        logger.info("📦 PRODUCTO CREADO | Nombre: {} | Stock inicial: {} | Precio: Q{}",
                guardado.getProducto(),
                guardado.getStock(),
                guardado.getPrecio());

        return guardado;
    }

    /**
     * 📌 LISTAR INVENTARIO
     * Muestra todos los productos disponibles.
     */
    public List<Inventario> listar(){
        logger.info("🔍 CONSULTA: Se ha solicitado el listado completo de productos.");
        return inventarioRepository.findAll();
    }

    /**
     * 🛒 RESERVAR STOCK (VENTA)
     * Resta la cantidad solicitada del inventario.
     * @Transactional asegura que si algo falla, no se guarde nada a medias.
     */
    @Transactional
    public Inventario reservarStock(String producto, int cantidad){

        // 1. Buscamos si el producto existe
        Inventario inventario = inventarioRepository
                .findByProducto(producto)
                .orElseThrow(() -> {
                    logger.error("❌ ERROR: El producto '{}' no existe en el catálogo.", producto);
                    return new RuntimeException("Producto no encontrado");
                });

        int stockActual = inventario.getStock();

        // 🚨 VALIDACIÓN DE ORO: No permitimos vender más de lo que hay
        if(stockActual < cantidad){
            logger.warn("🚫 VENTA RECHAZADA: '{}' insuficiente. Solicitado: {} | Disponible: {}",
                    producto, cantidad, stockActual);

            // Al lanzar este error, Spring hace "Rollback" automático a la DB
            throw new RuntimeException("No hay suficiente stock para completar la reserva");
        }

        // 🔻 Solo si hay stock, calculamos y restamos
        int nuevoStock = stockActual - cantidad;
        inventario.setStock(nuevoStock);

        // Guardamos los cambios
        Inventario actualizado = inventarioRepository.save(inventario);

        logger.info("✅ VENTA EXITOSA | Producto: {} | Cantidad Vendida: {} | Stock Antes: {} | Stock Ahora: {}",
                producto, cantidad, stockActual, actualizado.getStock());

        return actualizado;
    }

    /**
     * 🔁 LIBERAR STOCK (DEVOLUCIÓN)
     * Suma stock al inventario (ej. cuando se cancela un pedido).
     */
    @Transactional
    public Inventario liberarStock(String producto, int cantidad){

        Inventario inventario = inventarioRepository
                .findByProducto(producto)
                .orElseThrow(() -> {
                    logger.error("❌ ERROR: No se puede liberar stock de '{}' porque no existe.", producto);
                    return new RuntimeException("Producto no encontrado");
                });

        int stockAntes = inventario.getStock();

        // 🆙 Aumentamos el inventario
        inventario.setStock(stockAntes + cantidad);

        Inventario actualizado = inventarioRepository.save(inventario);

        logger.info("🔄 STOCK REPUESTO | Producto: {} | Cantidad Devuelta: {} | Stock Antes: {} | Stock Ahora: {}",
                producto, cantidad, stockAntes, actualizado.getStock());

        return actualizado;
    }
}