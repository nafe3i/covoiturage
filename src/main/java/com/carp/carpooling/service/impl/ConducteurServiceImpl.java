
package com.carp.carpooling.service.impl;

import com.carp.carpooling.dto.conducteur.ConducteurRequest;
import com.carp.carpooling.dto.conducteur.ConducteurResponse;
import com.carp.carpooling.dto.conducteur.ConducteurUpdateRequest;
import com.carp.carpooling.entity.Conducteur;
import com.carp.carpooling.exception.BusinessConflictException;
import com.carp.carpooling.exception.ForbiddenOperationException;
import com.carp.carpooling.exception.ResourceNotFoundException;
import com.carp.carpooling.mapper.ConducteurMapper;
import com.carp.carpooling.repository.ConducteurRepository;
import com.carp.carpooling.service.ConducteurService;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class ConducteurServiceImpl implements ConducteurService {

    private final ConducteurRepository repository;
    private final ConducteurMapper mapper;

    public ConducteurServiceImpl(
            ConducteurRepository repository,
            ConducteurMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ConducteurResponse creer(ConducteurRequest request) {

        String email = normaliserEmail(request.email());
        String permis = request.numeroPermis().trim();

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new BusinessConflictException(
                    "Cet email est deje utilise"
            );
        }

        if (repository.existsByNumeroPermis(permis)) {
            throw new BusinessConflictException(
                    "Ce numero de permis est deja utilise"
            );
        }

        Conducteur conducteur = new Conducteur(
                request.nom().trim(),
                request.prenom().trim(),
                email,
                request.telephone().trim(),
                permis
        );

        Conducteur saved = repository.saveAndFlush(conducteur);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConducteurResponse> lister() {

        return repository.findAll(Sort.by("id").ascending())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConducteurResponse trouverParId(Long id) {

        Conducteur conducteur = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conducteur introuvable : " + id
                        )
                );

        return mapper.toResponse(conducteur);
    }

    @Override
    @Transactional
    public ConducteurResponse modifier(
            Long id,
            Long driverId,
            ConducteurUpdateRequest request) {

        // R-36 : Seul le conducteur concerné peut modifier
        if (!Objects.equals(id, driverId)) {
            throw new ForbiddenOperationException(
                    "Vous ne pouvez modifier que votre propre profil"
            );
        }

        Conducteur conducteur = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conducteur introuvable : " + id
                        )
                );

        String email = normaliserEmail(request.email());
        String permis = request.numeroPermis().trim();

        // Exclure le conducteur courant des vérifications
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new BusinessConflictException(
                    "Cet email appartient deja à un autre conducteur"
            );
        }

        if (repository.existsByNumeroPermisAndIdNot(permis, id)) {
            throw new BusinessConflictException(
                    "Ce numéro de permis appartient déjà à un autre conducteur"
            );
        }

        conducteur.modifierProfil(
                request.nom().trim(),
                request.prenom().trim(),
                email,
                request.telephone().trim(),
                permis
        );

        // L'entité est déjà gérée par JPA.
        // Flush force la synchronisation SQL dans cette transaction.
        repository.flush();

        return mapper.toResponse(conducteur);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConducteurResponse> rechercher(
            String name,
            String email) {

        String nameFiltre = normaliserTexte(name);
        String emailFiltre = normaliserTexte(email);

        if (nameFiltre.isEmpty() && emailFiltre.isEmpty()) {
            throw new IllegalArgumentException(
                    "Au moins un critere de recherche est obligatoire"
            );
        }

        // Les caractères %, _ et ! doivent être échappés pour LIKE.
        String nameEchappe = echapperLike(nameFiltre);

        return repository.rechercher(nameEchappe, emailFiltre)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    private String normaliserEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normaliserTexte(String valeur) {
        return valeur == null
                ? ""
                : valeur.trim().toLowerCase(Locale.ROOT);
    }

    private String echapperLike(String valeur) {
        return valeur
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
    }
}
