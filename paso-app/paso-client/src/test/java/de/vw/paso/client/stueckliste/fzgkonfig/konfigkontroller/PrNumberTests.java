package de.vw.paso.client.stueckliste.fzgkonfig.konfigkontroller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberFamilyDTO;
import de.vw.paso.utility.StringConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrNumberTests {

    private final Map<String, PrNumberDTO> fullPrNumbermap;
    private final Collection<String> modelPrNumberList;
    private final List<PrNumberFamilyDTO> completePrNumberFamilyList;
    private final Map<String, Collection<PrNumberDTO>> selectedPrNumbers;
    private final Date validDate; //September 13, 2019

    private Map<String, PrNumberDTO> filteredMap;
    private Map<String, PrNumberDTO> filteredMapWithoutSelection;

    public PrNumberTests() {
        fullPrNumbermap = new HashMap<>();
        modelPrNumberList = new ArrayList<>();
        completePrNumberFamilyList = new ArrayList<>();
        selectedPrNumbers = new HashMap<>();
        validDate = new Date(Math.abs(1568332800000L));
    }

    @BeforeEach
    public void setUp() {
        createPrFamilies();
        createAndAllocatePrNumbers();
        fillFullPrNumberMap();
        defineModelPrNumberList();
        defineSelectedPrNumbers();
        filteredMap = PrNumberMapTestUtil.filterMapAndReturnFilteredMap(fullPrNumbermap, modelPrNumberList,
                completePrNumberFamilyList, selectedPrNumbers);
        filteredMapWithoutSelection = PrNumberMapTestUtil.filterMapAndReturnFilteredMap(fullPrNumbermap,
                new ArrayList<>(), completePrNumberFamilyList, selectedPrNumbers);
    }

    private void createPrFamilies() {
        PrNumberFamilyDTO familyLEPOnly = createPrNumberFamily("familyLEPOnly", StringConstant.EMPTY);
        completePrNumberFamilyList.add(familyLEPOnly);

        PrNumberFamilyDTO familyLEPAnd2WithoutSalesSettings = createPrNumberFamily("familyLEPand2WithoutSalesSettings",
                StringConstant.EMPTY);
        completePrNumberFamilyList.add(familyLEPAnd2WithoutSalesSettings);

        PrNumberFamilyDTO mixedFamilyWithSelection = createPrNumberFamily(
                "familyLEPand2WithoutSalesSettingsAndSelection", StringConstant.EMPTY);
        completePrNumberFamilyList.add(mixedFamilyWithSelection);

        PrNumberFamilyDTO familyWithoutSalesSettings = createPrNumberFamily("familyWithoutSalesSettings",
                StringConstant.EMPTY);
        completePrNumberFamilyList.add(familyWithoutSalesSettings);

        PrNumberFamilyDTO emptyFamilyNoSalesSettingsWithSelection = createPrNumberFamily(
                "familyWithoutSalesSettingsAndSelection", StringConstant.EMPTY);
        completePrNumberFamilyList.add(emptyFamilyNoSalesSettingsWithSelection);
    }

    private void createAndAllocatePrNumbers() {
        Collection<PrNumberDTO> tempPrNumberList = new ArrayList<>();
        PrNumberFamilyDTO first = completePrNumberFamilyList.getFirst();
        tempPrNumberList.add(createPrNumber("LEPFamilyL", null, null, first));
        tempPrNumberList.add(createPrNumber("LEPFamilyE", null, null, first));
        tempPrNumberList.add(createPrNumber("LEPFamilyP", null, null, first));
        for (PrNumberDTO prNumber : tempPrNumberList) {
            first.prNumbers().add(prNumber);
        }

        tempPrNumberList.clear();

        PrNumberFamilyDTO second = completePrNumberFamilyList.get(1);
        tempPrNumberList.add(createPrNumber("MixedFamilyL", null, null, second));
        tempPrNumberList.add(createPrNumber("MixedFamilyE", null, null, second));
        tempPrNumberList.add(createPrNumber("MixedFamilyP", null, null, second));
        tempPrNumberList.add(createPrNumber("MixedFamilyEmpty1", null, null, second));
        tempPrNumberList.add(createPrNumber("MixedFamilyEmpty2", null, null, second));
        for (PrNumberDTO prNumber : tempPrNumberList) {
            second.prNumbers().add(prNumber);
        }

        tempPrNumberList.clear();

        PrNumberFamilyDTO third = completePrNumberFamilyList.get(2);
        tempPrNumberList.add(createPrNumber("MixedFamilyWithSelectionL", null, null, third));
        tempPrNumberList.add(createPrNumber("MixedFamilyWithSelectionE", null, null, third));
        tempPrNumberList.add(createPrNumber("MixedFamilyWithSelectionP", null, null, third));
        tempPrNumberList.add(createPrNumber("MixedFamilyEmptyWithSelection1", null, null, third));
        tempPrNumberList.add(createPrNumber("MixedFamilyEmptyWithSelection2", null, null, third));
        for (PrNumberDTO prNumber : tempPrNumberList) {
            third.prNumbers().add(prNumber);
        }

        tempPrNumberList.clear();

        PrNumberFamilyDTO fourth = completePrNumberFamilyList.get(3);
        tempPrNumberList.add(createPrNumber("EmptyFamily1", null, null, fourth));
        tempPrNumberList.add(createPrNumber("EmptyFamily2", null, null, fourth));
        for (PrNumberDTO prNumber : tempPrNumberList) {
            fourth.prNumbers().add(prNumber);
        }

        tempPrNumberList.clear();

        PrNumberFamilyDTO fifth = completePrNumberFamilyList.get(4);
        tempPrNumberList.add(createPrNumber("EmptyFamilyWithSelection1", null, null, fifth));
        tempPrNumberList.add(createPrNumber("EmptyFamily2WithSelection", null, null, fifth));

        for (PrNumberDTO prNumber : tempPrNumberList) {
            fifth.prNumbers().add(prNumber);
        }

        tempPrNumberList.clear();
    }

    private void fillFullPrNumberMap() {
        for (PrNumberFamilyDTO family : completePrNumberFamilyList) {
            Collection<PrNumberDTO> prNumbers = family.prNumbers();
            for (PrNumberDTO number : prNumbers) {
                fullPrNumbermap.put(number.name(), number);
            }
        }
    }

    private void defineModelPrNumberList() {
        modelPrNumberList.add("LEPFamilyL");
        modelPrNumberList.add("LEPFamilyE");
        modelPrNumberList.add("LEPFamilyP");
        modelPrNumberList.add("MixedFamilyL");
        modelPrNumberList.add("MixedFamilyE");
        modelPrNumberList.add("MixedFamilyP");
        modelPrNumberList.add("MixedFamilyWithSelectionL");
        modelPrNumberList.add("MixedFamilyWithSelectionE");
        modelPrNumberList.add("MixedFamilyWithSelectionP");
    }

    private void defineSelectedPrNumbers() {
        Collection<PrNumberDTO> prNumbersOfFamilyWith2SelectedEntries = new ArrayList<>();
        prNumbersOfFamilyWith2SelectedEntries.add(fullPrNumbermap.get("MixedFamilyEmptyWithSelection1"));
        prNumbersOfFamilyWith2SelectedEntries.add(fullPrNumbermap.get("MixedFamilyEmptyWithSelection2"));
        Collection<PrNumberDTO> prNumbersOfFamilyWith1SelectedEntries = new ArrayList<>();
        prNumbersOfFamilyWith1SelectedEntries.add(fullPrNumbermap.get("EmptyFamily2WithSelection"));
        selectedPrNumbers.put("familyLEPand2WithoutSalesSettingsAndSelection", prNumbersOfFamilyWith2SelectedEntries);
        selectedPrNumbers.put("familyWithoutSalesSettingsAndSelection", prNumbersOfFamilyWith1SelectedEntries);
    }

    @Test
    public void testFullMapNotNullOrEmpty() {
        assertNotNull("full PrNumberMap should not be null", fullPrNumbermap);
        assertFalse("The full PrNumberMap should not be empty", fullPrNumbermap.isEmpty());
    }

    @Test
    public void testFilteredMapNotNullOrEmpty() {
        assertNotNull("Filtered PrNumberMap should not be null", filteredMap);
        assertFalse("The filtered PrNumberMap should not be empty", filteredMap.isEmpty());
    }

    @Test
    public void testFilteredMapNotEqualFullMapWithSalesSetting() {
        assertNotEquals(fullPrNumbermap.size(), filteredMap.size());
    }

    @Test
    public void testFilteredAndFullMapEqualWithoutSalesSetting() {
        assertEquals(filteredMapWithoutSelection.size(), fullPrNumbermap.size());
    }

    @Test
    public void testExpectedNumberOfEntriesInFilteredMap() {
        assertEquals(15, filteredMap.size());
    }

    @Test
    public void testAllPrNumbersWithSalesSetting() {
        boolean allPrNumbersPresent = false;
        for (PrNumberDTO prNumber : completePrNumberFamilyList.getFirst().prNumbers()) {
            allPrNumbersPresent = filteredMap.containsKey(prNumber.name());
        }

        assertTrue(allPrNumbersPresent);
    }

    @Test
    public void testPrNumbersWithAndWithoutSalesSettings() {
        boolean allPrNumbersWithSalesSettingPresentPresent = false;
        int numberOfPrNumbersOfFamilyInFilteredMap = 0;
        for (PrNumberDTO e : completePrNumberFamilyList.get(1).prNumbers()) {

            if (filteredMap.containsKey(e.name())) {
                allPrNumbersWithSalesSettingPresentPresent = filteredMap.containsKey(e.name());
                numberOfPrNumbersOfFamilyInFilteredMap++;
            }
        }

        assertTrue(allPrNumbersWithSalesSettingPresentPresent);
        assertEquals(3, numberOfPrNumbersOfFamilyInFilteredMap);
    }

    @Test
    public void testSelectedPRNinFamilyWithSalesSetting() {
        boolean allPrNumbersWithSalesSettingPresentPresent = false;
        int numberOfPrNumbersOfFamilyInFilteredMap = 0;
        for (PrNumberDTO e : completePrNumberFamilyList.get(2).prNumbers()) {

            if (filteredMap.containsKey(e.name())) {
                allPrNumbersWithSalesSettingPresentPresent = filteredMap.containsKey(e.name());
                numberOfPrNumbersOfFamilyInFilteredMap++;
            }
        }

        assertTrue(allPrNumbersWithSalesSettingPresentPresent);
        assertEquals(5, numberOfPrNumbersOfFamilyInFilteredMap);
    }

    @Test
    public void testSelectedPRNinFamilyWithoutSalesSetting() {
        boolean allPrNumbersWithSalesSettingPresentPresent = false;
        int numberOfPrNumbersOfFamilyInFilteredMap = 0;
        for (PrNumberDTO e : completePrNumberFamilyList.get(3).prNumbers()) {

            if (filteredMap.containsKey(e.name())) {
                allPrNumbersWithSalesSettingPresentPresent = filteredMap.containsKey(e.name());
                numberOfPrNumbersOfFamilyInFilteredMap++;
            }
        }

        assertTrue(allPrNumbersWithSalesSettingPresentPresent);
        assertEquals(2, numberOfPrNumbersOfFamilyInFilteredMap);
    }

    @Test
    public void testPrFamilyWithoutSalesSettingOrSelection() {
        boolean allPrNumbersWithSalesSettingPresentPresent = false;
        int numberOfPrNumbersOfFamilyInFilteredMap = 0;
        for (PrNumberDTO e : completePrNumberFamilyList.get(4).prNumbers()) {

            if (filteredMap.containsKey(e.name())) {
                allPrNumbersWithSalesSettingPresentPresent = filteredMap.containsKey(e.name());
                numberOfPrNumbersOfFamilyInFilteredMap++;
            }
        }

        assertTrue(allPrNumbersWithSalesSettingPresentPresent);
        assertEquals(2, numberOfPrNumbersOfFamilyInFilteredMap);
    }

    @Test
    public void testEinsatzIsNull() {
        PrNumberFamilyDTO family = createPrNumberFamily("family", null);
        PrNumberDTO prNumber = createPrNumber("prNumber", null, null, family);

        Map<String, PrNumberDTO> map = Map.of(prNumber.name(), prNumber);
        Collection<PrNumberDTO> invalidPrNumbers = PrNumberMapTestUtil.findInvalidPrNumbers(map, validDate);
        assertEquals(1, invalidPrNumbers.size());

    }

    @Test
    public void testEinsatzIsInvalid() {
        Date startDate = new Date(Math.abs(1568419200000L)); //September 14, 2019 - invalid
        PrNumberFamilyDTO family = createPrNumberFamily("family", null);
        PrNumberDTO prNumber = createPrNumber("prNumber", startDate, null, family);

        Map<String, PrNumberDTO> map = Map.of(prNumber.name(), prNumber);
        Collection<PrNumberDTO> invalidPrNumbers = PrNumberMapTestUtil.findInvalidPrNumbers(map, validDate);
        assertEquals(1, invalidPrNumbers.size());
    }

    @Test
    public void testEntfallIsNull() {
        Date startDate = new Date(Math.abs(1568246400000L)); // September 12, 2019 valid
        PrNumberFamilyDTO family = createPrNumberFamily("family", null);
        PrNumberDTO prNumber = createPrNumber("prNumber", startDate, null, family);

        Map<String, PrNumberDTO> map = Map.of(prNumber.name(), prNumber);
        Collection<PrNumberDTO> invalidPrNumbers = PrNumberMapTestUtil.findInvalidPrNumbers(map, validDate);
        assertEquals(0, invalidPrNumbers.size());
    }

    @Test
    public void testEntfallIsInvalid() {
        Date startDate = new Date(Math.abs(1568246400000L)); // September 12, 2019 - valid
        Date endDate = new Date(Math.abs(1568246400000L)); // September 12, 2019 - invalid
        PrNumberFamilyDTO family = createPrNumberFamily("family", null);
        PrNumberDTO prNumber = createPrNumber("prNumber", startDate, endDate, family);

        Map<String, PrNumberDTO> map = Map.of(prNumber.name(), prNumber);
        Collection<PrNumberDTO> invalidPrNumbers = PrNumberMapTestUtil.findInvalidPrNumbers(map, validDate);
        assertEquals(1, invalidPrNumbers.size());
    }

    @Test
    public void testEinsatzAndEntfallValid() {
        Date startDate = new Date(Math.abs(1568246400000L)); // September 12, 2019 - valid
        Date endDate = new Date(Math.abs(1568419200000L)); // September 12, 2019 - valid
        PrNumberFamilyDTO family = createPrNumberFamily("family", null);
        PrNumberDTO prNumber = createPrNumber("prNumber", startDate, endDate, family);

        Map<String, PrNumberDTO> map = Map.of(prNumber.name(), prNumber);
        Collection<PrNumberDTO> invalidPrNumbers = PrNumberMapTestUtil.findInvalidPrNumbers(map, validDate);
        assertEquals(0, invalidPrNumbers.size());
    }

    private PrNumberDTO createPrNumber(String prNumber, Date startDate, Date endDate,
            PrNumberFamilyDTO prNumberFamily) {
        return new PrNumberDTO(null, null, prNumber, null, null, startDate, null, endDate, null, null, prNumberFamily);
    }

    private PrNumberFamilyDTO createPrNumberFamily(String family, String description) {
        return new PrNumberFamilyDTO(null, family, description, new ArrayList<>(5));
    }
}
