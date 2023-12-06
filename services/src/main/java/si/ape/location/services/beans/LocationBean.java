package si.ape.location.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import org.eclipse.persistence.exceptions.DatabaseException;
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

    public enum DeleteResult {
        OK,
        NOT_FOUND,
        FOREIGN_KEY_VIOLATION
    }

    public List<Country> getCountry() {

        TypedQuery<CountryEntity> query = em.createNamedQuery(
                "CountryEntity.getAll", CountryEntity.class);


        List<CountryEntity> resultList = query.getResultList();

        return resultList.stream().map(CountryConverter::toDto).collect(Collectors.toList());

    }

    public List<Country> getCountryByParameters(String code, String name) {

        TypedQuery<CountryEntity> query = em.createNamedQuery(
                "CountryEntity.getAllByParameters", CountryEntity.class);

        query.setParameter("code", code);
        query.setParameter("name", name);

        List<CountryEntity> resultList = query.getResultList();

        return resultList.stream().map(CountryConverter::toDto).collect(Collectors.toList());

    }
    
    public List<Country> getCountryFilter(String name) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<CountryEntity> criteriaQuery = criteriaBuilder.createQuery(CountryEntity.class);
        Root<CountryEntity> root = criteriaQuery.from(CountryEntity.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (name != null && !name.isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get("name"), name));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        List<CountryEntity> resultList = em.createQuery(criteriaQuery).getResultList();

        return resultList.stream().map(CountryConverter::toDto).collect(Collectors.toList());

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

    public DeleteResult deleteCountry(String code) {

        CountryEntity country = em.find(CountryEntity.class, code);

        List<City> linkedCities = this.getCityByParameters(null, null, code);

        if (!linkedCities.isEmpty()) {
            return DeleteResult.FOREIGN_KEY_VIOLATION;
        }

        if (country != null) {
            try {
                beginTx();
                em.remove(country);
                commitTx();
            } catch (DatabaseException e) {
                rollbackTx();
                return DeleteResult.FOREIGN_KEY_VIOLATION;
            } catch (Exception e) {
                rollbackTx();
                return DeleteResult.FOREIGN_KEY_VIOLATION;
            }
        } else {
            return DeleteResult.NOT_FOUND;
        }

        return DeleteResult.OK;
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

    public List<City> getCityByParameters(String code, String name, String countryCode, Integer page, Integer size) {

        TypedQuery<CityEntity> query = em.createNamedQuery(
                "CityEntity.getAllByParameters", CityEntity.class);

        query.setParameter("code", code);
        query.setParameter("name", name);
        query.setParameter("country", countryCode);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<CityEntity> resultList = query.getResultList();

        return resultList.stream().map(CityConverter::toDto).collect(Collectors.toList());
    }
    
    public List<City> getCityFilter(String name, String countryCode) {

        QueryParameters queryParameters = QueryParameters.query("name=" + name + "&countryCode=" + countryCode ).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, CityEntity.class, queryParameters).stream()
                .map(CityConverter::toDto).collect(Collectors.toList());

    }

    public City getCity(String code, String name, String countryCode) {

        CityEntity cityEntity = em.find(CityEntity.class, new CityEntity.CityId(code, name, countryCode));

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

    public City putCity(String code, String name, String countryCode, City city) {

        CityEntity c = em.find(CityEntity.class, new CityEntity.CityId(code, name, countryCode));

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

    public DeleteResult deleteCity(String code, String name, String countryCode) {

        CityEntity city = em.find(CityEntity.class, new CityEntity.CityId(code, name, countryCode));

        List<Street> linkedStreets = this.getStreetByParameters(null, null, code, name, countryCode, 0, 0);

        if (!linkedStreets.isEmpty()) {
            return DeleteResult.FOREIGN_KEY_VIOLATION;
        }

        if (city != null) {
            try {
                beginTx();
                em.remove(city);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
                return DeleteResult.FOREIGN_KEY_VIOLATION;
            }
        } else {
            return DeleteResult.NOT_FOUND;
        }

        return DeleteResult.OK;
    }

    public List<Street> getStreet(int page, int size) {

        TypedQuery<StreetEntity> query = em.createNamedQuery(
                "StreetEntity.getAll", StreetEntity.class);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<StreetEntity> resultList = query.getResultList();

        return resultList.stream().map(StreetConverter::toDto).collect(Collectors.toList());
        

    }
    
    public List<Street> getStreetByParameters(String streetName, Integer streetNumber, String cityCode, String cityName, String countryCode, int page, int size) {

        TypedQuery<StreetEntity> query = em.createNamedQuery(
                "StreetEntity.getAllByParameters", StreetEntity.class);
        
        query.setParameter("streetName", streetName);
        query.setParameter("streetNumber", streetNumber);
        query.setParameter("cityCode", cityCode);
        query.setParameter("cityName", cityName);
        query.setParameter("countryCode", countryCode);

        if (page != 0 && size != 0) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        List<StreetEntity> resultList = query.getResultList();

        return resultList.stream().map(StreetConverter::toDto).collect(Collectors.toList());
        

    }
    

    public List<Street> getStreetFilter(String name) {

        QueryParameters queryParameters = QueryParameters.query("name=" + name ).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, StreetEntity.class, queryParameters).stream()
                .map(StreetConverter::toDto).collect(Collectors.toList());

    }

    public List<Street> getStreetFilter(String name, String cityCode, String cityName, String countryCode) {

        QueryParameters queryParameters = QueryParameters.query("name=" + name + "&cityCode=" + cityCode + "&cityName=" + cityName + "&countryCode=" + countryCode ).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, StreetEntity.class, queryParameters).stream()
                .map(StreetConverter::toDto).collect(Collectors.toList());

    }

    public Street getStreet(String streetName, Integer streetNumber, String cityCode, String cityName, String countryCode) {

        StreetEntity streetEntity = em.find(StreetEntity.class, new StreetEntity.StreetId(streetName, streetNumber, cityCode, cityName, countryCode));

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

        if (streetEntity.getStreetName() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return StreetConverter.toDto(streetEntity);
    }

    public Street putStreet(String streetName, Integer streetNumber, String cityCode, String cityName, String countryCode, Street street) {

        StreetEntity c = em.find(StreetEntity.class, new StreetEntity.StreetId(streetName, streetNumber, cityCode, cityName, countryCode));

        if (c == null) {
            return null;
        }

        StreetEntity updatedStreetEntity = StreetConverter.toEntity(street);

        try {
            beginTx();
            updatedStreetEntity.setStreetName(c.getStreetName());
            updatedStreetEntity.setStreetNumber(c.getStreetNumber());
            updatedStreetEntity.setCity(c.getCity());
            updatedStreetEntity = em.merge(updatedStreetEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return StreetConverter.toDto(updatedStreetEntity);
    }

    public DeleteResult deleteStreet(String streetName, Integer streetNumber, String cityCode, String cityName, String countryCode) {

        StreetEntity street = em.find(StreetEntity.class, new StreetEntity.StreetId(streetName, streetNumber, cityCode, cityName, countryCode));

        if (street != null) {
            try {
                beginTx();
                em.remove(street);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
                return DeleteResult.FOREIGN_KEY_VIOLATION;
            }
        } else {
            return DeleteResult.NOT_FOUND;
        }

        return DeleteResult.OK;
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
