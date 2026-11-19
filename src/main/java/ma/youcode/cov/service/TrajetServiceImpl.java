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
public class TrajetServiceImpl implements TrajetService {
    private final VehiculeRepository vehicules;
    private final TrajetRepository trajets;
    private final TrajetMapper mapper;
    private final Clock clock;

    public TrajetOut publier(Long conducteurId, TrajetIn in) {
        var vehicule = vehicules.verrouiller(in.vehiculeId())
                .orElseThrow(() -> ApiError.absent("Véhicule introuvable"));
        if (!vehicule.getConducteur().getId().equals(conducteurId)) {
            throw ApiError.interdit("Le véhicule appartient à un autre conducteur");
        }
        if (!vehicule.peutAccueillir(in.placesProposees())) {
            throw ApiError.invalide("Capacité maximale du véhicule : " + vehicule.getCapacite());
        }
        var points = in.points();
        var depart = points.get(0);
        if (!depart.heurePassage().isAfter(LocalDateTime.now(clock))) {
            throw ApiError.invalide("Le départ doit être dans le futur");
        }
        if (depart.prixDepuisDepart().signum() != 0) {
            throw ApiError.invalide("Le premier prix doit être égal à zéro");
        }

        var trajet = new Trajet();
        trajet.setVehicule(vehicule);
        trajet.setConducteur(vehicule.getConducteur());
        trajet.setPlacesProposees(in.placesProposees());
        trajet.setVilleDepart(depart.ville().strip());
        trajet.setVilleArrivee(points.get(points.size() - 1).ville().strip());
        trajet.setDateDepart(depart.heurePassage());
        Set<String> villes = new HashSet<>();
        PointIn precedent = null;
        int ordre = 0;
        for (PointIn etape : points) {
            String ville = etape.ville().strip();
            if (!villes.add(ville.toLowerCase(Locale.ROOT))) {
                throw ApiError.invalide("Ville déjà présente : " + ville);
            }
            if (precedent != null && (!etape.heurePassage().isAfter(precedent.heurePassage())
                    || etape.prixDepuisDepart().compareTo(precedent.prixDepuisDepart()) <= 0)) {
                throw ApiError.invalide("Les heures et les prix doivent augmenter strictement");
            }
            var point = new PointArret();
            point.setVille(ville);
            point.setOrdre(ordre++);
            point.setHeurePassage(etape.heurePassage());
            point.setPrixDepuisDepart(etape.prixDepuisDepart().setScale(2));
            trajet.ajouterPoint(point);
            precedent = etape;
        }
        trajets.saveAndFlush(trajet);
        log.info("Trajet publié : {}", trajet.getId());
        return mapper.versDto(trajet);
    }

    @Transactional(readOnly = true)
    public TrajetOut lire(Long id) {
        var trajet = trajets.trouverParcours(id)
                .orElseThrow(() -> ApiError.absent("Trajet introuvable"));
        // Deux requêtes fixes évitent à la fois N+1 et le produit points × réservations.
        trajets.trouverAvecReservations(id);
        return mapper.versDto(trajet);
    }

    @Transactional(readOnly = true)
    public Slice<RechercheOut> rechercher(String montee, String descente, LocalDate date,
                                          int places, BigDecimal prixMax, int page, int taille) {
        return trajets.rechercher(montee.strip(), descente.strip(), date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay(), places, prixMax, StatutTrajet.PUBLIE,
                        StatutReservation.CONFIRMEE, PageRequest.of(page, taille))
                .map(row -> new RechercheOut(row.getId(), row.getVilleMontee(), row.getVilleDescente(),
                        row.getDateDepart(), row.getHeureMontee(), row.getPrixSegment().setScale(2),
                        row.getPlacesRestantes().intValue()));
    }

    public void terminer(Long conducteurId, Long id) {
        var trajet = verrouillerPour(conducteurId, id);
        try {
            trajet.terminer(LocalDateTime.now(clock));
        } catch (IllegalStateException e) {
            throw ApiError.conflit(e.getMessage());
        }
        log.info("Trajet terminé : {}", id);
    }

    public void annuler(Long conducteurId, Long id) {
        var trajet = verrouillerPour(conducteurId, id);
        try {
            trajet.annuler();
        } catch (IllegalStateException e) {
            throw ApiError.conflit(e.getMessage());
        }
        trajet.getReservations().stream().filter(r -> r.getStatut() == StatutReservation.CONFIRMEE)
                .forEach(Reservation::annuler);
        log.info("Trajet et réservations annulés : {}", id);
    }

    private Trajet verrouillerPour(Long conducteurId, Long id) {
        var trajet = trajets.verrouiller(id).orElseThrow(() -> ApiError.absent("Trajet introuvable"));
        if (!trajet.getConducteur().getId().equals(conducteurId)) {
            throw ApiError.interdit("Ce trajet appartient à un autre conducteur");
        }
        return trajet;
    }
}