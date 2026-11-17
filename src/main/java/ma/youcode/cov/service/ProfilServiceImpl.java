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
public class ProfilServiceImpl implements ProfilService {
    private final ConducteurRepository conducteurs;
    private final PassagerRepository passagers;
    private final ProfilMapper mapper;

    public ConducteurOut enregistrer(Long id, ConducteurIn in) {
        Conducteur cible = id == null ? new Conducteur() : conducteur(id);
        mapper.remplir(in, cible);
        cible.setEmail(in.email().strip().toLowerCase(Locale.ROOT));
        cible.setNumeroPermis(in.numeroPermis().strip());
        return mapper.versDto(conducteurs.save(cible));
    }

    @Transactional(readOnly = true)
    public ConducteurOut lireConducteur(Long id) {
        return mapper.versDto(conducteur(id));
    }

    @Transactional(readOnly = true)
    public List<ConducteurOut> listerConducteurs(String recherche) {
        var resultat = recherche == null || recherche.isBlank()
                ? conducteurs.findAll()
                : conducteurs.findByNomContainingIgnoreCaseOrEmailIgnoreCase(recherche, recherche);
        return resultat.stream().map(mapper::versDto).toList();
    }

    public PassagerOut enregistrer(Long id, PassagerIn in) {
        Passager cible = id == null ? new Passager() : passager(id);
        mapper.remplir(in, cible);
        cible.setEmail(in.email().strip().toLowerCase(Locale.ROOT));
        return mapper.versDto(passagers.save(cible));
    }

    @Transactional(readOnly = true)
    public PassagerOut lirePassager(Long id) {
        return mapper.versDto(passager(id));
    }

    @Transactional(readOnly = true)
    public List<PassagerOut> listerPassagers(String email) {
        return (email == null || email.isBlank() ? passagers.findAll() : passagers.findByEmailIgnoreCase(email))
                .stream().map(mapper::versDto).toList();
    }

    private Conducteur conducteur(Long id) {
        return conducteurs.findById(id).orElseThrow(() -> ApiError.absent("Conducteur introuvable"));
    }

    private Passager passager(Long id) {
        return passagers.findById(id).orElseThrow(() -> ApiError.absent("Passager introuvable"));
    }
}