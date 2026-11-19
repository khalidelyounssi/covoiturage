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
@RequestMapping("/api")
public class ReservationController {
    private final ReservationService service;

    @PostMapping("/reservations") @ResponseStatus(HttpStatus.CREATED)
    public ConfirmationOut reserver(@RequestHeader("X-Passager-Id") Long acteur,
                                    @Valid @RequestBody ReservationIn in) {
        return service.reserver(acteur, in);
    }

    @GetMapping("/reservations")
    public List<ReservationOut> historique(@RequestHeader("X-Passager-Id") Long acteur) {
        return service.historique(acteur);
    }

    @GetMapping("/trajets/{trajetId}/reservations")
    public List<ReservationOut> pourTrajet(@RequestHeader("X-Conducteur-Id") Long acteur,
                                           @PathVariable Long trajetId) {
        return service.pourTrajet(acteur, trajetId);
    }

    @DeleteMapping("/trajets/{trajetId}/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void annuler(@RequestHeader("X-Passager-Id") Long acteur,
                        @PathVariable Long trajetId, @PathVariable Long id) {
        service.annuler(acteur, trajetId, id);
    }
}