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
@RequestMapping("/api/trajets")
public class TrajetController {
    private final TrajetService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public TrajetOut publier(@RequestHeader("X-Conducteur-Id") Long acteur,
                             @Valid @RequestBody TrajetIn in) {
        return service.publier(acteur, in);
    }

    @GetMapping("/{id}")
    public TrajetOut lire(@PathVariable Long id) {
        return service.lire(id);
    }

    @GetMapping
    public Map<String, Object> rechercher(@RequestParam @NotBlank String montee,
                                          @RequestParam @NotBlank String descente,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                          @RequestParam(defaultValue = "1") @Min(1) int places,
                                          @RequestParam(required = false) @PositiveOrZero BigDecimal prixMax,
                                          @RequestParam(defaultValue = "0") @Min(0) int page,
                                          @RequestParam(defaultValue = "10") @Min(1) @Max(100) int taille) {
        var resultat = service.rechercher(montee, descente, date, places, prixMax, page, taille);
        return Map.of("content", resultat.getContent(), "page", page, "taille", taille,
                "hasNext", resultat.hasNext());
    }

    @PatchMapping("/{id}/terminer") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void terminer(@RequestHeader("X-Conducteur-Id") Long acteur, @PathVariable Long id) {
        service.terminer(acteur, id);
    }

    @PatchMapping("/{id}/annuler") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void annuler(@RequestHeader("X-Conducteur-Id") Long acteur, @PathVariable Long id) {
        service.annuler(acteur, id);
    }
}