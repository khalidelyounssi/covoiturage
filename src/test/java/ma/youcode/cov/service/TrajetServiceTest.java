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

class TrajetServiceTest {
    private final VehiculeRepository vehicules = mock(VehiculeRepository.class);
    private final TrajetRepository trajets = mock(TrajetRepository.class);
    private final TrajetMapper mapper = mock(TrajetMapper.class);
    private final Clock clock = Clock.fixed(Instant.parse("2027-02-01T12:00:00Z"), ZoneOffset.UTC);
    private final TrajetService service = new TrajetServiceImpl(vehicules, trajets, mapper, clock);
    private final Vehicule vehicule = new Vehicule();
    private final LocalDateTime depart = LocalDateTime.now(clock).plusDays(3);

    @BeforeEach
    void preparer() {
        var conducteur = new Conducteur();
        conducteur.setId(10L);
        vehicule.setConducteur(conducteur);
        vehicule.setCapacite(4);
        when(vehicules.verrouiller(1L)).thenReturn(Optional.of(vehicule));
    }

    @Test
    void publier_remplitLesArretsEtLeStatut() {
        service.publier(10L, demande(3));
        verify(trajets).saveAndFlush(argThat(t -> t.getStatut() == StatutTrajet.PUBLIE
                && t.getPoints().size() == 2 && t.getPoints().get(1).getOrdre() == 1));
    }

    @Test
    void publier_refuseCinqPlacesDansUnVehiculeDeQuatre() {
        assertThatThrownBy(() -> service.publier(10L, demande(5)))
                .isInstanceOfSatisfying(ApiError.class, e -> assertThat(e.status()).isEqualTo(HttpStatus.BAD_REQUEST));
        verifyNoInteractions(trajets);
    }

    @Test
    void publier_refuseLeVehiculeDUnAutreConducteur() {
        assertThatThrownBy(() -> service.publier(99L, demande(3)))
                .isInstanceOfSatisfying(ApiError.class, e -> assertThat(e.status()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void terminer_refuseAvantLeDepart() {
        var trajet = new Trajet();
        trajet.setConducteur(vehicule.getConducteur());
        trajet.setDateDepart(depart);
        when(trajets.verrouiller(1L)).thenReturn(Optional.of(trajet));
        assertThatThrownBy(() -> service.terminer(10L, 1L))
                .isInstanceOfSatisfying(ApiError.class, e -> assertThat(e.status()).isEqualTo(HttpStatus.CONFLICT));
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
    }

    @Test
    void terminer_accepteApresLeDepart() {
        var trajet = new Trajet();
        trajet.setConducteur(vehicule.getConducteur());
        trajet.setDateDepart(LocalDateTime.now(clock).minusSeconds(1));
        when(trajets.verrouiller(1L)).thenReturn(Optional.of(trajet));
        service.terminer(10L, 1L);
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.TERMINE);
    }

    @Test
    void annuler_changeLeStatutDuTrajetEtDeSesReservations() {
        var trajet = new Trajet();
        trajet.setConducteur(vehicule.getConducteur());
        trajet.getReservations().add(new Reservation());
        trajet.getReservations().add(new Reservation());
        when(trajets.verrouiller(1L)).thenReturn(Optional.of(trajet));

        service.annuler(10L, 1L);

        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.ANNULE);
        assertThat(trajet.getReservations()).extracting(Reservation::getStatut)
                .containsExactly(StatutReservation.ANNULEE, StatutReservation.ANNULEE);
    }

    private TrajetIn demande(int places) {
        return new TrajetIn(1L, places, List.of(new PointIn("Rabat", depart, BigDecimal.ZERO),
                new PointIn("Marrakech", depart.plusHours(3), new BigDecimal("120.00"))));
    }
}