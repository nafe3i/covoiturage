
package com.carp.carpooling.controller;

import com.carp.carpooling.dto.vehicule.VehiculeCreateRequest;
import com.carp.carpooling.dto.vehicule.VehiculeResponse;
import com.carp.carpooling.dto.vehicule.VehiculeUpdateRequest;
import com.carp.carpooling.service.VehiculeService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    public ResponseEntity<VehiculeResponse> creer(
            @RequestHeader("X-Driver-Id") Long driverId,
            @Valid @RequestBody VehiculeCreateRequest request
    ) {
        VehiculeResponse response =
                vehiculeService.creer(driverId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeResponse> modifier(
            @PathVariable Long id,
            @RequestHeader("X-Driver-Id") Long driverId,
            @Valid @RequestBody VehiculeUpdateRequest request
    ) {

        VehiculeResponse response =
                vehiculeService.modifier(id, driverId, request);

        return ResponseEntity.ok(response);
    }
}
