
package com.carp.carpooling.service.impl;

import com.carp.carpooling.dto.vehicule.VehiculeCreateRequest;
import com.carp.carpooling.dto.vehicule.VehiculeResponse;
import com.carp.carpooling.dto.vehicule.VehiculeUpdateRequest;
import com.carp.carpooling.entity.Conducteur;
import com.carp.carpooling.entity.Vehicule;
import com.carp.carpooling.exception.BusinessConflictException;
import com.carp.carpooling.exception.ForbiddenOperationException;
import com.carp.carpooling.exception.ResourceNotFoundException;
import com.carp.carpooling.mapper.VehiculeMapper;
import com.carp.carpooling.repository.ConducteurRepository;
import com.carp.carpooling.repository.TrajetRepository;
import com.carp.carpooling.repository.VehiculeRepository;
import com.carp.carpooling.service.VehiculeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class VehiculeServiceImpl implements VehiculeService {

    private static final Logger log =
            LoggerFactory.getLogger(VehiculeServiceImpl.class);

    private final VehiculeRepository vehiculeRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeMapper vehiculeMapper;
    private final TrajetRepository trajetRepository;


    public VehiculeServiceImpl(
            VehiculeRepository vehiculeRepository,
            ConducteurRepository conducteurRepository,
            VehiculeMapper vehiculeMapper, TrajetRepository trajetRepository) {

        this.vehiculeRepository = vehiculeRepository;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeMapper = vehiculeMapper;
        this.trajetRepository = trajetRepository;
    }

    @Override
    @Transactional
    public VehiculeResponse creer(
            Long conducteurId,
            VehiculeCreateRequest request) {

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


    @Override
    @Transactional
    public VehiculeResponse modifier(
            Long vehiculeId,
            Long conducteurId,
            VehiculeUpdateRequest request) {

        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicule introuvable : " + vehiculeId
                        )
                );

        if (!Objects.equals(
                vehicule.getConducteur().getId(),
                conducteurId)) {

            log.warn("Modification refusee : vehicule={}, conducteur={}",
                    vehiculeId, conducteurId);

            throw new ForbiddenOperationException(
                    "Ce vehicule ne vous appartient pas"
            );
        }

        String immatriculation = request.immatriculation()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (vehiculeRepository
                .existsByImmatriculationIgnoreCaseAndIdNot(
                        immatriculation,
                        vehiculeId)) {

            log.warn("Modification refusee : immatriculation deja utilisee");

            throw new BusinessConflictException(
                    "Cette immatriculation est deja utilisee"
            );
        }

        vehicule.modifierInformations(
                request.marque().trim(),
                request.modele().trim(),
                immatriculation,
                request.couleur().trim(),
                request.capacite()
        );

        log.info("Modification du vehicule effectuee dans la transaction : id={}",
                vehiculeId);

        return vehiculeMapper.toResponse(vehicule);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponse trouverParId(Long vehiculeId) {

        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicule introuvable : " + vehiculeId
                        )
                );

        return vehiculeMapper.toResponse(vehicule);
    }


    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> listerParConducteur(
            Long conducteurId) {

        if (!conducteurRepository.existsById(conducteurId)) {
            throw new ResourceNotFoundException(
                    "Conducteur introuvable : " + conducteurId
            );
        }


        return vehiculeRepository
                .findAllByConducteurIdOrderByIdAsc(conducteurId)
                .stream()
                .map(vehiculeMapper::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public void supprimer(Long vehiculeId, Long conducteurId) {

        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicule introuvable : " + vehiculeId
                        )
                );

        if (!Objects.equals(
                vehicule.getConducteur().getId(),
                conducteurId
        )) {
            throw new ForbiddenOperationException(
                    "Ce vehicule ne vous appartient pas"
            );
        }

        if (trajetRepository.existsByVehicule_Id(vehiculeId)) {
            throw new BusinessConflictException(
                    "Impossible de supprimer un vehicule associe a un trajet"
            );
        }

        vehiculeRepository.delete(vehicule);

        log.info("Vehicule supprime : id={}", vehiculeId);
    }



}
