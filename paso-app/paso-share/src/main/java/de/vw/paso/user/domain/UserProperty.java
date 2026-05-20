package de.vw.paso.user.domain;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.user.PropertyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = UserProperty.TABLE_USER_PROPERTY)
public class UserProperty extends AbstractModifiableEntity<Long> {

    static final String TABLE_USER_PROPERTY = "USER_PROPERTY";

    private static final String PK_USER_PROPERTY_ID = "USER_PROPERTY_ID";
    private static final String FK_USER_ID = "USER_ID";
    private static final String COLUMN_PROPERTY_TYPE = "PROPERTY_TYPE";
    private static final String COLUMN_USER_DATA = "USER_DATA";

    @Id
    @Column(name = PK_USER_PROPERTY_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = FK_USER_ID, nullable = false)
    private User user;

    @Column(name = COLUMN_PROPERTY_TYPE, nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private PropertyType type;

    @Lob
    @Column(name = COLUMN_USER_DATA, nullable = false, columnDefinition = "longtext")
    private String userData;
}
