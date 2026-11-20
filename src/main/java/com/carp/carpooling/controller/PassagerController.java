
package com.carp.carpooling.controller;

import com.carp.carpooling.dto.passager.PassagerRequest;
import com.carp.carpooling.dto.passager.PassagerResponse;
import com.carp.carpooling.dto.passager.ReservationHistoriqueResponse;
import com.carp.carpooling.service.PassagerService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassagerController {

    private final PassagerService service;

    public PassagerController(PassagerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PassagerResponse> creer(
            @Valid @RequestBody PassagerRequest request) {

        PassagerResponse response = service.creer(request);

        return ResponseEntity
                .created(URI.create("/api/passengers/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<PassagerResponse>> lister() {

        return ResponseEntity.ok(service.lister());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassagerResponse> trouverParId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.trouverParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassagerResponse> modifier(
            @PathVariable Long id,
            @RequestHeader("X-Passenger-Id") Long passengerId,
            @Valid @RequestBody PassagerRequest request) {

        return ResponseEntity.ok(
                service.modifier(id, passengerId, request)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<PassagerResponse>> rechercher(
            @RequestParam(required = false) String email) {

        return ResponseEntity.ok(
                service.rechercherParEmail(email)
        );
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<List<ReservationHistoriqueResponse>> historique(
            @PathVariable Long id,
            @RequestHeader("X-Passenger-Id") Long passengerId) {

        return ResponseEntity.ok(
                service.historique(id, passengerId)
        );
    }
}
