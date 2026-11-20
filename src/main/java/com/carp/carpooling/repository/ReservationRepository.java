
package com.carp.carpooling.repository;

import com.carp.carpooling.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
}
