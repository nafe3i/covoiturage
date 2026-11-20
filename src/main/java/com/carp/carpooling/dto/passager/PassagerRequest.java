
package com.carp.carpooling.dto.passager;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PassagerRequest(

        @NotBlank
        @Size(max = 100)
        String nom,

        @NotBlank
        @Size(max = 100)
        String prenom,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(max = 30)
        String telephone

) {
}
