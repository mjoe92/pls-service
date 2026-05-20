package de.vw.paso.pll.dev.contextvisualizer.util.components;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.vw.paso.pll.PPFUtil;
import de.vw.paso.pll.dev.contextvisualizer.util.BackgroundExecutor;
import de.vw.paso.pll.dev.contextvisualizer.util.Icons;
import de.vw.paso.pll.dev.contextvisualizer.util.NodeIdentifier;
import de.vw.paso.pll.dev.contextvisualizer.util.ReflectionUtil;
import de.vw.paso.pll.dev.contextvisualizer.util.event.EventBus;
import de.vw.paso.pll.dev.contextvisualizer.util.event.Topic;
import de.vw.paso.pll.model.PlsEfsElement;
import de.vw.paso.pll.model.QuantityUnit;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.lang3.StringUtils;

public abstract class VisPage extends JPanel implements EventBus.EventBusListener {

  private JTextField searchField;
  private JButton recentSearchesBtn;
  protected JTree tree;
  protected JTable detailsTable;

  private List<String> recentSearchItems = new ArrayList<>();
  private List<TreePath> foundPaths = new ArrayList<>();
  private int currentSearchIndex = 0;

  private boolean selecting = false;
  private TreeSelectionListener treeSelectionListener;

  Table<String, Class<?>, ReflectionUtil.MethodChain> searches = HashBasedTable.create();

  protected BackgroundExecutor bgExec;

  protected VisPage(String title, BackgroundExecutor bgExec) {
    this.bgExec = bgExec;
    setLayout(new BorderLayout());
    add(createTopPanel(title), BorderLayout.NORTH);
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setTopComponent(new JScrollPane(createTree()));
    detailsTable = createDetailsTable();
    splitPane.setBottomComponent(new JScrollPane(detailsTable));
    splitPane.setDividerLocation(600);
    add(splitPane, BorderLayout.CENTER);
    add(createBottomPanel(), BorderLayout.SOUTH);
    EventBus.register(this);
  }

