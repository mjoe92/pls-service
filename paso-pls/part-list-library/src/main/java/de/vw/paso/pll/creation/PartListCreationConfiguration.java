package de.vw.paso.pll.creation;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

public class PartListCreationConfiguration {

  private final Iterator<String> linesIterator;
  private final String prNumberConfig;

  private final LocalDate validDate;

  private final Set<String> validBaukastenKennzeichen = Set.of("1", "X");

  public PartListCreationConfiguration(Iterator<String> linesIterator, String prNumbers, LocalDate validDate) {
    this.linesIterator = linesIterator;
    this.prNumberConfig = prNumbers;
    this.validDate = validDate;
  }

  public Iterator<String> getLinesIterator() {
    return linesIterator;
  }

  public String getPrNumberConfig() {
    return prNumberConfig;
  }

  public LocalDate getValidDate() {
    return validDate;
  }

  public boolean addLeitungsstraenge() {
    return false;
  }

  public Set<String> getValidBaukasten() {
    return validBaukastenKennzeichen;
  }
}
