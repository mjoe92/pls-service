package de.vw.paso.pll.dev.contextvisualizer.efs;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.Files;
import de.vw.paso.pll.creation.PartListCreationConfiguration;
import de.vw.paso.pll.creation.PartListCreationResult;
import de.vw.paso.pll.creation.PartListCreator;
import de.vw.paso.pll.dev.contextvisualizer.checker.PageTreeCompareResult;
import de.vw.paso.pll.dev.contextvisualizer.util.BackgroundExecutor;
import de.vw.paso.pll.dev.contextvisualizer.util.NodeIdentifier;
import de.vw.paso.pll.dev.contextvisualizer.util.PartNumberUtil;
import de.vw.paso.pll.dev.contextvisualizer.util.ProcessMonitor;
import de.vw.paso.pll.dev.contextvisualizer.util.TreeUtil;
import de.vw.paso.pll.dev.contextvisualizer.util.components.VisPage;
import de.vw.paso.pll.dev.contextvisualizer.util.event.EventBus;
import de.vw.paso.pll.dev.contextvisualizer.util.event.Topic;
import de.vw.paso.pll.model.PlsEfsElement;

public class EfsPage extends VisPage {

  private class PrConfiguration {

    private String name;
    private String prNumber;
    private String date;

    private PrConfiguration(String name, String prNumber, String date) {
      this.name = name;
      this.prNumber = prNumber;
      this.date = date;
    }

    public String getName() {
      return name;
    }

    public String getPrNumber() {
      return prNumber;
    }

    public String getDate() {
      return date;
    }

    @Override
    public String toString() {
      return getName();
    }
  }

  private File ppfFile;
  private PartListCreator creator;
  public PartListCreationResult result;
  private Map<NodeIdentifier, DefaultMutableTreeNode> treeNodeByNodeIdMap;
  private JTextArea prNumberArea;
  private JTextField validDateField;
  private ListMultimap<String, PlsEfsElement> duplicatesByNodeId;
  private PartListCreationConfiguration config;

  public EfsPage(BackgroundExecutor bgExec) {
    super("EFS", bgExec);
  }

