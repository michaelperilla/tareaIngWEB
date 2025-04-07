package com.example.demo.service;

import com.example.demo.Repositorio.repositorio;
import com.example.demo.entities.EquiposFutbolEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EquiposFutbolService {

    @Autowired
    private repositorio repositorio;

    // Obtener todos los equipos con paginación
    public ResponseEntity<?> getAllEquipos(Pageable pageable) {
        Page<EquiposFutbolEntity> equipos = repositorio.findAll(pageable);
        return buildPagedResponse(equipos);
    }

    // Obtener un equipo por ID
    public ResponseEntity<?> getEquipoById(UUID id) {
        Optional<EquiposFutbolEntity> equipo = repositorio.findById(id);
        if (equipo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("Status", "Equipo no encontrado con ID: " + id));
        }
        return ResponseEntity.ok(Collections.singletonMap("Equipo", equipo.get()));
    }

    // Buscar equipos por nombre
    public ResponseEntity<?> getEquiposByNombre(String nombre, Pageable pageable) {
        Page<EquiposFutbolEntity> equipos = repositorio.findAllByNombreEquipoContaining(nombre, pageable);
        return buildPagedResponse(equipos);
    }

    // Crear un nuevo equipo
    public ResponseEntity<?> createEquipo(EquiposFutbolEntity equipo) {
        Page<EquiposFutbolEntity> existentes = repositorio.findAllByNombreEquipoContaining(
                equipo.getNombreEquipo(), Pageable.unpaged());

        if (existentes.getTotalElements() > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("Status", "Ya existe un equipo con ese nombre."));
        }

        EquiposFutbolEntity guardado = repositorio.save(equipo);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Collections.singletonMap("Status", "Equipo creado con ID: " + guardado.getId()));
    }

    // Actualizar un equipo existente
    public ResponseEntity<?> updateEquipo(UUID id, EquiposFutbolEntity datosActualizados) {
        Optional<EquiposFutbolEntity> equipo = repositorio.findById(id);

        if (equipo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("Status", "Equipo con ID " + id + " no encontrado."));
        }

        EquiposFutbolEntity existente = equipo.get();
        existente.setNombreEquipo(datosActualizados.getNombreEquipo());
        existente.setCiudad(datosActualizados.getCiudad());
        existente.setEstadio(datosActualizados.getEstadio());

        repositorio.save(existente);

        return ResponseEntity.ok(Collections.singletonMap("Status", "Equipo actualizado con ID: " + id));
    }

    // Eliminar un equipo
    public ResponseEntity<?> deleteEquipo(UUID id) {
        Optional<EquiposFutbolEntity> equipo = repositorio.findById(id);

        if (equipo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("Status", "Equipo con ID " + id + " no existe."));
        }

        repositorio.deleteById(id);
        return ResponseEntity.ok(Collections.singletonMap("Status", "Equipo eliminado con ID: " + id));
    }

    // Armar respuesta con paginación
    private ResponseEntity<?> buildPagedResponse(Page<EquiposFutbolEntity> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("TotalElements", page.getTotalElements());
        response.put("TotalPages", page.getTotalPages());
        response.put("CurrentPage", page.getNumber());
        response.put("NumberOfElements", page.getNumberOfElements());
        response.put("Equipos", page.getContent());
        return ResponseEntity.ok(response);
    }
}
