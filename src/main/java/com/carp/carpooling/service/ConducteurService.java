
package com.carp.carpooling.service;

import com.carp.carpooling.dto.conducteur.ConducteurRequest;
import com.carp.carpooling.dto.conducteur.ConducteurResponse;
import com.carp.carpooling.dto.conducteur.ConducteurUpdateRequest;

import java.util.List;

public interface ConducteurService {

    ConducteurResponse creer(ConducteurRequest request);

    List<ConducteurResponse> lister();

    ConducteurResponse trouverParId(Long id);

    ConducteurResponse modifier(
            Long id,
            Long driverId,
            ConducteurUpdateRequest request
    );

    List<ConducteurResponse> rechercher(
            String name,
            String email
    );
}
