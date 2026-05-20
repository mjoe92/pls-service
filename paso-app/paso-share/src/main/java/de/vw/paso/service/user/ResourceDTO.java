package de.vw.paso.service.user;

import de.vw.paso.core.domain.AbstractModifiableDTO;
import de.vw.paso.user.ResourceType;

public class ResourceDTO extends AbstractModifiableDTO<Long> {

    private ResourceType type;
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public ResourceType getType() {
        return type;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }
}
