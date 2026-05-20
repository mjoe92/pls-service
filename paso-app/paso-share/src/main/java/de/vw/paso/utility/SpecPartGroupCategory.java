package de.vw.paso.utility;

import lombok.Getter;

@Getter
public enum SpecPartGroupCategory {

  NORM_PART_GROUP(100, "N"),
  WHT_PART_GROUP(101, "WHT"),
  A_PART_GROUP(102, "A**");

  private String categoryStr;
  private Integer category;

  SpecPartGroupCategory(Integer category, String categoryStr) {
    this.category = category;
    this.categoryStr = categoryStr;
  }

  public static String getStringForCategory(Integer category) {
    for (SpecPartGroupCategory specCat : SpecPartGroupCategory.values()) {
      if (specCat.category.equals(category)) {
        return specCat.categoryStr;
      }
    }

    return null;
  }

}
