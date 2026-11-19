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

public interface ReservationService {
    ConfirmationOut reserver(Long passagerId, ReservationIn in);
    List<ReservationOut> historique(Long passagerId);
    List<ReservationOut> pourTrajet(Long conducteurId, Long trajetId);
    void annuler(Long passagerId, Long trajetId, Long reservationId);
}