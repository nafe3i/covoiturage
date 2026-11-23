
package com.carp.carpooling.dto.reservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationCreateRequest(

        @NotNull(message = "Le trajet est obligatoire")
        @Positive
        Long trajetId,

        @NotNull(message = "Le point de montée est obligatoire")
        @Positive
        Long pointMonteeId,

        @NotNull(message = "Le point de descente est obligatoire")
        @Positive
        Long pointDescenteId,

        @NotNull(message = "Le nombre de places est obligatoire")
        @Min(value = 1, message = "Réservez au moins une place")
        Integer nombrePlaces

) {
}
