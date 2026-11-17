package ma.youcode.cov.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "reservation")
@Getter @Setter @NoArgsConstructor
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Passager passager;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Trajet trajet;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private PointArret pointMontee;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private PointArret pointDescente;
    @Column(nullable = false) private int nombrePlaces;
    @Column(nullable = false, precision = 13, scale = 2) private BigDecimal montantTotal;
    @Column(nullable = false) private LocalDateTime dateReservation;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private StatutReservation statut = StatutReservation.CONFIRMEE;

    public BigDecimal calculerMontantTotal() {
        return trajet.prixSegment(pointMontee, pointDescente)
                .multiply(BigDecimal.valueOf(nombrePlaces)).setScale(2, RoundingMode.UNNECESSARY);
    }

    public boolean peutEtreAnnulee(LocalDateTime maintenant) {
        return statut == StatutReservation.CONFIRMEE
                && !maintenant.isAfter(trajet.getDateDepart().minusHours(24));
    }

    public void annuler() {
        statut = StatutReservation.ANNULEE;
    }
}