package de.vw.paso.partlist.domain;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.service.masterdata.pst.PstDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = Pst.TABLE_PST)
@Getter
@Setter
public class Pst extends AbstractModifiableEntity<Long> {

    static final String TABLE_PST = "PST";
    private static final String FK_PARENT = "PARENT_ID";
    private static final String DESC_DE = "DESCRIPTION_DE";
    private static final String DESC_EN = "DESCRIPTION_EN";
    private static final String PST_ID = "PST_ID";
    private static final String PST_NAME = "PST_NAME";

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = PST_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = PST_NAME)
    private String name;

    @Column(name = DESC_DE)
    private String descDe;

    @Column(name = DESC_EN)
    private String descEn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = FK_PARENT)
    private Pst parent;

    @OneToMany(mappedBy = "parent")
    private List<Pst> children = new ArrayList<>();

    public static PstDTO toDTO(Pst pst) {
        return new PstDTO(pst.id, pst.name, pst.descEn, pst.descDe, pst.parent != null ? pst.parent.id : null);
    }

    public static Pst toEntity(PstDTO pstDTO) {
        return toEntity(pstDTO, null);
    }

    public static Pst toEntity(PstDTO pstDTO, Pst parent) {
        Pst pst = new Pst();
        if (pstDTO.getId() != null) {
            pst.setId(pstDTO.getId());
        }
        pst.setName(pstDTO.getName());
        pst.setDescEn(pstDTO.getDescEng());
        pst.setDescDe(pstDTO.getDescDe());
        pst.setParent(parent);
        return pst;
    }
}
