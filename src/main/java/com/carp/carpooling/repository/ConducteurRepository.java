package com.carp.carpooling.repository;

import com.carp.carpooling.entity.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConducteurRepository
        extends JpaRepository<Conducteur, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNumeroPermis(String numeroPermis);

    @Query("""
            SELECT c FROM Conducteur c
            WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))
              AND LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))
            ORDER BY c.id ASC
            """)
    List<Conducteur> rechercher(
            @Param("nom") String nom,
            @Param("email") String email
    );
}