
package com.carp.carpooling.entity;

import com.carp.carpooling.entity.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passager_id", nullable = false)
    private Passager passager;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_montee_id", nullable = false)
    private PointArret pointMontee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_descente_id", nullable = false)
    private PointArret pointDescente;

    @Column(name = "nombre_places", nullable = false)
    private Integer nombrePlaces;

    @Column(
            name = "montant_total",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal montantTotal;

    @Column(name = "date_reservation", nullable = false)
    private LocalDateTime dateReservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutReservation statut =
            StatutReservation.CONFIRMEE;

    public Reservation(
            Passager passager,
            Trajet trajet,
            PointArret pointMontee,
            PointArret pointDescente,
            Integer nombrePlaces,
            LocalDateTime dateReservation) {

        this.passager = Objects.requireNonNull(passager);
        this.trajet = Objects.requireNonNull(trajet);
        this.pointMontee = Objects.requireNonNull(pointMontee);
        this.pointDescente = Objects.requireNonNull(pointDescente);
        this.dateReservation =
                Objects.requireNonNull(dateReservation);

        if (nombrePlaces == null || nombrePlaces < 1) {
            throw new IllegalArgumentException(
                    "Le nombre de places doit être positif"
            );
        }

        if (pointMontee.getOrdre() >= pointDescente.getOrdre()) {
            throw new IllegalArgumentException(
                    "La montée doit précéder la descente"
            );
        }

        BigDecimal prixUnitaire =
                pointDescente.getPrixDepuisDepart()
                        .subtract(pointMontee.getPrixDepuisDepart());

        if (prixUnitaire.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Le prix du segment doit être positif"
            );
        }

        this.nombrePlaces = nombrePlaces;
        this.montantTotal = prixUnitaire
                .multiply(BigDecimal.valueOf(nombrePlaces))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void annuler() {
        if (statut != StatutReservation.CONFIRMEE) {
            throw new IllegalStateException(
                    "La réservation est déjà annulée"
            );
        }

        this.statut = StatutReservation.ANNULEE;
    }
}
