package de.vw.paso.pll.creation.filter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PartFilterResult {

  public static PartFilterResult notFilteredOut() {
    return new PartFilterResult(false, null);
  }

  public static PartFilterResult filteredOut(String msg) {
    return new PartFilterResult(true, msg);
  }

  public static PartFilterResult toResult(boolean filteredOut, String message) {
    if (filteredOut){
      return PartFilterResult.filteredOut(message);
    } else {
      return PartFilterResult.notFilteredOut();
    }
  }

  private boolean filteredOut;

  List<PartFilterResult> subResults = new ArrayList<>();
  private String message;
  private boolean removeChildren = false;

  private PartFilterResult(boolean filteredOut, String message) {
    this.filteredOut = filteredOut;
    this.message = message;
  }

  public boolean isFilteredOut() {
    return filteredOut;
  }

  public boolean isNotFilteredOut() {
    return !filteredOut;
  }

  public String getMessage() {
    return message;
  }

  public void addSubMatchingResult(PartFilterResult pfr) {
    subResults.add(pfr);
  }

  public boolean hasSubResults() {
    return subResults.size() > 0;
  }

  public List<PartFilterResult> getSubResults() {
    return subResults;
  }

  public String getErrorMessageAsHtml() {
    if (isNotFilteredOut()) {
      return null;
    }
    StringBuilder sb = new StringBuilder("<html>");
    addFilterRes(sb, this, 0);
    sb.append("</html>");
    return sb.toString();
  }

  private void addFilterRes(StringBuilder sb, PartFilterResult pfr, int lvl) {
    sb.append(StringUtils.leftPad(pfr.getMessage(), lvl, "&emsp;")).append("<br>");
    for (PartFilterResult result : pfr.getSubResults()) {
      addFilterRes(sb, result, lvl + 1);
    }
  }

  public boolean isRemoveChildren() {
    return removeChildren;
  }

  public void setRemoveChildren(boolean removeChildren) {
    this.removeChildren = removeChildren;
  }
}
