
package com.carp.carpooling.controller;

import com.carp.carpooling.dto.conducteur.ConducteurRequest;
import com.carp.carpooling.dto.conducteur.ConducteurResponse;
import com.carp.carpooling.dto.conducteur.ConducteurUpdateRequest;
import com.carp.carpooling.dto.vehicule.VehiculeResponse;
import com.carp.carpooling.service.ConducteurService;
import com.carp.carpooling.service.VehiculeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class ConducteurController {

    private final ConducteurService service;
    private final VehiculeService vehiculeService;

    public ConducteurController(ConducteurService service, VehiculeService vehiculeService) {
        this.service = service;
        this.vehiculeService = vehiculeService;
    }

    // F-01 : Creer un conducteur
    @PostMapping
    public ResponseEntity<ConducteurResponse> creer(
            @Valid @RequestBody ConducteurRequest request) {

        ConducteurResponse response = service.creer(request);

        return ResponseEntity
                .created(URI.create("/api/drivers/" + response.id()))
                .body(response);
    }

    // F-02 : Lister les conducteurs
    @GetMapping
    public ResponseEntity<List<ConducteurResponse>> lister() {

        return ResponseEntity.ok(service.lister());
    }

    // F-03 : Consulter un conducteur
    @GetMapping("/{id}")
    public ResponseEntity<ConducteurResponse> trouverParId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.trouverParId(id));
    }

    // F-04 : Modifier son propre profil
    @PutMapping("/{id}")
    public ResponseEntity<ConducteurResponse> modifier(
            @PathVariable Long id,
            @RequestHeader("X-Driver-Id") Long driverId,
            @Valid @RequestBody ConducteurUpdateRequest request) {

        return ResponseEntity.ok(
                service.modifier(id, driverId, request)
        );
    }

    // F-05 : Rechercher par nom/prénom ou email
    @GetMapping("/search")
    public ResponseEntity<List<ConducteurResponse>> rechercher(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {

        return ResponseEntity.ok(
                service.rechercher(name, email)
        );
    }

    @GetMapping("/{id}/vehicles")
    public ResponseEntity<List<VehiculeResponse>> listerVehicules(
            @PathVariable Long id) {

        List<VehiculeResponse> vehicules =
                vehiculeService.listerParConducteur(id);

        return ResponseEntity.ok(vehicules);
    }
}
