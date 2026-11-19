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
public class VehiculeServiceImpl implements VehiculeService {
    private final VehiculeRepository vehicules;
    private final ConducteurRepository conducteurs;
    private final TrajetRepository trajets;
    private final ProfilMapper mapper;

    public VehiculeOut ajouter(Long conducteurId, VehiculeIn in) {
        var conducteur = conducteurs.findById(conducteurId)
                .orElseThrow(() -> ApiError.absent("Conducteur introuvable"));
        var vehicule = new Vehicule();
        vehicule.setConducteur(conducteur);
        mapper.remplir(in, vehicule);
        return mapper.versDto(vehicules.save(vehicule));
    }

    @Transactional(readOnly = true)
    public List<VehiculeOut> lister(Long conducteurId) {
        return vehicules.findByConducteurIdOrderById(conducteurId).stream().map(mapper::versDto).toList();
    }

    @Transactional(readOnly = true)
    public VehiculeOut lire(Long conducteurId, Long id) {
        return mapper.versDto(proprietaire(conducteurId, trouver(id, false)));
    }

    public VehiculeOut modifier(Long conducteurId, Long id, VehiculeIn in) {
        var vehicule = proprietaire(conducteurId, trouver(id, true));
        if (trajets.existsByVehiculeIdAndPlacesProposeesGreaterThan(id, in.capacite())) {
            throw ApiError.conflit("Capacité inférieure aux places proposées sur un trajet");
        }
        mapper.remplir(in, vehicule);
        return mapper.versDto(vehicule);
    }

    public void supprimer(Long conducteurId, Long id) {
        var vehicule = proprietaire(conducteurId, trouver(id, true));
        if (trajets.existsByVehiculeId(id)) throw ApiError.conflit("Véhicule associé à un trajet");
        vehicules.delete(vehicule);
    }

    private Vehicule trouver(Long id, boolean verrou) {
        return (verrou ? vehicules.verrouiller(id) : vehicules.findById(id))
                .orElseThrow(() -> ApiError.absent("Véhicule introuvable"));
    }

    private Vehicule proprietaire(Long conducteurId, Vehicule vehicule) {
        if (!vehicule.getConducteur().getId().equals(conducteurId)) {
            throw ApiError.interdit("Ce véhicule appartient à un autre conducteur");
        }
        return vehicule;
    }
}