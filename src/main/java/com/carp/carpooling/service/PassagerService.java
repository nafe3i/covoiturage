
package com.carp.carpooling.service;

import com.carp.carpooling.dto.passager.PassagerRequest;
import com.carp.carpooling.dto.passager.PassagerResponse;
import com.carp.carpooling.dto.passager.ReservationHistoriqueResponse;

import java.util.List;

public interface PassagerService {

    PassagerResponse creer(PassagerRequest request);

    List<PassagerResponse> lister();

    PassagerResponse trouverParId(Long id);

    PassagerResponse modifier(
            Long id,
            Long passengerId,
            PassagerRequest request
    );

    List<PassagerResponse> rechercherParEmail(String email);

    List<ReservationHistoriqueResponse> historique(
            Long id,
            Long passengerId
    );


}
