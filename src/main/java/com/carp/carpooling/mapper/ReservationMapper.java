package com.carp.carpooling.mapper;

import com.carp.carpooling.dto.reservation.ReservationResponse;
import com.carp.carpooling.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "id",source = "reservation.id")
    @Mapping(target = "passagerId", source = "reservation.passager.id")
    @Mapping(target = "trajetId", source = "reservation.trajet.id")
    @Mapping(target = "pointMonteeId", source = "reservation.pointMontee.id")
    @Mapping(target = "pointDescenteId", source = "reservation.pointDescente.id")
    @Mapping(target = "nombrePlaces", source = "reservation.nombrePlaces")
    @Mapping(target = "montantTotal", source = "reservation.montantTotal")
    @Mapping(target = "dateReservation", source = "reservation.dateReservation")
    @Mapping(target = "statut", source = "reservation.statut")
    @Mapping(target = "placesRestantes", source = "placesRestantes")
    ReservationResponse toResponse(
            Reservation reservation,
            Integer placesRestantes
    );

}
