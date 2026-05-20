package de.vw.paso.pls.datarequest.ssh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SshCredStore {

  @Value("${data_request_strategy.ssh.host:}")
  private String host;
  @Value("${data_request_strategy.ssh.port:22}")
  private int port;
  @Value("${data_request_strategy.ssh.user:}")
  private String userName;
  @Value("${data_request_strategy.ssh.password}")
  private String password;

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }
}
