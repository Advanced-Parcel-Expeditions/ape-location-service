package si.ape.location.models.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "city")
@NamedQueries(value =
        {
                @NamedQuery(name = "CityEntity.getAll",
                        query = "SELECT c FROM CityEntity c")
        })
@IdClass(CityEntity.CityId.class)
public class CityEntity {

    @Id
    private String code;

    @Id
    private String name;

    @Id
    @ManyToOne
    @JoinColumn(name = "country_code", referencedColumnName = "code")
    private CountryEntity country;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public static class CityId implements Serializable {
        private String code;
        private String name;
        private String country;
        //private CountryEntity country;

        public CityId(String code, String name, String country ) {
            this.code = code;
            this.name = name;
            this.country = country;
            //this.country = country;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry_code() {
            return country;
        }

        public void setCountry_code(String country_code) {
            this.country = country_code;
        }

        /*public CountryEntity getCountry() {
            return country;
        }

        public void setCountry(CountryEntity country) {
            this.country = country;
        }*/

        /*@Override
        public boolean equals(final Object obj) {
            return obj instanceof CityId && ((CityId) obj).code.equals(this.code) && ((CityId) obj).name.equals(this.name) && ((CityId) obj).country.equals(this.country);
        }

        @Override
        public int hashCode() {
            return code.hashCode();
        }*/

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CityId)) return false;
            CityId cityId = (CityId) o;
            return getCode().equals(cityId.getCode()) &&
                    getName().equals(cityId.getName()) &&
                    getCountry_code().equals(cityId.getCountry_code());
        }

        @Override
        public int hashCode() {
            return code.hashCode();
        }

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CountryEntity getCountry() {
        return country;
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CityEntity && ((CityEntity) obj).code.equals(this.code) && ((CityEntity) obj).name.equals(this.name) && ((CityEntity) obj).country.equals(this.country);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

}
