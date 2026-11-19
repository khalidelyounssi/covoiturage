package ma.youcode.cov.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "trajet")
@Getter @Setter @NoArgsConstructor
public class Trajet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Conducteur conducteur;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Vehicule vehicule;
    @Column(nullable = false) private String villeDepart;
    @Column(nullable = false) private String villeArrivee;
    @Column(nullable = false) private LocalDateTime dateDepart;
    @Column(nullable = false) private int placesProposees;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private StatutTrajet statut = StatutTrajet.PUBLIE;
    @OneToMany(mappedBy = "trajet", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<PointArret> points = new ArrayList<>();
    @OneToMany(mappedBy = "trajet")
    private List<Reservation> reservations = new ArrayList<>();

    public void ajouterPoint(PointArret point) {
        point.setTrajet(this);
        points.add(point);
    }

    public int calculerPlacesRestantes() {
        return placesProposees - reservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE)
                .mapToInt(Reservation::getNombrePlaces).sum();
    }

    public BigDecimal prixSegment(PointArret montee, PointArret descente) {
        var trajetMontee = montee.getTrajet();
        boolean memeTrajet = trajetMontee == this
                || (id != null && trajetMontee != null && id.equals(trajetMontee.getId()));
        if (!memeTrajet || !montee.precede(descente)) {
            throw new IllegalArgumentException("Les arrêts doivent appartenir au trajet et être dans le bon ordre");
        }
        return descente.getPrixDepuisDepart().subtract(montee.getPrixDepuisDepart())
                .setScale(2, RoundingMode.UNNECESSARY);
    }

    public void actualiserDisponibilite() {
        if (statut == StatutTrajet.PUBLIE || statut == StatutTrajet.COMPLET) {
            statut = calculerPlacesRestantes() == 0 ? StatutTrajet.COMPLET : StatutTrajet.PUBLIE;
        }
    }

    public void terminer(LocalDateTime maintenant) {
        verifierModifiable();
        if (!maintenant.isAfter(dateDepart)) throw new IllegalStateException("Le trajet n’a pas encore démarré");
        statut = StatutTrajet.TERMINE;
    }

    public void annuler() {
        verifierModifiable();
        statut = StatutTrajet.ANNULE;
    }

    private void verifierModifiable() {
        if (statut == StatutTrajet.ANNULE || statut == StatutTrajet.TERMINE) {
            throw new IllegalStateException("Le trajet est dans un état final");
        }
    }
}