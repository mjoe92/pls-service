package de.vw.paso.tiwh.domain;

import de.vw.paso.core.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = TiWhMaktx.TABLE_WH_MAKTX)
public final class TiWhMaktx extends AbstractEntity<Long> {

    static final String TABLE_WH_MAKTX = "TI_WH_MAKTX";

    private static final long serialVersionUID = 1L;

    private static final String PK_TI_WH_MAKTX_ID = "TI_WH_MAKTX_ID";
    private static final String COLUMN_TEILENUMMER = "TEILENUMMER";
    private static final String COLUMN_TI_WH_IMPORT_ID = "TI_WH_IMPORT_ID";
    private static final String COLUMN_BEZEICHNUNG = "BEZEICHNUNG";
    private static final String COLUMN_SPRACHE = "SPRACHE";

    @Id
    @Column(name = PK_TI_WH_MAKTX_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COLUMN_TEILENUMMER, length = 18, nullable = false)
    private String teilenummer;

    @Column(name = COLUMN_TI_WH_IMPORT_ID, nullable = false)
    private Long tiWhImportId;

    @Column(name = COLUMN_BEZEICHNUNG, length = 40)
    private String bezeichnung1;

    @Column(name = COLUMN_SPRACHE, length = 1, nullable = false)
    private String sprache;

}
