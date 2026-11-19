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

    public record PointIn(@NotBlank String ville, @NotNull LocalDateTime heurePassage,
                          @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal prixDepuisDepart) {}

    public record PointOut(Long id, String ville, int ordre,
                           LocalDateTime heurePassage, BigDecimal prixDepuisDepart) {}

    public record TrajetIn(@NotNull @Positive Long vehiculeId,
                           @NotNull @Min(1) Integer placesProposees, @NotNull @Size(min = 2)
                           List<@NotNull @Valid PointIn> points) {}

    public record TrajetOut(Long id, Long conducteurId, Long vehiculeId, String villeDepart,
                            String villeArrivee, LocalDateTime dateDepart, int placesProposees, int placesRestantes,
                            StatutTrajet statut, List<PointOut> points) {}

    public record RechercheOut(Long id, String villeMontee, String villeDescente,
                               LocalDateTime dateDepart, LocalDateTime heureMontee, BigDecimal prixSegment,
                               int placesRestantes) {}

    public record ReservationIn(@NotNull @Positive Long trajetId,
                                @NotNull @Positive Long pointMonteeId, @NotNull @Positive Long pointDescenteId,
                                @NotNull @Min(1) Integer nombrePlaces) {}

    public record ReservationOut(Long id, Long passagerId, Long trajetId, Long pointMonteeId,
                                 Long pointDescenteId, int nombrePlaces, BigDecimal montantTotal,
                                 LocalDateTime dateReservation, StatutReservation statut) {}

    public record ConfirmationOut(ReservationOut reservation, int placesRestantes,
                                  StatutTrajet statutTrajet) {}
}