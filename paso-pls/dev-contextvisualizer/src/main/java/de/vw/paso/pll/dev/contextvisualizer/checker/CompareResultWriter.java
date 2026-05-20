package de.vw.paso.pll.dev.contextvisualizer.checker;

import de.vw.paso.pll.creation.PartListCreationConfiguration;
import de.vw.paso.pll.dev.contextvisualizer.util.CompareStatus;
import de.vw.paso.pll.dev.contextvisualizer.veron.VeronElement;
import de.vw.paso.pll.dev.contextvisualizer.veron.VeronFields;
import de.vw.paso.pll.model.PlsEfsElement;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompareResultWriter {

  interface CheckSummary<T> {
    boolean hasError(Map<T, CompareStatus> statusMap, T element, List<T> parents);

    String pathToString(List<T> element);

    List<T> getChildren(T t);
  }

  interface EfsCheckSummary extends CheckSummary<PlsEfsElement> {

    default  String pathToString(List<PlsEfsElement> elements) {
      StringBuilder sb = new StringBuilder();
      elements.forEach(element -> sb.append("->").append(element.getPartNumber()).append("(").append(element.getOriginNodeId()).append(")"));
      return sb.toString();
    }

    @Override
    default List<PlsEfsElement> getChildren(PlsEfsElement element) {
      return element.getChildren();
    }
  }

  interface VeronCheckSummary extends CheckSummary<VeronElement> {
    @Override
    default String pathToString(List<VeronElement> elements) {
      StringBuilder sb = new StringBuilder();
      elements.forEach(element -> sb.append("->").append(element.get(VeronFields.Teilenummer)).append("(").append(element.get(VeronFields.NODE_ID)).append(")"));
      return sb.toString();
    }

    @Override
    default List<VeronElement> getChildren(VeronElement veronElement) {
      return veronElement.getChildren();
    }
  }

  private class MissingInVeronCheckSummary implements EfsCheckSummary {
    private boolean foundPart;
    private MissingInVeronCheckSummary(boolean foundPart) {
      this.foundPart= foundPart;
    }

    @Override
    public boolean hasError(Map<PlsEfsElement, CompareStatus> statusMap, PlsEfsElement element, List<PlsEfsElement> parents) {
      //Ignore PArts with 000 in part number or in parent part number
      if (element.getPartNumber() != null && element.getPartNumber().startsWith("000")) {
        return false;
      }
      for (PlsEfsElement parent : parents ){
        if (parent.getPartNumber() != null && parent.getPartNumber().startsWith("000")) {
          return false;
        }
      }


      CompareStatus compareStatus = statusMap.get(element);
      if (CompareStatus.NOT_FOUND_IN_VERON.equals(compareStatus)) {
        if (foundPart) {
          return element.isPartFound();
        } else {
          return !element.isPartFound();
        }
      }
      return false;
    }
  }

  private class IgnoredPartNumberCheckSummary implements EfsCheckSummary {

    @Override
    public boolean hasError(Map<PlsEfsElement, CompareStatus> statusMap, PlsEfsElement element, List<PlsEfsElement> parents) {
      CompareStatus compareStatus = statusMap.get(element);
      if (CompareStatus.OK.equals(compareStatus)) {
        return false;
      }
      if (element.getPartNumber() != null && element.getPartNumber().startsWith("000")) {
        return true;
      }
      for (PlsEfsElement parent : parents ){
        if (parent.getPartNumber() != null && parent.getPartNumber().startsWith("000")) {
          return true;
        }
      }
      return false;
    }
  }

  public class NotEqualsCheckSummary implements EfsCheckSummary {
    @Override
    public boolean hasError(Map<PlsEfsElement, CompareStatus> statusMap, PlsEfsElement element, List<PlsEfsElement> parents) {
      CompareStatus compareStatus = statusMap.get(element);
      return CompareStatus.NOT_EQUALS.equals(compareStatus);
    }
  }

  private class ParentNotFoundCheckSummary implements EfsCheckSummary {
    @Override
    public boolean hasError(Map<PlsEfsElement, CompareStatus> statusMap, PlsEfsElement element, List<PlsEfsElement> parents) {
      CompareStatus compareStatus = statusMap.get(element);
      return CompareStatus.PARENT_NOT_FOUND_IN_VERON.equals(compareStatus);
    }
  }

  private class VeronCompareStatusCheckSummary implements VeronCheckSummary {
    private PartListCreationConfiguration config;
    public VeronCompareStatusCheckSummary(PartListCreationConfiguration config) {
      this.config = config;
    }

    @Override
    public boolean hasError(Map<VeronElement, CompareStatus> statusMap, VeronElement element, List<VeronElement> parents) {
      CompareStatus compareStatus = statusMap.get(element);
      if (CompareStatus.MISSING_IN_EFS.equals(compareStatus)) {
        if (!config.addLeitungsstraenge()) {
          List<VeronElement> elementsToCheck = new ArrayList<>(parents);
          elementsToCheck.add(element);
          return !elementsToCheck.stream().anyMatch(this::isLTGS);
        } else {
          return true;
        }
      }
      return false;
    }

    private boolean isLTGS(VeronElement element) {
      String bez = element.get(VeronFields.Bezeichnung1);
      return bez != null && bez.startsWith("LTGS");
    }
  }

  public void writeResult(Writer out, PartListCreationConfiguration config, PageTreeCompareResult result, PlsEfsElement efsRoot, VeronElement veronRoot) throws IOException {
    Map<PlsEfsElement, CompareStatus> efsStatusMap = result.getEfsStatusMap();
    out.write("=====================================\n");
    out.write("=   Errors found during comparing   =\n");
    out.write("=           EFS <-> Veron           =\n");
    out.write("=====================================\n");

    int totalErrorCount = 0;
    out.write("Elements not found in Veron with valid part:\n\n");
    int errorCount = checkEfsElement(out, efsStatusMap, efsRoot, new ArrayList<>(), new MissingInVeronCheckSummary(true));
    out.write("\t->" + errorCount + " Errors\n\n");
    totalErrorCount += errorCount;

    out.write("Elements not found in Veron but with part numbers or parent part numbers starting with '000':\n");
    errorCount = checkEfsElement(out, efsStatusMap, efsRoot, new ArrayList<>(), new IgnoredPartNumberCheckSummary());
    out.write("\t->" + errorCount + " Errors\n\n");
    totalErrorCount = errorCount;

    out.write("\nElements not found in Veron without valid part:\n");
    errorCount = checkEfsElement(out, efsStatusMap, efsRoot, new ArrayList<>(), new MissingInVeronCheckSummary(false));
    out.write("\t->" + errorCount + " Errors\n\n");
    totalErrorCount += errorCount;

    out.write("\nElements not equals with veron nodes:\n");
    errorCount = checkEfsElement(out, efsStatusMap, efsRoot, new ArrayList<>(), new NotEqualsCheckSummary());
    out.write("\t->" + errorCount + " Errors\n\n");
    totalErrorCount += errorCount;

    out.write("Elements in Veron but not in EFS:\n");
    errorCount = checkEfsElement(out, result.getVeronStatusMap(), veronRoot, new ArrayList<>(), new VeronCompareStatusCheckSummary(config));
    out.write("\t->" + errorCount + " Errors\n\n");
    totalErrorCount += errorCount;

    out.write("\n\t\t-> Total Error count: " + totalErrorCount + "\n\n\n");



    out.write("=====================================\n");
    out.write("=           Ignore List             =\n");
    out.write("=====================================\n");

    out.write("\nElements not found in Veron because parent was not found:\n");
    errorCount = checkEfsElement(out, efsStatusMap, efsRoot, new ArrayList<>(), new ParentNotFoundCheckSummary());
    out.write("\t->" + errorCount + " Errors\n\n");
    totalErrorCount += errorCount;
    out.write("\t\t-> Total ignored:" + totalErrorCount+ " Entries\n\n");

  }

  private <T> int checkEfsElement(Writer out, Map<T, CompareStatus> statusMap, T element, List<T> parentPath, CheckSummary<T> checkFunction) throws IOException {
    int errorCount = 0;
    parentPath.add(element);
    if (checkFunction.hasError(statusMap, element, parentPath)) {
      CompareStatus compareStatus = statusMap.get(element);
      out.write(compareStatus +":" + checkFunction.pathToString(parentPath) + "\n");
      errorCount++;
    }
    for (T e : checkFunction.getChildren(element)) {
      errorCount += checkEfsElement(out, statusMap, e, parentPath, checkFunction);
    }
    parentPath.remove(element);
    return errorCount;
  }
}
