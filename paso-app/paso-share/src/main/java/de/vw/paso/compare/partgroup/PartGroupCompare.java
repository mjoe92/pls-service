package de.vw.paso.compare.partgroup;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.SpecPartGroupCategory;
import de.vw.paso.utility.StringConstant;

public class PartGroupCompare {

    private static final String SEPARATOR = StringConstant.DASH;

    private final Map<String, PartGroupDTO> partGroups;

    public PartGroupCompare(Map<String, PartGroupDTO> partGroups) {
        this.partGroups = partGroups;
    }

    public PartGroupCompareResult compare(List<VehicleConfigDTO> vehicleConfigs, VehicleConfigDTO reference) {
        PartGroupCompareResult result = new PartGroupCompareResult(vehicleConfigs, reference);
        for (VehicleConfigDTO config : vehicleConfigs) {
            Collection<EfsElementDTO> elementsOfPArtList = EfsElementResolver.getElementsInPartList(
                    config.getVehiclePartList());
            processElements(elementsOfPArtList, config.getId(), result);
        }

        for (VehicleConfigDTO e : vehicleConfigs) {
            result.getRoot().getWeights(e.getId());
        }

        return result;
    }

    private void processElements(Collection<EfsElementDTO> elementsOfPartList, Long vehicleConfigId,
            PartGroupCompareResult result) {
        for (EfsElementDTO element : elementsOfPartList) {
            if (element.getPartNumber() == null || element.getPartNumber().equals(SpecialPartNumberType.GAP.getLabel())
                    || element.getEfsElementMara().getPartNumberMittelgruppe() == null
                    || element.getEfsElementMara().getPartNumberEndNumber() == null) {
                continue;
            }

            boolean isNotVnr = true;
            if (!isUgrNumeric(element.getEfsElementMara().getPartNumberVornummer())) {
                if (element.getEfsElementMara().getPartNumberVornummer()
                        .startsWith(SpecPartGroupCategory.NORM_PART_GROUP.getCategoryStr())) {
                    isNotVnr = false;

                    if (element.getEfsElementMara().getPartNumberMittelgruppe().equals("052")) {
                        addDataSetForPartGroups(result, element, vehicleConfigId,
                                SpecPartGroupCategory.NORM_PART_GROUP.getCategory(), 52);
                    } else {
                        addDataSetForPartGroups(result, element, vehicleConfigId,
                                SpecPartGroupCategory.NORM_PART_GROUP.getCategory(), 0);
                    }
                } else if (element.getEfsElementMara().getPartNumberVornummer()
                        .equals(SpecPartGroupCategory.WHT_PART_GROUP.getCategoryStr())) {
                    isNotVnr = false;

                    addDataSetForPartGroups(result, element, vehicleConfigId,
                            SpecPartGroupCategory.WHT_PART_GROUP.getCategory(), 0);
                } else if (element.getEfsElementMara().getPartNumberVornummer().startsWith("A")) {
                    isNotVnr = false;

                    addDataSetForPartGroups(result, element, vehicleConfigId,
                            SpecPartGroupCategory.A_PART_GROUP.getCategory(), 0);
                }
            }

            if (isNotVnr && isUgrNumeric(element.getEfsElementMara().getPartNumberEndNumber())) {
                String key = element.getEfsElementMara().getPartNumberMittelgruppe().charAt(0) + SEPARATOR
                        + element.getEfsElementMara().getPartNumberMittelgruppe() + SEPARATOR
                        + element.getEfsElementMara().getPartNumberEndNumber();

                addDataSetForPartGroups(result, element, vehicleConfigId, key);
            }
        }
    }

    private void addDataSetForPartGroups(PartGroupCompareResult result, EfsElementDTO element, Long vehicleConfigId,
            int category, int partNumberMittelgruppe) {
        String key = category + SEPARATOR + groupToString(partNumberMittelgruppe);
        if (partNumberMittelgruppe != 0) {
            key += SEPARATOR + element.getEfsElementMara().getPartNumberEndNumber();
        }

        addDataSetForPartGroups(result, element, vehicleConfigId, key);
    }

    private void addDataSetForPartGroups(PartGroupCompareResult result, EfsElementDTO element, Long vehicleConfigId,
            String key) {
        PartGroupCompareRow row = result.getRowForPartGroup(key);
        if (partGroups.containsKey(key)) {
            if (row == null) {
                String partGroupKey = getKey(partGroups.get(key));
                row = result.getRowForPartGroup(partGroupKey);

                if (row == null) {
                    row = createRow(partGroups.get(key), key, result);
                }
            }

            row.addDataSet(element, vehicleConfigId);

            return;
        }

        if (row != null) {
            row.addDataSet(element, vehicleConfigId);

            return;
        }

        PartGroupDTO partGroup = new PartGroupDTO();
        String category = key.substring(0, 3);
        if (!category.contains(SEPARATOR) && Integer.parseInt(category) >= 100) {
            partGroup.setCategory(Integer.parseInt(category));
        }

        partGroup.setMgr(Integer.parseInt(element.getEfsElementMara().getPartNumberMittelgruppe()));
        partGroup.setUgr(Integer.parseInt(element.getEfsElementMara().getPartNumberEndNumber()));

        row = createUnknownRow(partGroup, key, result, element, vehicleConfigId);
        result.addRow(row);

        PartGroupCompareRow unknownPartGroupRow = result.getRowForPartGroup(null);
        if (unknownPartGroupRow == null) {
            unknownPartGroupRow = new PartGroupCompareRow();
            result.addRow(unknownPartGroupRow);
            result.addRootNode(unknownPartGroupRow);
        }

        if (!unknownPartGroupRow.getChildren().contains(row)) {
            unknownPartGroupRow.addChildRow(row);
        }
    }

