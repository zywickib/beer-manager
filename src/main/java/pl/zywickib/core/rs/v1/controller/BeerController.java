package pl.zywickib.core.rs.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.zywickib.core.domain.dao.BeerDao;
import pl.zywickib.core.domain.model.Beer;
import pl.zywickib.core.rs.v1.dto.BeerResultDto;
import pl.zywickib.core.rs.v1.mapper.BeerMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Path("/v1")
@Tag(name = "beer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
public class BeerController {

    @Inject
    BeerDao beerDao;

    @Inject
    BeerMapper beerMapper;

    @GET
    @Path("beer/{guid}")
    @Operation(operationId = "getBeerByGuid", description = "Get beer by guid")
    @APIResponses({@APIResponse(responseCode = "200",
                                description = "OK",
                                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                   schema = @Schema(implementation = Beer.class, type = SchemaType.OBJECT))),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response getBeerByGuid(@PathParam("guid") String guid) {
        Beer beer = beerDao.findById(guid);
        log.info(beer.getName());
        return Response.ok(beer).build();
    }

    @GET
    @Path("beer")
    @Operation(operationId = "getBeerByGuid", description = "Get beer by guid")
    @APIResponses({@APIResponse(responseCode = "200",
                                description = "OK",
                                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                   schema = @Schema(implementation = Beer[].class))),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    public Response getAllBeers() {
        List<BeerResultDto> beers = beerDao.findAll().map(beerMapper::map).collect(Collectors.toList());
        return Response.ok(beers).build();
    }
}
