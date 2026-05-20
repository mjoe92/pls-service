package de.vw.paso.pll.dev.contextvisualizer.efs;

import de.vw.paso.pll.dev.contextvisualizer.util.components.AlternatingBackgroundTableCellRenderer;
import de.vw.paso.pll.dev.contextvisualizer.util.components.VisPage;
import de.vw.paso.pll.model.PlsEfsElement;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DuplicateDialog extends JDialog {

  PlsEfsElement originalElement;
  JList list;
  JTable table;

  public DuplicateDialog(PlsEfsElement usedElement, List<PlsEfsElement> duplicates) {
    super();
    originalElement = usedElement;
    setTitle("Duplicates");
    setSize(650, 700);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    list = createList(duplicates);
    table = createTable();

    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    split.setTopComponent(new JScrollPane(list));
    split.setBottomComponent(new JScrollPane(table));
    split.setDividerLocation(150);
    add(split, BorderLayout.CENTER);

  }

  private JTable createTable() {
    JTable table = new JTable();
    table.setDefaultRenderer(Object.class, new AlternatingBackgroundTableCellRenderer());
    return table;
  }

  private JList createList(List<PlsEfsElement> duplicates) {
    JList<PlsEfsElement> list = new JList<>(createModel(duplicates));
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        PlsEfsElement ele = list.getSelectedValue();
        showDetails(ele);
      }
    });
    list.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> jList, Object o, int i, boolean b, boolean b1) {
        JLabel component = (JLabel) super.getListCellRendererComponent(jList, o, i, b, b1);
        if (o instanceof PlsEfsElement) {
          if (!((PlsEfsElement) o).isMaraSet()) {
            component.setText(((PlsEfsElement) o).getNodeLabel());
          }
        }
        return component;
      }
    });
    return list;
  }

  private DefaultListModel<PlsEfsElement> createModel(List<PlsEfsElement> duplicates) {
    DefaultListModel<PlsEfsElement> model = new DefaultListModel<>();
    for (PlsEfsElement dup : duplicates ){
      model.addElement(dup);
    }
    return model;
  }

  private void showDetails(PlsEfsElement ele) {
    if (ele != null){
      DefaultTableModel model = new DefaultTableModel();
      model.addColumn("Property");
      model.addColumn("Original");
      model.addColumn("Duplicate");
      VisPage.addGetter(model, originalElement, ele);
      table.setModel(model);
    } else {
      table.setModel(new DefaultTableModel());
    }
  }
}
