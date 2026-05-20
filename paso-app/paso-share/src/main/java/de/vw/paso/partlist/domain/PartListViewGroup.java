package de.vw.paso.partlist.domain;

import de.vw.paso.core.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = PartListViewGroup.TABLE_PART_LIST_VIEW_GROUP)
public class PartListViewGroup extends AbstractEntity<Long> {

    static final String TABLE_PART_LIST_VIEW_GROUP = "PART_LIST_VIEW_GROUP";

    private static final long serialVersionUID = 1L;

    private static final String PK_PART_LIST_VIEW_GROUP_ID = "ID";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_COST_GROUP = "COST_GROUP";
    private static final String COLUMN_PART_GROUPS = "PART_GROUPS";
    private static final String COLUMN_RULE_DESCRIPTION = "RULE_DESCRIPTION";
    private static final String COLUMN_PART_LIST_VIEW_MODE = "PART_LIST_VIEW_MODE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = PK_PART_LIST_VIEW_GROUP_ID)
    private Long id;

    @Column(name = COLUMN_NAME)
    private String name;

    @Column(name = COLUMN_RULE_DESCRIPTION)
    private String ruleDescription;

    @Column(name = COLUMN_COST_GROUP)
    private String costGroup;

    @Column(name = COLUMN_PART_GROUPS)
    private String partGroups;

    @Column(name = COLUMN_PART_LIST_VIEW_MODE, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private PartListViewMode partListViewMode;

    @Override
    public void setId(Long aLong) {

    }

}
