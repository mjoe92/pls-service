package de.vw.paso.pll.dev.contextvisualizer.util.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AlternatingBackgroundTableCellRenderer extends DefaultTableCellRenderer {
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (!isSelected) {
      if (row % 2 == 1) {
        comp.setBackground(Color.decode("#dae4ef"));
      } else {
        comp.setBackground(Color.white);
      }
    }
    return comp;
  }
}
