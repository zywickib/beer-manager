package pl.zywickib.core.rs.v1.dto;

import lombok.Getter;
import lombok.Setter;
import pl.zywickib.core.domain.model.Brewery;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BeerUpdateDto {

    @NotBlank
    private String name;

    private String style;

    private Integer ibu;

    private Double abv;

    private String description;

    private Brewery brewery;
}
