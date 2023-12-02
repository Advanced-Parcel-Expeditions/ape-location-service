package si.ape.location.models.converters;

import si.ape.location.lib.City;
import si.ape.location.models.entities.CityEntity;

public class CityConverter {

    public static City toDto(CityEntity entity) {

        City dto = new City();
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setCountry(CountryConverter.toDto(entity.getCountry()));

        return dto;

    }

    public static CityEntity toEntity(City dto) {

        CityEntity entity = new CityEntity();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setCountry(CountryConverter.toEntity(dto.getCountry()));

        return entity;

    }

}
