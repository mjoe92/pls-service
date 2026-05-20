package de.vw.paso.pll.creation;

import de.vw.paso.pll.creation.filter.PartFilter;
import de.vw.paso.pll.creation.filter.PartFilterFactory;
import de.vw.paso.pll.creation.filter.PartFilterResult;
import de.vw.paso.pll.model.FilterType;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbkVsdFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbomFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static de.vw.paso.pll.creation.PartListCreatorUtil.getPartData;
import static de.vw.paso.pll.creation.PartListCreatorUtil.toLocalDate;

public class PartChecker {

  private final Set<String> activeRuleIds;
  private final PartListCreationConfiguration config;
  private final List<PartFilter> filter;

  public PartChecker(PartListCreationConfiguration config, PrNumberRuleReader reader) {
    this.config = config;
    this.filter = PartFilterFactory.getDefaultFilter(reader);
    this.activeRuleIds = reader.getValidRuleIds();
  }

  public boolean isRuleActive(String ruleId) {
    if (StringUtils.isEmpty(ruleId)) {
      return true;
    }
    return activeRuleIds.contains(ruleId);
  }

  public static boolean containsPrNumber(Set<String> prNumbers, String partRule) {
    if (StringUtils.isEmpty(partRule)) {
      return false;
    }

    for (String prNumber : prNumbers) {
      if (partRule.contains(prNumber)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if the given rule string matches to the set of pr numbers
   *
   * @param selectedPrNumbers the selected pr numbers
   * @param prNumberRule      the rule to check
   * @return true if valid rule
   */
  public static boolean matchesRule(Set<String> selectedPrNumbers, String prNumberRule) {
    String[] split = prNumberRule.split("\\+");
    for (String rulePart : split) {
      rulePart = rulePart.trim();
      if (StringUtils.isNotEmpty(rulePart)) {
        if (isOrRule(rulePart)) {
          if (!checkOr(selectedPrNumbers, rulePart)) {
            return false;
          }
        } else {
          if (!selectedPrNumbers.contains(rulePart)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Check if part is Or-Rule
   *
   * @param part part to check
   * @return true if it is an OR-rulepart (contains '/')
   */
  private static boolean isOrRule(String part) {
    return part.contains("/");
  }

  /**
   * Check if one is valid
   *
   * @param selectedPrNumbers the selected pr numbers
   * @param multi             the OR - rule part
   * @return true if one is selected
   */
  private static boolean checkOr(Set<String> selectedPrNumbers, String multi) {
    String[] parts = multi.split("/");
    for (String part : parts) {
      if (selectedPrNumbers.contains(part.trim())) {
        return true;
      }
    }
    return false;
  }

  public PartFilterResult checkEbomFilterAndDate(String[] partData, String[] nodeData) {
    for (PartFilter pf : filter) {
      PartFilterResult check = pf.check(nodeData, partData, null);
      if (check.isFilteredOut()) {
        return check;
      }
    }
    return checkValidDate(partData, EbomFields.PART_NUMBER, EbomFields.EINSATZ_DATE, EbomFields.ENTFALL_DATE);
  }

  public PartFilterResult checkEbkPartRelevant(String[] data) {
    for (PartFilter pf : filter) {
      PartFilterResult check = pf.check(null, null, data);
      if (check.isFilteredOut()) {
        return check;
      }
    }
    return checkValidDate(data, EbkVsdFields.BAUKASTEN_PARTNUMBER, EbkVsdFields.EINSATZ_DATE, EbkVsdFields.ENTFALL_DATE);
  }

  public PartFilterResult checkValidDate(String[] data, PPFField partNumberField, PPFField einsatzField, PPFField entfallField) {
    String partNumber = getPartData(data, partNumberField);
    if (StringUtils.isEmpty(partNumber)) {
      return PartFilterResult.filteredOut(FilterType.FILTER_EMPTY_PARTNUMBER.getKey());
    }

    LocalDate validDate = config.getValidDate();

    String einsatzStr = getPartData(data, einsatzField);
    LocalDate einsatz = toLocalDate(einsatzStr);
    if (einsatz != null && validDate.isBefore(einsatz)) {
      return PartFilterResult.filteredOut(FilterType.FILTER_VALID_DATE_BEFORE_EINSATZ.getKey());
    }

    String entfallStr = getPartData(data, entfallField);
    LocalDate entfall = toLocalDate(entfallStr);
    if (entfall == null) {
      return PartFilterResult.notFilteredOut();
    }
    boolean valid = !validDate.isEqual(entfall) && !validDate.isAfter(entfall);
    return PartFilterResult.toResult(!valid, FilterType.FILTER_VALID_DATE_NOT_BETWEEN_EINSATZ_ENTFALL.getKey());
  }
}
