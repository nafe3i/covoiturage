
package com.carp.carpooling.repository;

import com.carp.carpooling.entity.Conducteur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConducteurRepository
        extends JpaRepository<Conducteur, Long> {

    // Vérifications à la création
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNumeroPermis(String numeroPermis);

    // Vérifications à la modification
    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long id
    );

    boolean existsByNumeroPermisAndIdNot(
            String numeroPermis,
            Long id
    );

    // Recherche F-05
    @Query("""
            SELECT c
            FROM Conducteur c
            WHERE (
                :name = ''
                OR LOWER(c.nom) LIKE CONCAT('%', :name, '%') ESCAPE '!'
                OR LOWER(c.prenom) LIKE CONCAT('%', :name, '%') ESCAPE '!'
            )
            AND (
                :email = ''
                OR LOWER(c.email) = :email
            )
            ORDER BY c.id ASC
            """)
    List<Conducteur> rechercher(
            @Param("name") String name,
            @Param("email") String email
    );
}
