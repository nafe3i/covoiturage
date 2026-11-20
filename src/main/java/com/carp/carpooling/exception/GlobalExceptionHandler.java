
package com.carp.carpooling.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Ressource introuvable : {}", ex.getMessage());

        return buildProblem(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(
            ForbiddenOperationException ex,
            HttpServletRequest request) {

        log.warn("Action interdite : {}", ex.getMessage());

        return buildProblem(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(
            BusinessConflictException ex,
            HttpServletRequest request) {

        log.warn("Conflit metier : {}", ex.getMessage());

        return buildProblem(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Argument invalide : {}", ex.getMessage());

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField()
                        + " : " + error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Donnees de la requete invalides";
        }

        log.warn("Validation refusee : {}", message);

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ProblemDetail> handleMethodValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest request) {

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Parametre de requete invalide",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {

        log.error("Erreur interne sur {}", request.getRequestURI(), ex);

        return buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur interne est survenue",
                request
        );
    }

    private ResponseEntity<ProblemDetail> buildProblem(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(status, message);

        problem.setTitle(status.getReasonPhrase());
        problem.setType(
                URI.create("urn:carpooling:error:" + status.value())
        );

        problem.setProperty("timestamp", Instant.now(clock).toString());
        problem.setProperty("message", message);
        problem.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Corps de requête JSON invalide");

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Le corps JSON de la requete est invalide",
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleInvalidParameter(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("Parametre invalide : {}", ex.getName());

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Valeur invalide pour le parametre : " + ex.getName(),
                request
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ProblemDetail> handleMissingHeader(
            MissingRequestHeaderException ex,
            HttpServletRequest request) {

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "En-tete obligatoire manquant : " + ex.getHeaderName(),
                request
        );
    }
}
