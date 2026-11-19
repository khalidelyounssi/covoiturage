package ma.youcode.cov.service;

import ma.youcode.cov.dto.ApiDtos.RechercheOut;
import ma.youcode.cov.dto.ApiDtos.TrajetIn;
import ma.youcode.cov.dto.ApiDtos.TrajetOut;
import org.springframework.data.domain.Slice;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TrajetService {

    TrajetOut publier(Long conducteurId, TrajetIn in);

    TrajetOut lire(Long id);

    Slice<RechercheOut> rechercher(
            String montee,
            String descente,
            LocalDate date,
            int places,
            BigDecimal prixMax,
            int page,
            int taille
    );

    void terminer(Long conducteurId, Long id);

    void annuler(Long conducteurId, Long id);
}