  @Override
  protected JTree createTree() {
    tree = super.createTree();
    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3 && mouseEvent.getClickCount() == 1) {
          TreePath path = tree.getClosestPathForLocation(mouseEvent.getX(), mouseEvent.getY());
          if (path != null) {
            EfsPageTreeNode node = (EfsPageTreeNode) path.getLastPathComponent();
            tree.setSelectionPath(path);
            JPopupMenu popup = new JPopupMenu();
            if (node.hasDuplicates()) {
              JMenuItem showDuplicates = new JMenuItem("Show duplicates");
              showDuplicates.addActionListener(ae -> {
                DuplicateDialog dia = new DuplicateDialog(node.getPlsElement(), node.getDuplicates());
                dia.setVisible(true);
              });
              popup.add(showDuplicates);
            }

            popup.show(tree, mouseEvent.getX(), mouseEvent.getY());
          }
        } else if (mouseEvent.getButton() == MouseEvent.BUTTON1 && mouseEvent.getClickCount() == 2) {
          TreePath path = tree.getClosestPathForLocation(mouseEvent.getX(), mouseEvent.getY());
          if (path != null) {
            EfsPageTreeNode node = (EfsPageTreeNode) path.getLastPathComponent();
            if (node.hasDuplicates()) {
              DuplicateDialog dia = new DuplicateDialog(node.getPlsElement(), node.getDuplicates());
              dia.setVisible(true);
            }
          }
        }
      }
    });
    tree.setCellRenderer(new DefaultEfsTreeCellRenderer(result));
    return tree;
  }

  @Override
  protected JPanel createBottomPanel() {
    JPanel bottom = new JPanel(new BorderLayout());
    JPanel preSelectionPanel = new JPanel(new BorderLayout());
    preSelectionPanel.add(new JLabel("Default PR Number Configuration: "), BorderLayout.WEST);
    PrConfiguration[] prconfigs = { new PrConfiguration("veron-4240",
      "0A2 0AD 0B1 0BA 0EJ 0F5 0FA 0K0 0N1 0NB 0P0 0TA 0VA 0Y1 0YA 1AT 1D0 1E0 1EB 1G8 1JA 1KE 1MM 1N3 1NC 1PA 1Q2 1S3 1SA 1T0 1U8 1W2 1X0 1Y3 1Z0 1ZE 20A 2A0 2G5 2H0 2JB 2KW 2V1 2WA 31F 3B4 3C7 3CA 3D1 3FA 3H0 3L1 3NZ 3QT 3S0 3T2 3ZB 4A0 4AF 4GF 4I2 4KC 4L2 4P0 4R4 4UP 4X3 5C0 5D1 5J0 5K7 5MA 5RQ 5SL 5XS 5ZF 6A0 6E0 6FA 6K0 6KC 6NA 6Q1 6T0 6XN 6Y8 7AA 7B0 7E0 7K9 7L6 7LG 7M5 7MM 7P0 7Q0 7QA 7W0 7X0 7Y0 8G0 8GU 8IE 8K1 8M1 8N4 8Q1 8RE 8S1 8SQ 8SQ 8T0 8TB 8W0 8WA 8X0 8Y1 8Z5 8ZQ 9D0 9F0 9JA 9M0 9P3 9S5 9T0 9TC 9U1 9W0 9WC 9Z0 A61 A8B AV1 B00 B0A C36 CX9 DK8 E0A EA0 EF0 EL0 EM1 ER1 ES0 EV0 F0A FB0 FC0 FM0 G05 G0C GM1 GP1 H7F I8C J0V K8G KA0 KD0 KH6 KK1 KL0 L01 L0L N4C NY0 NZ0 Q1A QG1 QH0 QI6 QJ0 QK0 QN0 QQ0 QR8 QV0 TJ1 U5A UF0 UH2 UK1 V0A VF0 VL0",
      "2018-07-30"), new PrConfiguration("veron-3990",
      "E0A 1PA 2CC 0P0 UG1 4BF 1D0 4UF 1E5 8ZB 6XD EA0 5SL 5RQ 0K0 1X0 9WC A9G 6K2 1KA J1H 1ZS 6FF NY0 B0A 1S0 1AS 0VC 9T0 2H0 QJ0 C00 3S0 3FA 1JB G07 EF0 0EM 7AA 5MA 7M5 7X0 7W2 U9B 0FB 51A 4R4 VL6 VF0 8K3 2V1 F0A 8GG 0YB L23 TJ4 4U0 3U1 8T9 9E1 G0C 4Z4 6PA 8M1 0N6 6NA 3NQ 9AA 8ID FC0 6T0 U5A 4L2 0KA K8G QK2 0Y1 3Q6 5ZF 1Z0 A60 1A7 KA0 KK0 31K 5K7 3GA QQ0 L0L 8S3 1N1 3H0 8G0 7P0 1MM AV1 8RD 2C5 8Q1 6E0 9S5 EM0 8Z5 DI6 AW5 7Q0 8TL 8WA EL0 GP0 4P0 1ND CY0 I8I 9JA ER1 7K1 H9D V0A 1G8 0B1 4QV NZ2 4X3 6Q1 2KW 8VG QI6 9P4 3ZU 3C7 N4C 3L1 4A0 7L6 8Y0 6DF QH0 7Y9 4KC 2JF 0AR QN0 8X0 8N8 0NB 4I2 B01 5D1 4AP 0A2 QV0 1SA 1EX 2A0 00A 1T9 Q1A 9ZX QG1 7E0 4GF 8W0 8FA 2WA 0TA 4ZE 3A2 6U0",
      "2019-04-05"), new PrConfiguration("veron-4497",
      "4G0 E0A UD0 6H0 1PA 1CV 0GA 1D0 4UF 4S0 8ZQ 6XN EA0 GM1 5JA 1X2 J9A 9WC A8B 8J3 1KA J2S 1LA 6FG B0A KB2 1S2 0VA 9Z0 9T0 2H5 C00 GW2 LH1 3FA 1JA G01 7AA 7X2 7W0 U9B 0FA VF0 7G0 F0A 5LC 0YE L05 N06 8T9 G1Z IN0 3NZ KC0 8IR KS0 FC0 4N0 4L2 K8G 0Y1 3T2 5C0 KA0 KK3 31K 5K7 3GA QQ8 ES7 L0L 1N3 IG0 8G0 EV0 76C 7P0 2FB AV1 8RS 3D1 EM1 L1H 7UJ EL5 GP0 FT0 7PA 1NC F74 8AS ER1 7K1 J49 3QE NZ2 6C2 0SA 8VG 4E0 G9C 2F1 9P4 N0U 3L1 4A0 JX0 8Y0 5XJ QH0 7Y0 6I1 4KC 0BC 0AD 8N5 0NB 4I7 B00 4AF QV0 1EX 1T0 1J0 9ZX 7E6 4GS 2WA 0TA 3A2 0RH 9M0",
      "2020-05-18") };
    JComboBox<PrConfiguration> prConfigCbx = new JComboBox<>(prconfigs);
    prConfigCbx.addItemListener(itemEvent -> {
      PrConfiguration config = (PrConfiguration) itemEvent.getItem();
      prNumberArea.setText(config.getPrNumber());
      validDateField.setText(config.getDate());
    });
    preSelectionPanel.add(prConfigCbx, BorderLayout.CENTER);
    bottom.add(preSelectionPanel, BorderLayout.NORTH);

    prNumberArea = new JTextArea(
      " 0A2 0AD 0B1 0BA 0EJ 0F5 0FA 0K0 0N1 0NB 0P0 0TA 0VA 0Y1 0YA 1AT 1D0 1E0 1EB 1G8 1JA 1KE 1MM 1N3 1NC 1PA 1Q2 1S3 1SA 1T0 1U8 1W2 1X0 1Y3 1Z0 1ZE 20A 2A0 2G5 2H0 2JB 2KW 2V1 2WA 31F 3B4 3C7 3CA 3D1 3FA 3H0 3L1 3NZ 3QT 3S0 3T2 3ZB 4A0 4AF 4GF 4I2 4KC 4L2 4P0 4R4 4UP 4X3 5C0 5D1 5J0 5K7 5MA 5RQ 5SL 5XS 5ZF 6A0 6E0 6FA 6K0 6KC 6NA 6Q1 6T0 6XN 6Y8 7AA 7B0 7E0 7K9 7L6 7LG 7M5 7MM 7P0 7Q0 7QA 7W0 7X0 7Y0 8G0 8GU 8IE 8K1 8M1 8N4 8Q1 8RE 8S1 8SQ 8SQ 8T0 8TB 8W0 8WA 8X0 8Y1 8Z5 8ZQ 9D0 9F0 9JA 9M0 9P3 9S5 9T0 9TC 9U1 9W0 9WC 9Z0 A61 A8B AV1 B00 B0A C36 CX9 DK8 E0A EA0 EF0 EL0 EM1 ER1 ES0 EV0 F0A FB0 FC0 FM0 G05 G0C GM1 GP1 H7F I8C J0V K8G KA0 KD0 KH6 KK1 KL0 L01 L0L N4C NY0 NZ0 Q1A QG1 QH0 QI6 QJ0 QK0 QN0 QQ0 QR8 QV0 TJ1 U5A UF0 UH2 UK1 V0A VF0 VL0");
    prNumberArea.setLineWrap(true);
    prNumberArea.setRows(4);
    bottom.add(new JScrollPane(prNumberArea), BorderLayout.CENTER);

    JPanel rightPanel = new JPanel(new BorderLayout());
    validDateField = new JTextField();
    validDateField.setText("2018-07-30");
    rightPanel.add(validDateField, BorderLayout.NORTH);
    JButton btn = new JButton("Apply");
    btn.addActionListener(a -> {
      bgExec.doInBackground("Apply pr numbers", (processMonitor) -> {
        openEFS(processMonitor);
        EventBus.publish(Topic.CONFIG_CHANGED, creator);
      });
    });
    rightPanel.add(btn, BorderLayout.CENTER);

    bottom.add(rightPanel, BorderLayout.EAST);
    prConfigCbx.setSelectedIndex(1);
    return bottom;
  }

  private void openEFS(ProcessMonitor processMonitor) {
    openEFS(ppfFile, true, processMonitor);
  }

  public void openEFS(File ppfFile, boolean restoreExpansionState, ProcessMonitor processMonitor) {
    try {
      processMonitor.setCurrentStep("Create part list from PPF");
      clearSearch();
      String expansionState = TreeUtil.getExpansionState(tree, 0);
      this.ppfFile = ppfFile;

      List<String> lines = Files.readLines(ppfFile, Charset.defaultCharset());

      config = new PartListCreationConfiguration(lines.iterator(), prNumberArea.getText(),
        LocalDate.parse(validDateField.getText()));
      creator = new PartListCreator();
      result = creator.createPartList(config);
      tree.setCellRenderer(new DefaultEfsTreeCellRenderer(result));
      processMonitor.setCurrentStep("Prepare data for ContextVisualizer");
      treeNodeByNodeIdMap = new HashMap<>();
      duplicatesByNodeId = MultimapBuilder.ListMultimapBuilder.hashKeys().arrayListValues().build();
      for (String key : result.getEfsElementByNodeIds()) {
        List<PlsEfsElement> elementsForId = result.getEfsElementByNodeId(key);
        elementsForId.forEach(element -> {
          if (element.getDuplicateId() != null) {
            duplicatesByNodeId.put(element.getDuplicateId(), element);
          }
        });
      }

      processMonitor.setCurrentStep("Show data in View/Tree");
      Map<PlsEfsElement, Integer> countMap = new HashMap<>();
      countChildren(countMap, result.getRootElement());
      if (result.getRootElement() != null) {
        tree.setModel(new DefaultTreeModel(createTreeNode(countMap, result.getRootElement())));
      } else {
        tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("No valid part found")));
      }

      if (restoreExpansionState) {
        processMonitor.setCurrentStep("Restore tree expansion state");
        TreeUtil.restoreExpanstionState(tree, 0, expansionState);
      }

    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Could not createPart List: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private DefaultMutableTreeNode createTreeNode(Map<PlsEfsElement, Integer> countMap, PlsEfsElement plsEfsElement) {
    EfsPageTreeNode node = new EfsPageTreeNode(plsEfsElement, countMap);
    treeNodeByNodeIdMap.put(new NodeIdentifier(plsEfsElement.getOriginNodeId()), node);
    if (plsEfsElement.isPartFound()) {
      treeNodeByNodeIdMap.put(new NodeIdentifier(plsEfsElement.getOriginNodeId(), plsEfsElement.getPartSort()), node);
    }
    if (plsEfsElement.isEbk()) {
      treeNodeByNodeIdMap.put(new NodeIdentifier(plsEfsElement.getOriginNodeId(), plsEfsElement.getPartSort(),
        PartNumberUtil.toString(plsEfsElement.getPartNumber())), node);
    }

    for (PlsEfsElement ele : plsEfsElement.getChildren()) {
      node.add(createTreeNode(countMap, ele));
    }
    return node;
  }

  @Override
  protected TableModel createTablemodel(DefaultMutableTreeNode... node) {

    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Property");
    PlsEfsElement[] elements = new PlsEfsElement[node.length];
    for (int i = 0; i < node.length; i++) {
      model.addColumn("Value");
      if (node[i].getUserObject() instanceof PlsEfsElement) {
        PlsEfsElement plsElement = (PlsEfsElement) node[i].getUserObject();
        elements[i] = plsElement;
      }
    }
    addGetter(model, elements);
    return model;
  }

  @Override
  public NodeIdentifier getNodeId(DefaultMutableTreeNode node) {
    Object userObject = node.getUserObject();
    if (userObject instanceof PlsEfsElement) {
      PlsEfsElement plsElement = (PlsEfsElement) node.getUserObject();
      if (plsElement.isEbk()) {
        return new NodeIdentifier(plsElement.getOriginNodeId(), plsElement.getPartSort(),
          PartNumberUtil.toString(plsElement.getPartNumber()));
      }
      return new NodeIdentifier(plsElement.getOriginNodeId(), plsElement.getPartSort());
    }
    return null;
  }

  @Override
  protected DefaultMutableTreeNode getTreeNode(NodeIdentifier nodeId) {
    return treeNodeByNodeIdMap.get(nodeId);
  }

  public void addRules(List<String> rules) {
    Set<String> rulesToAdd = new HashSet<>();
    for (String rule : rules) {
      String[] split = rule.split("\\+");
      for (String part : split) {
        if (part.contains("/")) {
          String[] or = part.split("/");
          rulesToAdd.add(or[0]);
        } else {
          rulesToAdd.add(part);
        }
      }
    }
    rulesToAdd.forEach(e -> prNumberArea.append(e + " "));
  }

  private int countChildren(Map<PlsEfsElement, Integer> resutl, PlsEfsElement startNode) {
    int count = 0;
    if (startNode != null && startNode.hasChildren()) {
      for (PlsEfsElement child : startNode.getChildren()) {
        count += 1 + countChildren(resutl, child);
      }
    }
    resutl.put(startNode, count);
    return count;
  }

  public PartListCreationConfiguration getConfig() {
    return config;
  }

  public void setResult(PageTreeCompareResult result) {
    tree.setCellRenderer(new CompareEfsTreeCellRenderer(result.getEfsStatusMap()));
  }

  public PartListCreator getCreator() {
    return creator;
  }
}
