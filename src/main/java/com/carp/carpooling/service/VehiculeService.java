
package com.carp.carpooling.service;

import com.carp.carpooling.dto.vehicule.VehiculeCreateRequest;
import com.carp.carpooling.dto.vehicule.VehiculeResponse;
import com.carp.carpooling.dto.vehicule.VehiculeUpdateRequest;

import java.util.List;

public interface VehiculeService {

    VehiculeResponse creer(
            Long conducteurId,
            VehiculeCreateRequest request
    );

    VehiculeResponse modifier(
            Long vehiculeId,
            Long conducteurId,
            VehiculeUpdateRequest request
    );
    VehiculeResponse trouverParId(Long vehiculeId);
//
//    List<VehiculeResponse> listerParConducteur(
//            Long conducteurId
//    );
//
//
//    void supprimer(
//            Long vehiculeId,
//            Long conducteurId
//    );
}
