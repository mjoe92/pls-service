package de.vw.paso;

import java.util.HashSet;
import java.util.List;

import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.logic.activitylog.AdminActivityLogManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminActivityLogTest extends AbstractServiceTests {

  @Autowired
  private AdminActivityLogManager adminActivityLogManager;

  @Test
  public void testSavePermissionChangeLog() {
    adminActivityLogManager.logPermissionChange("adminActivityLogTest", new HashSet<>(List.of("user1")),
      List.of("role1"), List.of("role2"));
  }

  @Test
  public void testSaveUserActiveLog() {
    adminActivityLogManager.logUserActive("adminActivityLogTest", List.of("user1"), true);
  }
}
