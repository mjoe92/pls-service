package de.vw.paso.service.pls;

import java.util.Date;

import de.vw.paso.core.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MBT_IMPORT_TIMESTAMP")
public class MbtImportTimeStamp extends AbstractEntity<Long> {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_CREATION")
    private Date fileCreation;

    @Column(name = "IMPORT_DATE")
    private Date importDate;

    @Override
    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public Date getFileCreation() {
        return fileCreation;
    }

    public Date getImportDate() {
        return importDate;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileCreation(Date fileCreation) {
        this.fileCreation = fileCreation;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }
}
