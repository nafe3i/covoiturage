
package com.carp.carpooling.dto.trajet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TrajetSearchResponse(
        Long trajetId,
        String villeMontee,
        String villeDescente,
        LocalDateTime dateDepartTrajet,
        LocalDateTime heureMontee,
        LocalDateTime heureDescente,
        Integer placesRestantes,
        BigDecimal prixSegment
) {
}
