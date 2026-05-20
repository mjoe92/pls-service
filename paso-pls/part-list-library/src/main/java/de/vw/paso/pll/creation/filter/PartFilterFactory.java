package de.vw.paso.pll.creation.filter;

import de.vw.paso.pll.creation.PartChecker;
import de.vw.paso.pll.creation.PrNumberRuleReader;
import de.vw.paso.pll.model.FilterType;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbkVsdFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbomFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.NodeFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.vw.paso.pll.creation.PartListCreatorUtil.getPartData;
import static de.vw.paso.pll.creation.filter.PartFilterContainer.Mode.OR;
import static de.vw.paso.pll.creation.filter.PartFilterResult.toResult;

public class PartFilterFactory {

  public static List<PartFilter> getDefaultFilter(PrNumberRuleReader reader) {
    List<PartFilter> partFIlerList = new ArrayList<>();

    //Rules adapted from veron
    PartFilterContainer ausgleichgewichteFilter = new PartFilterContainer(FilterType.FILTER_AUSGLEICHGEWICHTE.getKey());
    ausgleichgewichteFilter.addPartilter(mittelGruppeContains("601"));
    ausgleichgewichteFilter.addPartilter(endnummerIsBetween(178, 200));
    ausgleichgewichteFilter.addPartilter(startsWith("AUSGLEICHGEWICHT", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    partFIlerList.add(ausgleichgewichteFilter);

    PartFilterContainer kundendienstteile = new PartFilterContainer(FilterType.FILTER_KUNDENDIENSTELLE.getKey());
    kundendienstteile.addPartilter(ruleContains(reader, "KD0"));
    partFIlerList.add(kundendienstteile);

    PartFilterContainer quantityUnitExtended = new PartFilterContainer(FilterType.FILTER_QUANTITY_UNIT_EXTENDED.getKey());
    PartFilterContainer sub = new PartFilterContainer(OR, FilterType.SUB_FILTER_MEE.getKey());
    sub.addPartilter(isIn(Arrays.asList("R", "E", "B"), EbomFields.QUANTITY_UNIT_EXTENDED, EbkVsdFields.QUANTITY_UNIT_ADDITION));
    sub.addPartilter(empty(EbomFields.QUANTITY_UNIT, EbkVsdFields.QUANTITY_UNIT));
    sub.addPartilter(equals("MM", EbomFields.QUANTITY_UNIT, EbkVsdFields.QUANTITY_UNIT));
    quantityUnitExtended.addPartilter(sub);
    quantityUnitExtended.addPartilter(isIn(Arrays.asList("Z_MTK", "Z_PBE", "BK"), NodeFields.NODE_TYPE));

    PartFilterContainer baukastenSUb = new PartFilterContainer(OR, FilterType.SUB_FILTER_BAUKASTEN.getKey());
    baukastenSUb.addPartilter(empty(EbomFields.BAUKASTEN_KZ));
    baukastenSUb.addPartilter(equalsNot(Arrays.asList("1", "X"), EbomFields.BAUKASTEN_KZ));
    quantityUnitExtended.addPartilter(baukastenSUb);

    partFIlerList.add(quantityUnitExtended);

    PartFilterContainer pflegeMittel = new PartFilterContainer(FilterType.FILTER_PFLEGEMITTEL.getKey());
    pflegeMittel.addPartilter(mittelGruppeContains("000"));
    pflegeMittel.addPartilter(startsWith("PFLEGE", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    partFIlerList.add(pflegeMittel);

    PartFilterContainer sammler000890 = new PartFilterContainer(OR, FilterType.FILTER_SAMMLER_000890.getKey(), true);
    sammler000890.addPartilter(equals("000890159", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    sammler000890.addPartilter(equals("000890159A", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    sammler000890.addPartilter(equals("000890159B", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    sammler000890.addPartilter(equals("000890159C", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    sammler000890.addPartilter(equals("000890159D", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    sammler000890.addPartilter(equals("000890159E", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    partFIlerList.add(sammler000890);

    PartFilterContainer software = new PartFilterContainer(OR, FilterType.FILTER_SOFTWARE.getKey());
    PartFilterContainer subSoftware = new PartFilterContainer(FilterType.SUB_RULE_SOFTWARE.getKey());
    subSoftware.addPartilter(partNumberInAt(Arrays.asList("909", "910"), 3, 3));
    subSoftware.addPartilter(startsWith("SOFTW", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    software.addPartilter(subSoftware);
    software.addPartilter(partNumberContainsAt("990201", 3));
    software.addPartilter(partNumberContainsAt("959800", 3));
    partFIlerList.add(software);

    PartFilterContainer teilenummernL = new PartFilterContainer(FilterType.FILTER_TEILENUMMER_L.getKey());
    teilenummernL.addPartilter(equals("Z_LAW", NodeFields.NODE_TYPE));

    PartFilterContainer verrechnungsnummer = new PartFilterContainer(FilterType.FILTER_VERRECHNUNGSNUMMER.getKey());
    verrechnungsnummer.addPartilter(equals("Verrechnungsnummer", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    partFIlerList.add(verrechnungsnummer);

    PartFilterContainer transportschutz = new PartFilterContainer(OR, FilterType.FILTER_TRANSPORTSCHUTZ.getKey(), true);
    transportschutz.addPartilter(contains("817685", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    transportschutz.addPartilter(contains("837804", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    transportschutz.addPartilter(contains("868395", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    transportschutz.addPartilter(contains("881173", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    transportschutz.addPartilter(startsWith("APS275 ", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    transportschutz.addPartilter(equals("000890158B", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    transportschutz.addPartilter(equals("000890259C", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    transportschutz.addPartilter(startsWith("PFLEGE", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    transportschutz.addPartilter(startsWith("SCHWELLERSCHUTZ", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    transportschutz.addPartilter(startsWith("SCHUTZBELAG TUER", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    transportschutz.addPartilter(startsWith("KANTENSCHUTZ", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    transportschutz.addPartilter(startsWith("SCHUTZFOLIE", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    transportschutz.addPartilter(startsWith("TRANSPORTSCHUTZ", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    partFIlerList.add(transportschutz);

    PartFilterContainer transportSicherung = new PartFilterContainer(FilterType.FILTER_TRANSPORTSICHERUNG.getKey(), true);
    transportSicherung.addPartilter(startsWith("TRANSPORT", EbomFields.TRANSLATION_GER, EbkVsdFields.TRANSLATION_GER));
    transportSicherung.addPartilter(partNumberContainsAt("616827", 3));
    partFIlerList.add(transportSicherung);

    //Process status
    PartFilterContainer precessStatus = new PartFilterContainer(OR, FilterType.FILTER_PROCESS_STATUS.getKey());
    precessStatus.addPartilter(isIn(Collections.singletonList("E"), EbomFields.PROCESS_STATUS));
    partFIlerList.add(precessStatus);

    //Paso specific rules
    PartFilterContainer pasoRules = new PartFilterContainer(OR, FilterType.FILTER_PASO_RULES.getKey());
    pasoRules.addPartilter(startsWith("V03", EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER));
    pasoRules.addPartilter(contains("transportschutz", NodeFields.NODE_LABEL));
    pasoRules.addPartilter(isIn(Arrays.asList("B", "R", "E"), EbomFields.QUANTITY_UNIT));
    pasoRules.addPartilter(isNotANumber(EbomFields.PART_NUMBER_MITTELGRUPPE));
    partFIlerList.add(pasoRules);

    return partFIlerList;
  }

  private static PartFilter mittelGruppeContains(String mg) {
    return partNumberContainsAt(mg, 3);
  }

  private static PartFilter partNumberContainsAt(String txt, int pos) {
    return (nodeData, partData, ebkData) -> {
      PPFField[] fields = {EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER};
      for (PPFField field : fields) {
        String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
        if (dataToCheck == null) {
          continue;
        }
        String partNumber = getPartData(dataToCheck, field);
        if (StringUtils.isNotEmpty(partNumber)) {
          String sub = partNumber.substring(pos);
          PartFilterResult result = toResult(sub.startsWith(txt), "Partnumber contains '" + txt + "' at index " + pos);
          if (result.isFilteredOut()) {
            return result;
          }
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }


  private static PartFilter partNumberInAt(List<String> in, int pos, int lengh) {
    return (nodeData, partData, ebkData) -> {
      PPFField[] fields = {EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER};
      for (PPFField field : fields) {
        String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
        if (dataToCheck == null) {
          continue;
        }
        String partNumber = getPartData(dataToCheck, field);
        if (StringUtils.isNotEmpty(partNumber)) {
          String sub = partNumber.substring(pos, pos + lengh);
          PartFilterResult result = toResult(in.contains(sub), "Partnumber contains one of " + in + " at index " + pos);
          if (result.isFilteredOut()) {
            return result;
          }
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter endnummerIsBetween(int start, int end) {
    return (nodeData, partData, ebkData) -> {
      PPFField[] fields = {EbomFields.PART_NUMBER, EbkVsdFields.BAUKASTEN_PARTNUMBER};
      for (PPFField field : fields) {
        String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
        if (dataToCheck == null) {
          continue;
        }
        String partNumber = getPartData(dataToCheck, EbomFields.PART_NUMBER);

        if (StringUtils.isNotEmpty(partNumber)) {
          String endnummer = partNumber.substring(6, 9);
          try {
            int i = Integer.parseInt(endnummer);
            PartFilterResult result = toResult(i > start && i < end, "Endnummer is between " + start + " and " + end);
            if (result.isFilteredOut()) {
              return result;
            }
          } catch (NumberFormatException xe) {
            //PArtnumbers can contain letters. So we jsut try to convert it and ignore possible exceptions.
          }
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter equals(String text, PPFField... fields) {
    return (nodeData, partData, ebkData) -> {
      for (PPFField field : fields) {
        String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
        if (dataToCheck == null) {
          continue;
        }
        PartFilterResult result = toResult(text.equals(getPartData(dataToCheck, field)), field + " equals '" + text + "'");
        if (result.isFilteredOut()) {
          return result;
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter equalsNot(List<String> texts, PPFField field) {
    return (nodeData, partData, ebkData) -> {
      String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
      if (dataToCheck != null) {
        String data = getPartData(dataToCheck, field);
        PartFilterResult result = toResult(!texts.contains(data), field + " is not in '" + texts + "'");
        if (result.isFilteredOut()) {
          return result;
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter equalsNot(String text, PPFField... fields) {
    return (nodeData, partData, ebkData) -> {
      for (PPFField field : fields) {
        String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
        if (dataToCheck == null) {
          continue;
        }
        PartFilterResult result = toResult(!text.equals(getPartData(dataToCheck, field)), field + " equals not '" + text + "'");
        if (result.isFilteredOut()) {
          return result;
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter startsWith(String txt, PPFField... fields) {
    return (nodeData, partData, ebkData) -> {
      for (PPFField field : fields) {
        String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
        if (dataToCheck == null) {
          continue;
        }
        String data = getPartData(dataToCheck, field);
        if (data != null) {
          PartFilterResult result = toResult(data.startsWith(txt), field + " starts with '" + txt + "'");
          if (result.isFilteredOut()) {
            return result;
          }
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter isIn(List<String> list, PPFField... fields) {
    return (nodeData, partData, ebkData) -> {
      for (PPFField field : fields) {
        String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
        if (dataToCheck == null) {
          continue;
        }
        String data = getPartData(dataToCheck, field);
        if (data != null) {
          PartFilterResult result = toResult(list.contains(data), field + " is one of:" + list);
          if (result.isFilteredOut()) {
            return result;
          }
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter empty(PPFField... fields) {
    return (nodeData, partData, ebkData) -> {
      for (PPFField field : fields) {
        String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
        if (dataToCheck == null) {
          continue;
        }
        String data = getPartData(dataToCheck, field);
        if (data != null) {
          PartFilterResult result = toResult(StringUtils.isEmpty(data), field + " is empty");
          if (result.isFilteredOut()) {
            return result;
          }
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter contains(String txt, PPFField... fields) {
    return (nodeData, partData, ebkData) -> {
      for (PPFField field : fields) {
        String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
        if (dataToCheck == null) {
          continue;
        }
        String data = getPartData(dataToCheck, field);
        if (data != null) {
          PartFilterResult result = toResult(data.contains(txt), field + " contains:" + txt);
          if (result.isFilteredOut()) {
            return result;
          }
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter ruleContains(PrNumberRuleReader reader, String prNumber) {
    return (nodeData, ebomData, ebkData) -> {
      PPFField[] ruleFields = new PPFField[]{EbomFields.RULE_ID, EbkVsdFields.PR_NUMBER_RULE_ID};
      for (PPFField ruleField : ruleFields) {
        String[] dataToCheck = getDataToCheck(nodeData, ebomData, ebkData, ruleField);
        if (dataToCheck == null) {
          continue;
        }
        String ruleId = getPartData(dataToCheck, ruleField);
        if (ruleId != null) {
          String rule = reader.getRuleForId(ruleId);
          if (StringUtils.isNotEmpty(rule)) {
            boolean isFilteredOut = PartChecker.containsPrNumber(Collections.singleton(prNumber), rule);
            return PartFilterResult.toResult(isFilteredOut, "PR Numbers contain '" + rule + "'");
          }
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static PartFilter isNotANumber(PPFField field) {
    return (nodeData, partData, ebkData) -> {
      String[] dataToCheck = getDataToCheck(nodeData, partData, ebkData, field);
      if (dataToCheck != null) {
        String data = getPartData(dataToCheck, field);
        if (data != null) {
          return toResult(!StringUtils.isNumeric(data), field + " is not a number");
        }
      }
      return PartFilterResult.notFilteredOut();
    };
  }

  private static String[] getDataToCheck(String[] nodeData, String[] partData, String[] ebkData, PPFField field) {
    if (field instanceof NodeFields) {
      return nodeData;
    } else if (field instanceof EbomFields) {
      return partData;
    } else if (field instanceof EbkVsdFields) {
      return ebkData;
    } else {
      throw new RuntimeException();
    }
  }
}
