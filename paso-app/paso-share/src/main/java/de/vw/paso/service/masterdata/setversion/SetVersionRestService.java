package de.vw.paso.service.masterdata.setversion;

public interface SetVersionRestService {

  String URL = "/api/set-versions";

  SetVersionListDTO loadSetVersions();

  SetVersionDTO addSetVersion(AddSetVersionRequestDTO requestDTO);

  SetVersionDTO updateSetVersion(Long setVersionId, UpdateSetVersionRequestDTO requestDTO);

  void deleteSetVersion(Long id);
}
