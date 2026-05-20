package de.vw.paso.util;

import java.util.ArrayList;
import java.util.Collection;

import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementImport;

public class EfsElementEntityUtil {

    public static EfsElement copyEfsElement(EfsElement efsElement) {
        EfsElement newEfsElement = new EfsElement();

        newEfsElement.setId(efsElement.getId());
        newEfsElement.setParentId(efsElement.getParentId());
        newEfsElement.setParent(efsElement.getParent());
        newEfsElement.setVehiclePartListId(efsElement.getVehiclePartListId());
        newEfsElement.setRevision(efsElement.getRevision());
        newEfsElement.setDeleted(efsElement.getDeleted());
        newEfsElement.setNodeId(efsElement.getNodeId());
        newEfsElement.setNodeLabel(efsElement.getNodeLabel());
        newEfsElement.setBomNumber(efsElement.getBomNumber());
        newEfsElement.setProduct(efsElement.getProduct());
        newEfsElement.setGap(efsElement.getGap());
        newEfsElement.setTisSort(efsElement.getTisSort());
        newEfsElement.setNodeType(efsElement.getNodeType());
        newEfsElement.setNodeValueParent(efsElement.getNodeValueParent());
        newEfsElement.setNodeValue(efsElement.getNodeValue());
        newEfsElement.setQuantity(efsElement.getQuantity());
        newEfsElement.setQuantityUnit(efsElement.getQuantityUnit());
        newEfsElement.setQuantityUnitExtended(efsElement.getQuantityUnitExtended());
        newEfsElement.setWeightControlFlag(efsElement.getWeightControlFlag());
        newEfsElement.setProductStructure(efsElement.getProductStructure());
        newEfsElement.setPositionVariant(efsElement.getPositionVariant());
        newEfsElement.setDeletionFlag(efsElement.getDeletionFlag());
        newEfsElement.setCostGroup(efsElement.getCostGroup());
        newEfsElement.setTiWhImportId(efsElement.getTiWhImportId());
        newEfsElement.setAp(efsElement.getAp());
        newEfsElement.setPrNumberRule(efsElement.getPrNumberRule());
        newEfsElement.setBeginDateKey(efsElement.getBeginDateKey());
        newEfsElement.setEndDateKey(efsElement.getEndDateKey());
        newEfsElement.setBeginDate(efsElement.getBeginDate());
        newEfsElement.setEndDate(efsElement.getEndDate());
        newEfsElement.setPartType(efsElement.getPartType());
        newEfsElement.setAggregate(efsElement.getAggregate());
        newEfsElement.setSetKey(efsElement.getSetKey());
        newEfsElement.setEfsElementMara(efsElement.getEfsElementMara());
        newEfsElement.setWahlweiseFall(efsElement.getWahlweiseFall());
        newEfsElement.setWahlweiseNr(efsElement.getWahlweiseNr());
        newEfsElement.setBaukasten(efsElement.getBaukasten());
        newEfsElement.setBaukastenStatus(efsElement.getBaukastenStatus());
        newEfsElement.setBaukastenNodeId(efsElement.getBaukastenNodeId());
        newEfsElement.setWorkPackageNumber(efsElement.getWorkPackageNumber());
        newEfsElement.setProcessStatus(efsElement.getProcessStatus());
        newEfsElement.setDmuRelevant(efsElement.getDmuRelevant());
        newEfsElement.setMaterialType(efsElement.getMaterialType());
        newEfsElement.setEarliestPvs(efsElement.getEarliestPvs());
        newEfsElement.setEarliestNs(efsElement.getEarliestNs());
        newEfsElement.setEarliestSop(efsElement.getEarliestSop());
        newEfsElement.setPActivationDate(efsElement.getPActivationDate());
        newEfsElement.setKonstructureDate(efsElement.getKonstructureDate());
        newEfsElement.setAvonStatus(efsElement.getAvonStatus());
        newEfsElement.setCogX(efsElement.getCogX());
        newEfsElement.setCogY(efsElement.getCogY());
        newEfsElement.setCogZ(efsElement.getCogZ());
        newEfsElement.setTimestampCreate(efsElement.getTimestampCreate());
        newEfsElement.setTimestampChange(efsElement.getTimestampChange());
        newEfsElement.setUserChange(efsElement.getUserChange());
        newEfsElement.setUserCreate(efsElement.getUserCreate());
        newEfsElement.setChildren(efsElement.getChildren());

        return newEfsElement;
    }

    public static Collection<EfsElement> convertToEfsElements(Collection<EfsElementImport> elements) {
        Collection<EfsElement> result = new ArrayList<>(elements.size());
        for (EfsElementImport importElement : elements) {
            result.add(convertToEfsElement(importElement));
        }

        return result;
    }

    public static EfsElement convertToEfsElement(EfsElementImport importElement) {
        EfsElement element = new EfsElement();
        element.setId(importElement.getId());
        element.setDeleted(importElement.getDeleted());
        element.setNodeId(importElement.getNodeId());
        element.setNodeLabel(importElement.getNodeLabel());
        element.setGap(importElement.getGap());
        element.setTisSort(importElement.getTisSort());
        element.setNodeType(importElement.getNodeType());
        element.setNodeValue(importElement.getNodeValue());
        element.setQuantity(importElement.getQuantity());
        element.setQuantityUnit(importElement.getQuantityUnit());
        element.setQuantityUnitExtended(importElement.getQuantityUnitExtended());
        element.setWeightControlFlag(importElement.getWeightControlFlag());
        element.setConstructionsGroup(importElement.getConstructionsGroup());
        element.setCostGroup(importElement.getCostGroup());
        element.setTiWhImportId(importElement.getTiWhImportId());
        element.setAp(importElement.getAp());
        element.setPrNumberRule(importElement.getPrNumberRule());
        element.setBeginDate(importElement.getBeginDate());
        element.setEndDate(importElement.getEndDate());
        element.setAggregate(importElement.getAggregate());
        element.setSetKey(importElement.getSetKey());
        element.setDuplicateId(importElement.getDuplicateId());
        element.setEfsElementMara(importElement.getEfsElementMara());
        element.setVehiclePartListId(importElement.getVehiclePartListId());
        element.setWahlweiseFall(importElement.getWahlweiseFall());
        element.setWahlweiseNr(importElement.getWahlweiseNr());
        element.setBaukasten(importElement.getBaukastenFlag());
        element.setChildren(convertToEfsElements(importElement.getChildren()));

        EfsElementImport parent = importElement.getParent();
        element.setParentId(parent == null ? null : parent.getId());

        return element;
    }
}
