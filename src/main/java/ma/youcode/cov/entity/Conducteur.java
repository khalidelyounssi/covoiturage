package ma.youcode.cov.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "conducteur")
@Getter @Setter @NoArgsConstructor
public class Conducteur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String nom;
    @Column(nullable = false) private String prenom;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String telephone;
    @Column(nullable = false, unique = true) private String numeroPermis;

    public boolean possede(Vehicule vehicule) {
        return id != null && id.equals(vehicule.getConducteur().getId());
    }
}