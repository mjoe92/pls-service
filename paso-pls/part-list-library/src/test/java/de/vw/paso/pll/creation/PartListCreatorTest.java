package de.vw.paso.pll.creation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

import de.vw.paso.pll.PPFUtil;
import de.vw.paso.pll.model.PlsEfsElement;
import de.vw.paso.pll.model.QuantityUnit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PartListCreatorTest {

  @Test
  @Disabled("outdated test data (missing values)")
    //FIXME
  void testCreateIncompletePartList() throws IOException {
    InputStream in = getClass().getResourceAsStream("/ppl/result.txt");
    LocalDate validDate = LocalDate.parse("2019-07-01");
    PartListCreationConfiguration config = createConfig(in, "", validDate);
    PartListCreator creator = new PartListCreator();
    PartListCreationResult result = creator.createPartList(config);
    PlsEfsElement rootElement = result.getRootElement();
    assertEquals(1, rootElement.getChildren().size(), "Check Children count");
  }

  @Test
  @Disabled("outdated test data (missing values)")
    //FIXME
  void testCreateIncompletePartList2() throws IOException {
    InputStream in = getClass().getResourceAsStream("/ppl/result.txt");
    String prNumberConfig = "1MM TJ1";
    LocalDate validDate = LocalDate.parse("2019-07-01");
    PartListCreationConfiguration config = createConfig(in, prNumberConfig, validDate);
    PartListCreator creator = new PartListCreator();
    PartListCreationResult result = creator.createPartList(config);
    PlsEfsElement rootElement = result.getRootElement();
    assertNotNull(rootElement, "Root element found");
    PlsEfsElement lvl2 = rootElement.getChildren().get(0);
    assertEquals(1, lvl2.getChildren().size(), "Check Children count");
  }

  @Test
  @Disabled
  void testCreatePartListBig() throws IOException {
    InputStream in = getClass().getResourceAsStream("/ppl/3986.ppf");
    String prNumberConfig = "1MM TJ1";
    LocalDate validDate = LocalDate.parse("2019-07-01");
    PartListCreationConfiguration config = createConfig(in, prNumberConfig, validDate);
    PartListCreator creator = new PartListCreator();
    PartListCreationResult result = creator.createPartList(config);
    PlsEfsElement rootElement = result.getRootElement();
    assertNotNull(rootElement, "Root element found");
    checkValidDate(rootElement, validDate);
  }

  @Test
  void testCreatePartListConfigWithInvalidDate() {
    InputStream in = getClass().getResourceAsStream("/ppl/result.txt");
    PartListCreationConfiguration config = createConfig(in, "", null);

    assertThrows(PartListCreationException.class, () -> new PartListCreator().createPartList(config),
      "Check creation with invalid date");
  }

  @Test
  void testCreatePartListConfigWithInvalidInputStream() {
    PartListCreationConfiguration config = createConfig(null, "", LocalDate.parse("2019-07-01"));

    assertThrows(PartListCreationException.class, () -> new PartListCreator().createPartList(config),
      "Check creation with invalid input stream");
  }

  @Test
  void testCreatePartListNullConfig() {
    assertThrows(PartListCreationException.class, () -> new PartListCreator().createPartList(null),
      "Check creation with null config");
  }

  @Test
  @Disabled("outdated test data (missing values)")
    //FIXME
  void testCreatePartListSmall() throws IOException {
    InputStream in = getClass().getResourceAsStream("/ppl/result.txt");
    String prNumberConfig = "1MM TJ1";
    LocalDate validDate = LocalDate.parse("2019-07-01");
    PartListCreationConfiguration config = createConfig(in, prNumberConfig, validDate);
    PartListCreator creator = new PartListCreator();
    PartListCreationResult result = creator.createPartList(config);
    PlsEfsElement rootElement = result.getRootElement();
    assertNotNull(rootElement, "Root element found");
    PlsEfsElement lvl2 = rootElement.getChildren().get(0);
    assertEquals(1, lvl2.getChildren().size(), "Check Children count");

    PlsEfsElement invalidPart = lvl2.getChildren().get(0);
    assertTrue(invalidPart.isPartFound(), "Check part not found");
    checkValidDate(rootElement, validDate);
  }

  @Test
  void testPrNumberRuleReader() {
    String prNumberConfig = "TJ4 1MM 2PE";
    LocalDate validDate = LocalDate.parse("2017-01-01");
    String s = """
      0:+TJ1+1MM
      1:+T5I+1MM
      2:+TJ1+2FB/2FD/2FE
      3:+T5I+2FB/2FD/2FE
      4:+TJ1+2PD/2PE
      5:+TJ4+1MM
      6:+TJ4+2FB/2FD/2FE
      7:+TJ4+2PD/2PE
      8:

      ---
      """;

    PartListCreationConfiguration config = createConfig(new ByteArrayInputStream(s.getBytes()), prNumberConfig, validDate);

    PrNumberRuleReader ruleReader = new PrNumberRuleReader();
    ruleReader.readRules(config, config.getLinesIterator());
    Set<String> validIds = ruleReader.getValidRuleIds();
    assertEquals(3, validIds.size(), "Check valid ids");
  }

  @Test
  @Disabled("outdated test data (missing values)")
    //FIXME
  void testQuantityUnitConvertion() throws IOException {
    InputStream in = getClass().getResourceAsStream("/ppl/3986.ppf");
    String prNumberConfig = "1MM TJ1";
    LocalDate validDate = LocalDate.parse("2019-07-01");
    PartListCreationConfiguration config = createConfig(in, prNumberConfig, validDate);
    PartListCreator creator = new PartListCreator();
    PartListCreationResult result = creator.createPartList(config);
    PlsEfsElement rootElement = result.getRootElement();
    assertNotNull(rootElement, "Root element found");
    checkQuantityUnit(rootElement);
  }

  private void checkQuantityUnit(PlsEfsElement rootElement) {
    if (rootElement.getQuantity() != null) {
      assertNotEquals(QuantityUnit.UNKNOWN, rootElement.getQuantityUnit(),
        "Check quantity unit: " + rootElement.getPartNumber());
    }
    for (PlsEfsElement element : rootElement.getChildren()) {
      checkQuantityUnit(element);
    }
  }

  private void checkValidDate(PlsEfsElement rootElement, LocalDate validDate) {
    boolean isValid = true;
    if (rootElement.getBeginDate() != null) {
      LocalDate einsatz = LocalDate.parse(PPFUtil.formatDate(rootElement.getBeginDate()));
      if (einsatz.isAfter(validDate)) {
        isValid = false;
      }
      if (rootElement.getEndDate() != null) {
        LocalDate entfall = LocalDate.parse(PPFUtil.formatDate(rootElement.getEndDate()));
        if (entfall.isBefore(einsatz)) {
          fail("EndDate is before BeginDate");
        }
        if (entfall.isBefore(validDate)) {
          isValid = false;
        }
      }
      if (!isValid) {
        fail("Date is not valid");
      }
    } else {
      fail("Node has no beginDate");
    }

    rootElement.getChildren().forEach(e -> checkValidDate(e, validDate));
  }

  private static PartListCreationConfiguration createConfig(InputStream in, String prNumberConfig,
    LocalDate validDate) {
    if (in == null) {
      return new PartListCreationConfiguration(null, prNumberConfig, validDate);
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
      Iterator<String> iterator = reader.lines().toList().iterator();
      return new PartListCreationConfiguration(iterator, prNumberConfig, validDate);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
