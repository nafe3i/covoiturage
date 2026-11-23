
package com.carp.carpooling.repository;

import com.carp.carpooling.entity.Reservation;
import com.carp.carpooling.entity.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    @Query("""
            SELECT r
            FROM Reservation r
            JOIN FETCH r.trajet
            JOIN FETCH r.pointMontee
            JOIN FETCH r.pointDescente
            WHERE r.passager.id = :passagerId
            ORDER BY r.dateReservation DESC, r.id DESC
            """)
    List<Reservation> findHistoriqueByPassagerId(
            @Param("passagerId") Long passagerId
    );


    @Query("""
            SELECT COALESCE(SUM(r.nombrePlaces), 0L)
            FROM Reservation r
            WHERE r.trajet.id = :trajetId
              AND r.statut = :statut
            """)
    Long sumPlacesByTrajetIdAndStatut(
            @Param("trajetId") Long trajetId,
            @Param("statut") StatutReservation statut
    );

    @Query("""
            select count(r)>0 from Reservation r join r.passager p join r.trajet t where p.id= :passagerId and t.id = :trajetId and r.statut = :status
            """)
    boolean existsByPassagerIdAndTrajetIdAndStatut(
            @Param("passagerId") Long passagerId,
            @Param("trajetId") Long trajetId,
            @Param("status") StatutReservation statut
    );


    @Query("""
            SELECT r.trajet.id
            FROM Reservation r
            WHERE r.id = :reservationId
            """)
    Optional<Long> findTrajetIdByReservationId(
            @Param("reservationId") Long reservationId
    );

    List<Reservation> findByTrajetIdAndStatut(Long trajetId, StatutReservation statut);



    @Query("""
    SELECT r
    FROM Reservation r
    JOIN FETCH r.passager
    JOIN FETCH r.pointMontee
    JOIN FETCH r.pointDescente
    WHERE r.trajet.id = :trajetId
    ORDER BY r.dateReservation DESC, r.id DESC
    """)
    List<Reservation> findAllDetailsByTrajetId(
            @Param("trajetId") Long trajetId
    );



}
