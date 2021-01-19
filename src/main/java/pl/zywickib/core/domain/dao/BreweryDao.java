package pl.zywickib.core.domain.dao;

import lombok.extern.slf4j.Slf4j;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import pl.zywickib.core.domain.model.Brewery;
import pl.zywickib.core.domain.model.Brewery_;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class BreweryDao extends AbstractDAO<Brewery> {

    public Brewery findByName(String name) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Brewery> cq = cb.createQuery(Brewery.class);
            Root<Brewery> root = cq.from(Brewery.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(Brewery_.NAME), name));
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            List<Brewery> breweries = getEntityManager().createQuery(cq).getResultStream().collect(Collectors.toList());
            if (breweries.size() == 0) {
                return null;
            } else {
                return breweries.get(0);
            }
        } catch (Exception e) {
            throw new DAOException(ErrorKeys.FIND_BREWERY_ERROR_MESSAGE_BY_NAME, e);
        }
    }

    public enum ErrorKeys {
        FIND_BREWERY_ERROR_MESSAGE_BY_NAME
    }
}
