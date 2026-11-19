package com.carp.carpooling.repository;

import com.carp.carpooling.entity.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConducteurRepository
        extends JpaRepository<Conducteur, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNumeroPermis(String numeroPermis);
}