
package com.carp.carpooling.dto.trajet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TrajetCreateRequest(

        @NotNull(message = "Le véhicule est obligatoire")
        Long vehicleId,

        @NotNull(message = "Le nombre de places est obligatoire")
        @Min(value = 1, message = "Au moins une place doit être proposée")
        Integer placesProposees,

        @NotNull(message = "Le parcours est obligatoire")
        @Size(
                min = 2,
                message = "Un trajet doit contenir au moins deux points d'arrêt"
        )
        List<@NotNull @Valid PointArretRequest> stops

) {
}
