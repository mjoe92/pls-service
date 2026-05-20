package de.vw.paso.services.masterdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.logic.partlist.TableConfigManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.repository.tableconfiguration.TableConfigRepository;
import de.vw.paso.repository.user.UserPropertyRepository;
import de.vw.paso.service.tablecolumnconfig.TableConfigRestController;
import de.vw.paso.service.tableconfig.TableConfigDTO;
import de.vw.paso.tableconfig.TableConfig;
import de.vw.paso.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TableConfigTests extends AbstractServiceTests {

  @Autowired
  private TableConfigRestController tableConfigRestController;

  @Autowired
  private TableConfigManager tableConfigManager;

  @Autowired
  private TableConfigRepository tableConfigRepository;

  @Autowired
  private UserPropertyRepository userPropertyRepository;

  @Autowired
  private UserManager userManager;

  @Test
  public void queryConfigs() {
    User otherUser = setUpOtherUser();
    User user = userManager.getUser(TEST_USER_ID);
    tableConfigRepository.saveAll(
      List.of(new TableConfig(null, user, "Config1", List.of("partNumber"), List.of("partNumber"), true),
        new TableConfig(null, user, "Config2", List.of("partNumber"), List.of("partNumber"), false),
        new TableConfig(null, otherUser, "Config3", List.of("partNumber"), List.of("partNumber"), true),
        new TableConfig(null, otherUser, "Config4", List.of("partNumber"), List.of("partNumber"), false)));

    List<TableConfigDTO> configDTOS = tableConfigRestController.getConfigurationsForUser().tableConfigDTOs();

    assertEquals(List.of("Config1", "Config2", "Config3"), configDTOS.stream().map(TableConfigDTO::getName).toList());

    tableConfigRepository.deleteAll();
  }

  @Test
  public void createTableConfig() {
    User user = userManager.getUser(TEST_USER_ID);
    TableConfig tableConfig = new TableConfig(null, user, "Config", List.of("partNumber"), List.of("partNumber"),
      false);
    tableConfigRestController.saveConfiguration(tableConfigManager.convertToDTO(tableConfig));
    assertEquals(1L, tableConfigRepository.findAll().size());

    tableConfigRepository.deleteAll();
  }

  @Test
  public void createTableConfigWithoutPartNumber() {
    User user = userManager.getUser(TEST_USER_ID);
    TableConfig tableConfig = new TableConfig(null, user, "Config", List.of(), List.of(), false);

    assertThrows(RuntimeException.class,
      () -> tableConfigRestController.saveConfiguration(tableConfigManager.convertToDTO(tableConfig)));

    tableConfigRepository.deleteAll();
  }

  @Test
  public void editTableConfig() {
    User user = userManager.getUser(TEST_USER_ID);
    TableConfig tableConfig = new TableConfig(null, user, "Config", List.of("partNumber"), List.of("partNumber"),
      false);

    tableConfig = tableConfigRepository.save(tableConfig);

    tableConfig.setName("ConfigUpdated");

    tableConfigRestController.saveConfiguration(tableConfigManager.convertToDTO(tableConfig));

    assertEquals("ConfigUpdated", tableConfigRepository.findById(tableConfig.getId()).orElseThrow().getName());

    tableConfigRepository.deleteAll();
  }

  @Test
  public void editTableConfigForOtherUser() {
    User user = setUpOtherUser();
    TableConfig tableConfig = tableConfigRepository.save(
      new TableConfig(null, user, "Config ", List.of("partNumber"), List.of("partNumber"), false));

    tableConfig.setName("ConfigUpdated");

    assertThrows(RuntimeException.class,
      () -> tableConfigRestController.saveConfiguration(tableConfigManager.convertToDTO(tableConfig)));

    tableConfigRepository.deleteAll();
  }

  @Test
  public void deleteOtherUsersConfig() {
    User user = setUpOtherUser();
    TableConfig tableConfig = new TableConfig(null, user, "Config", List.of("partNumber"), List.of("partNumber"),
      false);
    tableConfigRestController.saveConfiguration(tableConfigManager.convertToDTO(tableConfig));
    assertThrows(RuntimeException.class, () -> tableConfigRestController.deleteConfiguration(tableConfig.getId()));

    tableConfigRepository.deleteAll();
  }

  private User setUpOtherUser() {
    User user = new User();
    user.setId("Other-user");
    user.setRoles(Set.of());
    user.setUserGroups(List.of());
    user.setActive(true);
    user.setEmail("email");
    user.setCostCenter("costCenter");
    user.setFirstName("firstName");
    user.setLastName("lastName");
    user.setPreferredLanguage("EN");
    return userManager.saveUser(user);
  }
}
