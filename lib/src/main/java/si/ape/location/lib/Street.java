package si.ape.location.lib;

public class Street {

    private String street_name;

    private Integer street_number;

    private City city;

    //private Country country;

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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    /*public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }*/

}
