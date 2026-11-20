
package com.carp.carpooling.service.impl;

import com.carp.carpooling.dto.passager.*;
import com.carp.carpooling.entity.Passager;
import com.carp.carpooling.exception.ForbiddenOperationException;
import com.carp.carpooling.exception.ResourceNotFoundException;
import com.carp.carpooling.mapper.PassagerMapper;
import com.carp.carpooling.repository.PassagerRepository;
import com.carp.carpooling.repository.ReservationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassagerServiceImplTest {

    @Mock
    private PassagerRepository passagerRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PassagerMapper mapper;

    @InjectMocks
    private PassagerServiceImpl service;

    private PassagerRequest request() {
        return new PassagerRequest(
                "Alami",
                "Salma",
                "SALMA@example.com",
                "0612345678"
        );
    }

    @Test
    void shouldCreatePassenger() {

        when(passagerRepository.save(any(Passager.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(mapper.toResponse(any(Passager.class)))
                .thenReturn(new PassagerResponse(
                        1L, "Alami", "Salma",
                        "salma@example.com", "0612345678"
                ));

        PassagerResponse result = service.creer(request());

        assertEquals("salma@example.com", result.email());
        verify(passagerRepository).save(any(Passager.class));
    }

    @Test
    void shouldRejectModificationByAnotherPassenger() {

        assertThrows(
                ForbiddenOperationException.class,
                () -> service.modifier(1L, 2L, request())
        );

        verifyNoInteractions(
                passagerRepository,
                reservationRepository,
                mapper
        );
    }

    @Test
    void shouldModifyOwnProfile() {

        Passager passager = new Passager(
                "Alami", "Salma",
                "salma@example.com", "0612345678"
        );

        PassagerRequest update = new PassagerRequest(
                "Alami", "Salma",
                "salma@example.com", "0699999999"
        );

        when(passagerRepository.findById(1L))
                .thenReturn(Optional.of(passager));

        service.modifier(1L, 1L, update);

        assertEquals("0699999999", passager.getTelephone());

        verify(passagerRepository, never())
                .save(any(Passager.class));
    }

    @Test
    void shouldReturnNotFoundForUnknownPassenger() {

        when(passagerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.trouverParId(99L)
        );
    }

    @Test
    void shouldReturnEmptyReservationHistory() {

        when(passagerRepository.existsById(1L))
                .thenReturn(true);

        when(reservationRepository.findHistoriqueByPassagerId(1L))
                .thenReturn(List.of());

        assertTrue(service.historique(1L, 1L).isEmpty());
    }

    @Test
    void shouldRejectAccessToAnotherPassengersHistory() {

        assertThrows(
                ForbiddenOperationException.class,
                () -> service.historique(1L, 2L)
        );

        verifyNoInteractions(
                passagerRepository,
                reservationRepository,
                mapper
        );
    }

    @Test
    void shouldRejectSearchWithoutEmail() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.rechercherParEmail(" ")
        );

        verifyNoInteractions(passagerRepository);
    }
}
