package com.carp.carpooling.controller;

import com.carp.carpooling.dto.conducteur.*;
import com.carp.carpooling.service.ConducteurService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
}