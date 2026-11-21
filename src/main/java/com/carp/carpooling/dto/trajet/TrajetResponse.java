
package com.carp.carpooling.dto.trajet;

import com.carp.carpooling.entity.enums.StatutTrajet;

import java.time.LocalDateTime;
import java.util.List;

public record TrajetResponse(

        Long id,

        Long conducteurId,

        Long vehiculeId,

        String villeDepart,

        String villeArrivee,

        LocalDateTime dateDepart,

        Integer placesProposees,

        Integer placesRestantes,

        StatutTrajet statut,

        List<PointArretResponse> stops

) {
}
