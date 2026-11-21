
package com.carp.carpooling.service;

import com.carp.carpooling.dto.trajet.TrajetCreateRequest;
import com.carp.carpooling.dto.trajet.TrajetResponse;

public interface TrajetService {

    TrajetResponse publier(
            Long conducteurId,
            TrajetCreateRequest request
    );

    TrajetResponse trouverParId(Long trajetId);
}
