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
import pl.zywickib.core.domain.dao.BreweryDao;
import pl.zywickib.core.domain.model.Beer;
import pl.zywickib.core.domain.model.Brewery;
import pl.zywickib.core.rs.v1.dto.BeerResultDto;
import pl.zywickib.core.rs.v1.dto.BeerUpdateDto;
import pl.zywickib.core.rs.v1.dto.BreweryDto;
import pl.zywickib.core.rs.v1.dto.BreweryResultDto;
import pl.zywickib.core.rs.v1.mapper.BeerMapper;
import pl.zywickib.core.rs.v1.mapper.BreweryMapper;

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
@Path("/v1/brewery")
@Tag(name = "brewery")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
public class BreweryController {


    @Inject
    BreweryDao breweryDao;

    @Inject
    BreweryMapper breweryMapper;

    @GET
    @Path("{guid}")
    @Operation(operationId = "getBreweryByGuid", description = "Get brewery by guid")
    @APIResponses({ @APIResponse(responseCode = "200",
                                 description = "OK",
                                 content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                    schema = @Schema(implementation = BreweryResultDto.class, type = SchemaType.OBJECT))),
                    @APIResponse(responseCode = "400", description = "Bad request"),
                    @APIResponse(responseCode = "404", description = "Not Found"),
                    @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    public Response getBreweryByGuid(@PathParam("guid") String guid) {
        Brewery brewery = breweryDao.findById(guid);
        if (brewery == null) {
            return Response.status(404).entity("{\"message\":\"Brewery with guid: " + guid + " not found\"}").build();
        }
        return Response.ok(breweryMapper.map(brewery)).build();
    }

    @GET
    @Path("")
    @Operation(operationId = "getAllBreweries", description = "Get all breweries")
    @APIResponses({@APIResponse(responseCode = "200",
                                description = "OK",
                                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                   schema = @Schema(implementation = BreweryResultDto[].class))),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    public Response getAllBreweries() {
        List<BreweryResultDto> breweries = breweryDao.findAll().map(breweryMapper::map).collect(Collectors.toList());
        return Response.ok(breweries).build();
    }

    @POST
    @Path("")
    @Operation(operationId = "addNewBrewery", description = "Add new brewery")
    @APIResponses({@APIResponse(responseCode = "200",
                                description = "OK",
                                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                   schema = @Schema(implementation = BreweryResultDto.class))),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Response addBrewery(@Parameter @Valid @NotNull BreweryDto breweryDto) {
        Brewery dbBrewery = breweryDao.findByName(breweryDto.getName());
        if (dbBrewery != null) {
            return Response.status(400).entity("{\"message\":\"Brewery with name: " + breweryDto.getName()
                                                   + " already exists\"}").build();
        }
        Brewery createdBrewery = breweryDao.create(breweryMapper.map(breweryDto));
        return Response.ok(breweryMapper.map(createdBrewery)).build();
    }

    @DELETE
    @Path("{guid}")
    @Operation(operationId = "deleteBrewery", description = "Get brewery by guid")
    @APIResponses({@APIResponse(responseCode = "204", description = "No Content"),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Response deleteBrewery(@PathParam("guid") String guid) {
        Brewery brewery = breweryDao.findById(guid);
        if (brewery == null) {
            return Response.status(404).entity("{\"message\":\"Brewery with guid: " + guid + " not found\"}").build();
        }
        breweryDao.deleteQueryById(guid);
        return Response.noContent().build();
    }

    @PUT
    @Path("{guid}")
    @Operation(operationId = "updateBreweryByGuid", description = "update brewery")
    @APIResponses({@APIResponse(responseCode = "200",
                                description = "OK",
                                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                   schema = @Schema(implementation = BreweryResultDto.class))),
                   @APIResponse(responseCode = "400", description = "Bad request"),
                   @APIResponse(responseCode = "404", description = "Not Found"),
                   @APIResponse(responseCode = "500", description = "Internal Server Error")
                  })
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Response updateBrewery(@PathParam("guid") String guid, @Parameter @Valid @NotNull BreweryDto breweryDto) {
        Brewery dbBreweryByName = breweryDao.findByName(breweryDto.getName());
        if (dbBreweryByName != null) {
            return Response.status(400).entity("{\"message\":\"Brewery with name: " + breweryDto.getName()
                                                   + " already exists\"}").build();
        }
        Brewery dbBrewery = breweryDao.findById(guid);
        if (dbBrewery == null) {
            return Response.status(404).entity("{\"message\":\"Brewery with guid: " + guid + " not found\"}").build();
        }
        dbBrewery.setName(breweryDto.getName());
        dbBrewery.setLogo(breweryDto.getLogo());
        Brewery updatedBrewery = breweryDao.update(dbBrewery);
        return Response.ok(breweryMapper.map(updatedBrewery)).build();
    }
}
