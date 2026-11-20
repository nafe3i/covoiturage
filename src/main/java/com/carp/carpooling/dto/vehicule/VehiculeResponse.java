
package com.carp.carpooling.dto.vehicule;

public record VehiculeResponse(
        Long id,
        String marque,
        String modele,
        String immatriculation,
        String couleur,
        Integer capacite,
        Long conducteurId
) {
}
