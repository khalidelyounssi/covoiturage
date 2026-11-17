package ma.youcode.cov.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "vehicule")
@Getter @Setter @NoArgsConstructor
public class Vehicule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String marque;
    @Column(nullable = false) private String modele;
    @Column(nullable = false, unique = true) private String immatriculation;
    @Column(nullable = false) private String couleur;
    @Column(nullable = false) private int capacite;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Conducteur conducteur;

    public boolean peutAccueillir(int nombrePlaces) {
        return nombrePlaces >= 1 && nombrePlaces <= capacite;
    }
}