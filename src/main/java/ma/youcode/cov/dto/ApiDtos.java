package ma.youcode.cov.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ma.youcode.cov.entity.StatutReservation;
import ma.youcode.cov.entity.StatutTrajet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}

    public record ConducteurIn(@NotBlank String nom, @NotBlank String prenom,
                               @NotBlank @Email String email, @NotBlank String telephone,
                               @NotBlank String numeroPermis) {}

    public record ConducteurOut(Long id, String nom, String prenom,
                                String email, String telephone, String numeroPermis) {}

    public record PassagerIn(@NotBlank String nom, @NotBlank String prenom,
                             @NotBlank @Email String email, @NotBlank String telephone) {}

    public record PassagerOut(Long id, String nom, String prenom,
                              String email, String telephone) {}

    public record VehiculeIn(@NotBlank String marque, @NotBlank String modele,
                             @NotBlank String immatriculation, @NotBlank String couleur,
                             @NotNull @Min(1) @Max(8) Integer capacite) {}

    public record VehiculeOut(Long id, String marque, String modele,
                              String immatriculation, String couleur, int capacite, Long conducteurId) {}
}