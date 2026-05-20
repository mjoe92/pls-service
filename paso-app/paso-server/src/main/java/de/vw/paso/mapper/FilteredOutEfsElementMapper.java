package de.vw.paso.mapper;

import de.vw.paso.partlist.domain.EfsElementImport;
import de.vw.paso.partlist.domain.FilteredOutEfsElement;

public final class FilteredOutEfsElementMapper {

    public static FilteredOutEfsElement toEntity(EfsElementImport efsElement, long vehicleConfigId, String reason) {
        FilteredOutEfsElement result = new FilteredOutEfsElement();

        result.setReason(reason);
        result.setVehicleConfigId(vehicleConfigId);
        result.setBomNumber(efsElement.getBomNumber());
        result.setProduct(efsElement.getProduct());
        result.setAggregate(efsElement.getAggregate());
        result.setDeleted(efsElement.getDeleted());
        result.setBeginDate(efsElement.getBeginDate());
        result.setBeginDateKey(efsElement.getBeginDateKey());
        result.setEndDate(efsElement.getEndDate());
        result.setEndDateKey(efsElement.getEndDateKey());
        result.setPartType(efsElement.getPartType());
        result.setWeightControlFlag(efsElement.getWeightControlFlag());
        result.setConstructionsGroup(efsElement.getConstructionsGroup());
        result.setProductStructure(efsElement.getProductStructure());
        result.setAp(efsElement.getAp());
        result.setCostGroup(efsElement.getCostGroup());
        result.setQuantity(efsElement.getQuantity());
        result.setQuantityUnit(efsElement.getQuantityUnit());
        result.setQuantityUnitExtended(efsElement.getQuantityUnitExtended());
        result.setNodeId(efsElement.getNodeId());
        result.setNodeLabel(efsElement.getNodeLabel());
        result.setNodeLevel(efsElement.getNodeLevel());
        result.setNodeType(efsElement.getNodeType());
        result.setNodeValue(efsElement.getNodeValue());
        result.setNodeValueParent(efsElement.getNodeValueParent());
        result.setPrNumberRule(efsElement.getPrNumberRule());
        result.setRevision(efsElement.getRevision());
        result.setGap(efsElement.getGap());
        result.setSetKey(efsElement.getSetKey());
        result.setWahlweiseFall(efsElement.getWahlweiseFall());
        result.setWahlweiseNr(efsElement.getWahlweiseNr());
        result.setProcessStatus(efsElement.getProcessStatus());
        result.setDmuRelevant(efsElement.getDmuRelevant());
        result.setBaukasten(efsElement.getBaukastenFlag());
        result.setBaukastenStatus(efsElement.getBaukastenStatus());
        result.setBaukastenNodeId(efsElement.getBaukastenNodeId());
        result.setMaterialType(efsElement.getMaterialType());
        result.setEarliestPvs(efsElement.getEarliestPvs());
        result.setEarliestNs(efsElement.getEarliestNs());
        result.setEarliestSop(efsElement.getEarliestSop());
        result.setPActivationDate(efsElement.getPActivationDate());
        result.setKonstructureDate(efsElement.getKonstructureDate());
        result.setAvonStatus(efsElement.getAvonStatus());
        result.setEfsElementMara(efsElement.getEfsElementMara());
        result.setTisSort(efsElement.getTisSort());

        return result;
    }
}
