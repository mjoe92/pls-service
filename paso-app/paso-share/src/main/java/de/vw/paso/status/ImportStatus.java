package de.vw.paso.status;

public enum ImportStatus {

  REQUESTED(100L, 1),
  IMPORTED(200L, 0),
  NO_DATA(300L, 2),
  ERROR(400L, 3);

  private final long status;
  private final int sortOrder;

  ImportStatus(Long status, int sortOrder) {
    this.status = status;
    this.sortOrder = sortOrder;
  }

  public long getStatus() {
    return status;
  }

  public int getSortOrder() {
    return sortOrder;
  }
}
