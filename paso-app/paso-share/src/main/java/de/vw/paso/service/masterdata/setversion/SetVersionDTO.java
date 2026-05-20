package de.vw.paso.service.masterdata.setversion;

import java.util.Objects;

import de.vw.paso.core.domain.AbstractModifiableDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SetVersionDTO extends AbstractModifiableDTO<Long> {

    private Long id;
    private String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetVersionDTO that)) {
            return false;
        }
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
