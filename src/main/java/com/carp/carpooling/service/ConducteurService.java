package com.carp.carpooling.service;

import com.carp.carpooling.dto.conducteur.*;

public interface ConducteurService {
    ConducteurResponse creer(ConducteurRequest request);
}