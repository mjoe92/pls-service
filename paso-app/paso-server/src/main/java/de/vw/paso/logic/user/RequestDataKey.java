package de.vw.paso.logic.user;

public enum RequestDataKey {

  USERID("userid");

  private final String keyName;

  RequestDataKey(String keyName) {
    this.keyName = keyName;
  }

  public String getKeyName() {
    return keyName;
  }
}
