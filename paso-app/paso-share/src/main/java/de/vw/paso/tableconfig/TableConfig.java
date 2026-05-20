package de.vw.paso.tableconfig;

import java.util.List;

import de.vw.paso.core.domain.AbstractEntity;
import de.vw.paso.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = TableConfig.TABLE)
public class TableConfig extends AbstractEntity<Long> {

    public static final String TABLE = "TABLE_CONFIG";

    private static final String PK_TABLE_CONFIG_ID = "ID";
    private static final String FK_USER_ID = "USER_ID";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_SELECTED_COLUMNS = "SELECTED_COLUMNS";
    private static final String COLUMN_SELECTED_COLUMN_IDS = "SELECTED_COLUMN_IDS";
    private static final String COLUMN_IS_PUBLIC = "IS_PUBLIC";
    private static final String COLUMN_IS_DEFAULT = "IS_DEFAULT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = PK_TABLE_CONFIG_ID)
    private Long id;
    @JoinColumn(name = FK_USER_ID)
    @ManyToOne
    private User user;
    @Column(name = COLUMN_NAME)
    private String name;
    @Lob
    @Column(name = COLUMN_SELECTED_COLUMNS, columnDefinition = "longtext")
    @Convert(converter = StringListConverter.class)
    private List<String> selectedColumns;
    @Lob
    @Column(name = COLUMN_SELECTED_COLUMN_IDS, columnDefinition = "longtext")
    @Convert(converter = StringListConverter.class)
    private List<String> selectedColumnIds;
    @Column(name = COLUMN_IS_PUBLIC, columnDefinition = "int(1)")
    private boolean isPublic;

    public String getUserId() {
        return user != null ? user.getId() : null;
    }

    @Override
    public String toString() {
        return name;
    }
}
