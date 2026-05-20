package de.vw.paso.service;

import java.util.Date;

import de.vw.paso.service.buildinfo.BuildInfoRestService;
import de.vw.paso.service.buildinfo.ServerBuildInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = BuildInfoRestService.URL)
public class BuildInfoRestController implements BuildInfoRestService {

  private final BuildProperties buildProperties;

  private final Environment environment;

  private final String stage;

  public BuildInfoRestController(BuildProperties buildProperties, Environment environment,
    @Value("${stage}") String stage) {
    this.buildProperties = buildProperties;
    this.environment = environment;
    this.stage = stage;
  }

  @Override
  @GetMapping
  public ServerBuildInfoDTO getBuildInfo() {
    ServerBuildInfoDTO info = new ServerBuildInfoDTO();
    info.setProfiles(environment.getActiveProfiles());
    info.setBuildDate(Date.from(buildProperties.getTime()));
    info.setBuildNumber(buildProperties.getVersion());
    info.setStage(stage);

    return info;
  }

}
