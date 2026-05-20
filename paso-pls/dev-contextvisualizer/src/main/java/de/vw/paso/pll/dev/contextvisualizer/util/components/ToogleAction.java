package de.vw.paso.pll.dev.contextvisualizer.util.components;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class ToogleAction extends AbstractAction {

  public interface ToggleActionListener {
    void toggled(boolean newState);
  }

  private boolean selected = false;

  private List<ToggleActionListener> listener = new ArrayList<>();

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    selected = !selected;
    for (ToggleActionListener l : listener) {
      l.toggled(selected);
    }
    doAction(actionEvent);
  }

  protected abstract void doAction(ActionEvent actionEvent);

  public void addListenert(ToggleActionListener l) {
    listener.add(l);
  }

  public void register(AbstractButton btn) {
    btn.addActionListener(e -> actionPerformed(e));
    addListenert(new ToggleActionListener() {
      @Override
      public void toggled(boolean newState) {
        btn.setSelected(newState);
      }
    });
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected){
    this.selected = selected;
    for (ToggleActionListener l : listener) {
      l.toggled(selected);
    }
  }
}
