package ma.youcode.cov.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "point_arret")
@Getter @Setter @NoArgsConstructor
public class PointArret {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Trajet trajet;
    @Column(nullable = false) private String ville;
    @Column(nullable = false) private int ordre;
    @Column(nullable = false) private LocalDateTime heurePassage;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal prixDepuisDepart;

    public boolean precede(PointArret autre) {
        if (trajet == null || autre.trajet == null) return false;
        boolean memeTrajet = trajet == autre.trajet
                || (trajet.getId() != null && trajet.getId().equals(autre.trajet.getId()));
        return memeTrajet && ordre < autre.ordre;
    }
}