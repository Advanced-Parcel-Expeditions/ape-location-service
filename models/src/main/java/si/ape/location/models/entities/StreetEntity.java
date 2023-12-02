package si.ape.location.models.entities;

import si.ape.location.lib.City;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "street")
@NamedQueries(value =
        {
                @NamedQuery(name = "StreetEntity.getAll",
                        query = "SELECT s FROM StreetEntity s")
        })
@IdClass(StreetEntity.StreetId.class)
public class StreetEntity {

    @Id
    private String street_name;

    @Id
    private Integer street_number;

    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "code", referencedColumnName = "code"),
            @JoinColumn(name = "name", referencedColumnName = "name"),
            @JoinColumn(name = "country", referencedColumnName = "country_code")
    })
    private CityEntity city;

    /*@Id
    @ManyToOne
    @JoinColumn(name = "country_code", referencedColumnName = "country_code")
    private CountryEntity country;*/

    public static class StreetId implements Serializable {
        private String street_name;
        private Integer street_number;

        private CityEntity.CityId city;
        /*private String code;
        private String name;
        private String country;*/

        public StreetId(String street_name, Integer street_number, String city_code, String city_name, String country_code) {
            this.street_name = street_name;
            this.street_number = street_number;
            /*this.code = city_code;
            this.name = city_name;
            this.country = country_code;*/
            this.city = new CityEntity.CityId(city_code, city_name, country_code);
        }

        public String getStreet_name() {
            return street_name;
        }

        public void setStreet_name(String street_name) {
            this.street_name = street_name;
        }

        public Integer getStreet_number() {
            return street_number;
        }

        public void setStreet_number(Integer street_number) {
            this.street_number = street_number;
        }

        public String getCity_code() {
            //return code;
            return city.getCode();
        }

        public void setCity_code(String city_code) {
            //this.code = city_code;
            this.city.setCode(city_code);
        }

        public String getCity_name() {
            //return name;
            return city.getName();
        }

        public void setCity_name(String city_name) {
            //this.name = city_name;
            this.city.setName(city_name);
        }

        public String getCountry_code() {
            //return country;
            return city.getCountry_code();
        }

        public void setCountry_code(String country_code) {
            //this.country = country_code;
            this.city.setCountry_code(country_code);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StreetId)) return false;
            StreetId that = (StreetId) o;
            return getStreet_name().equals(that.getStreet_name()) &&
                    getStreet_number().equals(that.getStreet_number()) &&
                    getCity_code().equals(that.getCity_code()) &&
                    getCity_name().equals(that.getCity_name())
                    && getCountry_code().equals(that.getCountry_code());
        }

        @Override
        public int hashCode() {
            return street_name.hashCode();
        }
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public Integer getStreet_number() {
        return street_number;
    }

    public void setStreet_number(Integer street_number) {
        this.street_number = street_number;
    }

    public CityEntity getCity() {
        return city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    /*public CountryEntity getCountry() {
        return country;
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }*/

}
