package ma.youcode.cov.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "passager")
@Getter @Setter @NoArgsConstructor
public class Passager {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String nom;
    @Column(nullable = false) private String prenom;
    @Column(nullable = false) private String email;
    @Column(nullable = false) private String telephone;

    public boolean estTitulaire(Reservation reservation) {
        return id != null && id.equals(reservation.getPassager().getId());
    }
}