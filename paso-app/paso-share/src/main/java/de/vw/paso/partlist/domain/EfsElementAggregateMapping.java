package de.vw.paso.partlist.domain;

import static de.vw.paso.partlist.domain.EfsElementAggregateMapping.TABLE_NAME;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TABLE_NAME)
@Getter
@Setter
public class EfsElementAggregateMapping {

    public static final String TABLE_NAME = "efs_element_aggregate";

    public static final String COLUMN_ID = "efs_element_id";
    public static final String COLUMN_PRODUCT_DATA_ID = "product_data_id";
    public static final String COLUMN_IMPORT_DATE = "aggregate_import_date";
    public static final String COLUMN_FILE_LOCK_ID = "aggregate_pls_file_lock_id";

    @Id
    @Column(name = COLUMN_ID)
    private Long efsElementId;

    @Column(name = COLUMN_PRODUCT_DATA_ID)
    private String productDataId;

    @Column(name = COLUMN_IMPORT_DATE, columnDefinition = "date")
    private Date importDate;

    @Column(name = COLUMN_FILE_LOCK_ID)
    private String plsFileLockId;

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }
}
