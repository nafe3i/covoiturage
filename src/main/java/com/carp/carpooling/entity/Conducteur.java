
package com.carp.carpooling.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@Table(name = "conducteurs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conducteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 30)
    private String telephone;

    @Column(
            name = "numero_permis",
            nullable = false,
            unique = true,
            length = 50
    )
    private String numeroPermis;

    public Conducteur(String nom, String prenom, String email, String telephone, String numeroPermis) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.numeroPermis = numeroPermis;
    }

}
