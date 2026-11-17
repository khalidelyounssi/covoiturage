package ma.youcode.cov.mapper;

import ma.youcode.cov.dto.ApiDtos.ConducteurIn;
import ma.youcode.cov.dto.ApiDtos.ConducteurOut;
import ma.youcode.cov.dto.ApiDtos.PassagerIn;
import ma.youcode.cov.dto.ApiDtos.PassagerOut;
import ma.youcode.cov.entity.Conducteur;
import ma.youcode.cov.entity.Passager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfilMapper {
    ConducteurOut versDto(Conducteur source);
    PassagerOut versDto(Passager source);

    @Mapping(target = "id", ignore = true)
    void remplir(ConducteurIn source, @MappingTarget Conducteur cible);

    @Mapping(target = "id", ignore = true)
    void remplir(PassagerIn source, @MappingTarget Passager cible);
}