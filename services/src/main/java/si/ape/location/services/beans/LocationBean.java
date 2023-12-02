package si.ape.location.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import si.ape.location.lib.Country;
import si.ape.location.models.converters.CountryConverter;
import si.ape.location.models.entities.CountryEntity;

import si.ape.location.lib.City;
import si.ape.location.models.converters.CityConverter;
import si.ape.location.models.entities.CityEntity;

import si.ape.location.lib.Street;
import si.ape.location.models.converters.StreetConverter;
import si.ape.location.models.entities.StreetEntity;

@RequestScoped
public class LocationBean {

    private Logger log = Logger.getLogger(LocationBean.class.getName());

    @Inject
    private EntityManager em;

    public List<Country> getCountry() {

        log.log(java.util.logging.Level.INFO, "getCountry() called");

        TypedQuery<CountryEntity> query = em.createNamedQuery(
                "CountryEntity.getAll", CountryEntity.class);



        List<CountryEntity> resultList = query.getResultList();

        return resultList.stream().map(CountryConverter::toDto).collect(Collectors.toList());

    }

    public List<Country> getCountryFilter(String name) {

        QueryParameters queryParameters = QueryParameters.query("name=" + name ).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, CountryEntity.class, queryParameters).stream()
                .map(CountryConverter::toDto).collect(Collectors.toList());

    }

    public Country getCountry(String code) {

        CountryEntity countryEntity = em.find(CountryEntity.class, code);

        if (countryEntity == null) {
            throw new NotFoundException();
        }

        Country country = CountryConverter.toDto(countryEntity);

        return country;

    }

    public Country createCountry(Country country) {

        CountryEntity countryEntity = CountryConverter.toEntity(country);

        try {
            beginTx();
            em.persist(countryEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (countryEntity.getCode() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return CountryConverter.toDto(countryEntity);
    }

    public Country putCountry(String code, Country country) {

        CountryEntity c = em.find(CountryEntity.class, code);

        if (c == null) {
            return null;
        }

        CountryEntity updatedCountryEntity = CountryConverter.toEntity(country);

        try {
            beginTx();
            updatedCountryEntity.setCode(c.getCode());
            updatedCountryEntity = em.merge(updatedCountryEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return CountryConverter.toDto(updatedCountryEntity);
    }

    public boolean deleteCountry(String code) {

        CountryEntity country = em.find(CountryEntity.class, code);

        if (country != null) {
            try {
                beginTx();
                em.remove(country);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }

        return true;
    }

    public List<City> getCity() {

        TypedQuery<CityEntity> query = em.createNamedQuery(
                "CityEntity.getAll", CityEntity.class);

        List<CityEntity> resultList = query.getResultList();

        return resultList.stream().map(CityConverter::toDto).collect(Collectors.toList());

    }

    public List<City> getCityFilter(String name) {

        QueryParameters queryParameters = QueryParameters.query("name=" + name ).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, CityEntity.class, queryParameters).stream()
                .map(CityConverter::toDto).collect(Collectors.toList());

    }

    public List<City> getCityFilter(String name, String country_code) {

        QueryParameters queryParameters = QueryParameters.query("name=" + name + "&country_code=" + country_code ).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, CityEntity.class, queryParameters).stream()
                .map(CityConverter::toDto).collect(Collectors.toList());

    }

    public City getCity(String code, String name, String country_code) {

        CityEntity cityEntity = em.find(CityEntity.class, new CityEntity.CityId(code, name, country_code));

        if (cityEntity == null) {
            throw new NotFoundException();
        }

        City city = CityConverter.toDto(cityEntity);

        return city;

    }

    public City createCity(City city) {

        CityEntity cityEntity = CityConverter.toEntity(city);

        try {
            beginTx();
            em.persist(cityEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (cityEntity.getCode() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return CityConverter.toDto(cityEntity);
    }

    public City putCity(String code, String name, String country_code, City city) {

        CityEntity c = em.find(CityEntity.class, new CityEntity.CityId(code, name, country_code));

        if (c == null) {
            return null;
        }

        CityEntity updatedCityEntity = CityConverter.toEntity(city);

        try {
            beginTx();
            updatedCityEntity.setCode(c.getCode());
            updatedCityEntity.setName(c.getName());
            updatedCityEntity.setCountry(c.getCountry());
            updatedCityEntity = em.merge(updatedCityEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return CityConverter.toDto(updatedCityEntity);
    }

    public boolean deleteCity(String code, String name, String country_code) {

        CityEntity city = em.find(CityEntity.class, new CityEntity.CityId(code, name, country_code));

        if (city != null) {
            try {
                beginTx();
                em.remove(city);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }

        return true;
    }

    public List<Street> getStreet() {

        TypedQuery<StreetEntity> query = em.createNamedQuery(
                "StreetEntity.getAll", StreetEntity.class);

        List<StreetEntity> resultList = query.getResultList();

        return resultList.stream().map(StreetConverter::toDto).collect(Collectors.toList());

    }

    public List<Street> getStreetFilter(String name) {

        QueryParameters queryParameters = QueryParameters.query("name=" + name ).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, StreetEntity.class, queryParameters).stream()
                .map(StreetConverter::toDto).collect(Collectors.toList());

    }

    public List<Street> getStreetFilter(String name, String city_code, String city_name, String country_code) {

        QueryParameters queryParameters = QueryParameters.query("name=" + name + "&city_code=" + city_code + "&city_name=" + city_name + "&country_code=" + country_code ).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, StreetEntity.class, queryParameters).stream()
                .map(StreetConverter::toDto).collect(Collectors.toList());

    }

    public Street getStreet(String street_name, Integer street_number, String city_code, String city_name, String country_code) {

        StreetEntity streetEntity = em.find(StreetEntity.class, new StreetEntity.StreetId(street_name, street_number, city_code, city_name, country_code));

        if (streetEntity == null) {
            throw new NotFoundException();
        }

        Street street = StreetConverter.toDto(streetEntity);

        return street;

    }

    public Street createStreet(Street street) {

        StreetEntity streetEntity = StreetConverter.toEntity(street);

        try {
            beginTx();
            em.persist(streetEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (streetEntity.getStreet_name() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return StreetConverter.toDto(streetEntity);
    }

    public Street putStreet(String street_name, Integer street_number, String city_code, String city_name, String country_code, Street street) {

        StreetEntity c = em.find(StreetEntity.class, new StreetEntity.StreetId(street_name, street_number, city_code, city_name, country_code));

        if (c == null) {
            return null;
        }

        StreetEntity updatedStreetEntity = StreetConverter.toEntity(street);

        try {
            beginTx();
            updatedStreetEntity.setStreet_name(c.getStreet_name());
            updatedStreetEntity.setStreet_number(c.getStreet_number());
            updatedStreetEntity.setCity(c.getCity());
            //updatedStreetEntity.setCountry(c.getCountry());
            updatedStreetEntity = em.merge(updatedStreetEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return StreetConverter.toDto(updatedStreetEntity);
    }

    public boolean deleteStreet(String street_name, Integer street_number, String city_code, String city_name, String country_code) {

        StreetEntity street = em.find(StreetEntity.class, new StreetEntity.StreetId(street_name, street_number, city_code, city_name, country_code));

        if (street != null) {
            try {
                beginTx();
                em.remove(street);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

}
