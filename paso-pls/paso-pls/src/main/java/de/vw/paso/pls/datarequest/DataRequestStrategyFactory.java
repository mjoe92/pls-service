package de.vw.paso.pls.datarequest;

import de.vw.paso.pls.LogService;
import de.vw.paso.pls.datarequest.fs.FileSystemRequestStrategy;
import de.vw.paso.pls.datarequest.ssh.SshClient;
import de.vw.paso.pls.datarequest.ssh.SshCredStore;
import de.vw.paso.pls.datarequest.ssh.SshDataRequestStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataRequestStrategyFactory {

  private static final Logger LOG = LoggerFactory.getLogger(DataRequestStrategyFactory.class);

  private static final String TYPE_SSH = "ssh";
  private static final String TYPE_FILESYSTEM = "fs";

  private final SshCredStore sshCredStore;

  @Value("${data_request_strategy.type:fs}")
  private String strategyType;

  @Value("${stage:default}")
  private String stage;

  public DataRequestStrategyFactory(SshCredStore sshCredStore) {
    this.sshCredStore = sshCredStore;
  }

  public AbstractDataRequestStrategy getStrategy() throws DataRequestException {
    AbstractDataRequestStrategy strat = createStrategy();
    strat.init();
    return strat;
  }

  private AbstractDataRequestStrategy createStrategy() {
    if (TYPE_SSH.equals(strategyType)) {
      SshClient ssh = new SshClient(sshCredStore);

      return new SshDataRequestStrategy(ssh, stage);
    } else if (TYPE_FILESYSTEM.equals(strategyType)) {
      return new FileSystemRequestStrategy();
    } else {
      LOG.warn("Unknown request strategy {}. Using FileSystemRequestStrategy as fallback", strategyType);
      return new FileSystemRequestStrategy();
    }
  }
}
