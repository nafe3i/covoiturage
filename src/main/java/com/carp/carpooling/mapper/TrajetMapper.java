
package com.carp.carpooling.mapper;

import com.carp.carpooling.dto.trajet.TrajetResponse;
import com.carp.carpooling.entity.Trajet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = PointArretMapper.class
)
public interface TrajetMapper {

    @Mapping(target = "conducteurId",
            source = "trajet.conducteur.id")

    @Mapping(target = "vehiculeId",
            source = "trajet.vehicule.id")

    @Mapping(target = "stops",
            source = "trajet.pointsArret")

    @Mapping(target = "villeDepart",
            expression = "java(trajet.getPointsArret().get(0).getVille())")

    @Mapping(target = "villeArrivee",
            expression = "java(trajet.getPointsArret().get(trajet.getPointsArret().size() - 1).getVille())")

    @Mapping(target = "placesRestantes",
            source = "placesRestantes")
    TrajetResponse toResponse(
            Trajet trajet,
            Integer placesRestantes
    );
}
