package ma.youcode.cov.service;

import ma.youcode.cov.dto.ApiDtos.*;
import ma.youcode.cov.entity.*;
import ma.youcode.cov.mapper.TrajetMapper;
import ma.youcode.cov.repository.*;
import ma.youcode.cov.web.ApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.time.*;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {
    private final TrajetRepository trajets = mock(TrajetRepository.class);
    private final ReservationRepository reservations = mock(ReservationRepository.class);
    private final PassagerRepository passagers = mock(PassagerRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2027-02-01T12:00:00Z"), ZoneOffset.UTC);
    private final ReservationService service = new ReservationServiceImpl(
            trajets, passagers, reservations, mock(TrajetMapper.class), clock);
    private final Trajet trajet = new Trajet();

    @BeforeEach
    void preparer() {
        trajet.setPlacesProposees(1);
        trajet.setDateDepart(LocalDateTime.now(clock).plusDays(3));
        trajet.ajouterPoint(point(10L, 0, "0.00"));
        trajet.ajouterPoint(point(11L, 1, "70.00"));
        trajet.ajouterPoint(point(12L, 2, "120.00"));
        var passager = new Passager();
        passager.setId(1L);
        when(passagers.findById(1L)).thenReturn(Optional.of(passager));
        when(trajets.verrouiller(1L)).thenReturn(Optional.of(trajet));
        when(reservations.save(any())).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(4L);
            return r;
        });
    }

    @Test
    void reserver_calcule50DhEtFermeLeTrajet() {
        var out = service.reserver(1L, new ReservationIn(1L, 11L, 12L, 1));
        assertThat(trajet.getReservations().get(0).getMontantTotal()).isEqualByComparingTo("50.00");
        assertThat(out.placesRestantes()).isZero();
        assertThat(out.statutTrajet()).isEqualTo(StatutTrajet.COMPLET);
    }

    @Test
    void reserver_refuseLeSensInverse() {
        assertThatThrownBy(() -> service.reserver(1L, new ReservationIn(1L, 12L, 11L, 1)))
                .isInstanceOfSatisfying(ApiError.class, e -> assertThat(e.status()).isEqualTo(HttpStatus.BAD_REQUEST));
        verify(reservations, never()).save(any());
    }

    @Test
    void annuler_rendLaPlaceDisponible() {
        service.reserver(1L, new ReservationIn(1L, 11L, 12L, 1));
        service.annuler(1L, 1L, 4L);
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
        assertThat(trajet.calculerPlacesRestantes()).isEqualTo(1);
    }

    @Test
    void annuler_refuseDeuxHeuresAvant() {
        trajet.setDateDepart(LocalDateTime.now(clock).plusHours(2));
        service.reserver(1L, new ReservationIn(1L, 11L, 12L, 1));
        assertThatThrownBy(() -> service.annuler(1L, 1L, 4L))
                .isInstanceOfSatisfying(ApiError.class, e -> assertThat(e.status()).isEqualTo(HttpStatus.CONFLICT));
    }

    private PointArret point(Long id, int ordre, String prix) {
        var p = new PointArret();
        p.setId(id);
        p.setOrdre(ordre);
        p.setPrixDepuisDepart(new BigDecimal(prix));
        return p;
    }
}