package com.carp.carpooling.dto.vehicule;

import jakarta.validation.constraints.*;

public record VehiculeUpdateRequest(

        @NotBlank
        @Size(max = 100)
        String marque,

        @NotBlank
        @Size(max = 100)
        String modele,

        @NotBlank
        @Size(max = 30)
        String immatriculation,

        @NotBlank
        @Size(max = 50)
        String couleur,

        @NotNull
        @Min(1)
        @Max(8)
        Integer capacite

) {}