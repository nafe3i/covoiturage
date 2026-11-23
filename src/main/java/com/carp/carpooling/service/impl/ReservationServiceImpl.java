
package com.carp.carpooling.service.impl;

import com.carp.carpooling.dto.reservation.ReservationConducteurResponse;
import com.carp.carpooling.dto.reservation.ReservationCreateRequest;
import com.carp.carpooling.dto.reservation.ReservationResponse;
import com.carp.carpooling.entity.Passager;
import com.carp.carpooling.entity.PointArret;
import com.carp.carpooling.entity.Reservation;
import com.carp.carpooling.entity.Trajet;
import com.carp.carpooling.entity.enums.StatutReservation;
import com.carp.carpooling.entity.enums.StatutTrajet;
import com.carp.carpooling.exception.BusinessConflictException;
import com.carp.carpooling.exception.ForbiddenOperationException;
import com.carp.carpooling.exception.ResourceNotFoundException;
import com.carp.carpooling.mapper.ReservationMapper;
import com.carp.carpooling.repository.PassagerRepository;
import com.carp.carpooling.repository.ReservationRepository;
import com.carp.carpooling.repository.TrajetRepository;
import com.carp.carpooling.service.ReservationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final PassagerRepository passagerRepository;
    private final TrajetRepository trajetRepository;
    private final ReservationRepository reservationRepository;
    private final Clock clock;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(
            PassagerRepository passagerRepository,
            TrajetRepository trajetRepository,
            ReservationRepository reservationRepository,
            Clock clock,
            ReservationMapper reservationMapper
    ) {
        this.passagerRepository = passagerRepository;
        this.trajetRepository = trajetRepository;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
        this.reservationMapper = reservationMapper;
    }

    @Override
    @Transactional
    public ReservationResponse reserver(
            Long passagerId,
            ReservationCreateRequest request
    ) {

        if (passagerId == null || passagerId <= 0) {
            throw new IllegalArgumentException(
                    "Identifiant du passager invalide"
            );
        }


        Passager passager = passagerRepository
                .findById(passagerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Passager introuvable : " + passagerId
                        )
                );

        Trajet trajet = trajetRepository
                .findById(request.trajetId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Trajet introuvable : " + request.trajetId()
                        )
                );

        if (trajet.getStatut() != StatutTrajet.PUBLIE) {
            throw new BusinessConflictException(
                    "Le trajet n'est pas ouvert à la reservation"
            );
        }

        LocalDateTime maintenant = LocalDateTime.now(clock);

        if (!trajet.getDateDepart().isAfter(maintenant)) {
            throw new BusinessConflictException(
                    "Le trajet a déja commence"
            );
        }

        boolean dejaReserve = reservationRepository
                .existsByPassagerIdAndTrajetIdAndStatut(
                        passagerId,
                        trajet.getId(),
                        StatutReservation.CONFIRMEE
                );

        if (dejaReserve) {
            throw new BusinessConflictException(
                    "Vous avez deja une reservation confirmee sur ce trajet"
            );
        }

        PointArret pointMontee = trajet.getPointsArret()
                .stream()
                .filter(p -> Objects.equals(
                        p.getId(),
                        request.pointMonteeId()
                ))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Le point de montee n appartient pas au trajet"
                        )
                );

        PointArret pointDescente = trajet.getPointsArret()
                .stream()
                .filter(p -> Objects.equals(
                        p.getId(),
                        request.pointDescenteId()
                ))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Le point de descente n appartient pas au trajet"
                        )
                );

        if (pointMontee.getOrdre() >= pointDescente.getOrdre()) {
            throw new IllegalArgumentException(
                    "La montee doit preceder la descente"
            );
        }

        Long placesReservees = reservationRepository
                .sumPlacesByTrajetIdAndStatut(
                        trajet.getId(),
                        StatutReservation.CONFIRMEE
                );

        int placesDisponibles = Math.toIntExact(
                trajet.getPlacesProposees() - placesReservees
        );

        if (placesDisponibles < request.nombrePlaces()) {
            throw new BusinessConflictException(
                    "Nombre de places insuffisant"
            );
        }

        Reservation reservation = new Reservation(
                passager,
                trajet,
                pointMontee,
                pointDescente,
                request.nombrePlaces(),
                maintenant
        );

        int placesRestantes =
                placesDisponibles - request.nombrePlaces();

        if (placesRestantes == 0) {
            trajet.marquerComplet();
        }

        Reservation enregistree =
                reservationRepository.save(reservation);

        return reservationMapper.toResponse(
                enregistree,
                placesRestantes
        );
    }


    @Override
    @Transactional
    public void annuler(Long reservationId, Long passagerId) {

        // 1. Identifier le trajet sans charger la réservation
        Long trajetId = reservationRepository
                .findTrajetIdByReservationId(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Réservation introuvable : " + reservationId
                        )
                );

        // 2. Verrouiller le trajet
        Trajet trajet = trajetRepository
                .findById(trajetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Trajet introuvable : " + trajetId
                        )
                );

        // 3. Charger la réservation après le verrou
        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Réservation introuvable : " + reservationId
                        )
                );

        // 4. Vérifier le propriétaire
        if (!Objects.equals(
                reservation.getPassager().getId(),
                passagerId
        )) {
            throw new ForbiddenOperationException(
                    "Cette réservation n'appartient pas au passager"
            );
        }

        // 5. Vérifier le statut de la réservation
        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new BusinessConflictException(
                    "Cette réservation est déjà annulée"
            );
        }

        // 6. Vérifier le délai de 24 heures
        LocalDateTime maintenant = LocalDateTime.now(clock);

        LocalDateTime dateLimiteAnnulation =
                trajet.getDateDepart().minusHours(24);

        if (maintenant.isAfter(dateLimiteAnnulation)) {
            throw new BusinessConflictException(
                    "Annulation impossible : délai de 24 heures dépassé"
            );
        }

        // 7. Annuler la réservation
        reservation.annuler();

        // 8. Rouvrir le trajet si nécessaire
        if (trajet.getStatut() == StatutTrajet.COMPLET) {
            trajet.reouvrirApresAnnulation();
        }

    }


    @Override
    @Transactional(readOnly = true)
    public List<ReservationConducteurResponse> consulterReservationsTrajet(
            Long trajetId,
            Long conducteurId
    ) {

        // 1. Vérifier que le trajet existe et retrouver son propriétaire
        Long proprietaireId = trajetRepository
                .findConducteurIdByTrajetId(trajetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Trajet introuvable : " + trajetId
                        )
                );

        // 2. Vérifier que le conducteur est propriétaire du trajet
        if (!Objects.equals(proprietaireId, conducteurId)) {
            throw new ForbiddenOperationException(
                    "Vous ne pouvez consulter que les reservations de vos trajets"
            );
        }

        // 3. Récupérer les réservations confirmées et annulées
        return reservationRepository
                .findAllDetailsByTrajetId(trajetId)
                .stream()
                .map(reservationMapper::toConducteurResponse)
                .toList();
    }


}