    private PartGroupCompareRow createRow(PartGroupDTO partGroup, String partGroupKey, PartGroupCompareResult result) {
        PartGroupCompareRow row = new PartGroupCompareRow(partGroup, partGroupKey);
        String parentPartGroupKey = getParentKey(partGroup);

        if (parentPartGroupKey == null) {
            result.addRootNode(row);
            result.addRow(row);

            return row;
        }

        PartGroupCompareRow parentRow = result.getRowForPartGroup(parentPartGroupKey);

        if (parentRow == null) {
            if (partGroups.get(parentPartGroupKey) != null) {
                parentRow = createRow(partGroups.get(parentPartGroupKey), parentPartGroupKey, result);
            } else {
                PartGroupDTO partGroup1 = new PartGroupDTO();
                partGroup1.setCategory(partGroup.getCategory());
                partGroup1.setMgr(partGroup.getMgr());

                parentRow = createRow(partGroup1, parentPartGroupKey, result);
            }
        }

        parentRow.addChildRow(row);
        result.addRow(row);

        return row;
    }

    private PartGroupCompareRow createUnknownRow(PartGroupDTO partGroup, String partGroupKey,
            PartGroupCompareResult result, EfsElementDTO element, Long vehicleConfigId) {
        String parentPartGroupKey;
        if (partGroup.getCategory() == null
                || partGroup.getCategory() < SpecPartGroupCategory.NORM_PART_GROUP.getCategory()) {
            parentPartGroupKey = getParentKey(partGroup);
        } else {
            PartGroupDTO pg = new PartGroupDTO();
            pg.setMgr(partGroup.getMgr());
            pg.setUgr(partGroup.getUgr());

            parentPartGroupKey = getParentKey(pg);
        }

        PartGroupCompareRow row = new PartGroupCompareRow(partGroup, partGroupKey);
        if (parentPartGroupKey == null) {
            return row;
        }

        PartGroupCompareRow parentRow = result.getRowForPartGroup(parentPartGroupKey);

        if (parentRow == null) {
            String parentKey;
            if (partGroup.isUgr()) {
                String categoryKey = partGroupKey.substring(0, 3);
                parentKey = categoryKey.contains(SEPARATOR)
                        || Integer.parseInt(categoryKey) < SpecPartGroupCategory.NORM_PART_GROUP.getCategory()
                        ? partGroupKey.substring(0, 5) : partGroupKey.substring(0, 7);
            } else {
                parentKey = parentPartGroupKey;
            }

            PartGroupDTO parent = new PartGroupDTO();
            if (partGroups.get(parentKey) == null) {
                parent.setMgr(partGroup.getMgr());
            } else {
                parent.setMgr(partGroups.get(parentKey).getMgr());
                parent.setDescription(partGroups.get(parentKey).getDescription());
            }
            parentRow = createUnknownRow(parent, parentPartGroupKey, result, element, vehicleConfigId);
        }

        parentRow.addChildRow(row);
        result.addRow(row);
        row.addDataSet(element, vehicleConfigId);

        row = parentRow;

        return row;
    }

    private String getKey(PartGroupDTO partGroup) {
        String key = null;
        if (partGroup.isCategory()) {
            if (partGroup.getCategory() != null) {
                key = partGroup.getCategory().toString();
            }
        } else if (partGroup.isMgr()) {
            key = partGroup.getCategory() == null ? groupToString(partGroup.getMgr())
                    : partGroup.getCategory() + SEPARATOR + groupToString(partGroup.getMgr());
        } else if (partGroup.isUgr()) {
            key = partGroup.getCategory() == null ? groupToString(partGroup.getMgr()) + SEPARATOR + groupToString(
                    partGroup.getUgr())
                    : partGroup.getCategory() + SEPARATOR + groupToString(partGroup.getMgr()) + SEPARATOR
                            + groupToString(partGroup.getUgr());
        }

        return key;
    }

    private String getParentKey(PartGroupDTO partGroup) {
        if (partGroup.isCategory()) {
            return null;
        }

        if (partGroup.isUgr()) {
            return partGroup.getCategory() == null ? groupToString(partGroup.getMgr())
                    : partGroup.getCategory() + SEPARATOR + groupToString(partGroup.getMgr());
        }

        return partGroup.getCategory() == null ? null : partGroup.getCategory().toString();
    }

    private String groupToString(Integer integer) {
        return integer == null ? StringConstant.EMPTY : String.format("%03d", integer);
    }

    private static boolean isUgrNumeric(String strNum) {
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }

        return true;
    }
}