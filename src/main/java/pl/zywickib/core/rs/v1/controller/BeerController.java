package pl.zywickib.core.rs.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.zywickib.core.domain.dao.BeerDao;
import pl.zywickib.core.domain.dao.BreweryDao;
import pl.zywickib.core.domain.model.Beer;
import pl.zywickib.core.domain.model.Brewery;
import pl.zywickib.core.rs.v1.dto.BeerResultDto;
import pl.zywickib.core.rs.v1.dto.BeerUpdateDto;
import pl.zywickib.core.rs.v1.mapper.BeerMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Path("/v1/beer")
@Tag(name = "beer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
public class BeerController {

    @Inject
    BeerDao beerDao;

    @Inject
    BreweryDao breweryDao;

    @Inject
    BeerMapper beerMapper;

    @GET
    @Path("{guid}")
    @Operation(operationId = "getBeerByGuid", description = "Get beer by guid")
    @APIResponses({@APIResponse(responseCode = "200",
                                description = "OK",
                                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                   schema = @Schema(implementation = BeerResultDto.class, type = SchemaType.OBJECT))),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response getBeerByGuid(@PathParam("guid") String guid) {
        Beer beer = beerDao.findById(guid);
        if (beer == null) {
            return Response.status(404).entity("{\"message\":\"Beer with guid: " + guid + " not found\"}").build();
        }
        log.info(beer.getName());
        return Response.ok(beerMapper.map(beer)).build();
    }

    @GET
    @Path("")
    @Operation(operationId = "getAllBeers", description = "Get beer by guid")
    @APIResponses({@APIResponse(responseCode = "200",
                                description = "OK",
                                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                   schema = @Schema(implementation = BeerResultDto[].class))),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    public Response getAllBeers() {
        List<BeerResultDto> beers = beerDao.findAll().map(beerMapper::map).collect(Collectors.toList());
        return Response.ok(beers).build();
    }

    @POST
    @Path("")
    @Operation(operationId = "addNewBeer", description = "Get beer by guid")
    @APIResponses({@APIResponse(responseCode = "200",
                                description = "OK",
                                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                   schema = @Schema(implementation = BeerResultDto.class))),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Response addBeer(@Parameter @Valid @NotNull BeerUpdateDto beerUpdateDto) {
        Brewery dbBrewery = breweryDao.findByName(beerUpdateDto.getBrewery().getName());
        if (dbBrewery == null) {
            return Response.status(404).entity("{\"message\":\"Brewery with name: " + beerUpdateDto.getBrewery().getName()
                                                   + " not found\"}").build();
        }
        beerUpdateDto.setBrewery(dbBrewery);
        Beer createdBeer = beerDao.create(beerMapper.map(beerUpdateDto));
        return Response.ok(beerMapper.map(createdBeer)).build();
    }

    @DELETE
    @Path("{guid}")
    @Operation(operationId = "getBeerByGuid", description = "Get beer by guid")
    @APIResponses({@APIResponse(responseCode = "204", description = "No Content"),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Response deleteBeer(@PathParam("guid") String guid) {
        Beer beer = beerDao.findById(guid);
        if (beer == null) {
            return Response.status(404).entity("{\"message\":\"Beer with guid: " + guid + " not found\"}").build();
        }
        beerDao.deleteQueryById(guid);
        return Response.noContent().build();
    }

    @PUT
    @Path("{guid}")
    @Operation(operationId = "updateBeerByGuid", description = "Get beer by guid")
    @APIResponses({@APIResponse(responseCode = "200",
                                description = "OK",
                                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                   schema = @Schema(implementation = BeerResultDto.class))),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Response updateBeer(@PathParam("guid") String guid, @Parameter @Valid @NotNull BeerUpdateDto beerUpdateDto) {
        Beer beer = beerDao.findById(guid);
        if (beer == null) {
            return Response.status(404).entity("{\"message\":\"Beer with guid: " + guid + " not found\"}").build();
        }
        Beer testBeer = updateBeerValues(beer, beerUpdateDto);
        Beer updatedBeer = beerDao.update(testBeer);
        return Response.ok(beerMapper.map(updatedBeer)).build();
    }

    private Beer updateBeerValues(Beer beer, BeerUpdateDto beerUpdateDto) {
        if (beerUpdateDto != null) {
            Brewery dbBrewery = breweryDao.findByName(beerUpdateDto.getBrewery().getName());
            if (dbBrewery == null) {
                throw new BadRequestException("Brewery with name: " + beerUpdateDto.getName() + "not found");
            }
            beer.setBrewery(dbBrewery);
            beer.setStyle(beerUpdateDto.getStyle());
            beer.setIbu(beerUpdateDto.getIbu());
            beer.setDescription(beerUpdateDto.getDescription());
            beer.setAbv(beerUpdateDto.getAbv());
            beer.setName(beerUpdateDto.getName());
        }
        return beer;
    }


}
