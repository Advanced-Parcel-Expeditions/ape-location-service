package si.ape.location.models.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "country")
@NamedQueries(value =
        {
                @NamedQuery(name = "CountryEntity.getAll",
                        query = "SELECT c FROM CountryEntity c")
        })
public class CountryEntity {

    @Id
    private String code;

    @Column(name = "name")
    private String name;

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

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CountryEntity && ((CountryEntity) obj).code.equals(this.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

}
