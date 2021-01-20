package pl.zywickib.core.rs.v1.mapper;

import org.mapstruct.Mapper;
import pl.zywickib.core.domain.model.Brewery;
import pl.zywickib.core.rs.v1.dto.BreweryDto;
import pl.zywickib.core.rs.v1.dto.BreweryResultDto;

@Mapper(componentModel = "cdi")
public interface BreweryMapper {

    Brewery map(BreweryDto breweryDto);
    BreweryResultDto map(Brewery brewery);
}
