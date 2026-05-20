package de.vw.paso.services.partlistwizard;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import de.vw.paso.consumer.partlistwizard.LoadPRNumberForVehicleConsumer;
import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.service.masterdata.prnumber.PrNumberListDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PRNumberLoadTests extends AbstractServiceTests {

    @Autowired
    private LoadPRNumberForVehicleConsumer loadPRNumberConsumer;

    // todo: Needs refactoring
    public void loadPrNumbersThatExistForVehicle() {
        Long vehicleProjectId = 1188L;
        loadPRNumberConsumer.loadPrNumbersForVehicle(vehicleProjectId);
        PrNumberListDTO result = loadPRNumberConsumer.getResult();
        assertNotNull("List of PrNumbers is null", result);
        assertTrue("contains no PrNumbers, loading PrNumbers failed", !result.prNumberDTOList().isEmpty());
    }

    @Test
    public void throwsExceptionOnNonExistingVehicle() {
        Long vehicleProjectId = 999999999L;

        loadPRNumberConsumer.loadPrNumbersForVehicle(vehicleProjectId);
        assertThrows(RuntimeException.class, () -> loadPRNumberConsumer.getResult());
    }
}
