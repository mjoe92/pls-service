package de.vw.paso.user.domain;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.user.ResourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = Resource.TABLE_RESOURCE)
public final class Resource extends AbstractModifiableEntity<Long> {

    static final String TABLE_RESOURCE = "RESOURCE";

    private static final long serialVersionUID = 1L;

    private static final String PK_RESOURCE_ID = "RESOURCE_ID";
    private static final String COLUMN_TYPE = "TYPE";

    @Id
    @Column(name = PK_RESOURCE_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COLUMN_TYPE, length = 20, nullable = false, columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private ResourceType type;

}
