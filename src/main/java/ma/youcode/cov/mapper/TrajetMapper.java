package ma.youcode.cov.mapper;

import ma.youcode.cov.dto.ApiDtos.*;
import ma.youcode.cov.entity.*;
import ma.youcode.cov.repository.*;
import ma.youcode.cov.mapper.*;
import ma.youcode.cov.web.ApiError;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TrajetMapper {
    PointOut versDto(PointArret source);

    @Mapping(target = "conducteurId", source = "conducteur.id")
    @Mapping(target = "vehiculeId", source = "vehicule.id")
    @Mapping(target = "placesRestantes", expression = "java(source.calculerPlacesRestantes())")
    TrajetOut versDto(Trajet source);

    @Mapping(target = "passagerId", source = "passager.id")
    @Mapping(target = "trajetId", source = "trajet.id")
    @Mapping(target = "pointMonteeId", source = "pointMontee.id")
    @Mapping(target = "pointDescenteId", source = "pointDescente.id")
    ReservationOut versDto(Reservation source);
}