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
@RequestMapping("/api/vehicules")
public class VehiculeController {
    private final VehiculeService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public VehiculeOut ajouter(@RequestHeader("X-Conducteur-Id") Long acteur,
                               @Valid @RequestBody VehiculeIn in) {
        return service.ajouter(acteur, in);
    }

    @GetMapping
    public List<VehiculeOut> lister(@RequestHeader("X-Conducteur-Id") Long acteur) {
        return service.lister(acteur);
    }

    @GetMapping("/{id}")
    public VehiculeOut lire(@RequestHeader("X-Conducteur-Id") Long acteur, @PathVariable Long id) {
        return service.lire(acteur, id);
    }

    @PutMapping("/{id}")
    public VehiculeOut modifier(@RequestHeader("X-Conducteur-Id") Long acteur, @PathVariable Long id,
                                @Valid @RequestBody VehiculeIn in) {
        return service.modifier(acteur, id, in);
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@RequestHeader("X-Conducteur-Id") Long acteur, @PathVariable Long id) {
        service.supprimer(acteur, id);
    }
}