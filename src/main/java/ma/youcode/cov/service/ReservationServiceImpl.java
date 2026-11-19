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

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {
    private final TrajetRepository trajets;
    private final PassagerRepository passagers;
    private final ReservationRepository reservations;
    private final TrajetMapper mapper;
    private final Clock clock;

    public ConfirmationOut reserver(Long passagerId, ReservationIn in) {
        var passager = passagers.findById(passagerId)
                .orElseThrow(() -> ApiError.absent("Passager introuvable"));
        var trajet = verrouiller(in.trajetId());
        if (trajet.getStatut() != StatutTrajet.PUBLIE) {
            throw ApiError.conflit("La réservation est ouverte uniquement sur un trajet PUBLIE");
        }
        PointArret montee = point(trajet, in.pointMonteeId());
        PointArret descente = point(trajet, in.pointDescenteId());
        if (montee.getOrdre() >= descente.getOrdre()) {
            throw ApiError.invalide("Le point de montée doit précéder la descente");
        }
        boolean dejaReserve = trajet.getReservations().stream()
                .anyMatch(r -> passager.estTitulaire(r) && r.getStatut() == StatutReservation.CONFIRMEE);
        if (dejaReserve) throw ApiError.conflit("Le passager a déjà une réservation confirmée");
        if (in.nombrePlaces() > trajet.calculerPlacesRestantes()) {
            throw ApiError.conflit("Nombre de places disponibles insuffisant");
        }
        var reservation = new Reservation();
        reservation.setTrajet(trajet);
        reservation.setPassager(passager);
        reservation.setPointMontee(montee);
        reservation.setPointDescente(descente);
        reservation.setNombrePlaces(in.nombrePlaces());
        reservation.setDateReservation(LocalDateTime.now(clock));
        reservation.setMontantTotal(reservation.calculerMontantTotal());
        reservations.save(reservation);
        trajet.getReservations().add(reservation);
        trajet.actualiserDisponibilite();
        log.info("Réservation confirmée : {}", reservation.getId());
        return new ConfirmationOut(mapper.versDto(reservation),
                trajet.calculerPlacesRestantes(), trajet.getStatut());
    }

    @Transactional(readOnly = true)
    public List<ReservationOut> historique(Long passagerId) {
        if (!passagers.existsById(passagerId)) throw ApiError.absent("Passager introuvable");
        return reservations.findByPassagerIdOrderByIdAsc(passagerId).stream()
                .map(mapper::versDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationOut> pourTrajet(Long conducteurId, Long trajetId) {
        var trajet = trajets.trouverAvecReservations(trajetId)
                .orElseThrow(() -> ApiError.absent("Trajet introuvable"));
        if (!trajet.getConducteur().getId().equals(conducteurId)) {
            throw ApiError.interdit("Ce trajet appartient à un autre conducteur");
        }
        return trajet.getReservations().stream().sorted(Comparator.comparing(Reservation::getId))
                .map(mapper::versDto).toList();
    }

    public void annuler(Long passagerId, Long trajetId, Long reservationId) {
        var trajet = verrouiller(trajetId);
        var reservation = trajet.getReservations().stream()
                .filter(r -> r.getId().equals(reservationId)).findFirst()
                .orElseThrow(() -> ApiError.absent("Réservation introuvable pour ce trajet"));
        if (!reservation.getPassager().getId().equals(passagerId)) {
            throw ApiError.interdit("Cette réservation appartient à un autre passager");
        }
        if (trajet.getStatut() != StatutTrajet.PUBLIE && trajet.getStatut() != StatutTrajet.COMPLET) {
            throw ApiError.conflit("Le trajet est dans un état final");
        }
        if (!reservation.peutEtreAnnulee(LocalDateTime.now(clock))) {
            throw ApiError.conflit("Réservation non confirmée ou délai de 24 h dépassé");
        }
        reservation.annuler();
        trajet.actualiserDisponibilite();
        log.info("Réservation annulée : {}", reservationId);
    }

    private Trajet verrouiller(Long id) {
        return trajets.verrouiller(id).orElseThrow(() -> ApiError.absent("Trajet introuvable"));
    }

    private PointArret point(Trajet trajet, Long id) {
        return trajet.getPoints().stream().filter(p -> p.getId().equals(id)).findFirst()
                .orElseThrow(() -> ApiError.invalide("Point d’arrêt absent du trajet : " + id));
    }
}