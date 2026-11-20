
package com.carp.carpooling.mapper;

import com.carp.carpooling.dto.vehicule.VehiculeResponse;
import com.carp.carpooling.entity.Vehicule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    @Mapping(
            target = "conducteurId",
            source = "conducteur.id"
    )
    VehiculeResponse toResponse(Vehicule vehicule);
}
