package de.vw.paso.pll.creation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.vw.paso.pll.model.PlsEfsElement;
import org.apache.commons.lang3.StringUtils;

public final class WahlweiseUtil {

  private WahlweiseUtil() {
    // noop
  }

  public static void handleWahlweiseFall(List<PlsEfsElement> possibleElements) {
    Map<String, List<PlsEfsElement>> wahlweiseFallToElements = new HashMap<>();
    for (PlsEfsElement ele : possibleElements) {
      if (StringUtils.isNotEmpty(getWahlweiseFall(ele))) {
        wahlweiseFallToElements.computeIfAbsent(ele.getWahlweiseFall(), a -> new ArrayList<>()).add(ele);
      }
    }

    for (Entry<String, List<PlsEfsElement>> entry : wahlweiseFallToElements.entrySet()) {
      List<PlsEfsElement> elementsForWahlweiseFall = entry.getValue();
      Map<Integer, List<PlsEfsElement>> elementsByNr = new HashMap<>();
      for (PlsEfsElement plsEfsElement : elementsForWahlweiseFall) {
        elementsByNr.computeIfAbsent(plsEfsElement.getWahlweiseNr(), a -> new ArrayList<>()).add(plsEfsElement);
      }

      List<Integer> wahlweiseNrList = elementsByNr.keySet().stream()
        .sorted(Comparator.nullsLast(Comparator.naturalOrder())).toList();
      List<PlsEfsElement> chosenElements = elementsByNr.get(wahlweiseNrList.getFirst());

      wahlweiseNrList.stream().skip(1).forEach(wahlweiseNr -> {
        List<PlsEfsElement> elementsWithSameWahlweiseNr = elementsByNr.get(wahlweiseNr);
        possibleElements.removeAll(elementsWithSameWahlweiseNr);

        if (elementsWithSameWahlweiseNr.size() == 1) {
          for (PlsEfsElement element : elementsWithSameWahlweiseNr) {
            element.getChildren().stream().filter(e -> !e.isEbk())
              .forEach(e -> chosenElements.getFirst().getChildren().add(e));
          }
        }
      });
    }
    for (PlsEfsElement e : possibleElements) {
      handleWahlweiseFall(e.getChildren());
    }
  }

  public static String getWahlweiseFall(PlsEfsElement element) {
    String wahlweiseFall = element.getWahlweiseFall();
    if (StringUtils.isEmpty(wahlweiseFall)) {
      return null;
    }
    if (wahlweiseFall.equals("0000")) {
      Integer wahlweiseNr = element.getWahlweiseNr();
      if (wahlweiseNr != null && wahlweiseNr == 0) {
        return null;
      }
    }
    return wahlweiseFall;
  }
}
