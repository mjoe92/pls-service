package de.vw.paso.client.stueckliste.efs.inspector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.util.QuantityUnit;
import de.vw.paso.delegate.stueckliste.costgroup.CostGroupRestClientHolder;
import de.vw.paso.delegate.stueckliste.inspector.InspectorRestClientHolder;
import de.vw.paso.delegate.stueckliste.setkey.SetKeyRestClientHolder;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.partlist.dto.EfsElementAggregateMappingDTO;
import de.vw.paso.partlist.dto.EfsElementAggregateMappingListDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupListDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupRestService;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.partlist.inspector.InspectorRestService;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.partlist.setkey.SetKeyListDTO;
import de.vw.paso.service.partlist.setkey.SetKeyRestService;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.vehicle.domain.VehicleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InspectorTest {

    private static final String PARENT = "parent";
    private static final String CHILD = "child";

    static VehiclePartListDTO vehiclePartList;

    @BeforeEach
    public void setUp() {
        CostGroupRestService costGroupRestClientMock = mock(CostGroupRestService.class);
        when(costGroupRestClientMock.loadCostGroups(anyLong())).thenReturn(createDummyCostGroups());
        CostGroupRestClientHolder.setInstance(costGroupRestClientMock);

        SetKeyRestService setKeyRestClientMock = mock(SetKeyRestService.class);
        when(setKeyRestClientMock.loadSetKeys(anyLong())).thenReturn(createDummySetKeys());
        SetKeyRestClientHolder.setInstance(setKeyRestClientMock);

        VehicleProjectDTO vp = new VehicleProjectDTO();
        vp.setProductKey("5G0");
        vp.setBrandCode(Brand.VW);
        vp.setProjectName("VW 360/0 EU");

        VehicleConfigDTO newConfig = VehicleFactory.createFzgConfig("Test", vp);
        newConfig.setCostGroupVersion(1L);
        newConfig.setSetVersionId(1L);
        vehiclePartList = PartListFactory.createVehiclePartList(newConfig);
    }

    private static SetKeyListDTO createDummySetKeys() {
        List<SetKeyDTO> setKeys = new ArrayList<>(3);
        setKeys.add(new SetKeyDTO("SK", null, null, 1L));
        setKeys.add(new SetKeyDTO("SK0", null, null, 1L));
        setKeys.add(new SetKeyDTO("SK1", null, null, 1L));

        return new SetKeyListDTO(setKeys);

    }

    private static CostGroupListDTO createDummyCostGroups() {
        List<CostGroupDTO> cg = new ArrayList<>(3);
        cg.add(new CostGroupDTO("CG", 1L));
        cg.add(new CostGroupDTO("CG0", 1L));
        cg.add(new CostGroupDTO("CG1", 1L));

        return new CostGroupListDTO(cg);
    }

    @Test
    public void checkEverythingButWeightControlFlagIncorrectForDefaultElement() {
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", WeightControlFlag.YES, WeightControlFlag.NO,
                10D);
        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());
        assertEquals(0, entries.keys().stream().filter(type -> !InspectorEntryType.GWS_INCORRECT.equals(type)).count());
    }

    @Test
    public void checkRuleWeightSet() {
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", WeightControlFlag.NO, WeightControlFlag.YES,
                null);
        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());
        assertEquals(1, entries.size(), "Check for error count");
        InspectorEntryType firstEntryType = entries.keys().iterator().next();
        assertEquals(InspectorEntryType.GWS_INCORRECT, firstEntryType);
    }

    @Test
    public void checkRuleQuantityUnitSet() {
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", WeightControlFlag.YES, WeightControlFlag.NO,
                10D);
        elementMap.get(CHILD).setQuantityUnit(null);

        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());

        assertTrue(entries.keys().contains(InspectorEntryType.WEIGHT_BUT_NO_UNIT));
    }

    @Test
    public void checkRuleSetKeySet() {
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", null, WeightControlFlag.YES, WeightControlFlag.NO,
                10D);
        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());
        assertTrue(entries.keys().contains(InspectorEntryType.MISSING_SET_KEY));
    }

    @Test
    public void checkRuleUnknownSetKey() {
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "ABC", WeightControlFlag.YES, WeightControlFlag.NO,
                10D);
        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());
        assertTrue(entries.keys().contains(InspectorEntryType.UNKNOWN_SET_KEY));
    }

    @Test
    public void checkRuleCostGroupSet() {
        Map<String, EfsElementDTO> elementMap = createDefault(null, "SK0", WeightControlFlag.YES, WeightControlFlag.NO,
                10D);
        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());
        assertTrue(entries.keys().contains(InspectorEntryType.MISSING_COST_GROUP));
    }

    @Test
    public void checkRuleUnknownCostGroup() {
        Map<String, EfsElementDTO> elementMap = createDefault("ABC", "SK0", WeightControlFlag.YES, WeightControlFlag.NO,
                10D);
        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());
        assertTrue(entries.keys().contains(InspectorEntryType.UNKNOWN_COST_GROUP));
    }

    @Test
    public void checkRuleAPRule() {
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", null, null, 100D);
        elementMap.get(CHILD).setAp("*K");

        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());

        assertTrue(entries.containsKey(InspectorEntryType.UNKNOWN_AP));
        assertEquals(1, entries.get(InspectorEntryType.UNKNOWN_AP).size());
    }

    @Test
    public void checkRuleAggregateMissingGearboxAllSetNoMapping() {
        InspectorRestService service = InspectorRestClientHolder.getInstance();
        when(service.loadAggregateMapping(anyLong())).thenReturn(
                new EfsElementAggregateMappingListDTO(new ArrayList<>()));
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", null, null, 100D);
        EfsElementDTO childElement = elementMap.get(CHILD);
        childElement.getEfsElementMara().setDescription1De(EfsElementDTO.AGGREGAT_GETRIEBE);
        childElement.getEfsElementMara().setPartNumber("100100100");
        childElement.setAggregate("AAGR");

        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());

        assertTrue(entries.containsKey(InspectorEntryType.MISSING_AGGREGATE_GEARBOX));
        assertEquals(1, entries.get(InspectorEntryType.MISSING_AGGREGATE_GEARBOX).size());
    }

    @Test
    public void checkRuleAggregateMissingGearboxAllSetWithMapping() {
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", null, null, 100D);
        elementMap.get(CHILD).getEfsElementMara().setDescription1De(EfsElementDTO.AGGREGAT_GETRIEBE);

        EfsElementAggregateMappingDTO mapping = new EfsElementAggregateMappingDTO(elementMap.get(CHILD).getId(), null,
                null, null);
        InspectorRestService service = InspectorRestClientHolder.getInstance();
        when(service.loadAggregateMapping(any())).thenReturn(
                new EfsElementAggregateMappingListDTO(Collections.singletonList(mapping)));

        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());

        assertFalse(entries.containsKey(InspectorEntryType.MISSING_AGGREGATE_GEARBOX));
    }

    @Test
    public void checkRuleAggregateMissingEngineAllSetNoMapping() {
        InspectorRestService service = mock(InspectorRestService.class);
        when(service.loadAggregateMapping(anyLong())).thenReturn(
                new EfsElementAggregateMappingListDTO(new ArrayList<>()));
        InspectorRestClientHolder.setInstance(service);
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", null, null, 100D);
        EfsElementDTO childElement = elementMap.get(CHILD);
        childElement.getEfsElementMara().setDescription1De(EfsElementDTO.AGGREGAT_MOTOR);
        childElement.getEfsElementMara().setPartNumber("100100100");
        childElement.setAggregate("AAGR");

        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());

        assertTrue(entries.containsKey(InspectorEntryType.MISSING_AGGREGATE_ENGINE));
        assertEquals(1, entries.get(InspectorEntryType.MISSING_AGGREGATE_ENGINE).size());
    }

    @Test
    public void checkRuleAggregateMissingEngineAllSetWithMapping() {
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", null, null, 100D);
        EfsElementDTO childElement = elementMap.get(CHILD);
        childElement.getEfsElementMara().setDescription1De(EfsElementDTO.AGGREGAT_MOTOR);
        childElement.getEfsElementMara().setPartNumber("100100100");
        childElement.setAggregate("AAGR");

        EfsElementAggregateMappingDTO mapping = new EfsElementAggregateMappingDTO(childElement.getId(), null, null,
                null);
        InspectorRestService service = mock(InspectorRestService.class);
        when(service.loadAggregateMapping(anyLong())).thenReturn(
                new EfsElementAggregateMappingListDTO(Collections.singletonList(mapping)));
        InspectorRestClientHolder.setInstance(service);

        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());

        assertTrue(entries.containsKey(InspectorEntryType.MISSING_AGGREGATE_ENGINE));
        assertEquals(1, entries.get(InspectorEntryType.MISSING_AGGREGATE_ENGINE).size());
    }

    @Test
    public void checkRuleAggregateMissingEngineNoAggregateSet() {
        InspectorRestService service = InspectorRestClientHolder.getInstance();
        when(service.loadAggregateMapping(anyLong())).thenReturn(
                new EfsElementAggregateMappingListDTO(new ArrayList<>()));
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", null, null, 100D);
        EfsElementDTO childElement = elementMap.get(CHILD);
        childElement.getEfsElementMara().setDescription1De(EfsElementDTO.AGGREGAT_MOTOR);
        childElement.getEfsElementMara().setPartNumber("100100100");

        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());

        assertFalse(entries.containsKey(InspectorEntryType.MISSING_AGGREGATE_ENGINE));
    }

    @Test
    public void checkRuleAggregateMissingEngineWrongPartNumber() {
        InspectorRestService service = InspectorRestClientHolder.getInstance();
        when(service.loadAggregateMapping(anyLong())).thenReturn(
                new EfsElementAggregateMappingListDTO(new ArrayList<>()));
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", null, null, 100D);
        EfsElementDTO childElement = elementMap.get(CHILD);
        childElement.getEfsElementMara().setDescription1De(EfsElementDTO.AGGREGAT_MOTOR);
        childElement.getEfsElementMara().setPartNumber("100200100");
        childElement.setAggregate("AAGR");

        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());

        assertFalse(entries.containsKey(InspectorEntryType.MISSING_AGGREGATE_ENGINE));
    }

    @Test
    public void checkRuleAggregateMissingEngineWrongDescription() {
        InspectorRestService service = InspectorRestClientHolder.getInstance();
        when(service.loadAggregateMapping(anyLong())).thenReturn(
                new EfsElementAggregateMappingListDTO(new ArrayList<>()));
        Map<String, EfsElementDTO> elementMap = createDefault("CG0", "SK0", null, null, 100D);
        EfsElementDTO childElement = elementMap.get(CHILD);
        childElement.getEfsElementMara().setPartNumber("100100100");
        childElement.setAggregate("AAGR");

        ListMultimap<InspectorEntryType, InspectorEntry> entries = check(elementMap.values());

        assertFalse(entries.containsKey(InspectorEntryType.MISSING_AGGREGATE_ENGINE));
    }

    private ListMultimap<InspectorEntryType, InspectorEntry> check(Collection<EfsElementDTO> elements) {
        return new Inspector().checkElements(elements, null, vehiclePartList.getVehicleConfig());
    }

    private Map<String, EfsElementDTO> createDefault(String costGroupChild, String setKeyChild,
            WeightControlFlag gwsParent, WeightControlFlag gwsChild, Double weightEstimateChild) {
        Map<String, EfsElementDTO> elementMap = new HashMap<>();

        EfsElementDTO parentElement = new EfsElementBuilder().id(1L).partNumber("1111111111")
                .vehiclePartList(vehiclePartList).weightControlFlag(gwsParent).costGroup("CG").setKey("SK")
                .weightEstimatedTe(10d).quantityUnit(QuantityUnit.PIECE).quantity(1).build();

        EfsElementDTO childElement = new EfsElementBuilder().id(2L).partNumber("2222222222")
                .vehiclePartList(vehiclePartList).weightControlFlag(gwsChild).costGroup(costGroupChild)
                .setKey(setKeyChild).weightEstimatedTe(weightEstimateChild).quantityUnit(QuantityUnit.PIECE).quantity(1)
                .parent(parentElement).build();

        parentElement.getChildren().add(childElement);

        elementMap.put(PARENT, parentElement);
        elementMap.put(CHILD, childElement);

        return elementMap;
    }

    private static class EfsElementBuilder {

        EfsElementDTO element;

        public EfsElementBuilder() {
            EfsElementMaraDTO mara = PartListFactory.createEfsElementMara();
            element = PartListFactory.createEfsElement();
            element.setEfsElementMara(mara);
            element.setChildren(new ArrayList<>());
        }

        public EfsElementBuilder weightEstimatedTe(Double weight) {
            element.getEfsElementMara().setWeightEstimatedTe(weight);
            return this;
        }

        public EfsElementBuilder quantityUnit(QuantityUnit einheit) {
            element.setQuantityUnit(einheit.getShortName());
            return this;
        }

        public EfsElementBuilder quantity(Integer q) {
            element.setQuantity(q);
            return this;
        }

        public EfsElementBuilder costGroup(String cg) {
            element.setCostGroup(cg);
            return this;
        }

        public EfsElementBuilder setKey(String sk) {
            element.setSetKey(sk);
            return this;
        }

        public EfsElementBuilder weightControlFlag(WeightControlFlag gws) {
            element.setWeightControlFlag(gws);
            return this;
        }

        public EfsElementBuilder partNumber(String pn) {
            element.getEfsElementMara().setPartNumber(pn);
            return this;
        }

        public EfsElementBuilder parent(EfsElementDTO ele) {
            element.setParent(ele);
            element.setParentId(ele.getParentId());
            ele.getChildren().add(element);
            return this;
        }

        public EfsElementBuilder vehiclePartList(VehiclePartListDTO vehiclePartList) {
            element.setVehiclePartListId(vehiclePartList.getId());
            return this;
        }

        public EfsElementBuilder id(Long id) {
            element.setId(id);
            return this;
        }

        public EfsElementDTO build() {
            return element;
        }
    }
}
