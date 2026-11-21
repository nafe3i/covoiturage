
package com.carp.carpooling.controller;

import com.carp.carpooling.dto.trajet.TrajetCreateRequest;
import com.carp.carpooling.dto.trajet.TrajetResponse;
import com.carp.carpooling.service.TrajetService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping
    public ResponseEntity<TrajetResponse> publier(
            @RequestHeader("X-Driver-Id") Long conducteurId,
            @Valid @RequestBody TrajetCreateRequest request
    ) {

        TrajetResponse response =
                trajetService.publier(conducteurId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
