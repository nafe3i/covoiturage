package com.carp.carpooling.service;

import com.carp.carpooling.dto.conducteur.*;

import java.util.List;

public interface ConducteurService {
    ConducteurResponse creer(ConducteurRequest request);

    List<ConducteurResponse> rechercher(String nom, String email);

    ConducteurResponse trouverParId(Long id);
}