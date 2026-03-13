package com.example.labMicro.repositories;

import com.example.labMicro.entities.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProducto(String producto);

}