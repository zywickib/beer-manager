package pl.zywickib.core.domain.dao;

import lombok.extern.slf4j.Slf4j;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import pl.zywickib.core.domain.model.Beer;
import pl.zywickib.core.domain.model.Brewery;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class BeerDao extends AbstractDAO<Beer> {
    public Beer create(Beer beer, String breweryId) {
        Brewery brewery = getEntityManager().getReference(Brewery.class, breweryId);
        beer.setBrewery(brewery);
        return super.create(beer);
    }
}
