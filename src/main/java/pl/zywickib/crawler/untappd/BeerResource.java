package pl.zywickib.crawler.untappd;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import pl.zywickib.core.domain.dao.BeerDao;
import pl.zywickib.core.domain.dao.BreweryDao;
import pl.zywickib.core.domain.model.Beer;
import pl.zywickib.core.domain.model.Brewery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/untappd")
@Slf4j
@ApplicationScoped
public class BeerResource {

    @RestClient
    BeerService beerService;

    @Inject
    BeerDao beerDao;

    @Inject
    BreweryDao breweryDao;

    @GET
    @Path("crawl")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Response crawlBeers(
        @QueryParam("username") String username,
        @QueryParam("clientId") String clientId,
        @QueryParam("clientSecret") String clientSecret
    ) {
        UntappdResponse firstResponses = beerService.getUserBeers(username,
                                                             clientId,
                                                             clientSecret,
                                                                   "50",
                                                                   "0");
        firstResponses.getResponse().getBeers().getItems().forEach(this::persistApiData);
        log.info(String.valueOf(firstResponses.getResponse().getTotal_count()));
        for (int i = 1; i <= firstResponses.getResponse().getTotal_count() / 50; i++) {
            int offset = 50 * i;
            log.info("offset: " + offset);
            UntappdResponse responses = beerService.getUserBeers(username,
                                                                 clientId,
                                                                 clientSecret,
                                                                 "50", String.valueOf(offset));
            responses.getResponse().getBeers().getItems().forEach(this::persistApiData);
        }
        return Response.ok("{\"message\":\"success\"}").build();
    }

    private void persistApiData(UntappdResponse.Response.Beers.Item item) {
        Brewery brewery = addBreweryIsPossible(item.getBrewery());
        breweryDao.create(brewery);
        Beer beer = mapBeer(item.getBeer(), item.getBrewery().getBrewery_name());
        beerDao.create(beer);
    }

    private Brewery addBreweryIsPossible(UntappdResponse.Response.Beers.Item.Brewery apiBrewery) {
        Brewery dbBrewery = breweryDao.findByName(apiBrewery.getBrewery_name());
        if (dbBrewery == null) {
            return mapBrewery(apiBrewery);
        }
        return dbBrewery;
    }

    private Beer mapBeer(UntappdResponse.Response.Beers.Item.Beer apiBeer, String breweryName) {
        Beer beer = new Beer();
        beer.setAbv(apiBeer.getBeer_abv());
        beer.setDescription(apiBeer.getBeer_description());
        beer.setName(apiBeer.getBeer_name());
        beer.setStyle(apiBeer.getBeer_style());
        if (apiBeer.getBeer_ibu() == 0) {
            beer.setIbu(null);
        } else {
            beer.setIbu(apiBeer.getBeer_ibu());
        }

        beer.setBrewery(breweryDao.findByName(breweryName));
        return beer;
    }

    private Brewery mapBrewery(UntappdResponse.Response.Beers.Item.Brewery apiBrewery) {
        Brewery brewery = new Brewery();
        brewery.setLogo(apiBrewery.getBrewery_label());
        brewery.setName(apiBrewery.getBrewery_name());
        return brewery;
    }
}
