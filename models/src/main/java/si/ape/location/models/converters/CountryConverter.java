package si.ape.location.models.converters;

import si.ape.location.lib.Country;
import si.ape.location.models.entities.CountryEntity;

public class CountryConverter {

    public static Country toDto(CountryEntity entity) {

        Country dto = new Country();
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());

        return dto;

    }

    public static CountryEntity toEntity(Country dto) {

        CountryEntity entity = new CountryEntity();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());

        return entity;

    }

}
