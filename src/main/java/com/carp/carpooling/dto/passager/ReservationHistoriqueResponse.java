
package com.carp.carpooling.dto.passager;

import com.carp.carpooling.entity.enums.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationHistoriqueResponse(
        Long id,
        Long trajetId,
        String villeMontee,
        String villeDescente,
        Integer nombrePlaces,
        BigDecimal montantTotal,
        LocalDateTime dateReservation,
        StatutReservation statut
) {
}
