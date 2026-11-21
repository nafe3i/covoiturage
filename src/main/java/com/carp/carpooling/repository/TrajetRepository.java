package com.carp.carpooling.repository;

import com.carp.carpooling.entity.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
