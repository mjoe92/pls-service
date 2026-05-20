package de.vw.paso.services.partlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import de.vw.paso.consumer.partlist.LoadVariantConsumer;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsriss.AlternativePartsForGapListDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EfsRissTests extends AbstractEfsTests {

    @Autowired
    private LoadVariantConsumer loadVariantConsumer;

    @Test
    @Disabled
    public void loadVariants() {
        loadVariantConsumer.loadVariants("5DNH", vehiclePartList().getVehicleConfig().getId());

        Map<EfsElementDTO, String> variants = AlternativePartsForGapListDTO.createEfsElementMap(
                loadVariantConsumer.getResult());
        assertEquals(1, variants.size());
    }

    @Test
    public void loadVariantsWithNonExistentNodeId() {
        loadVariantConsumer.loadVariants("ZZZ", vehiclePartList().getVehicleConfig().getId());

        Map<EfsElementDTO, String> variants = AlternativePartsForGapListDTO.createEfsElementMap(
                loadVariantConsumer.getResult());
        assertTrue(variants.isEmpty());
    }

    @Test
    public void loadVariantsWithNonExistentVehicleConfigId() {
        loadVariantConsumer.loadVariants("5DNH", 0L);

        Map<EfsElementDTO, String> variants = AlternativePartsForGapListDTO.createEfsElementMap(
                loadVariantConsumer.getResult());
        assertTrue(variants.isEmpty());
    }
}
