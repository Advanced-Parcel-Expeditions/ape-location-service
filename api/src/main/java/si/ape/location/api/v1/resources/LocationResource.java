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
import si.ape.location.lib.City;
import si.ape.location.lib.Country;
import si.ape.location.lib.Street;
import si.ape.location.models.entities.CountryEntity;
import si.ape.location.services.beans.LocationBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.stream.Location;
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

    @Operation(description = "Get all streets by parameters.", summary = "Get all streets by parameters")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of locations",
                    content = @Content(schema = @Schema(implementation = Street.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getStreetsByParameters(@QueryParam("streetName") String streetName,
                                           @QueryParam("streetNumber") Integer streetNumber,
                                           @QueryParam("cityCode") String cityCode,
                                           @QueryParam("cityName") String cityName,
                                           @QueryParam("countryCode") String countryCode,
                                           @QueryParam("page") @DefaultValue("1") Integer page,
                                           @QueryParam("size") @DefaultValue("50") Integer size) {

        if (size > 150) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<Street> streets = locationBean.getStreetByParameters(streetName, streetNumber, cityCode, cityName, countryCode, page, size);
        return Response.status(Response.Status.OK).entity(streets).build();
    }

    @Operation(description = "Get all streets by parameters.", summary = "Get all streets by parameters")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of locations",
                    content = @Content(schema = @Schema(implementation = Street.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    @Path("/street-search/{searchString}")
    public Response getStreetsBySearchString(@PathParam("searchString") String searchString,
                                             @QueryParam("page") @DefaultValue("0") Integer page,
                                             @QueryParam("size") @DefaultValue("50") Integer size) {

        if (size > 150) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<Street> streets = locationBean.getStreetBySearchString(searchString, page, size);
        return Response.status(Response.Status.OK).entity(streets).build();
    }

    @Operation(description = "Get cities by parameters.", summary = "Get cities by parameters")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of cities",
                    content = @Content(schema = @Schema(implementation = City.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    @Path("/cities")
    public Response getCitiesByParameters(@QueryParam("cityCode") String cityCode,
                                          @QueryParam("cityName") String cityName,
                                          @QueryParam("countryCode") String countryCode,
                                          @QueryParam("page") @DefaultValue("1") Integer page,
                                          @QueryParam("size") @DefaultValue("50") Integer size) {

        if (size > 150) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<City> cities = locationBean.getCityByParameters(cityCode, cityName, countryCode, page, size);
        return Response.status(Response.Status.OK).entity(cities).build();
    }

    @Operation(description = "Get countries by parameters.", summary = "Get countries by parameters")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of countries",
                    content = @Content(schema = @Schema(implementation = Country.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    @Path("/countries")
    public Response getCountriesByParameters(@QueryParam("countryCode") String countryCode,
                                              @QueryParam("countryName") String countryName) {

        List<Country> countries = locationBean.getCountryByParameters(countryCode, countryName);
        return Response.status(Response.Status.OK).entity(countries).build();
    }

    @Operation(description = "Add street.", summary = "Add street")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Street successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createStreet(@RequestBody(
            description = "DTO object with street.",
            required = true, content = @Content(
            schema = @Schema(implementation = Street.class))) Street street) {

        if ((street.getStreetName() == null || street.getStreetNumber() == null || street.getStreetNumber() < 1 || street.getCity() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if ((street.getCity().getCode() == null || street.getCity().getName() == null || street.getCity().getCountry() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // The country code is in the ISO 3166-1 alpha-3 format.
        if (street.getCity().getCountry().getCode() == null || street.getCity().getCountry().getCode().length() != 3) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        street = locationBean.createStreet(street);
        return Response.status(Response.Status.OK).entity(street).build();
    }

    @Operation(description = "Add city.", summary = "Add city")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "City successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    @Path("/cities")
    public Response createCity(@RequestBody(
            description = "DTO object with city.",
            required = true, content = @Content(
            schema = @Schema(implementation = City.class))) City city) {

        if ((city.getCode() == null || city.getName() == null || city.getCountry() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // The country code is in the ISO 3166-1 alpha-3 format.
        if (city.getCountry().getCode() == null || city.getCountry().getCode().length() != 3) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        city = locationBean.createCity(city);
        return Response.status(Response.Status.OK).entity(city).build();
    }

    @Operation(description = "Add country.", summary = "Add country")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Country successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    @Path("/countries")
    public Response createCountry(@RequestBody(
            description = "DTO object with country.",
            required = true, content = @Content(
            schema = @Schema(implementation = Country.class))) Country country) {

        if ((country.getCode() == null || country.getName() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // The country code is in the ISO 3166-1 alpha-3 format.
        if (country.getCode().length() != 3) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        country = locationBean.createCountry(country);
        return Response.status(Response.Status.OK).entity(country).build();
    }

    @Operation(description = "Update street.", summary = "Update street")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Street successfully updated.",
                    content = @Content(
                            schema = @Schema(implementation = Street.class))
            ),
            @APIResponse(responseCode = "404", description = "Street not found."),
            @APIResponse(responseCode = "405", description = "Validation error.")
    })
    @PUT
    public Response putStreet(@Parameter(description = "Street id.", required = true)
                                      @RequestBody(
                                              description = "DTO object with street.",
                                              required = true, content = @Content(
                                              schema = @Schema(implementation = Street.class)))
                                              Street street){

        street = locationBean.putStreet(
                street.getStreetName(),
                street.getStreetNumber(),
                street.getCity().getCode(),
                street.getCity().getName(),
                street.getCity().getCountry().getCode(),
                street
        );

        if (street == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(street).build();
    }

    @Operation(description = "Update city.", summary = "Update city")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "City successfully updated.",
                    content = @Content(
                            schema = @Schema(implementation = City.class))
            ),
            @APIResponse(responseCode = "404", description = "City not found."),
            @APIResponse(responseCode = "405", description = "Validation error.")
    })
    @PUT
    @Path("/cities")
    public Response putCity(@Parameter(description = "City id.", required = true)
                                      @RequestBody(
                                              description = "DTO object with city.",
                                              required = true, content = @Content(
                                              schema = @Schema(implementation = City.class)))
                                              City city){

        city = locationBean.putCity(
                city.getCode(),
                city.getName(),
                city.getCountry().getCode(),
                city
        );

        if (city == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(city).build();
    }

    @Operation(description = "Update country.", summary = "Update country")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Country successfully updated.",
                    content = @Content(
                            schema = @Schema(implementation = Country.class))
            ),
            @APIResponse(responseCode = "404", description = "Country not found."),
            @APIResponse(responseCode = "405", description = "Validation error.")
    })
    @PUT
    @Path("/countries")
    public Response putCountry(@Parameter(description = "Country id.", required = true)
                                      @RequestBody(
                                              description = "DTO object with country.",
                                              required = true, content = @Content(
                                              schema = @Schema(implementation = Country.class)))
                                              Country country){

        country = locationBean.putCountry(
                country.getCode(),
                country
        );

        if (country == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(country).build();
    }

    @Operation(description = "Delete street.", summary = "Delete street")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Street successfully deleted."
            ),
            @APIResponse(responseCode = "404", description = "Street not found.")
    })
    @DELETE
    public Response deleteStreet(@Parameter(description = "Street id.", required = true)
                                      @RequestBody(
                                              description = "DTO object with street.",
                                              required = true, content = @Content(
                                              schema = @Schema(implementation = Street.class)))
                                              Street street){

        LocationBean.DeleteResult deleteResult = locationBean.deleteStreet(
                street.getStreetName(),
                street.getStreetNumber(),
                street.getCity().getCode(),
                street.getCity().getName(),
                street.getCity().getCountry().getCode()
        );

        if (deleteResult == LocationBean.DeleteResult.NOT_FOUND) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (deleteResult == LocationBean.DeleteResult.FOREIGN_KEY_VIOLATION) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Operation(description = "Delete city.", summary = "Delete city")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "City successfully deleted."
            ),
            @APIResponse(responseCode = "404", description = "City not found.")
    })
    @DELETE
    @Path("/cities")
    public Response deleteCity(@Parameter(description = "City id.", required = true)
                                      @RequestBody(
                                              description = "DTO object with city.",
                                              required = true, content = @Content(
                                              schema = @Schema(implementation = City.class)))
                                              City city){

        LocationBean.DeleteResult deleteResult = locationBean.deleteCity(
                city.getCode(),
                city.getName(),
                city.getCountry().getCode()
        );

        if (deleteResult == LocationBean.DeleteResult.NOT_FOUND) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (deleteResult == LocationBean.DeleteResult.FOREIGN_KEY_VIOLATION) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Operation(description = "Delete country.", summary = "Delete country")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Country successfully deleted."
            ),
            @APIResponse(responseCode = "404", description = "Country not found.")
    })
    @DELETE
    @Path("/countries")
    public Response deleteCountry(@Parameter(description = "Country id.", required = true)
                                      @RequestBody(
                                              description = "DTO object with country.",
                                              required = true, content = @Content(
                                              schema = @Schema(implementation = Country.class)))
                                              Country country){

        LocationBean.DeleteResult deleteResult = locationBean.deleteCountry(
                country.getCode()
        );

        if (deleteResult == LocationBean.DeleteResult.NOT_FOUND) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (deleteResult == LocationBean.DeleteResult.FOREIGN_KEY_VIOLATION) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
