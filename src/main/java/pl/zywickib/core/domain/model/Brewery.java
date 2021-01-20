package pl.zywickib.core.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Brewery")
public class Brewery extends TraceableEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "logo")
    private String logo;

    @Column(name = "brewery")
    @OneToMany(mappedBy = "brewery", cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE,
        CascadeType.ALL
    }, orphanRemoval = true)
    private Set<Beer> beer;
}
