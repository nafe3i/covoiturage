
package com.carp.carpooling.mapper;

import com.carp.carpooling.dto.trajet.PointArretResponse;
import com.carp.carpooling.entity.PointArret;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointArretMapper {

    PointArretResponse toResponse(PointArret pointArret);
}
