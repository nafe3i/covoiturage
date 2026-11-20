
package com.carp.carpooling.service.impl;

import com.carp.carpooling.dto.passager.PassagerRequest;
import com.carp.carpooling.dto.passager.PassagerResponse;
import com.carp.carpooling.dto.passager.ReservationHistoriqueResponse;
import com.carp.carpooling.entity.Passager;
import com.carp.carpooling.exception.BusinessConflictException;
import com.carp.carpooling.exception.ForbiddenOperationException;
import com.carp.carpooling.exception.ResourceNotFoundException;
import com.carp.carpooling.mapper.PassagerMapper;
import com.carp.carpooling.repository.PassagerRepository;
import com.carp.carpooling.repository.ReservationRepository;
import com.carp.carpooling.service.PassagerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class PassagerServiceImpl implements PassagerService {

    private static final Logger log =
            LoggerFactory.getLogger(PassagerServiceImpl.class);

    private final PassagerRepository passagerRepository;
    private final ReservationRepository reservationRepository;
    private final PassagerMapper mapper;

    public PassagerServiceImpl(
            PassagerRepository passagerRepository,
            ReservationRepository reservationRepository,
            PassagerMapper mapper) {

        this.passagerRepository = passagerRepository;
        this.reservationRepository = reservationRepository;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public PassagerResponse creer(PassagerRequest request) {

        String email = normaliserEmail(request.email());

        if (passagerRepository.existsByEmailIgnoreCase(email)) {
            log.warn("Creation refusee : email deja utilise");

            throw new BusinessConflictException(
                    "Cet email est deja utilise"
            );
        }

        Passager passager = new Passager(
                request.nom().trim(),
                request.prenom().trim(),
                email,
                request.telephone().trim()
        );

        Passager saved = passagerRepository.save(passager);

        log.info("Passager cree : id={}", saved.getId());

        return mapper.toResponse(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<PassagerResponse> lister() {

        return passagerRepository
                .findAll(Sort.by("id").ascending())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PassagerResponse trouverParId(Long id) {

        Passager passager = trouverPassager(id);

        return mapper.toResponse(passager);
    }


    @Override
    @Transactional
    public PassagerResponse modifier(
            Long id,
            Long passengerId,
            PassagerRequest request) {

        verifierProprietaire(id, passengerId);

        Passager passager = trouverPassager(id);

        String email = normaliserEmail(request.email());

        if (passagerRepository.existsByEmailIgnoreCaseAndIdNot(
                email, id)) {

            log.warn("Modification refusee : email deja utilise");

            throw new BusinessConflictException(
                    "Cet email appartient deja a un autre passager"
            );
        }

        passager.modifierProfil(
                request.nom().trim(),
                request.prenom().trim(),
                email,
                request.telephone().trim()
        );

        log.info("Profil passager modifie : id={}", id);

        return mapper.toResponse(passager);
    }


    @Override
    @Transactional(readOnly = true)
    public List<PassagerResponse> rechercherParEmail(
            String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "L'email de recherche est obligatoire"
            );
        }

        return passagerRepository
                .findByEmailIgnoreCase(email.trim())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationHistoriqueResponse> historique(
            Long id,
            Long passengerId) {

        verifierProprietaire(id, passengerId);

        // Distinguer passager inexistant et historique vide.
        if (!passagerRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Passager introuvable : " + id
            );
        }

        return reservationRepository
                .findHistoriqueByPassagerId(id)
                .stream()
                .map(mapper::toHistoriqueResponse)
                .toList();
    }

    private Passager trouverPassager(Long id) {

        return passagerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Passager introuvable : " + id
                        )
                );
    }

    private void verifierProprietaire(
            Long id,
            Long passengerId) {

        if (!Objects.equals(id, passengerId)) {
            throw new ForbiddenOperationException(
                    "Vous ne pouvez acceder qu'a vos propres donnees"
            );
        }
    }

    private String normaliserEmail(String email) {

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
