package de.vw.paso.services.partlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static testutil.TestUtils.saveUserGroup;

import java.util.List;
import java.util.Optional;

import de.vw.paso.logic.partlist.EfsElementHistoryManager;
import de.vw.paso.partlist.domain.EfsElementHistory;
import de.vw.paso.repository.partlist.EfsElementHistoryRepository;
import de.vw.paso.repository.user.UserGroupRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RevisionTests extends AbstractEfsTests {

    @Autowired
    private EfsElementHistoryManager efsElementHistoryManager;

    @Autowired
    private EfsElementHistoryRepository efsElementHistoryRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private VehicleConfigRepository vehicleConfigRepository;

    @Test
    public void testRevisionEditingEfsElement() {
        assertEquals(Integer.valueOf(111), getOriginal(0).getQuantity());

        EfsElementDTO element = getOriginal(0);
        element.setQuantity(5);

        Long maraRevision = element.getEfsElementMara().getRevision();

        saveEfsElementConsumer.saveEfsElement(element);

        EfsElementDTO savedEfsElement = getOriginal(0);
        Long savedMaraRevision = savedEfsElement.getEfsElementMara().getRevision();
        Long revision = savedEfsElement.getRevision();

        evaluateRevisionVehiclePartList(initialRevision + 1);
        assertEquals(initialRevision + 1, revision.longValue());
        assertEquals(maraRevision, savedMaraRevision);
        assertEquals(Integer.valueOf(5), getOriginal(0).getQuantity());
    }

    @Test
    public void testRevisionEditingEfsElementMara() {
        saveUserGroup(userManager, userGroupRepository, "Test-User-ID", vehicleConfigRepository);

        EfsElementDTO element = getOriginal(0);

        element.getEfsElementMara().setWeightCalculatedTe(10D);
        element.getEfsElementMara().setChange(userManager.getCurrentUserId());
        saveEfsElementConsumer.saveEfsElement(element);

        EfsElementDTO savedEfsElement = getOriginal(0);
        Long maraRevision = savedEfsElement.getEfsElementMara().getRevision();
        Long revision = savedEfsElement.getRevision();

        evaluateRevisionVehiclePartList(initialRevision + 1);
        assertEquals(initialRevision + 1, revision.longValue());
        assertEquals(initialRevision + 1, maraRevision.longValue());
        assertEquals(Double.valueOf(10), savedEfsElement.getEfsElementMara().getWeightCalculatedTe());
    }

    @Test
    public void testRevisionEditingEfsElementPartNumberToNonExists() {
        saveUserGroup(userManager, userGroupRepository, "Test-User-ID", vehicleConfigRepository);
        EfsElementDTO element = getOriginal(0);

        element.getEfsElementMara().setPartNumber("XXXXXXXXX");
        element.getEfsElementMara().setChange(userManager.getCurrentUserId());

        saveEfsElementConsumer.saveEfsElement(element);

        EfsElementDTO savedEfsElement = getOriginal(0);
        Long maraRevision = savedEfsElement.getEfsElementMara().getRevision();
        Long revision = savedEfsElement.getRevision();

        evaluateRevisionVehiclePartList(initialRevision + 1);
        assertEquals(initialRevision + 1, revision.longValue());
        assertEquals(initialRevision + 1, maraRevision.longValue());
    }

    @Test
    public void testRevisionEditingEfsElementPartNumber() {
        saveUserGroup(userManager, userGroupRepository, "Test-User-ID", vehicleConfigRepository);
        EfsElementDTO element = getOriginal(0);

        element.getEfsElementMara().setPartNumber("11961111111");

        saveEfsElementConsumer.saveEfsElement(element);

        EfsElementDTO savedEfsElement = getOriginal(0);
        Long maraRevision = savedEfsElement.getEfsElementMara().getRevision();
        Long revision = savedEfsElement.getRevision();

        evaluateRevisionVehiclePartList(initialRevision + 1);
        assertEquals(initialRevision + 1, revision.longValue());
        assertEquals(1, maraRevision.longValue());
    }

    @Test
    public void testRevertRevisionSingleEditOneEfsElement() {
        saveUserGroup(userManager, userGroupRepository, "Test-User-ID", vehicleConfigRepository);
        //init
        EfsElementDTO element = getOriginal(0);
        element.setQuantity(5);
        saveEfsElementConsumer.saveEfsElement(element);

        element = getOriginal(0);
        Long oldRevision = element.getRevision();
        element.setQuantity(10);
        saveEfsElementConsumer.saveEfsElement(element);
        EfsElementDTO savedEfsElement = getOriginal(0);
        Long newRevision = savedEfsElement.getRevision();
        assertTrue("Check revision increased", oldRevision < newRevision);
        assertEquals(10, savedEfsElement.getQuantity().intValue(), "Check changed value");

        efsElementHistoryManager.revertToRevision(element.getVehiclePartListId(), oldRevision);
        EfsElementDTO revertedElement = getOriginal(0);
        assertEquals(oldRevision, revertedElement.getRevision(), "Check reverted revision");
        assertEquals(5, revertedElement.getQuantity().intValue(), "Check reverted value");

        List<EfsElementHistory> histories = efsElementHistoryRepository.findAllByVehiclePartListIdAndEfsElementIdOrderByRevision(
                element.getVehiclePartListId(), element.getId());
        Optional<EfsElementHistory> any = histories.stream().filter(e -> e.getRevision() > oldRevision).findAny();
        assertFalse("Check revisions deleted", any.isPresent());

    }

    @Test
    public void testRevertRevisionMultiEditOneEfsElement() {
        saveUserGroup(userManager, userGroupRepository, "Test-User-ID", vehicleConfigRepository);
        //init
        EfsElementDTO element = getOriginal(0);
        element.setQuantity(5);
        saveEfsElementConsumer.saveEfsElement(element);

        element = getOriginal(0);
        Long oldRevision = element.getRevision();
        element.setQuantity(6);
        saveEfsElementConsumer.saveEfsElement(element);
        element = getOriginal(0);
        element.setQuantity(7);
        saveEfsElementConsumer.saveEfsElement(element);
        element.setQuantity(8);
        saveEfsElementConsumer.saveEfsElement(element);
        element.setQuantity(9);
        saveEfsElementConsumer.saveEfsElement(element);

        EfsElementDTO savedEfsElement = getOriginal(0);
        Long newRevision = savedEfsElement.getRevision();
        assertTrue("Check revision increased", oldRevision < newRevision);
        assertEquals(9, savedEfsElement.getQuantity().intValue(), "Check changed value");

        efsElementHistoryManager.revertToRevision(element.getVehiclePartListId(), oldRevision);
        EfsElementDTO revertedElement = getOriginal(0);
        assertEquals(oldRevision, revertedElement.getRevision(), "Check reverted revision");
        assertEquals(5, revertedElement.getQuantity().intValue(), "Check reverted value");

        List<EfsElementHistory> histories = efsElementHistoryRepository.findAllByVehiclePartListIdAndEfsElementIdOrderByRevision(
                element.getVehiclePartListId(), element.getId());
        Optional<EfsElementHistory> any = histories.stream().filter(e -> e.getRevision() > oldRevision).findAny();
        assertFalse("Check revisions deleted", any.isPresent());
    }

    @Test
    public void testRevertRevisionMultiEditMultiEfsElement() {
        saveUserGroup(userManager, userGroupRepository, "Test-User-ID", vehicleConfigRepository);
        //init
        EfsElementDTO element1 = getOriginal(0);
        element1.setQuantity(5);
        saveEfsElementConsumer.saveEfsElement(element1);
        EfsElementDTO element2 = getOriginal(1);
        element2.setQuantity(5);
        saveEfsElementConsumer.saveEfsElement(element2);

        element1 = getOriginal(0);
        Long oldRevision = element1.getRevision();
        element1.setQuantity(6);
        saveEfsElementConsumer.saveEfsElement(element1);
        element1 = getOriginal(0);
        element1.setQuantity(7);
        saveEfsElementConsumer.saveEfsElement(element1);
        element1 = getOriginal(0);
        element1.setQuantity(8);
        saveEfsElementConsumer.saveEfsElement(element1);

        element2 = getOriginal(1);
        element2.setQuantity(6);
        saveEfsElementConsumer.saveEfsElement(element2);
        element2 = getOriginal(1);
        element2.setQuantity(7);
        saveEfsElementConsumer.saveEfsElement(element2);
        element2 = getOriginal(1);
        element2.setQuantity(8);
        saveEfsElementConsumer.saveEfsElement(element2);

        efsElementHistoryManager.revertToRevision(element1.getVehiclePartListId(), oldRevision);

        EfsElementDTO revertedElement1 = getOriginal(0);
        assertEquals(oldRevision, revertedElement1.getRevision(), "Check reverted revision");
        assertEquals(5, revertedElement1.getQuantity().intValue(), "Check reverted value");
        EfsElementDTO revertedElement2 = getOriginal(0);
        assertEquals(oldRevision, revertedElement2.getRevision(), "Check reverted revision");
        assertEquals(5, revertedElement2.getQuantity().intValue(), "Check reverted value");

        List<EfsElementHistory> histories = efsElementHistoryRepository.findAllByVehiclePartListIdAndEfsElementIdOrderByRevision(
                element1.getVehiclePartListId(), element1.getId());
        Optional<EfsElementHistory> any = histories.stream().filter(e -> e.getRevision() > oldRevision).findAny();
        assertFalse("Check revisions deleted", any.isPresent());
        List<EfsElementHistory> histories2 = efsElementHistoryRepository.findAllByVehiclePartListIdAndEfsElementIdOrderByRevision(
                element1.getVehiclePartListId(), element1.getId());
        Optional<EfsElementHistory> any2 = histories2.stream().filter(e -> e.getRevision() > oldRevision).findAny();
        assertFalse("Check revisions deleted", any2.isPresent());
    }
}
