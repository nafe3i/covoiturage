
package com.carp.carpooling.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-11-19T12:00:00Z"),
                ZoneOffset.UTC
        );

        handler = new GlobalExceptionHandler(clock);

        request = mock(HttpServletRequest.class);
        when(request.getRequestURI())
                .thenReturn("/api/reservations");
    }

    @Test
    void shouldReturn404WhenResourceNotFound() {
        var response = handler.handleNotFound(
                new ResourceNotFoundException("Trajet introuvable"),
                request
        );

        verifyProblem(response, 404, "Trajet introuvable");
    }

    @Test
    void shouldReturn403WhenOperationForbidden() {
        var response = handler.handleForbidden(
                new ForbiddenOperationException("Accès interdit"),
                request
        );

        verifyProblem(response, 403, "Accès interdit");
    }

    @Test
    void shouldReturn409WhenBusinessConflict() {
        var response = handler.handleConflict(
                new BusinessConflictException("Trajet complet"),
                request
        );

        verifyProblem(response, 409, "Trajet complet");
    }

    @Test
    void shouldReturn400WhenArgumentInvalid() {
        var response = handler.handleBadRequest(
                new IllegalArgumentException("Ordre invalide"),
                request
        );

        verifyProblem(response, 400, "Ordre invalide");
    }

    @Test
    void shouldHideInternalErrorDetails() {
        var response = handler.handleUnexpected(
                new RuntimeException("Erreur SQL sensible"),
                request
        );

        verifyProblem(
                response,
                500,
                "Une erreur interne est survenue"
        );
    }

    private void verifyProblem(
            ResponseEntity<ProblemDetail> response,
            int expectedStatus,
            String expectedMessage) {

        assertThat(response.getStatusCode().value())
                .isEqualTo(expectedStatus);

        ProblemDetail body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(expectedStatus);
        assertThat(body.getDetail()).isEqualTo(expectedMessage);

        assertThat(body.getProperties())
                .containsEntry("message", expectedMessage)
                .containsEntry("path", "/api/reservations")
                .containsEntry(
                        "timestamp",
                        "2026-11-19T12:00:00Z"
                );
    }
}
