package pl.zywickib.core.rs.v1.mapper;

import org.mapstruct.Mapper;
import pl.zywickib.core.domain.model.Beer;
import pl.zywickib.core.rs.v1.dto.BeerResultDto;

@Mapper(componentModel = "cdi")
public interface BeerMapper {

    BeerResultDto map(Beer beer);
}
