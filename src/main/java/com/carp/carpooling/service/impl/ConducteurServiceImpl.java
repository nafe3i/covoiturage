package com.carp.carpooling.service.impl;

import com.carp.carpooling.dto.conducteur.*;
import com.carp.carpooling.entity.Conducteur;
import com.carp.carpooling.exception.BusinessConflictException;
import com.carp.carpooling.mapper.ConducteurMapper;
import com.carp.carpooling.repository.ConducteurRepository;
import com.carp.carpooling.service.ConducteurService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class ConducteurServiceImpl implements ConducteurService {

    private final ConducteurRepository repository;
    private final ConducteurMapper mapper;

    public ConducteurServiceImpl(
            ConducteurRepository repository,
            ConducteurMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ConducteurResponse creer(ConducteurRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        String permis = request.numeroPermis().trim();

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new BusinessConflictException(
                    "Cet email est déjà utilisé"
            );
        }

        if (repository.existsByNumeroPermis(permis)) {
            throw new BusinessConflictException(
                    "Ce numéro de permis est déjà utilisé"
            );
        }

        Conducteur conducteur = new Conducteur(
                request.nom().trim(),
                request.prenom().trim(),
                email,
                request.telephone().trim(),
                permis
        );

        Conducteur saved = repository.save(conducteur);

        return mapper.toResponse(saved);
    }
}