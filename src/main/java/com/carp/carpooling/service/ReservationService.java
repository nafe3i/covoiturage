
package com.carp.carpooling.service;

import com.carp.carpooling.dto.reservation.ReservationCreateRequest;
import com.carp.carpooling.dto.reservation.ReservationResponse;

public interface ReservationService {

    ReservationResponse reserver(
            Long passagerId,
            ReservationCreateRequest request
    );
}
