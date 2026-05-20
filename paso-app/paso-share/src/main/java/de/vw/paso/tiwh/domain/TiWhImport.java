package de.vw.paso.tiwh.domain;

import java.util.Objects;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.tiwh.IDatenstand;
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
@Table(name = TiWhImport.TABLE_WH_IMPORT)
public final class TiWhImport extends AbstractModifiableEntity<Long> implements IDatenstand {

    static final String TABLE_WH_IMPORT = "TI_WH_IMPORT";

    private static final long serialVersionUID = 1L;

    private static final String PK_TI_WH_IMPORT_ID = "TI_WH_IMPORT_ID";
    private static final String COLUMN_PRODUCT_KEY = "PRODUCT_KEY";
    private static final String COLUMN_IMPORT_STATUS = "IMPORT_STATUS";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = PK_TI_WH_IMPORT_ID)
    private Long id;

    @Column(name = COLUMN_PRODUCT_KEY, columnDefinition = "char(4)", nullable = false, updatable = false)
    private String productKey;

    @Column(name = COLUMN_IMPORT_STATUS, length = 11, nullable = false, columnDefinition = "varchar(11)")
    @Enumerated(EnumType.STRING)
    private ImportStatus importStatus;

    public static TiWhImportDTO convertToTiWhImportDTO(TiWhImport tiWhImport) {
        if (Objects.isNull(tiWhImport)) {
            return null;
        }
        TiWhImportDTO tiWhImportDTO = new TiWhImportDTO();
        tiWhImportDTO.setId(tiWhImport.getId());
        tiWhImportDTO.setProductKey(tiWhImport.getProductKey());
        tiWhImportDTO.setImportStatus(tiWhImport.getImportStatus());
        tiWhImportDTO.setUserCreate(tiWhImport.getUserCreate());
        tiWhImportDTO.setUserChange(tiWhImport.getUserChange());
        tiWhImportDTO.setTimestampCreate(tiWhImport.getTimestampCreate());
        tiWhImportDTO.setTimestampChange(tiWhImport.getTimestampChange());
        tiWhImportDTO.setEntityChange(tiWhImport.isEntityChange());

        return tiWhImportDTO;
    }

    public static TiWhImport convertToTiWhImportEntity(TiWhImportDTO tiWhImportDTO) {
        if (Objects.isNull(tiWhImportDTO)) {
            return null;
        }
        TiWhImport tiWhImport = new TiWhImport();
        tiWhImport.setId(tiWhImportDTO.getId());
        tiWhImport.setProductKey(tiWhImportDTO.getProductKey());
        tiWhImport.setImportStatus(tiWhImportDTO.getImportStatus());
        tiWhImport.setUserCreate(tiWhImportDTO.getUserCreate());
        tiWhImport.setUserChange(tiWhImportDTO.getUserChange());
        tiWhImport.setTimestampCreate(tiWhImportDTO.getTimestampCreate());
        tiWhImport.setTimestampChange(tiWhImportDTO.getTimestampChange());
        tiWhImport.setEntityChange(tiWhImportDTO.isEntityChange());

        return tiWhImport;
    }
}
