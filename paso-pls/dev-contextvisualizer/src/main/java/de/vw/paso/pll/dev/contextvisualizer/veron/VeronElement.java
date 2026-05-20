package de.vw.paso.pll.dev.contextvisualizer.veron;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class VeronElement extends DefaultMutableTreeNode {

  private String[] data;

  public VeronElement(String[] data) {
    this.data = data;
  }

  public String get(VeronFields field) {
    return field.get(data);
  }

  public List<VeronElement> getChildren() {
    Enumeration children = children();
    return Collections.list(children);
  }

  @Override
  public String toString() {
    return VeronFields.Teilenummer.get(data) + " " + VeronFields.Bezeichnung1.get(data);
  }
}
