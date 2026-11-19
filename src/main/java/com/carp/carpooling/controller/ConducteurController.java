package com.carp.carpooling.controller;

import com.carp.carpooling.dto.conducteur.*;
import com.carp.carpooling.service.ConducteurService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/conducteurs")
public class ConducteurController {

    private final ConducteurService service;

    public ConducteurController(ConducteurService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConducteurResponse> creer(
            @Valid @RequestBody ConducteurRequest request) {

        ConducteurResponse response = service.creer(request);

        return ResponseEntity
                .created(URI.create("/api/conducteurs/" + response.id()))
                .body(response);
    }

    @GetMapping
    public List<ConducteurResponse> rechercher(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String email) {

        return service.rechercher(nom, email);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConducteurResponse> trouverParId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.trouverParId(id));
    }
}