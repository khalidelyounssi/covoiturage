package ma.youcode.cov.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(ApiError.class)
    ProblemDetail metier(ApiError e, HttpServletRequest request) {
        return probleme(e.status(), e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail champs(MethodArgumentNotValidException e, HttpServletRequest request) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage()).collect(Collectors.joining(", "));
        return probleme(HttpStatus.BAD_REQUEST, detail, request);
    }

    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class,
            ServletRequestBindingException.class, MethodArgumentTypeMismatchException.class})
    ProblemDetail requete(Exception e, HttpServletRequest request) {
        String detail = e instanceof ConstraintViolationException ? e.getMessage()
                : "Vérifie le JSON, les paramètres et les en-têtes requis.";
        return probleme(HttpStatus.BAD_REQUEST, detail, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail contrainte(HttpServletRequest request) {
        return probleme(HttpStatus.CONFLICT, "Unicité ou intégrité de la ressource violée", request);
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail inattendue(Exception e, HttpServletRequest request) {
        if (e instanceof ErrorResponse erreur) {
            return probleme(HttpStatus.valueOf(erreur.getStatusCode().value()),
                    erreur.getBody().getDetail(), request);
        }
        log.error("Erreur sur {}", request.getRequestURI(), e);
        return probleme(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne", request);
    }

    private ProblemDetail probleme(HttpStatus statut, String detail, HttpServletRequest request) {
        if (statut.is4xxClientError()) {
            log.warn("{} {} — {}", statut.value(), request.getRequestURI(), detail);
        }
        var corps = ProblemDetail.forStatusAndDetail(statut, detail);
        corps.setType(URI.create("urn:cov:http:" + statut.value()));
        corps.setInstance(URI.create(request.getRequestURI()));
        corps.setProperty("timestamp", Instant.now());
        return corps;
    }
}