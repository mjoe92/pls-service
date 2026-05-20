package de.vw.paso.pll.creation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.vw.paso.pll.PPFUtil;
import org.apache.commons.lang3.StringUtils;

public class PrNumberRuleReader {

  private Set<String> validRuleIds = new HashSet<>();
  private Map<String, String> ruleByID = new HashMap<>();

  void readRules(PartListCreationConfiguration config, Iterator<String> lines) {
    Set<String> selectedPrNumbers = new HashSet<>();

    if (config != null) {
      selectedPrNumbers = getSelectedPrNumbers(config);
    }

    while (lines.hasNext()) {
      String line = lines.next();
      if (line.equals(PPFUtil.SECTION_SEPARATOR)) {
        break;
      }

      line = line.trim();
      if (StringUtils.isNotEmpty(line)) {
        String[] data = line.split(":");
        String ruleId = getRuleId(data);
        String rule = getRule(data);
        ruleByID.put(ruleId, rule);
        if (!selectedPrNumbers.isEmpty()) {
          if (isActiveRule(selectedPrNumbers, rule)) {
            validRuleIds.add(ruleId);
          }
        }
      }
    }
  }

  private String getRuleId(String[] data) {
    return data[0];
  }

  private String getRule(String[] data) {
    if (data.length < 2) {
      return null;
    }
    return data[1];
  }

  private Set<String> getSelectedPrNumbers(PartListCreationConfiguration config) {
    String prNumberConfig = config.getPrNumberConfig();
    String[] split = prNumberConfig.split(" ");
    return new HashSet<>(Arrays.asList(split));
  }

  /**
   * Check if the given rule string is valid. For this it uses the selected pr number set. Empty rule is valid by default.
   * @param selectedPrNumbers the selected pr numbers
   * @param rule the rule to check
   * @return true if valid rule
   */
  private boolean isActiveRule(Set<String> selectedPrNumbers, String rule) {
    //Empty rule is valid
    if (StringUtils.isEmpty(rule)) {
      return true;
    }
    return PartChecker.matchesRule(selectedPrNumbers, rule);
  }

  public Set<String> getValidRuleIds() {
    return validRuleIds;
  }

  public String getRuleForId(String id) {
    return ruleByID.get(id);
  }

  public boolean isValidRuleId(String ruleId) {
    return validRuleIds.contains(ruleId);
  }
}
