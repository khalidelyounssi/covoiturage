package ma.youcode.cov.repository;

import ma.youcode.cov.dto.ApiDtos.*;
import ma.youcode.cov.entity.*;
import ma.youcode.cov.repository.*;
import ma.youcode.cov.mapper.*;
import ma.youcode.cov.web.ApiError;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {
    boolean existsByVehiculeId(Long vehiculeId);
    boolean existsByVehiculeIdAndPlacesProposeesGreaterThan(Long vehiculeId, int capacite);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trajet t where t.id = :id")
    Optional<Trajet> verrouiller(@Param("id") Long id);

    @Query("""
        select distinct t from Trajet t
        join fetch t.conducteur join fetch t.vehicule
        left join fetch t.points
        where t.id = :id
        """)
    Optional<Trajet> trouverParcours(@Param("id") Long id);

    @Query("select distinct t from Trajet t left join fetch t.reservations where t.id = :id")
    Optional<Trajet> trouverAvecReservations(@Param("id") Long id);

    interface SegmentRow {
        Long getId();
        String getVilleMontee();
        String getVilleDescente();
        LocalDateTime getDateDepart();
        LocalDateTime getHeureMontee();
        BigDecimal getPrixSegment();
        Long getPlacesRestantes();
    }

    @Query("""
        select t.id as id, m.ville as villeMontee, d.ville as villeDescente,
               t.dateDepart as dateDepart, m.heurePassage as heureMontee,
               (d.prixDepuisDepart - m.prixDepuisDepart) as prixSegment,
               (t.placesProposees - coalesce(sum(r.nombrePlaces), 0)) as placesRestantes
          from Trajet t
          join t.points m
          join t.points d
          left join t.reservations r on r.statut = :confirmee
         where lower(m.ville) = lower(:montee) and lower(d.ville) = lower(:descente)
           and m.ordre < d.ordre and t.statut = :publie
           and t.dateDepart >= :debut and t.dateDepart < :fin
           and d.prixDepuisDepart - m.prixDepuisDepart <= coalesce(:prixMax, d.prixDepuisDepart - m.prixDepuisDepart)
         group by t.id, m.ville, d.ville, t.dateDepart, m.heurePassage,
                  d.prixDepuisDepart, m.prixDepuisDepart, t.placesProposees
        having t.placesProposees - coalesce(sum(r.nombrePlaces), 0) >= :places
         order by t.dateDepart asc, t.id asc
        """)
    Slice<SegmentRow> rechercher(
            @Param("montee") String montee, @Param("descente") String descente,
            @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin,
            @Param("places") int places, @Param("prixMax") BigDecimal prixMax,
            @Param("publie") StatutTrajet publie, @Param("confirmee") StatutReservation confirmee,
            Pageable pageable);
}