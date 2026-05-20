package de.vw.paso.partlist.dto;

import java.util.Date;

public record EfsElementAggregateMappingDTO(Long efsElementId, String productDataId, Date importDate,
                                            String plsFileLockId) { }
