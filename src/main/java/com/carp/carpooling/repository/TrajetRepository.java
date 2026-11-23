package com.carp.carpooling.repository;

import com.carp.carpooling.entity.Trajet;
import com.carp.carpooling.entity.enums.StatutReservation;
import com.carp.carpooling.entity.enums.StatutTrajet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.carp.carpooling.dto.trajet.TrajetSearchResponse;
import com.carp.carpooling.entity.enums.StatutTrajet;
import com.carp.carpooling.entity.enums.StatutReservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {


    //            JOIN FETCH t.conducteur
//            JOIN FETCH t.vehicule
    @Query("""
            SELECT DISTINCT t
            FROM Trajet t
            JOIN FETCH t.pointsArret
            WHERE t.id = :id
            """)
    Optional<Trajet> findDetailById(@Param("id") Long id);


    @Query(
            value = """
                    SELECT DISTINCT new com.carp.carpooling.dto.trajet.TrajetSearchResponse(
                        t.id,
                        pm.ville,
                        pd.ville,
                        t.dateDepart,
                        pm.heurePassage,
                        pd.heurePassage,
                        CAST(
                            t.placesProposees - (
                                SELECT COALESCE(SUM(r.nombrePlaces), 0L)
                                FROM Reservation r
                                WHERE r.trajet.id = t.id
                                  AND r.statut = :reservationStatus
                            ) AS Integer
                        ),
                        pd.prixDepuisDepart - pm.prixDepuisDepart
                    )
                    FROM Trajet t
                    JOIN t.pointsArret pm
                    JOIN t.pointsArret pd
                    WHERE LOWER(pm.ville) = LOWER(:departureCity)
                      AND LOWER(pd.ville) = LOWER(:arrivalCity)
                      AND pm.ordre < pd.ordre
                      AND t.statut = :statut
                      AND t.dateDepart >= :debutJour
                      AND t.dateDepart < :finJour
                      AND t.placesProposees - (
                          SELECT COALESCE(SUM(r.nombrePlaces), 0L)
                          FROM Reservation r
                          WHERE r.trajet.id = t.id
                            AND r.statut = :reservationStatus
                      ) >= :seats
                      AND (
                          pd.prixDepuisDepart - pm.prixDepuisDepart
                          <= COALESCE(
                              :maxPrice,
                              pd.prixDepuisDepart - pm.prixDepuisDepart
                          )
                      )
                    ORDER BY t.dateDepart ASC, t.id ASC
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT t.id)
                    FROM Trajet t
                    JOIN t.pointsArret pm
                    JOIN t.pointsArret pd
                    WHERE LOWER(pm.ville) = LOWER(:departureCity)
                      AND LOWER(pd.ville) = LOWER(:arrivalCity)
                      AND pm.ordre < pd.ordre
                      AND t.statut = :statut
                      AND t.dateDepart >= :debutJour
                      AND t.dateDepart < :finJour
                      AND t.placesProposees - (
                          SELECT COALESCE(SUM(r.nombrePlaces), 0L)
                          FROM Reservation r
                          WHERE r.trajet.id = t.id
                            AND r.statut = :reservationStatus
                      ) >= :seats
                      AND (
                          pd.prixDepuisDepart - pm.prixDepuisDepart
                          <= COALESCE(
                              :maxPrice,
                              pd.prixDepuisDepart - pm.prixDepuisDepart
                          )
                      )
                    """
    )
    Page<TrajetSearchResponse> searchTrips(
            @Param("departureCity") String departureCity,
            @Param("arrivalCity") String arrivalCity,
            @Param("statut") StatutTrajet statut,
            @Param("debutJour") LocalDateTime debutJour,
            @Param("finJour") LocalDateTime finJour,
            @Param("reservationStatus") StatutReservation reservationStatus,
            @Param("seats") Integer seats,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trajet> findById(Long id);

//    @Query("""
//            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
//            FROM Trajet t
//            JOIN t.pointsArret p
//            WHERE t.id = :trajetId
//              AND p.id = :pointId
//            """)
//    boolean existsPointArretInTrajet(
//            @Param("trajetId") Long trajetId,
//            @Param("pointId") Long pointId
//    );

}
