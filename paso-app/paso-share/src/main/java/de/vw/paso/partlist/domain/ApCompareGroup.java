package de.vw.paso.partlist.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum ApCompareGroup {

  HUT("table.column.hut", "hut", AP.EMPTY, AP.HUT, AP.X),
  PLATFORM("table.column.platform", "platform", AP.PLATFORM),
  SYSTEM("table.column.system", "system", AP.SYSTEM),
  SUM("table.column.sum", "sum");

  private final String i18nKey;
  private final String columnId;
  private final Set<AP> aps = new HashSet<>();

  ApCompareGroup(String i18nKey, String columnId, AP... aps) {
    this.i18nKey = i18nKey;
    this.columnId = columnId;
    Collections.addAll(this.aps, aps);
  }

  public boolean containsAp(String ap) {
    return aps.contains(AP.getApByAbbreviation(ap));
  }

  public String getI18nKey() {
    return i18nKey;
  }

  public String getColumnId() {
    return columnId;
  }
}
