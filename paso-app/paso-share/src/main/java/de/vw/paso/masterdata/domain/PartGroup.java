package de.vw.paso.masterdata.domain;

import de.vw.paso.core.domain.AbstractEntity;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = PartGroup.TABLE_PART_GROUP)
public class PartGroup extends AbstractEntity<Long> {

    static final String TABLE_PART_GROUP = "PART_GROUP";

    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_MGR_START = "mgr_start";
    private static final String COLUMN_MGR_END = "mgr_end";
    private static final String COLUMN_UGR = "ugr";
    private static final String COLUMN_DESCRIPTION = "description";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = COLUMN_CATEGORY, columnDefinition = "int(1)")
    private Integer category;

    @Column(name = COLUMN_MGR_START, columnDefinition = "int(3)")
    private Integer mgr;

    @Column(name = COLUMN_MGR_END, columnDefinition = "int(3)")
    private Integer mgrEnd;

    @Column(name = COLUMN_UGR, columnDefinition = "int(3)")
    private Integer ugr;

    @Column(name = COLUMN_DESCRIPTION)
    private String description;

    public PartGroup(PartGroupDTO partGroupDTO) {
        this(partGroupDTO.getId(), partGroupDTO.getCategory(), partGroupDTO.getMgr(), partGroupDTO.getMgrEnd(),
                partGroupDTO.getUgr(), partGroupDTO.getDescription());
    }

    @Transient
    public boolean isCategory() {
        return mgr == null;
    }

    @Transient
    public boolean isMgr() {
        return mgr != null && ugr == null;
    }

    @Transient
    public boolean isUgr() {
        return ugr != null;
    }
}
