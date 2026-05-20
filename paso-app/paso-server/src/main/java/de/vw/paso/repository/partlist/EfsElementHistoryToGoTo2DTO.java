package de.vw.paso.repository.partlist;

public record EfsElementHistoryToGoTo2DTO(Long efsElementHistoryId, Long efsElementId, String nodeLabel,
                                          Long historyRevision, Long currentRevision){
}
