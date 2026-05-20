package de.vw.paso.pll.dev.contextvisualizer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import de.vw.paso.pll.dev.contextvisualizer.checker.CompareResultWriter;
import de.vw.paso.pll.dev.contextvisualizer.checker.PageTreeComparator;
import de.vw.paso.pll.dev.contextvisualizer.checker.PageTreeCompareResult;
import de.vw.paso.pll.dev.contextvisualizer.efs.EfsPage;
import de.vw.paso.pll.dev.contextvisualizer.ppf.PPFPage;
import de.vw.paso.pll.dev.contextvisualizer.util.BackgroundExecutor;
import de.vw.paso.pll.dev.contextvisualizer.util.FileUtil;
import de.vw.paso.pll.dev.contextvisualizer.util.Icons;
import de.vw.paso.pll.dev.contextvisualizer.util.ProcessMonitor;
import de.vw.paso.pll.dev.contextvisualizer.util.components.MenuAndToolbar;
import de.vw.paso.pll.dev.contextvisualizer.util.components.MultiSplitPane;
import de.vw.paso.pll.dev.contextvisualizer.util.components.ToogleAction;
import de.vw.paso.pll.dev.contextvisualizer.veron.VeronElement;
import de.vw.paso.pll.dev.contextvisualizer.veron.VeronPage;
import de.vw.paso.pll.model.PlsEfsElement;
import de.vw.paso.pll.preprocessing.PartListPreprocessor;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.reader.FileChecker;

/**
 * Class for visualizing Processed Part List Structures
 * quick and dirty, non productive code. just for development
 */
