
package com.carp.carpooling.dto.passager;

public record PassagerResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone
) {
}
