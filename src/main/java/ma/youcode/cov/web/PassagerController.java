package ma.youcode.cov.web;

import ma.youcode.cov.dto.ApiDtos.*;
import ma.youcode.cov.entity.*;
import ma.youcode.cov.repository.*;
import ma.youcode.cov.mapper.*;
import ma.youcode.cov.web.ApiError;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import ma.youcode.cov.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Page;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/passagers")
public class PassagerController {
    private final ProfilService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PassagerOut creer(@Valid @RequestBody PassagerIn in) {
        return service.enregistrer(null, in);
    }

    @GetMapping
    public List<PassagerOut> lister(@RequestParam(required = false) String email) {
        return service.listerPassagers(email);
    }

    @GetMapping("/{id}")
    public PassagerOut lire(@PathVariable Long id) {
        return service.lirePassager(id);
    }

    @PutMapping("/{id}")
    public PassagerOut modifier(@PathVariable Long id, @RequestHeader("X-Passager-Id") Long acteur,
                                @Valid @RequestBody PassagerIn in) {
        Acteur.verifierProprietaire(acteur, id);
        return service.enregistrer(id, in);
    }
}