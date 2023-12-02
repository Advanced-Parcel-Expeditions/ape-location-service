package si.ape.location.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.ape.location.lib.Country;
import si.ape.location.services.beans.LocationBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



@ApplicationScoped
@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {

    private Logger log = Logger.getLogger(LocationResource.class.getName());

    @Inject
    private LocationBean locationBean;

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all locations.", summary = "Get all locations")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of locations",
                    content = @Content(schema = @Schema(implementation = Country.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getLocations() {

        List<Country> countries = locationBean.getCountry();
        return Response.status(Response.Status.OK).entity(countries).build();
    }

    @Operation(description = "Get location by code.", summary = "Get location by code")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Location",
                    content = @Content(schema = @Schema(implementation = Country.class))
            )})
    @GET
    @Path("/{code}")
    public Response getLocation(@Parameter(description = "Location code", required = true) @PathParam("code") String code) {

        Country country = locationBean.getCountry(code);

        if (country == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(country).build();
    }

    @Operation(description = "Get location by name.", summary = "Get location by name")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Location",
                    content = @Content(schema = @Schema(implementation = Country.class))
            )})
    @GET
    @Path("/name/{name}")
    public Response getLocationsByName(@Parameter(description = "Location name", required = true) @PathParam("name") String name) {

        List<Country> country = locationBean.getCountryFilter(name);

        if (country == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(country).build();
    }

    @Operation(description = "Add location.", summary = "Add location")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Location successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createLocation(@RequestBody(
            description = "DTO object with location.",
            required = true, content = @Content(
            schema = @Schema(implementation = Country.class))) Country country) {

        if ((country.getCode() == null || country.getName() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            country = locationBean.createCountry(country);
        }

        return Response.status(Response.Status.CONFLICT).entity(country).build();
    }

    @Operation(description = "Update location.", summary = "Update location")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Location successfully updated.",
                    content = @Content(
                            schema = @Schema(implementation = Country.class))
            ),
            @APIResponse(responseCode = "404", description = "Location not found."),
            @APIResponse(responseCode = "405", description = "Validation error.")
    })
    @PUT
    @Path("{code}")
    public Response putLocation(@Parameter(description = "Location code.", required = true)
                                      @PathParam("code") String code,
                                      @RequestBody(
                                              description = "DTO object with location.",
                                              required = true, content = @Content(
                                              schema = @Schema(implementation = Country.class)))
                                              Country country){

        country = locationBean.putCountry(code, country);

        if (country == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(country).build();
    }

    @Operation(description = "Delete location.", summary = "Delete location")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Location successfully deleted."
            ),
            @APIResponse(responseCode = "404", description = "Location not found.")
    })
    @DELETE
    @Path("{code}")
    public Response deleteLocation(@Parameter(description = "Location code.", required = true)
                                      @PathParam("code") String code){

        boolean deleted = locationBean.deleteCountry(code);

        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
