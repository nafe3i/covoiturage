
package com.carp.carpooling.service.impl;

import com.carp.carpooling.dto.trajet.PointArretRequest;
import com.carp.carpooling.dto.trajet.TrajetCreateRequest;
import com.carp.carpooling.dto.trajet.TrajetResponse;
import com.carp.carpooling.entity.Conducteur;
import com.carp.carpooling.entity.PointArret;
import com.carp.carpooling.entity.Trajet;
import com.carp.carpooling.entity.Vehicule;
import com.carp.carpooling.exception.ForbiddenOperationException;
import com.carp.carpooling.exception.InvalidTrajetException;
import com.carp.carpooling.exception.ResourceNotFoundException;
import com.carp.carpooling.mapper.TrajetMapper;
import com.carp.carpooling.repository.ConducteurRepository;
import com.carp.carpooling.repository.TrajetRepository;
import com.carp.carpooling.repository.VehiculeRepository;
import com.carp.carpooling.service.TrajetService;

import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
public class TrajetServiceImpl implements TrajetService {

    private final ConducteurRepository conducteurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final Clock clock;
    private final TrajetRepository trajetRepository;
    private final TrajetMapper trajetMapper;

    public TrajetServiceImpl(
            ConducteurRepository conducteurRepository,
            VehiculeRepository vehiculeRepository, Clock clock, TrajetRepository trajetRepository, TrajetMapper trajetMapper) {

        this.conducteurRepository = conducteurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.clock = clock;
        this.trajetRepository = trajetRepository;
        this.trajetMapper = trajetMapper;
    }

    @Override
    @Transactional
    public TrajetResponse publier(
            Long conducteurId,
            TrajetCreateRequest request) {

        Conducteur conducteur = conducteurRepository
                .findById(conducteurId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conducteur introuvable : " + conducteurId
                        )
                );

        Vehicule vehicule = vehiculeRepository
                .findById(request.vehicleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Véhicule introuvable : " + request.vehicleId()
                        )
                );

        if (!Objects.equals(
                vehicule.getConducteur().getId(),
                conducteur.getId())) {

            throw new ForbiddenOperationException(
                    "Ce véhicule n'appartient pas au conducteur"
            );
        }

        Integer placesProposees = request.placesProposees();

        if (placesProposees == null
                || placesProposees < 1
                || placesProposees > vehicule.getCapacite()) {

            throw new IllegalArgumentException(
                    "Le nombre de places proposees doit être compris entre 1 et "
                            + vehicule.getCapacite()
            );
        }
        if (request.stops() == null
                || request.stops().size() < 2) {

            throw new InvalidTrajetException(
                    "Un trajet doit contenir au moins deux points d'arrêt"
            );
        }


        LocalDateTime maintenant = LocalDateTime.now(clock);
        LocalDateTime heurePrecedente = null;

        for (PointArretRequest stop : request.stops()) {

            if (stop == null || stop.heurePassage() == null) {
                throw new InvalidTrajetException(
                        "Chaque point doit avoir une heure de passage"
                );
            }

            LocalDateTime heureActuelle = stop.heurePassage();

            if (heurePrecedente == null) {

                if (!heureActuelle.isAfter(maintenant)) {
                    throw new InvalidTrajetException(
                            "La date de départ doit être dans le futur"
                    );
                }

            } else {

                if (!heureActuelle.isAfter(heurePrecedente)) {
                    throw new InvalidTrajetException(
                            "Les heures de passage doivent être strictement croissantes"
                    );
                }
            }

            heurePrecedente = heureActuelle;
        }


        Set<String> villesVisitees = new HashSet<>();

        for (PointArretRequest stop : request.stops()) {

            if (stop == null
                    || stop.ville() == null
                    || stop.ville().isBlank()) {

                throw new InvalidTrajetException(
                        "Chaque point d arret doit avoir une ville"
                );
            }

            String villeNormalisee = stop.ville()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            if (!villesVisitees.add(villeNormalisee)) {
                throw new InvalidTrajetException(
                        "La ville apparaît plusieurs fois dans le parcours : "
                                + stop.ville()
                );
            }
        }


        BigDecimal prixPrecedent = null;

        for (PointArretRequest stop : request.stops()) {

            if (stop == null || stop.prixDepuisDepart() == null) {
                throw new InvalidTrajetException(
                        "Chaque point d'arrêt doit avoir un prix"
                );
            }

            BigDecimal prixActuel = stop.prixDepuisDepart();

            if (prixPrecedent == null) {

                if (prixActuel.compareTo(BigDecimal.ZERO) != 0) {
                    throw new InvalidTrajetException(
                            "Le prix du point de départ doit être égal à 0"
                    );
                }

            } else {

                if (prixActuel.compareTo(prixPrecedent) <= 0) {
                    throw new InvalidTrajetException(
                            "Les prix doivent être strictement croissants"
                    );
                }
            }

            prixPrecedent = prixActuel;
        }

        Trajet trajet = new Trajet(
                conducteur,
                vehicule,
                placesProposees
        );

        for (int i = 0; i < request.stops().size(); i++) {

            PointArretRequest stop = request.stops().get(i);

            PointArret point = new PointArret(
                    stop.ville().trim(),
                    i,
                    stop.heurePassage(),
                    stop.prixDepuisDepart()
                            .setScale(2, RoundingMode.UNNECESSARY)
            );

            trajet.ajouterPointArret(point);
        }

        Trajet trajetEnregistre =
                trajetRepository.saveAndFlush(trajet);

        return trajetMapper.toResponse(
                trajetEnregistre,
                trajetEnregistre.getPlacesProposees()
        );


    }
}
