package de.vw.paso.pll.dev.contextvisualizer.util.components;

import de.vw.paso.pll.dev.contextvisualizer.veron.VeronPage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MultiSplitPane extends JPanel {

  private int orientation;
  private List<Component> components = new ArrayList<>();
  private List<JSplitPane> splitPanes = new ArrayList<>();

  public MultiSplitPane(int orientation) {
    this.orientation = orientation;
    setLayout(new BorderLayout());
    JSplitPane sp = new JSplitPane(orientation);
    splitPanes.add(sp);
    add(sp, BorderLayout.CENTER);
  }

  public void addComponent(JComponent comp) {
    components.add(comp);
    JSplitPane splitPane = splitPanes.get(splitPanes.size() - 1);
    if (splitPane.getLeftComponent() == null) {
      splitPane.setLeftComponent(comp);
    } else if (splitPane.getRightComponent() == null) {
      splitPane.setRightComponent(comp);
    } else {
      JSplitPane nextSplit = new JSplitPane(orientation);
      Component existingRightComp = splitPane.getRightComponent();
      nextSplit.setLeftComponent(existingRightComp);
      nextSplit.setRightComponent(comp);
      splitPane.setRightComponent(nextSplit);
      splitPanes.add(nextSplit);
    }
  }

  public void autoResize() {
    double partSize = getWidth() / (splitPanes.size() + 1f);
    for (int i = splitPanes.size() - 1; i >= 0; i--) {
      splitPanes.get(i).setDividerLocation((int) partSize);
    }
  }

  public void removeComponent(VeronPage veronPage) {
    JSplitPane firstSplitPane = splitPanes.get(0);
    if (firstSplitPane.getLeftComponent() == veronPage) {
      if (firstSplitPane.getRightComponent() instanceof JSplitPane) {
        add(firstSplitPane.getRightComponent(), BorderLayout.CENTER);
        splitPanes.remove(firstSplitPane);
      } else {
        firstSplitPane.setLeftComponent(firstSplitPane.getRightComponent());
      }
    } else {
      for (int i = 0; i < splitPanes.size(); i++) {
        JSplitPane sp = splitPanes.get(i);
        if (sp.getLeftComponent() == veronPage) {
          JSplitPane previousSplitPane = splitPanes.get(i - 1);
          previousSplitPane.setRightComponent(sp.getRightComponent());
          splitPanes.remove(sp);
          break;
        } else if (sp.getRightComponent() == veronPage) {
          sp.setRightComponent(null);
        }

      }

    }
  }
}