public class ContextVisualizer extends JFrame {

  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    ContextVisualizer vis = new ContextVisualizer();
    vis.setVisible(true);
  }

  private static String TITLE = "PreprocessingContext Visualizer";

  private boolean replaceIds = false;
  private String lastSelectedDir;
  private String lastOpenDir;
  private JMenuItem currentSelectedItem;

  private MultiSplitPane multiSplit;

  private PPFPage ppfPage;
  private EfsPage efsPage;
  private VeronPage veronPage;

  private ToogleAction syncViewAction;

  private BackgroundExecutor bgExec;

  private PageTreeCompareResult compareResult;

  private ContextVisualizer() {
    setSize(1200, 900);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle(TITLE);
    setIconImage(Icons.APP.APP_ICON.getImage());

    bgExec = new BackgroundExecutor(this);

    setLayout(new BorderLayout());
    MenuAndToolbar mat = createMenuBar();
    add(mat, BorderLayout.NORTH);
    ppfPage = new PPFPage(bgExec);
    efsPage = new EfsPage(bgExec);

    ppfPage.setAddRuleAction(rules -> efsPage.addRules(rules));

    multiSplit = new MultiSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    multiSplit.addComponent(ppfPage);
    multiSplit.addComponent(efsPage);
    add(multiSplit, BorderLayout.CENTER);

    syncViewAction.setSelected(true);
    enableLink(true);
  }

  private MenuAndToolbar createMenuBar() {
    MenuAndToolbar mat = new MenuAndToolbar();
    mat.getMenuBar().add(createFileMenu());

    JMenu settingsMenu = new JMenu("Settings");
    mat.getMenuBar().add(settingsMenu);

    //Replace Ids
    ToogleAction replaceIdAction = new ToogleAction() {
      @Override
      protected void doAction(ActionEvent actionEvent) {
        bgExec.doInBackground("Replace IDS", processMonitor -> {
          replaceIds = isSelected();
          if (lastOpenDir != null) {
            openPartList(lastOpenDir, true, processMonitor);
          }
        });
      }
    };
    JCheckBoxMenuItem replaceIdMenuItem = new JCheckBoxMenuItem("Replace IDs");
    replaceIdMenuItem.setIcon(Icons.MENU.MENU_REPLACEID);
    settingsMenu.add(replaceIdMenuItem);
    replaceIdAction.register(replaceIdMenuItem);

    //Sync Views
    syncViewAction = new ToogleAction() {
      @Override
      protected void doAction(ActionEvent actionEvent) {
        enableLink(isSelected());
      }
    };
    JCheckBoxMenuItem syncViewMenuItem = new JCheckBoxMenuItem("Link selection");
    syncViewMenuItem.setIcon(Icons.MENU.MENU_LINK_VIEW);
    settingsMenu.add(syncViewMenuItem);
    syncViewAction.register(syncViewMenuItem);

    JToggleButton syncViewToggleButton = new JToggleButton(Icons.MENU.MENU_LINK_VIEW);
    syncViewToggleButton.setToolTipText("Link selection");
    mat.getToolbar().add(syncViewToggleButton);
    syncViewAction.register(syncViewToggleButton);

    JButton resizeBtn = new JButton(Icons.MENU.MENU_RESIZE);
    resizeBtn.setToolTipText("Resize Views");
    resizeBtn.addActionListener(ae -> multiSplit.autoResize());
    mat.getToolbar().add(resizeBtn);

    mat.getToolbar().add(new JToolBar.Separator());

    JButton compareBtn = new JButton(Icons.Compare.COMPARE);
    compareBtn.addActionListener(e -> {
      JFileChooser veronChooser = new JFileChooser();
      veronChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      if (veronChooser.showOpenDialog(this) == JOptionPane.OK_OPTION) {
        bgExec.doInBackground("Comparing", pm -> {
          File veronFile = veronChooser.getSelectedFile();
          try {
            pm.setCurrentStep("Read Veron file");
            veronPage = new VeronPage("Veron", bgExec);
            veronPage.setJumpOnSelection(syncViewAction.isSelected());
            veronPage.openVeron(veronFile);
            multiSplit.addComponent(veronPage);

            PageTreeComparator comparator = new PageTreeComparator();
            compareResult = comparator.compareTrees(efsPage, veronPage);
            multiSplit.autoResize();
          } catch (IOException e1) {
            throw new RuntimeException(e1);
          }
        });
      }
    });
    compareBtn.setToolTipText("Compare with Veron");
    mat.getToolbar().add(compareBtn);
    JButton exportCompareResultBtn = new JButton(Icons.Compare.EXPORT);
    exportCompareResultBtn.addActionListener(ae -> {
      try {
        File tempFile = File.createTempFile("compare-result", ".txt");
        DefaultMutableTreeNode efsRootNode = (DefaultMutableTreeNode) efsPage.getTree().getModel().getRoot();
        DefaultMutableTreeNode veronRootNode = (DefaultMutableTreeNode) veronPage.getTree().getModel().getRoot();
        FileWriter writer = new FileWriter(tempFile);
        new CompareResultWriter().writeResult(writer, efsPage.getConfig(), compareResult,
          (PlsEfsElement) efsRootNode.getUserObject(), (VeronElement) veronRootNode);
        writer.close();
        FileUtil.openTextFile(tempFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    exportCompareResultBtn.setToolTipText("Export result to file");
    mat.getToolbar().add(exportCompareResultBtn);

    return mat;
  }

  private JMenu createFileMenu() {
    JMenu datasetMenu = new JMenu("Daten");
    File f = new File("build/resources/test/");
    File[] dataDirectories = f.listFiles();
    if (dataDirectories != null) {
      for (File dir : dataDirectories) {
        datasetMenu.add(createDataSetMenuItem(dir.getName(), dir.getAbsolutePath()));
      }
    }
    datasetMenu.add(
      createDataSetMenuItem("small example for tests", new File("src/test/resources/ppl").getAbsolutePath()));
    datasetMenu.addSeparator();
    JMenuItem openCustom = new JCheckBoxMenuItem("Open other");
    openCustom.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      if (lastSelectedDir != null) {
        chooser.setCurrentDirectory(new File(lastSelectedDir));
      } else {
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
      }
      if (chooser.showOpenDialog(this) == JOptionPane.OK_OPTION) {
        bgExec.doInBackground("Load data", processMonitor -> {
          File dir = chooser.getSelectedFile();
          openPartList(dir.getAbsolutePath(), false, processMonitor);
          if (currentSelectedItem != null) {
            currentSelectedItem.setSelected(false);
          }
          openCustom.setSelected(true);
          currentSelectedItem = openCustom;
          lastSelectedDir = dir.getAbsolutePath();
        });
      }
    });
    datasetMenu.add(openCustom);
    return datasetMenu;
  }

  private JCheckBoxMenuItem createDataSetMenuItem(String itemName, String path) {
    JCheckBoxMenuItem item = new JCheckBoxMenuItem(itemName);
    item.addActionListener(e -> bgExec.doInBackground("Lade " + itemName, (processMonitor) -> {
      openPartList(path, false, processMonitor);
      processMonitor.setCurrentStep("Set selection");
      item.setSelected(true);
      if (currentSelectedItem != null) {
        currentSelectedItem.setSelected(false);
      }
      currentSelectedItem = item;
    }));
    return item;
  }

  private void openPartList(String path, boolean restoreExpansion, ProcessMonitor processMonitor) {
    try {
      processMonitor.setCurrentStep("Create Context");
      PreprocessingContext ctx = ContextVisualizer.createContext(path);
      ctx.setReplaceIds(replaceIds);
      processMonitor.setCurrentStep("Pre processing raw files");
      PartListPreprocessor plp = new PartListPreprocessor();
      File ppfResult = File.createTempFile("ppf", ".txt");
      processMonitor.setCurrentStep("Create temporary ppf file: " + ppfResult.getAbsolutePath());
      ppfResult.deleteOnExit();
      FileWriter out = new FileWriter(ppfResult);
      plp.processPartLists(ctx, out);

      out.close();

      ppfPage.openPPF(ctx, ppfResult, restoreExpansion, processMonitor);
      efsPage.openEFS(ppfResult, restoreExpansion, processMonitor);
      if (veronPage != null) {
        veronPage.clear();
        multiSplit.removeComponent(veronPage);
      }

      ppfPage.setPartChecker(efsPage.getCreator().getPartChecker());
      setTitle(TITLE + " - " + path);
      lastOpenDir = path;
    } catch (Exception e1) {
      e1.printStackTrace();
      JOptionPane.showMessageDialog(this, "Could not open partlist");
    }
  }

  private void enableLink(boolean enable) {
    efsPage.setJumpOnSelection(enable);
    ppfPage.setJumpOnSelection(enable);
    if (veronPage != null) {
      veronPage.setJumpOnSelection(enable);
    }
  }

  private static PreprocessingContext createContext(String directory) {
    return createContext(directory, true);
  }

  private static PreprocessingContext createContext(String directory, boolean checkCopleteness) {
    List<File> files = getAllFiles(directory).collect(Collectors.toList());
    PreprocessingContext ctx = new PreprocessingContext(files);
    if (checkCopleteness) {
      new FileChecker().checkCompleteness(ctx);
    }
    return ctx;
  }

  private static Stream<File> getAllFiles(String directory) {
    return Arrays.stream(new File(directory).listFiles()).filter(f -> f.isFile());
  }
}
