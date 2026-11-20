
package com.carp.carpooling.controller;

import com.carp.carpooling.dto.vehicule.VehiculeCreateRequest;
import com.carp.carpooling.dto.vehicule.VehiculeResponse;
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
}
