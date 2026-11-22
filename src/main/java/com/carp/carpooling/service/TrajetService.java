
package com.carp.carpooling.service;

import com.carp.carpooling.dto.trajet.TrajetCreateRequest;
import com.carp.carpooling.dto.trajet.TrajetResponse;
import com.carp.carpooling.dto.trajet.TrajetSearchResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TrajetService {

    TrajetResponse publier(
            Long conducteurId,
            TrajetCreateRequest request
    );

    TrajetResponse trouverParId(Long trajetId);


    Page<TrajetSearchResponse> searchTrips(
            String departureCity,
            String arrivalCity,
            LocalDate date,
            Integer seats,
            BigDecimal maxPrice,
            int page,
            int size
    );

}
