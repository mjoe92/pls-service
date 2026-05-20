package de.vw.paso.partlist.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CostGroupVersionPK implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String PK_COST_GROUP = "COST_GROUP";
    private static final String PK_VERSION = "VERSION";

    @Column(name = PK_COST_GROUP, columnDefinition = "char(4)", nullable = false, updatable = false)
    private String costGroup;

    @Column(name = PK_VERSION, nullable = false, updatable = false)
    private Long version;

}
