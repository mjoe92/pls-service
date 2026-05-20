package de.vw.paso.service.buildinfo;

public interface BuildInfoRestService {
  String URL = "/api/build-info";

  ServerBuildInfoDTO getBuildInfo();
}
