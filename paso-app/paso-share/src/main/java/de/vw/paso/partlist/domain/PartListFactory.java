package de.vw.paso.partlist.domain;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public final class PartListFactory {

    private PartListFactory() {
    }

    public static EfsElementDTO createEfsElement() {
        return new EfsElementDTO();
    }

    public static EfsElementDTO createEfsElement(Long parentId, EfsElementMaraDTO efsElementMara, int quantity,
            String quantityUnit, Long vehiclePartListId) {
        EfsElementDTO efsElement = new EfsElementDTO();

        efsElement.setParentId(parentId);
        efsElement.setEfsElementMara(efsElementMara);
        efsElement.setVehiclePartListId(vehiclePartListId);
        efsElement.setEntityChange(true);
        efsElement.setQuantity(quantity);
        efsElement.setQuantityUnit(quantityUnit);

        return efsElement;
    }

    public static EfsElementHistory createEfsElementHistory() {
        return new EfsElementHistory();
    }

    public static EfsElementMaraDTO createEfsElementMara() {
        return new EfsElementMaraDTO();
    }

    public static EfsElementMaraDTO createEfsElementMara(String description1De, String partNumber) {
        EfsElementMaraDTO mara = new EfsElementMaraDTO();

        mara.setDescription1De(description1De);
        mara.setPartNumber(partNumber);

        StringBuilder stringBuilder = new StringBuilder();

        int iii = 1;
        if (!(partNumber.equals(SpecialPartNumberType.GAP.getLabel()) || partNumber.equals(
                SpecialPartNumberType.NO_MARA.getLabel()))) {
            for (int index = 0; index < partNumber.length(); ) {
                if (stringBuilder.length() % 4 == 3) {
                    if (iii == 1) {
                        mara.setPartNumberVornummer(stringBuilder.toString());
                    } else if (iii == 2) {
                        mara.setPartNumberMittelgruppe(stringBuilder.toString());
                    } else if (iii == 3) {
                        mara.setPartNumberEndNumber(stringBuilder.toString());
                    }

                    stringBuilder.delete(0, index);
                    iii++;

                    continue;
                }

                stringBuilder.append(partNumber.charAt(index++));
                if (index == partNumber.length() && iii == 4) {
                    index++;

                    mara.setPartNumberIndex(stringBuilder.toString());
                }
            }
        }

        return mara;
    }

    public static VehiclePartListDTO createVehiclePartList(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig.getVehiclePartList() != null) {
            throw new RuntimeException(
                    "VehicleConfiguration (" + vehicleConfig.getId() + ") already contains a part list.");
        }

        VehiclePartListDTO vehiclePartListDTO = new VehiclePartListDTO();
        vehiclePartListDTO.setVehicleConfig(vehicleConfig);
        return vehiclePartListDTO;
    }
}