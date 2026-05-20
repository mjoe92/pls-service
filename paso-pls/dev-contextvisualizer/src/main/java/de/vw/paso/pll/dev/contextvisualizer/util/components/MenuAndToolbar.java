package de.vw.paso.pll.dev.contextvisualizer.util.components;

import javax.swing.*;
import java.awt.*;

public class MenuAndToolbar extends JPanel {

  private JMenuBar menubar;
  private JToolBar toolbar;

  public MenuAndToolbar() {
    setLayout(new BorderLayout());
    menubar = new JMenuBar();
    add(menubar, BorderLayout.NORTH);
    toolbar = new JToolBar();
    add(toolbar, BorderLayout.SOUTH);
  }

  public JMenuBar getMenuBar() {
    return menubar;
  }

  public JToolBar getToolbar() {
    return toolbar;
  }
}
