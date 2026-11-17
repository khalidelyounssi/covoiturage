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
@RequestMapping("/api/conducteurs")
public class ConducteurController {
    private final ProfilService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ConducteurOut creer(@Valid @RequestBody ConducteurIn in) {
        return service.enregistrer(null, in);
    }

    @GetMapping
    public List<ConducteurOut> lister(@RequestParam(required = false) String recherche) {
        return service.listerConducteurs(recherche);
    }

    @GetMapping("/{id}")
    public ConducteurOut lire(@PathVariable Long id) {
        return service.lireConducteur(id);
    }

    @PutMapping("/{id}")
    public ConducteurOut modifier(@PathVariable Long id, @RequestHeader("X-Conducteur-Id") Long acteur,
                                  @Valid @RequestBody ConducteurIn in) {
        Acteur.verifierProprietaire(acteur, id);
        return service.enregistrer(id, in);
    }
}