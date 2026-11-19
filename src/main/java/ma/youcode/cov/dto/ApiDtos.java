package ma.youcode.cov.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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
}