package de.vw.paso.pll.dev.contextvisualizer.ppf;

import de.vw.paso.pll.FileUtil;
import de.vw.paso.pll.PPFUtil;
import de.vw.paso.pll.creation.PartChecker;
import de.vw.paso.pll.creation.PartListCreator;
import de.vw.paso.pll.creation.PartListCreatorUtil;
import de.vw.paso.pll.dev.contextvisualizer.checker.NotRelevantChecker;
import de.vw.paso.pll.dev.contextvisualizer.util.*;
import de.vw.paso.pll.dev.contextvisualizer.util.components.VisPage;
import de.vw.paso.pll.dev.contextvisualizer.util.event.Topic;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.formats.ppf.EbomPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;
import de.vw.paso.pll.preprocessing.formats.ppf.PPF;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbkVsdFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbomFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.NodeFields;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class PPFPage extends VisPage {

  private JLabel ppfSizeLabel;
  private File ppfFile;

  private Map<NodeIdentifier, DefaultMutableTreeNode> nodeByNodeIdMap;

  private Consumer<List<String>> addRuleAction;

  public PPFPage(BackgroundExecutor bgExec) {
    super("PPF - PreProcessedFormat", bgExec);
  }

  @Override
  protected JTree createTree() {
    JTree tree = super.createTree();
    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
          TreePath path = tree.getClosestPathForLocation(mouseEvent.getX(), mouseEvent.getY());
          if (path != null) {
            JPopupMenu popup = new JPopupMenu();

            tree.setSelectionPath(path);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = node.getUserObject();

            boolean add = false;
            if (userObject instanceof NodePPF) {
              add = true;
              NodePPF ppf = (NodePPF) userObject;
              if (!ppf.getEboms().isEmpty()) {
                JMenuItem addRule = new JMenuItem("Make part valid");
                addRule.addActionListener(ae -> addRuleAction.accept(Collections.singletonList(ppf.getEboms().first().getEbom().getPrNrRule())));
                popup.add(addRule);
              }
              JMenuItem addRule = new JMenuItem("Make subtree valid");
              List<String> rules = collectRules(ppf);
              addRule.addActionListener(ae -> addRuleAction.accept(rules));
              popup.add(addRule);

            } else if (userObject instanceof EbomPPF) {
              add = true;
              JMenuItem addRule = new JMenuItem("Make part valid");
              addRule.addActionListener(ae -> addRuleAction.accept(Collections.singletonList(((EbomPPF) userObject).getEbom().getPrNrRule())));
              popup.add(addRule);
            }
            if (add) {
              popup.show(tree, mouseEvent.getX(), mouseEvent.getY());
            }
          }
        }
      }

      private List<String> collectRules(NodePPF ppf) {
        List<String> rules = new ArrayList<>();
        SortedSet<EbomPPF> eboms = ppf.getEboms();
        if (!eboms.isEmpty()) {
          rules.add(eboms.first().getEbom().getPrNrRule());
        }
        for (NodePPF child : ppf.getChildren()) {
          rules.addAll(collectRules(child));
        }
        return rules;
      }
    });
    tree.setCellRenderer(new DefaultTreeCellRenderer() {

      Font defaultFont;

      @Override
      public Component getTreeCellRendererComponent(JTree jTree, Object node, boolean b, boolean b1, boolean b2, int i, boolean b3) {
        JLabel comp = (JLabel) super.getTreeCellRendererComponent(jTree, node, b, b1, b2, i, b3);
        comp.setText(comp.getText().replace("\t", " "));
        if (defaultFont == null) {
          defaultFont = comp.getFont();
        }
        comp.setForeground(Color.BLACK);
        comp.setFont(defaultFont);
        comp.setToolTipText(comp.getText());
        if (node instanceof AbstractPPFTreeNode) {
          AbstractPPFTreeNode ppfNode = (AbstractPPFTreeNode) node;
          ImageIcon icon = null;
          if (node instanceof NodePPFTreeNode) {
            icon = Icons.PPF.PPF_N;
          } else if (node instanceof EbomPPFTreeNode) {
            icon = Icons.PPF.PPF_P;
          } else if (node instanceof EbkvsdPPFTreeNode) {
            icon = Icons.PPF.PPF_B;
          }
          if (ppfNode.getFilterResult().isFilteredOut()) {
            Font font = defaultFont.deriveFont(Font.ITALIC);
            comp.setFont(font);
            comp.setForeground(Color.gray);
          }
          if (!ppfNode.isRuleActive()) {
            HashMap attributes = new HashMap(comp.getFont().getAttributes());
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            comp.setFont(comp.getFont().deriveFont(attributes));
          }
          if (icon != null) {
            comp.setIcon(icon);
          }
        }
        return comp;
      }
    });
    return tree;
  }

  @Override
  protected void configureButtonPanel(JPanel buttonPanel) {
    super.configureButtonPanel(buttonPanel);

    JButton exportPPF = new JButton("EXP");
    exportPPF.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        int[] selectionRows = tree.getSelectionModel().getSelectionRows();
        if (selectionRows != null && selectionRows.length > 0) {
          Arrays.sort(selectionRows);
          StringBuilder sb = new StringBuilder();
          for (int i : selectionRows) {
            TreePath path = tree.getPathForRow(i);
            AbstractPPFTreeNode node = (AbstractPPFTreeNode) path.getLastPathComponent();
            sb.append(String.join(PPFUtil.FIELD_SEPARATOR, node.getData())).append("\n");
          }
          JTextArea area = new JTextArea(sb.toString());
          JScrollPane sp = new JScrollPane(area);
          sp.setPreferredSize(new Dimension(700, 400));
          JOptionPane.showMessageDialog(null, sp);
        }
      }
    });
    buttonPanel.add(exportPPF);
  }

  @Override
  protected JPanel createBottomPanel() {
    ppfSizeLabel = new JLabel(getSizeText(null));
    JPanel utilBar = new JPanel();
    utilBar.add(ppfSizeLabel, BorderLayout.CENTER);
    JButton createFile = new JButton("Open File");
    createFile.addActionListener(a -> {
      if (ppfFile != null) {
        try {
          de.vw.paso.pll.dev.contextvisualizer.util.FileUtil.openTextFile(ppfFile);
        } catch (IOException e) {
          JOptionPane.showMessageDialog(null, "Could not open file: " + e.getMessage());
          e.printStackTrace();
        }
      }
    });
    utilBar.add(createFile, BorderLayout.EAST);
    return utilBar;
  }

  @Override
  protected TableModel createTablemodel(DefaultMutableTreeNode... nodes) {
    DefaultTableModel defModel = new DefaultTableModel();
    defModel.addColumn("Property");
    for (DefaultMutableTreeNode node : nodes) {
      defModel.addColumn("Value");
    }
    AbstractPPFTreeNode first = (AbstractPPFTreeNode) nodes[0];


    for (Enum nodeField : first.getFields()) {
      Object[] row = new Object[nodes.length + 1];
      row[0] = nodeField;
      for (int i = 0; i < nodes.length; i++) {
        AbstractPPFTreeNode node = (AbstractPPFTreeNode) nodes[i];
        if (node.getClass().equals(first.getClass())) {
          row[i + 1] = node.getData(nodeField);
        }
      }
      defModel.addRow(row);
    }
    return defModel;
  }

  private String getSizeText(PreprocessingContext ctx) {
    if (ctx == null) {
      return "No Partlist selected";
    }
    try {
      return "PreprocessingContext size/ResultFile Size:  " + getSize(ctx) + "/" + FileUtil.formatSize(Files.size(Paths.get(ppfFile.getAbsolutePath())));
    } catch (IOException e) {
      e.printStackTrace();
      return "Could not calculate size";
    }
  }

  private String getSize(PreprocessingContext ctx) {
//    long objectSize = ObjectSizeCalculator.getObjectSize(ctx);
//    return FileUtil.formatSize(objectSize);
    return "";
  }

  public void openPPF(PreprocessingContext newCtx, File ppfFile, boolean restoreExpansion, ProcessMonitor pm) throws IOException {
    clearSearch();
    pm.setCurrentStep("Save previous tree expansion state");
    String expansionState = TreeUtil.getExpansionState(tree, 0);
    this.ppfFile = ppfFile;
    pm.setCurrentStep("Create and set PPF tablemodel");
    TreeModel treeModel = createTreeModel(ppfFile);
    tree.setModel(treeModel);
    detailsTable.setModel(new DefaultTableModel());

    pm.setCurrentStep("Calculate ppf size");
    ppfSizeLabel.setText(getSizeText(newCtx));
    if (restoreExpansion && tree != null) {
      pm.setCurrentStep("Restore expansion state");
      TreeUtil.restoreExpanstionState(tree, 0, expansionState);
    }
  }

  private TreeModel createTreeModel(File file) throws IOException {
    nodeByNodeIdMap = new HashMap<>();
    Map<String, DefaultMutableTreeNode> ebkMap = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

      int filePart = 0;
      while (filePart < 2) {
        String line = reader.readLine();
        if (line.equals(PPFUtil.SECTION_SEPARATOR)) {
          filePart++;
        }
      }

      AbstractPPFTreeNode root = null;
      NodePPFTreeNode lastPPFNode = null;
      EbomPPFTreeNode lastPPFEbom = null;
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        PPF lineType = PartListCreatorUtil.getLineType(line);
        String[] data = PartListCreatorUtil.splitData(line, lineType);
        if (lineType.equals(PPF.NODE)) {
          NodePPFTreeNode ppfTreeNode = new NodePPFTreeNode(data);
          nodeByNodeIdMap.put(ppfTreeNode.createNodeIdentifier(), ppfTreeNode);

          String parentId = data[NodeFields.NODE_PARENT_ID.ordinal() + 1];
          DefaultMutableTreeNode parentNode = nodeByNodeIdMap.get(new NodeIdentifier(parentId));
          if (parentNode != null) {
            parentNode.add(ppfTreeNode);
          }
          if (root == null) {
            root = ppfTreeNode;
          }
          lastPPFNode = ppfTreeNode;
        } else if (PPF.PART.equals(lineType)) {
          ebkMap = new HashMap<>();
          EbomPPFTreeNode ppfEbom = new EbomPPFTreeNode(lastPPFNode.getData(NodeFields.NODE_ID), data, lastPPFNode.getData());
          nodeByNodeIdMap.put(ppfEbom.createNodeIdentifier(), ppfEbom);
          lastPPFNode.add(ppfEbom);
          lastPPFEbom = ppfEbom;
        } else if (PPF.EBK.equals(lineType)) {
          EbkvsdPPFTreeNode ppfEbk = new EbkvsdPPFTreeNode(data);
          nodeByNodeIdMap.put(ppfEbk.createNodeIdentifier(), ppfEbk);
          String partNumber = ppfEbk.getData(EbkVsdFields.BAUKASTEN_PARTNUMBER);
          ebkMap.put(partNumber, ppfEbk);

          String ebkParentPartnumber = ppfEbk.getData(EbkVsdFields.BAUKASTEN_PARTNUMBER_PARENT);
          String lastEbomPartNumber = lastPPFEbom.getData(EbomFields.PART_NUMBER);
          if (lastEbomPartNumber.equals(ebkParentPartnumber)) {
            lastPPFEbom.add(ppfEbk);
          } else {
            DefaultMutableTreeNode parentNode = ebkMap.get(ebkParentPartnumber);
            if (parentNode != null) {
              parentNode.add(ppfEbk);
            }
          }
        }
      }

      return new DefaultTreeModel(root);
    }
  }

  @Override
  public NodeIdentifier getNodeId(DefaultMutableTreeNode node) {
    if (node instanceof AbstractPPFTreeNode) {
      return ((AbstractPPFTreeNode) node).createNodeIdentifier();
    } else {
      return null;
    }
  }

  @Override
  protected DefaultMutableTreeNode getTreeNode(NodeIdentifier nodeId) {
    return nodeByNodeIdMap.get(nodeId);
  }

  public void setAddRuleAction(Consumer<List<String>> addRuleAction) {
    this.addRuleAction = addRuleAction;
  }

  public void setPartChecker(PartChecker checker) {
    AbstractPPFTreeNode root = (AbstractPPFTreeNode) tree.getModel().getRoot();
    root.accept(new NotRelevantChecker(checker));
  }

  @Override
  public void onEvent(Topic topic, Object obj) {
    super.onEvent(topic, obj);
    if (topic == Topic.CONFIG_CHANGED) {
      AbstractPPFTreeNode root = (AbstractPPFTreeNode) tree.getModel().getRoot();
      PartListCreator creator = (PartListCreator) obj;
      root.accept(new NotRelevantChecker(creator.getPartChecker()));
    }
  }
}
