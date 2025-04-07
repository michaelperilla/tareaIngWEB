package com.example.demo.controller;

import com.example.demo.entities.EquiposFutbolEntity;
import com.example.demo.service.EquiposFutbolService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipos")
@Validated
public class DemoController {

    private final EquiposFutbolService equipoService;

    public DemoController(EquiposFutbolService equipoService) {
        this.equipoService = equipoService;
    }

    @GetMapping
    public ResponseEntity<?> getAllEquipos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "nombreEquipo,asc") String[] sort) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
            return equipoService.getAllEquipos(pageable);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Dirección de ordenamiento inválida. Usa 'asc' o 'desc'.");
        }
    }

    private Sort.Order parseSort(String[] sort) {
        if (sort.length < 2) {
            throw new IllegalArgumentException("El parámetro de ordenamiento debe incluir campo y dirección. Ej: 'nombreEquipo,desc'");
        }

        String property = sort[0];
        String direction = sort[1].toLowerCase();

        List<String> validDirections = Arrays.asList("asc", "desc");
        if (!validDirections.contains(direction)) {
            throw new IllegalArgumentException("Dirección inválida. Usa 'asc' o 'desc'.");
        }

        return new Sort.Order(Sort.Direction.fromString(direction), property);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEquipo(@PathVariable UUID id) {
        return equipoService.getEquipoById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getEquiposByNombre(
        @RequestParam String nombre,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "nombreEquipo,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
        return equipoService.getEquiposByNombre(nombre, pageable);
    }

    @PostMapping
    public ResponseEntity<?> createEquipo(@Valid @RequestBody EquiposFutbolEntity equipo) {
        return equipoService.createEquipo(equipo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEquipo(@PathVariable UUID id, @Valid @RequestBody EquiposFutbolEntity equipo) {
        return equipoService.updateEquipo(id, equipo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquipo(@PathVariable UUID id) {
        return equipoService.deleteEquipo(id);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hola Mundo";
    }
}
