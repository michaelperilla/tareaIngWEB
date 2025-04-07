package com.example.demo.Repositorio;

import com.example.demo.entities.EquiposFutbolEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface repositorio extends JpaRepository<EquiposFutbolEntity, UUID> {

    // Buscar equipos
    Page<EquiposFutbolEntity> findAllByNombreEquipoContaining(String nombre, Pageable pageable);
}
