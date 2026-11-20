
package com.carp.carpooling.mapper;

import com.carp.carpooling.dto.passager.PassagerResponse;
import com.carp.carpooling.dto.passager.ReservationHistoriqueResponse;
import com.carp.carpooling.entity.Passager;
import com.carp.carpooling.entity.Reservation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PassagerMapper {

    PassagerResponse toResponse(Passager passager);

    @Mapping(target = "trajetId", source = "trajet.id")
    @Mapping(target = "villeMontee", source = "pointMontee.ville")
    @Mapping(target = "villeDescente", source = "pointDescente.ville")
    ReservationHistoriqueResponse toHistoriqueResponse(
            Reservation reservation
    );
}
