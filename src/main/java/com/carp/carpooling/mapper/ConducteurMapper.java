
package com.carp.carpooling.mapper;

import com.carp.carpooling.dto.conducteur.ConducteurResponse;
import com.carp.carpooling.entity.Conducteur;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConducteurMapper {

    ConducteurResponse toResponse(Conducteur conducteur);
}
