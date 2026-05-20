package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum StuecklisteIcon implements Icon {

  EFS_16X16("efs-16x16.png"),
  EFS_24X24("efs-24x24.png"),
  EFS_32X32("efs-32x32.png"),
  EFS_48X48("efs-48x48.png"),

  EFS_INSPECTOR_16X16("efs-inspector-16x16.png"),
  EFS_INSPECTOR_24X24("efs-inspector-24x24.png"),
  EFS_INSPECTOR_32X32("efs-inspector-32x32.png"),
  EFS_INSPECTOR_48X48("efs-inspector-48x48.png"),

  EFS_PART_PROPERTIES_16X16("efs-part-properties-16x16.png"),
  EFS_PART_PROPERTIES_32X32("efs-part-properties-32x32.png"),

  EFS_REPLACE_AGGREGATE_16X16("efs-aggregate-16x16.png"),
  EFS_REPLACE_AGGREGATE__24X24("efs-aggregate-24x24.png"),
  EFS_REPLACE_AGGREGATE_32X32("efs-aggregate-32x32.png"),

  RISS_ERSETZEN_16X16("riss-ersetzen-16x16.png"),
  RISS_ERSETZEN_24X24("riss-ersetzen-24x24.png"),
  RISS_ERSETZEN_32X32("riss-ersetzen-32x32.png"),
  RISS_ERSETZEN_48X48("riss-ersetzen-48x48.png"),

  HISTORIE_16X16("historie-16x16.png"),
  HISTORIE_24X24("historie-24x24.png"),
  HISTORIE_32X32("historie-32x32.png"),
  HISTORIE_48X48("historie-48x48.png"),

  REVISIONEN_16X16("revisionen-16x16.png"),
  REVISIONEN_24X14("revisionen-24x24.png"),
  REVISIONEN_32X32("revisionen-32x32.png"),
  REVISIONEN_48X48("revisionen-48x48.png"),

  AENDERUNGSANSICHT_16X16("aenderungsansicht-16x16.png"),
  AENDERUNGSANSICHT_24X24("aenderungsansicht-24x24.png"),
  AENDERUNGSANSICHT_32X32("aenderungsansicht-32x32.png"),
  AENDERUNGSANSICHT_48X48("aenderungsansicht-48x48.png"),

  RISS_AUSWERTUNG_16X16("riss-auswertung-16x16.png"),
  RISS_AUSWERTUNG_24X24("riss-auswertung-24x24.png"),
  RISS_AUSWERTUNG_32X32("riss-auswertung-32x32.png"),
  RISS_AUSWERTUNG_48X48("riss-auswertung-48x48.png"),

  TABLE_16X16("table-16x16.png"),
  TABLE_24X24("table-24x24.png"),
  TABLE_32X32("table-32x32.png"),
  TABLE_48X48("table-48x48.png"),

  CROSSHIGHLIGHTINGTABLE("CrossHighlightingTable.png"),
  CROSSHIGHLIGHT_16X16("CrossSelection_Table-16x16.png"),
  CROSSHIGHLIGHT_24X24("CrossSelection_Table-24x24.png"),
  CROSSHIGHLIGHT_32X32("CrossSelection_Table-32x32.png"),
  CROSSHIGHLIGHT_48X48("CrossSelection_Table-48x48.png"),
  SINGLEHIGHLIGHT_16X16("SingleSelection_Table-16x16.png"),
  SINGLEHIGHLIGHT_24X24("SingleSelection_Table-24x24.png"),
  SINGLEHIGHLIGHT_32X32("SingleSelection_Table-32x32.png"),
  SINGLEHIGHLIGHT_48X48("SingleSelection_Table-48x48.png"),

  TREE_VIEW_16x16("group-view-16x16.png"),
  TREE_VIEW_32x32("group-view-32x32.png"),

  GROUP_VIEW_32x32("table-group-32x32.png"),

  COMPARE_16x16("compare-16x16.png"),
  COMPARE_20x20("compare-20x20.png"),
  COMPARE_32x32("compare-32x32.png"),

  REFERENCE_PART_LIST_FLAG_16x16("reference-flag-16x16.png"),
  REFERENCE_PART_LIST_FLAG_20x20("reference-flag-20x20.png"),
  REFERENCE_PART_LIST_FLAG_24x24("reference-flag-24x24.png"),
  REFERENCE_PART_LIST_FLAG_32x32("reference-flag-32x32.png"),

  HIGHLIGHT_DIFFERENCES_16x16("highlight-diff-16x16.png"),
  HIGHLIGHT_DIFFERENCES_24x24("highlight-diff-24x24.png"),
  HIGHLIGHT_DIFFERENCES_32x32("highlight-diff-32x32.png"),
  HIGHLIGHT_DIFFERENCES_48x48("highlight-diff-48x48.png"),

  HIGHLIGHT_COMMON_16x16("highlight-common-16x16.png"),
  HIGHLIGHT_COMMON_24x24("highlight-common-24x24.png"),
  HIGHLIGHT_COMMON_32x32("highlight-common-32x32.png"),
  HIGHLIGHT_COMMON_48x48("highlight-common-48x48.png"),

  GROUP_PRNUMBER_32x32("group-prnumbers-32x32.png"),

  NUMBER_OF_PARTS_16x16("display-count-16x16.png"),
  NUMBER_OF_PARTS_32x32("display-count-32x32.png"),

  DISPLAY_DELTA_16x16("display-delta-16x16.png"),
  DISPLAY_DELTA_24x24("display-delta-24x24.png"),
  DISPLAY_DELTA_32x32("display-delta-32x32.png"),

  DISPLAY_ALL_16x16("display-all-16x16.png"),
  DISPLAY_ALL_24x24("display-all-24x24.png"),
  DISPLAY_ALL_32x32("display-all-32x32.png"),
  DISPLAY_ALL_48x48("display-all-48x48.png");

  private static final String FOLDER_NAME = "icons/stueckliste";

  private final String fileName;
  private final Image image;

  StuecklisteIcon(final String fileName) {
    this.fileName = fileName;
    this.image = IconUtil.loadImage(getPath());
  }

  public String getPath() {
    return FOLDER_NAME + "/" + getFileName();
  }

  public String getFileName() {
    return fileName;
  }

  public Image getImage() {
    return image;
  }

}
