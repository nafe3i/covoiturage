
package com.carp.carpooling.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "vehicules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String marque;

    @Column(nullable = false, length = 100)
    private String modele;

    @Column(nullable = false, unique = true, length = 30)
    private String immatriculation;

    @Column(nullable = false, length = 50)
    private String couleur;

    @Column(nullable = false)
    private Integer capacite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    public Vehicule(
            String marque,
            String modele,
            String immatriculation,
            String couleur,
            Integer capacite,
            Conducteur conducteur) {

        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.couleur = couleur;
        this.capacite = capacite;
        this.conducteur = conducteur;
    }
}