  protected JPanel createTopPanel(String title) {
    GridBagLayout layout = new GridBagLayout();
    layout.columnWeights = new double[]{1,0, 0};
    JPanel p = new JPanel(layout);
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(titleLabel.getFont().deriveFont(24f));
    GridBagConstraints con = new GridBagConstraints();
    con.gridx = 0;
    con.gridy = 0;
    con.anchor = GridBagConstraints.CENTER;
    con.gridwidth = 3;
    con.insets = new Insets(5, 5, 5, 5);
    p.add(titleLabel, con);

    searchField = new JTextField();
    searchField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
          if (!recentSearchItems.isEmpty()) {
            String s = recentSearchItems.get(recentSearchItems.size() - 1);
            if (!s.equals(searchField.getText())) {
              recentSearchItems.add(searchField.getText());
            }
          } else {
            recentSearchItems.add(searchField.getText());
          }
          if (recentSearchItems.size() > 11) {
              recentSearchItems.remove(0);
          }


          if (foundPaths.isEmpty()) {
            foundPaths = searchToString(searchField.getText(), (DefaultMutableTreeNode) tree.getModel().getRoot());
          }
          selectNext();
        } else {
          foundPaths.clear();
          currentSearchIndex = 0;
        }
      }
    });
    con = new GridBagConstraints();
    con.gridx = 0;
    con.gridy = 1;
    con.fill = GridBagConstraints.BOTH;
    p.add(searchField, con);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
    configureButtonPanel(buttonPanel);
    con = new GridBagConstraints();
    con.gridx = 1;
    con.gridy = 1;
    con.fill = GridBagConstraints.BOTH;
    p.add(buttonPanel, con);

    return p;
  }

  protected void configureButtonPanel(JPanel buttonPanel) {
    recentSearchesBtn = new JButton(Icons.SEARCH_RECENT);
    recentSearchesBtn.addActionListener(ae -> {
      JPopupMenu popup = new JPopupMenu();
      for (int i = recentSearchItems.size() - 1; i >= 0; i--) {
        String item = recentSearchItems.get(i);
        popup.add(new AbstractAction(item) {
          @Override
          public void actionPerformed(ActionEvent actionEvent) {
            searchField.setText(item);
            if (foundPaths.isEmpty()) {
              foundPaths = searchToString(searchField.getText(), (DefaultMutableTreeNode) tree.getModel().getRoot());
            }
            selectNext();
            SwingUtilities.invokeLater(() -> searchField.requestFocus());
          }
        });
      }
      popup.show(recentSearchesBtn, 0, recentSearchesBtn.getHeight());
    });
    buttonPanel.add(recentSearchesBtn);
    JButton collapseButton = new JButton(Icons.COLLAPSE);
    collapseButton.addActionListener(ae -> {
      for (int i = tree.getRowCount() -1; i > 1; i--){
        tree.collapseRow(i);
      }
    });
    buttonPanel.add(collapseButton);
  }

  protected JTree createTree() {
    tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode())){
      @Override
      public String getToolTipText(MouseEvent mouseEvent) {
        if (getRowForLocation(mouseEvent.getX(), mouseEvent.getY()) == -1) return null;
        TreePath curPath = getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        Object node = curPath.getLastPathComponent();
        if (node instanceof TooltipProviderNode) {
          return ((TooltipProviderNode) node).getToolTip();
        }
        return null;
      }
    };
    ToolTipManager.sharedInstance().registerComponent(tree);

    tree.getSelectionModel().addTreeSelectionListener(treeSelectionEvent -> {

      TreePath[] selectionPaths = tree.getSelectionPaths();
      if (selectionPaths != null && selectionPaths.length > 0) {
        DefaultMutableTreeNode[] selectedNodes = new DefaultMutableTreeNode[selectionPaths.length];
        for (int i = 0; i < selectionPaths.length;i++) {
          TreePath selectionPath = selectionPaths[i];
          selectedNodes[i] = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        }
        detailsTable.setModel(createTablemodel(selectedNodes));
      } else {
        detailsTable.setModel(new DefaultTableModel());
      }
    });
    return tree;
  }

  protected abstract TableModel createTablemodel(DefaultMutableTreeNode... node);

  public static void addGetter(DefaultTableModel model, PlsEfsElement... elements) {
    if (elements != null && elements.length > 0 && elements[0] != null) {
      Method[] methods = elements[0].getClass().getMethods();
      List<Method> sortedMEthods = Arrays.asList(methods);
      sortedMEthods.sort(Comparator.comparing(Method::getName));
      for (Method m : sortedMEthods) {
        if (isGetter(m)) {
          Object[] row = new Object[elements.length + 1];
          row[0] = m.getName();
          for (int i = 0; i < elements.length; i++) {
            try {
              if (isReturnType(m, String.class, int.class, Number.class, QuantityUnit.class, Boolean.class, boolean.class)) {
                row[i + 1] = m.invoke(elements[i]);
              } else if (isReturnType(m, Date.class)) {
                row[i + 1] = PPFUtil.formatDate((Date) m.invoke(elements[i]));
              }
            } catch (Exception e) {
              row[i + 1] = "[ERROR]";
            }
          }
          model.addRow(row);
        }
      }
    } else {
      model.addRow(new Object[]{"not set"});
    }
  }

  private static boolean isGetter(Method m) {
    return (m.getName().startsWith("get") || m.getName().startsWith("is")) && m.getParameterCount() == 0;
  }

  private static boolean isReturnType(Method m, Class<?>... types) {
    if (types != null) {
      for (Class<?> clazz : types) {
        if (clazz.isAssignableFrom(m.getReturnType())) {
          return true;
        }
      }
    }
    return false;
  }

  public void setJumpOnSelection(boolean enable) {
    if (enable) {
      treeSelectionListener = e -> {
        TreePath path = tree.getSelectionPath();
        if (path != null && !selecting) {
          selecting = true;
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
          fireNodeSelectedEvent(getNodeId(node));
          selecting = false;
        }
      };
      tree.getSelectionModel().addTreeSelectionListener(treeSelectionListener);

    } else if (treeSelectionListener != null) tree.getSelectionModel().removeTreeSelectionListener(treeSelectionListener);
  }

  public abstract NodeIdentifier getNodeId(DefaultMutableTreeNode node);

  private JTable createDetailsTable() {
    detailsTable = new JTable();
    detailsTable.setDefaultRenderer(Object.class, new AlternatingBackgroundTableCellRenderer());
    detailsTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3 && mouseEvent.getClickCount() == 1) {
          int i = detailsTable.rowAtPoint(mouseEvent.getPoint());
          detailsTable.getSelectionModel().setSelectionInterval(i,i);
          if (i > -1) {
            JPopupMenu menu = new JPopupMenu();
            menu.add(new AbstractAction("Search") {
              @Override
              public void actionPerformed(ActionEvent actionEvent) {
                Object getter = detailsTable.getModel().getValueAt(i, 0);
                Object value = detailsTable.getModel().getValueAt(i, 1);
                searchField.setText("#" + getter + "=" + value);
              }
            });
            menu.show(detailsTable, mouseEvent.getX(), mouseEvent.getY());
          }
        }
      }
    });
    return detailsTable;
  }

  protected abstract JPanel createBottomPanel();

  private List<TreePath> searchToString(String text, DefaultMutableTreeNode node) {
    String trimmedText = text.trim();
    Function<DefaultMutableTreeNode, String> searchFunction;
    String searchText;
    if (trimmedText.startsWith("#")) {
      int splitIndex = trimmedText.indexOf("=");
      String getter = trimmedText.substring(1, splitIndex);
      searchText = trimmedText.substring(splitIndex + 1);
      searchFunction = searchNode -> findValueForGetter(getter, searchNode.getUserObject());
    } else {
      searchFunction = e -> e.toString().toLowerCase();
      searchText = text;
    }
    return search(searchText, node, searchFunction);
  }

  private String findValueForGetter(String methodName, Object obj) {
    if (obj != null) {
      ReflectionUtil.MethodChain methodChain = searches.get(methodName, obj.getClass());
      if (methodChain == null) {
        methodChain = ReflectionUtil.findChain(methodName, obj);
        if (methodChain != null) {
          searches.put(methodName, obj.getClass(), methodChain);
        }
      }

      if (methodChain != null) {
        Object value = methodChain.getValue(obj);
        if (value != null) {
          return value.toString();
        }
      }
    }
    return "";
  }

  private List<TreePath> search(String text, DefaultMutableTreeNode node, Function<DefaultMutableTreeNode, String> getter) {
    List<TreePath> results = new ArrayList<>();
    String value = getter.apply(node);
    if (StringUtils.isEmpty(text)) {
      if (StringUtils.isEmpty(value)) {
        results.add(getPath(node));
      }
    } else {
      if (value.toLowerCase().contains(text.toLowerCase())) {
        results.add(getPath(node));
      }
    }
    for (int i = 0; i < node.getChildCount(); i++) {
      DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) node.getChildAt(i);
      results.addAll(search(text, childAt, getter));
    }
    return results;
  }

  private TreePath getPath(DefaultMutableTreeNode node) {
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    TreeNode[] toSelect = model.getPathToRoot(node);
    return new TreePath(toSelect);
  }

  private void selectNext() {
    if (!foundPaths.isEmpty()) {
      TreePath toSelect = foundPaths.get(currentSearchIndex++);
      tree.setSelectionPath(toSelect);
      if (currentSearchIndex == foundPaths.size()) {
        currentSearchIndex = 0;
      }
      tree.scrollPathToVisible(toSelect);
    }
  }

  public void showNode(NodeIdentifier id) {
    DefaultMutableTreeNode node = getTreeNode(id);
    if (node != null && !selecting){
      selecting = true;
      TreePath path = new TreePath(node.getPath());
      tree.setSelectionPath(path);
      tree.scrollPathToVisible(path);
      selecting = false;
    } else {
      selecting = true;
      tree.clearSelection();
      selecting = false;
    }
  }

  protected abstract DefaultMutableTreeNode getTreeNode(NodeIdentifier nodeId);

  protected void clearSearch() {
    foundPaths.clear();
    currentSearchIndex = 0;
  }

  boolean nodeSelectionEventRunning = false;
  protected void fireNodeSelectedEvent(NodeIdentifier ni) {
    nodeSelectionEventRunning = true;
    EventBus.publish(Topic.NODE_SELECTED, ni);
    nodeSelectionEventRunning = false;
  }

  @Override
  public void onEvent(Topic topic, Object obj) {
    switch (topic) {
      case NODE_SELECTED:
        if (nodeSelectionEventRunning) return;
        showNode((NodeIdentifier) obj);
        break;
    }
  }

  public JTree getTree() {
    return tree;
  }
}
