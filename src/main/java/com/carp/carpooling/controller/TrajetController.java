
package com.carp.carpooling.controller;

import com.carp.carpooling.dto.reservation.ReservationConducteurResponse;
import com.carp.carpooling.dto.trajet.TrajetCreateRequest;
import com.carp.carpooling.dto.trajet.TrajetResponse;
import com.carp.carpooling.dto.trajet.TrajetSearchResponse;
import com.carp.carpooling.service.ReservationService;
import com.carp.carpooling.service.TrajetService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TrajetController {

    private final TrajetService trajetService;
    private final ReservationService reservationService;


    public TrajetController(TrajetService trajetService, ReservationService reservationService) {
        this.trajetService = trajetService;
        this.reservationService = reservationService;
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


    @GetMapping("/{id}")
    public ResponseEntity<TrajetResponse> trouverParId(
            @PathVariable Long id) {

        TrajetResponse response =
                trajetService.trouverParId(id);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<TrajetSearchResponse>> searchTrips(

            @RequestParam(required = false)
            String departureCity,

            @RequestParam(required = false)
            String arrivalCity,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(defaultValue = "1")
            Integer seats,

            @RequestParam(required = false)
            BigDecimal maxPrice,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        Page<TrajetSearchResponse> resultats =
                trajetService.searchTrips(
                        departureCity,
                        arrivalCity,
                        date,
                        seats,
                        maxPrice,
                        page,
                        size
                );

        return ResponseEntity.ok(resultats);
    }


    @PatchMapping("/{id}/finish")
    public ResponseEntity<Void> terminer(
            @PathVariable Long id,
            @RequestHeader("X-Driver-Id") Long conducteurId
    ) {

        trajetService.terminer(id, conducteurId);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> annuler(
            @PathVariable Long id,
            @RequestHeader("X-Driver-Id") Long conducteurId
    ) {

        trajetService.annuler(id, conducteurId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/reservations")
    public ResponseEntity<List<ReservationConducteurResponse>>
    consulterReservations(
            @PathVariable Long id,
            @RequestHeader("X-Driver-Id") Long conducteurId
    ) {

        List<ReservationConducteurResponse> reservations =
                reservationService.consulterReservationsTrajet(
                        id,
                        conducteurId
                );

        return ResponseEntity.ok(reservations);
    }



}
