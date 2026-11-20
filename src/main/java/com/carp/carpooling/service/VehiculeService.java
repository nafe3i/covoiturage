
package com.carp.carpooling.service;

import com.carp.carpooling.dto.vehicule.VehiculeCreateRequest;
//import com.carp.carpooling.dto.vehicule.VehiculeUpdateRequest;
import com.carp.carpooling.dto.vehicule.VehiculeResponse;

import java.util.List;

public interface VehiculeService {

    VehiculeResponse creer(
            Long conducteurId,
            VehiculeCreateRequest request
    );

//    VehiculeResponse trouverParId(Long vehiculeId);
//
//    List<VehiculeResponse> listerParConducteur(
//            Long conducteurId
//    );
//
//    VehiculeResponse modifier(
//            Long vehiculeId,
//            Long conducteurId,
//            VehiculeUpdateRequest request
//    );
//
//    void supprimer(
//            Long vehiculeId,
//            Long conducteurId
//    );
}
