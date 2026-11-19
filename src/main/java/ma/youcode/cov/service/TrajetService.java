package ma.youcode.cov.service;

import ma.youcode.cov.dto.ApiDtos.*;
import ma.youcode.cov.entity.*;
import ma.youcode.cov.repository.*;
import ma.youcode.cov.mapper.*;
import ma.youcode.cov.web.ApiError;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

public interface TrajetService {
    TrajetOut publier(Long conducteurId, TrajetIn in);
    TrajetOut lire(Long id);
    Slice<RechercheOut> rechercher(String montee, String descente, LocalDate date,
                                   int places, BigDecimal prixMax, int page, int taille);
    void terminer(Long conducteurId, Long id);
    void annuler(Long conducteurId, Long id);
}