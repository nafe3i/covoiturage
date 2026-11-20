package com.carp.carpooling.service.impl;

import com.carp.carpooling.dto.conducteur.*;
import com.carp.carpooling.entity.Conducteur;
import com.carp.carpooling.exception.BusinessConflictException;
import com.carp.carpooling.exception.ForbiddenOperationException;
import com.carp.carpooling.exception.ResourceNotFoundException;
import com.carp.carpooling.mapper.ConducteurMapper;
import com.carp.carpooling.repository.ConducteurRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConducteurServiceImplTest {

    @Mock
    private ConducteurRepository repository;

    @Mock
    private ConducteurMapper mapper;

    @InjectMocks
    private ConducteurServiceImpl service;

    private ConducteurRequest creationValide() {
        return new ConducteurRequest(
                "Alami",
                "Hamid",
                "HAMID@example.com",
                "0612345678",
                "PERMIS123"
        );
    }

    private ConducteurUpdateRequest modificationValide() {
        return new ConducteurUpdateRequest(
                "Alami",
                "Hamid",
                "hamid@example.com",
                "0699999999",
                "PERMIS123"
        );
    }

    @Test
    void shouldCreateDriverSuccessfully() {

        ConducteurResponse expected = new ConducteurResponse(
                1L, "Alami", "Hamid",
                "hamid@example.com",
                "0612345678", "PERMIS123"
        );

        when(repository.saveAndFlush(any(Conducteur.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toResponse(any(Conducteur.class)))
                .thenReturn(expected);

        ConducteurResponse result =
                service.creer(creationValide());

        assertEquals(expected, result);

        ArgumentCaptor<Conducteur> captor =
                ArgumentCaptor.forClass(Conducteur.class);

        verify(repository).saveAndFlush(captor.capture());

        assertEquals(
                "hamid@example.com",
                captor.getValue().getEmail()
        );
    }

    @Test
    void shouldRejectDuplicateEmail() {

        when(repository.existsByEmailIgnoreCase(
                "hamid@example.com"
        )).thenReturn(true);

        assertThrows(
                BusinessConflictException.class,
                () -> service.creer(creationValide())
        );

        verify(repository, never())
                .saveAndFlush(any(Conducteur.class));
    }

    @Test
    void shouldRejectDuplicateDrivingLicense() {

        when(repository.existsByNumeroPermis("PERMIS123"))
                .thenReturn(true);

        assertThrows(
                BusinessConflictException.class,
                () -> service.creer(creationValide())
        );

        verify(repository, never())
                .saveAndFlush(any(Conducteur.class));
    }

    @Test
    void shouldRejectModificationByAnotherDriver() {

        assertThrows(
                ForbiddenOperationException.class,
                () -> service.modifier(
                        1L,
                        2L,
                        modificationValide()
                )
        );

        verifyNoInteractions(repository, mapper);
    }

    @Test
    void shouldAllowDriverToModifyOwnProfile() {

        Conducteur conducteur = new Conducteur(
                "Alami", "Hamid",
                "hamid@example.com",
                "0612345678",
                "PERMIS123"
        );

        ConducteurResponse expected = new ConducteurResponse(
                1L, "Alami", "Hamid",
                "hamid@example.com",
                "0699999999", "PERMIS123"
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(conducteur));

        when(mapper.toResponse(conducteur))
                .thenReturn(expected);

        ConducteurResponse result = service.modifier(
                1L, 1L, modificationValide()
        );

        assertEquals(expected, result);
        assertEquals(
                "0699999999",
                conducteur.getTelephone()
        );

        verify(repository).flush();
    }

    @Test
    void shouldReturnNotFoundForUnknownDriver() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.trouverParId(99L)
        );
    }

    @Test
    void shouldRejectSearchWithoutCriteria() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.rechercher(null, "  ")
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldReturnEmptyListWhenNoDriversExist() {

        when(repository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of());

        assertTrue(service.lister().isEmpty());
    }
}
