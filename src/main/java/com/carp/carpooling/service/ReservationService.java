
package com.carp.carpooling.service;

import com.carp.carpooling.dto.reservation.ReservationConducteurResponse;
import com.carp.carpooling.dto.reservation.ReservationCreateRequest;
import com.carp.carpooling.dto.reservation.ReservationResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse reserver(
            Long passagerId,
            ReservationCreateRequest request
    );

    void annuler(Long reservationId, Long passagerId);


    List<ReservationConducteurResponse> consulterReservationsTrajet(
            Long trajetId,
            Long conducteurId
    );

}
