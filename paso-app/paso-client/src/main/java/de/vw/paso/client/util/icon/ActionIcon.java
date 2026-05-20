package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum ActionIcon implements Icon {

  COPY_16X16("copy-16x16.png"),
  COPY_24X24("copy-24x24.png"),
  COPY_32X32("copy-32x32.png"),
  COPY_48X48("copy-48x48.png"),

  PASTE_16X16("paste-16x16.png"),
  PASTE_24X24("paste-24x24.png"),
  PASTE_32X32("paste-32x32.png"),
  PASTE_48X48("paste-48x48.png"),

  NEW_16X16("new-16x16.png"),
  NEW_24X24("new-24x24.png"),
  NEW_32X32("new-32x32.png"),
  NEW_48X48("new-48x48.png"),

  EDIT_16X16("edit-16x16.png"),
  EDIT_24X24("edit-24x24.png"),
  EDIT_32X32("edit-32x32.png"),
  EDIT_48X48("edit-48x48.png"),

  CUT_16X16("cut-16x16.png"),
  CUT_24X24("cut-24x24.png"),
  CUT_32X32("cut-32x32.png"),
  CUT_48X48("cut-48x48.png"),

  CLOSE_16X16("close-16x16.png"),
  CLOSE_24X24("close-24x24.png"),
  CLOSE_32X32("close-32x32.png"),

  DELETE_16X16("delete-16x16.png"),
  DELETE_24X24("delete-24x24.png"),
  DELETE_32X32("delete-32x32.png"),

  REFRESH_16X16("refresh-16x16.png"),
  REFRESH_24X24("refresh-24x24.png"),
  REFRESH_32X32("refresh-32x32.png"),
  REFRESH_48X48("refresh-48x48.png"),

  SEARCH_16X16("search-16x16.png"),
  SEARCH_24X24("search-24x24.png"),
  SEARCH_32X32("search-32x32.png"),
  SEARCH_48X48("search-48x48.png"),

  FILTER_16X16("filter-16x16.png"),
  FILTER_24X24("filter-24x24.png"),
  FILTER_32X32("filter-32x32.png"),
  FILTER_48X48("filter-48x48.png"),

  COLLAPSE_16x16("collapse-16x16.png"),
  COLLAPSEALL_16x16("collapseAll-16x16.png"),
  EXPAND_16x16("expand-16x16.png"),
  EXPANDALL_16x16("expandAll-16x16.png"),

  BACK_16x16("back-16x16.png"),
  BACK_24x24("back-24x24.png"),
  BACK_32x32("back-32x32.png"),
  BACK_48x48("back-48x48.png"),

  FORWARD_16x16("forward-16x16.png"),
  FORWARD_24x24("forward-24x24.png"),
  FORWARD_32x32("forward-32x32.png"),
  FORWARD_48x48("forward-48x48.png"),

  ARROW_LEFT_GRAY_16x16("arrow-left-gray-16x16.png"),
  ARROW_LEFT_GRAY_24x24("arrow-left-gray-24x24.png"),
  ARROW_LEFT_GRAY_32x32("arrow-left-gray-32x32.png"),

  ARROW_RIGHT_GRAY_16x16("arrow-right-gray-16x16.png"),
  ARROW_RIGHT_GRAY_24x24("arrow-right-gray-24x24.png"),
  ARROW_RIGHT_GRAY_32x32("arrow-right-gray-32x32.png"),

  ARROW_UP_16x16("arrow-up-16x16.png"),
  ARROW_DOWN_16x16("arrow-down-16x16.png"),

  EXCEL_16x16("excel-16x16.png"),
  EXCEL_24x24("excel-24x24.png"),
  EXCEL_32x32("excel-32x32.png"),
  EXCEL_48x48("excel-48x48.png"),

  RESET_SORTING_16x16("reset-sort-16x16.png"),
  RESET_SORTING_20x20("reset-sort-20x20.png"),
  RESET_SORTING_24x24("reset-sort-24x24.png"),
  RESET_SORTING_32x32("reset-sort-32x32.png"),

  RESET_DELETION_32X32("reset-deletion-32x32.png"),

  COMPARE_RELOAD_32x32("compareReload-32x32.png"),

  CHECK_32x32("propSelection-32x32.png");

  private static final String FOLDER_NAME = "icons/action";

  private final String fileName;
  private final Image image;

  ActionIcon(final String fileName) {
    this.fileName = fileName;
    this.image = IconUtil.loadImage(getPath());
  }

  public String getPath() {
    return FOLDER_NAME + "/" + getFileName();
  }

  @Override
  public String getFileName() {
    return fileName;
  }

  @Override
  public Image getImage() {
    return image;
  }

}
