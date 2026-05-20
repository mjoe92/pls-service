package de.vw.paso.client.stueckliste.fzgkonfig.konfigkontroller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberFamilyDTO;

class PrNumberMapTestUtil {

    static Map<String, PrNumberDTO> filterMapAndReturnFilteredMap(Map<String, PrNumberDTO> fullPrNumbermap,
        Collection<String> modelPrNumberKeySet, Collection<PrNumberFamilyDTO> completePrNumberFamilyList,
        Map<String, Collection<PrNumberDTO>> selectedPrNumbers) {
        Map<String, PrNumberDTO> filteredPrNumberMap = new HashMap<>();

        for (PrNumberFamilyDTO family : completePrNumberFamilyList) {
            Collection<PrNumberDTO> prNumbersWithSalesSetting = new ArrayList<>();
            boolean noSalesSetting = false;
            int[] numberOfSelectedPrNumbers = { 0 };

            for (PrNumberDTO prNumber : family.prNumbers()) {

                String number = prNumber.name();

                if (number != null) {
                    if (modelPrNumberKeySet.contains(number)) {
                        prNumbersWithSalesSetting.add(prNumber);

                    } else if (selectedPrNumbers.get(family.name()) != null && fullPrNumbermap.containsKey(number)) {

                        selectedPrNumbers.get(family.name()).forEach(prNumberListItem -> {
                            if (prNumberListItem.name().matches(number)) {
                                prNumbersWithSalesSetting.add(prNumber);
                                numberOfSelectedPrNumbers[0]++;
                            }
                        });
                    }
                }
            }
            // calculation to see if the PR name has no salessetting
            if (numberOfSelectedPrNumbers[0] > 0
                && prNumbersWithSalesSetting.size() - numberOfSelectedPrNumbers[0] == 0) {
                noSalesSetting = true;
            }
            Collection<PrNumberDTO> selectedPRNumber = null;
            if (selectedPrNumbers.get(family.name()) != null) {
                selectedPRNumber = selectedPrNumbers.get(family.name());
            }
            if (!prNumbersWithSalesSetting.isEmpty()) {
                for (PrNumberDTO prNumber : prNumbersWithSalesSetting) {
                    filteredPrNumberMap.put(prNumber.name(), fullPrNumbermap.get(prNumber.name()));
                }
                Boolean[] isSelectedInModel = { false };
                if (selectedPRNumber != null) {
                    selectedPRNumber.forEach(selectedNumber -> {
                        if (modelPrNumberKeySet.contains(selectedNumber.name())) {
                            isSelectedInModel[0] = true;
                        }
                    });
                }
                if (!isSelectedInModel[0]) {
                    for (PrNumberDTO e : family.prNumbers()) {
                        if (fullPrNumbermap.containsKey(e.name()) && noSalesSetting) {
                            filteredPrNumberMap.put(e.name(), fullPrNumbermap.get(e.name()));
                        }
                    }
                }
            } else {
                for (PrNumberDTO e : family.prNumbers()) {
                    if (fullPrNumbermap.containsKey(e.name())) {
                        filteredPrNumberMap.put(e.name(), fullPrNumbermap.get(e.name()));
                    }
                }
            }
        }

        return filteredPrNumberMap;
    }

    static List<PrNumberDTO> findInvalidPrNumbers(Map<String, PrNumberDTO> prNumberMap, Date validDate) {
        List<PrNumberDTO> invalidPrNumbers = new ArrayList<>();

        for (String aKeySet : prNumberMap.keySet()) {
            PrNumberDTO pr = prNumberMap.get(aKeySet);
            if (pr == null) {
                continue;
            }

            Date startDate = pr.startDate();
            Date endDate = pr.endDate();
            if (startDate == null || startDate.after(validDate) || (endDate != null && validDate.after(endDate))) {
                invalidPrNumbers.add(pr);
            }
        }

        return invalidPrNumbers;
    }
}
