package pl.zywickib.core.rs.v1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeerResultDto {
    private String name;
    private String style;
    private Integer ibu;
    private Double abv;
    private String description;
    private BreweryDto brewery;
}
