
package com.carp.carpooling.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "points_arret")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointArret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(nullable = false)
    private int ordre;

    @Column(name = "heure_passage", nullable = false)
    private LocalDateTime heurePassage;

    @Column(
            name = "prix_depuis_depart",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal prixDepuisDepart;

    public PointArret(
            String ville,
            int ordre,
            LocalDateTime heurePassage,
            BigDecimal prixDepuisDepart) {

        this.ville = Objects.requireNonNull(ville);
        this.ordre = ordre;
        this.heurePassage =
                Objects.requireNonNull(heurePassage);
        this.prixDepuisDepart =
                Objects.requireNonNull(prixDepuisDepart);
    }

    void rattacherAuTrajet(Trajet trajet) {
        this.trajet = Objects.requireNonNull(trajet);
    }
}
