package pl.zywickib.crawler.untappd;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v4/user/beers")
@RegisterRestClient
public interface BeerService {

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    UntappdResponse getUserBeers(@PathParam("username") String username,
        @QueryParam("client_id") String client_id,
        @QueryParam("client_secret") String client_secret,
        @QueryParam("limit") String limit,
        @QueryParam("offset") String offset);
}