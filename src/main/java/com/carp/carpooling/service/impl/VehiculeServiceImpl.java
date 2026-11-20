
package com.carp.carpooling.service.impl;

import com.carp.carpooling.dto.vehicule.VehiculeCreateRequest;
import com.carp.carpooling.dto.vehicule.VehiculeResponse;
import com.carp.carpooling.entity.Conducteur;
import com.carp.carpooling.entity.Vehicule;
import com.carp.carpooling.exception.BusinessConflictException;
import com.carp.carpooling.exception.ResourceNotFoundException;
import com.carp.carpooling.mapper.VehiculeMapper;
import com.carp.carpooling.repository.ConducteurRepository;
import com.carp.carpooling.repository.VehiculeRepository;
import com.carp.carpooling.service.VehiculeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class VehiculeServiceImpl implements VehiculeService {

    private static final Logger log =
            LoggerFactory.getLogger(VehiculeServiceImpl.class);

    private final VehiculeRepository vehiculeRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeMapper vehiculeMapper;

    public VehiculeServiceImpl(
            VehiculeRepository vehiculeRepository,
            ConducteurRepository conducteurRepository,
            VehiculeMapper vehiculeMapper) {

        this.vehiculeRepository = vehiculeRepository;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeMapper = vehiculeMapper;
    }

    @Override
    @Transactional
    public VehiculeResponse creer(
            Long conducteurId,
            VehiculeCreateRequest request) {

        // R-07 : le conducteur doit exister
        Conducteur conducteur = conducteurRepository
                .findById(conducteurId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conducteur introuvable : " + conducteurId
                        )
                );

        String immatriculation = request.immatriculation()
                .trim()
                .toUpperCase(Locale.ROOT);

        // R-05 : immatriculation unique
        if (vehiculeRepository
                .existsByImmatriculationIgnoreCase(immatriculation)) {

            log.warn(
                    "Creation vehicule refusee : immatriculation deja utilisee"
            );

            throw new BusinessConflictException(
                    "Cette immatriculation est deja utilisee"
            );
        }

        Vehicule vehicule = new Vehicule(
                request.marque().trim(),
                request.modele().trim(),
                immatriculation,
                request.couleur().trim(),
                request.capacite(),
                conducteur
        );

        Vehicule saved = vehiculeRepository.save(vehicule);

        log.info(
                "Vehicule cree : id={}, conducteurId={}",
                saved.getId(),
                conducteurId
        );

        return vehiculeMapper.toResponse(saved);
    }
}
