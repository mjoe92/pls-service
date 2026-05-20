package de.vw.paso.service.modelimport;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ModelDTO implements Serializable {

    private Long id;
    private ModelImportDTO modelImport;
    private String modelKey;
    private String description;
    private String status;
    private String modelVersion;
    private Date beginDate;
    private Date endDate;
}
