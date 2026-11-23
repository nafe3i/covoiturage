
package com.carp.carpooling.entity;

import com.carp.carpooling.entity.enums.StatutTrajet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "trajets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @Column(name = "date_depart", nullable = false)
    private LocalDateTime dateDepart;

    @Column(name = "places_proposees", nullable = false)
    private Integer placesProposees;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutTrajet statut = StatutTrajet.PUBLIE;

    @OneToMany(
            mappedBy = "trajet",
            cascade = CascadeType.PERSIST
    )
    @OrderBy("ordre ASC")
    @Getter(AccessLevel.NONE)
    private List<PointArret> pointsArret = new ArrayList<>();

    public Trajet(
            Conducteur conducteur,
            Vehicule vehicule,
            Integer placesProposees) {

        this.conducteur =
                Objects.requireNonNull(conducteur);
        this.vehicule =
                Objects.requireNonNull(vehicule);
        this.placesProposees = placesProposees;
    }

    public void ajouterPointArret(PointArret point) {
        Objects.requireNonNull(point);

        if (point.getOrdre() != pointsArret.size()) {
            throw new IllegalArgumentException(
                    "L'ordre du point est invalide"
            );
        }

        if (point.getTrajet() != null
                && point.getTrajet() != this) {
            throw new IllegalArgumentException(
                    "Le point appartient à un autre trajet"
            );
        }

        if (pointsArret.isEmpty()) {
            this.dateDepart = point.getHeurePassage();
        }

        point.rattacherAuTrajet(this);
        pointsArret.add(point);
    }

    public List<PointArret> getPointsArret() {
        return Collections.unmodifiableList(pointsArret);
    }

    public void marquerComplet() {
        if (statut != StatutTrajet.PUBLIE) {
            throw new IllegalStateException(
                    "Seul un trajet publie peut devenir complet"
            );
        }

        this.statut = StatutTrajet.COMPLET;
    }
}
