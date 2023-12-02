package si.ape.location.models.converters;

import si.ape.location.lib.City;
import si.ape.location.lib.Street;
import si.ape.location.models.entities.StreetEntity;

public class StreetConverter {

    public static Street toDto(StreetEntity entity) {

        Street dto = new Street();
        dto.setStreet_name(entity.getStreet_name());
        dto.setStreet_number(entity.getStreet_number());
        dto.setCity(CityConverter.toDto(entity.getCity()));
        //dto.setCountry(CountryConverter.toDto(entity.getCountry()));

        return dto;

    }

    public static StreetEntity toEntity(Street dto) {

        StreetEntity entity = new StreetEntity();
        entity.setStreet_name(dto.getStreet_name());
        entity.setStreet_number(dto.getStreet_number());
        entity.setCity(CityConverter.toEntity(dto.getCity()));
        //entity.setCountry(CountryConverter.toEntity(dto.getCountry()));

        return entity;

    }

}
