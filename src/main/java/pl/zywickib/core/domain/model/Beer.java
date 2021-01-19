package pl.zywickib.core.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Beer")
public class Beer extends TraceableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "style")
    private String style;

    @Column(name = "ibu")
    private Integer ibu;

    @Column(name = "abv")
    private Double abv;

    @Column(name = "description", length = 10000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Brewery brewery;
}
