
package com.carp.carpooling.controller;

import com.carp.carpooling.dto.reservation.ReservationCreateRequest;
import com.carp.carpooling.dto.reservation.ReservationResponse;
import com.carp.carpooling.service.ReservationService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reserver(
            @RequestHeader("X-Passenger-Id") Long passagerId,
            @Valid @RequestBody ReservationCreateRequest request
    ) {

        ReservationResponse response =
                reservationService.reserver(passagerId, request);

        return ResponseEntity
                .created(URI.create(
                        "/api/reservations/" + response.id()
                ))
                .body(response);
    }
}
