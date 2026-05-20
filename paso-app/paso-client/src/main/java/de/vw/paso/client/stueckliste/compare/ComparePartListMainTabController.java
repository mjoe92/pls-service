package de.vw.paso.client.stueckliste.compare;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.exception.ControllerException;
import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;
import de.vw.paso.client.main.ribbonmenu.compare.config.RibbonMenuCompareConfigEvent;
import de.vw.paso.client.main.ribbonmenu.compare.costgroup.RibbonMenuCompareCostGroupEvent;
import de.vw.paso.client.main.ribbonmenu.compare.fgset.RibbonMenuCompareFgSetEvent;
import de.vw.paso.client.main.ribbonmenu.compare.partgroup.RibbonMenuComparePartGroupEvent;
import de.vw.paso.client.main.ribbonmenu.compare.partlist.RibbonMeunComparePartlisEvent;
import de.vw.paso.client.main.tab.AbstractMainTabController;
import de.vw.paso.client.stueckliste.compare.partlist.PartListCompareTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedTabController;
import de.vw.paso.client.stueckliste.util.PartGroupUtil;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.icon.StuecklisteIcon;
import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.compare.costgroup.CostGroupCompare;
import de.vw.paso.compare.costgroup.CostGroupCompareResult;
import de.vw.paso.compare.fgset.FGSetCompare;
import de.vw.paso.compare.fgset.FgSetCompareResult;
import de.vw.paso.compare.partgroup.PartGroupCompare;
import de.vw.paso.compare.partgroup.PartGroupCompareResult;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FXController(name = "compare-part-list-main-tab")
public class ComparePartListMainTabController extends AbstractMainTabController implements Initializable {

  @FXML
  private GridPane paneEfsHeader;

  @FXML
  private Tab comparePartListTab;

  @FXML
  private TabPane compareTabs;

  private FGSetCompareTabController fgSetTabController;
  private CostGroupCompareTabController costGroupTabController;
  private PartGroupCompareTabController partGroupTabController;
  private ConfigCompareTabController configCompareTabController;
  private PartListCompareTabController partListCompareTabController;

  private VehicleConfigDTO vehicleConfig;

  private List<VehicleConfigDTO> comparedVehicleConfigs;

  private Map<BorderPane, AbstractSummarisedTabController> summaryViewControllers = new HashMap<>();

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    super.initialize(location, resources);

    compareTabs.setSide(Side.BOTTOM);
    compareTabs.getStyleClass().add("ribbon-menu-tab-pane");
    comparePartListTab.setGraphic(new ImageView(StuecklisteIcon.COMPARE_16x16.getImage()));
    comparePartListTab.setText(I18N.getString("tab.compare.title"));

    loadTabConfig();
    loadTabPartlist();
    loadTabFgSet();
    loadTabCostGroup();
    loadTabPartGroup();

    compareTabs.getSelectionModel().selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> EventBus.getInstance().post(getRibbonMenuEvent()));

    for (final Tab tab : compareTabs.getTabs()) {
      tab.setClosable(false);
      tab.getStyleClass().add("ribbon-menu-tab");
    }
  }

  @Override
  public void stop() {
    super.stop();

    summaryViewControllers.clear();
  }

  private void loadTabConfig() {
    try {

      configCompareTabController = BaseController.load(ConfigCompareTabController.class);
      configCompareTabController.start();

      registerSubController(configCompareTabController);
      compareTabs.getTabs().add(configCompareTabController.getControl());
    } catch (final ControllerException exception) {
      handleException(exception);
    }
  }

  private void loadTabPartlist() {
    partListCompareTabController = BaseController.load(PartListCompareTabController.class);
    partListCompareTabController.start();

    registerSubController(partListCompareTabController);
    compareTabs.getTabs().add(partListCompareTabController.getControl());
  }

  private void loadTabFgSet() {
    try {
      fgSetTabController = BaseController.load(FGSetCompareTabController.class);
      fgSetTabController.setMapOfOpenedSummaryController(summaryViewControllers);
      fgSetTabController.start();

      registerSubController(fgSetTabController);
      compareTabs.getTabs().add(fgSetTabController.getControl());
    } catch (final ControllerException exception) {
      handleException(exception);
    }
  }

  private void loadTabCostGroup() {
    costGroupTabController = BaseController.load(CostGroupCompareTabController.class);
    costGroupTabController.setMapOfOpenedSummaryController(summaryViewControllers);
    costGroupTabController.start();

    registerSubController(costGroupTabController);
    compareTabs.getTabs().add(costGroupTabController.getControl());
  }

  private void loadTabPartGroup() {
    partGroupTabController = BaseController.load(PartGroupCompareTabController.class);
    partGroupTabController.setMapOfOpenedSummaryController(summaryViewControllers);
    partGroupTabController.start();

    registerSubController(partGroupTabController);
    compareTabs.getTabs().add(partGroupTabController.getControl());
  }

  @Override
  public AbstractRibbonMenuEvent<?> getRibbonMenuEvent() {
    if (compareTabs.getSelectionModel().getSelectedItem().equals(configCompareTabController.getTab())) {
      return new RibbonMenuCompareConfigEvent(configCompareTabController,
        configCompareTabController.getTab().getText());
    } else if (compareTabs.getSelectionModel().getSelectedItem().equals(costGroupTabController.getTab())) {
      return new RibbonMenuCompareCostGroupEvent(costGroupTabController, costGroupTabController.getTab().getText());
    } else if (compareTabs.getSelectionModel().getSelectedItem().equals(partGroupTabController.getTab())) {
      return new RibbonMenuComparePartGroupEvent(partGroupTabController, partGroupTabController.getTab().getText());
    } else if (compareTabs.getSelectionModel().getSelectedItem().equals(fgSetTabController.getTab())) {
      return new RibbonMenuCompareFgSetEvent(fgSetTabController, fgSetTabController.getTab().getText());
    } else if (compareTabs.getSelectionModel().getSelectedItem().equals(partListCompareTabController.getControl())) {
      return new RibbonMeunComparePartlisEvent(partListCompareTabController,
        partListCompareTabController.getControl().getText());
    } else {
      return null;
    }
  }

  @Override
  public Tab getControl() {
    return comparePartListTab;
  }

  @Override
  public Parent getStyleableParent() {
    return comparePartListTab.getTabPane();
  }

  public void setData(List<VehicleConfigDTO> vehicleConfigs, VehicleConfigDTO reference) {
    comparedVehicleConfigs = vehicleConfigs;
    vehicleConfig = vehicleConfigs.get(0);
    configCompareTabController.setVehicleConfigs(vehicleConfigs, reference);
    partListCompareTabController.setVehicleConfigs(vehicleConfigs);

    Map<String, SetKeyDTO> setKeys = new HashMap<>();
    for (VehicleConfigDTO vehicleConfigDTO : vehicleConfigs) {
      for (SetKeyDTO setKey : CacheManager.getSetKeys(vehicleConfigDTO.getSetVersionId())) {
        setKeys.put(setKey.getSetKeyName(), setKey);
      }
    }
    Map<String, CostGroupDTO> costGroups = new HashMap<>();
    for (VehicleConfigDTO vehicleConfigDTO : vehicleConfigs) {
      for (CostGroupDTO costGroup : CacheManager.getCostGroups(vehicleConfigDTO.getCostGroupVersion())) {
        costGroups.put(costGroup.getCostGroupName(), costGroup);
      }
    }

    FGSetCompare comparer = new FGSetCompare(setKeys);
    FgSetCompareResult result = comparer.compare(vehicleConfigs, reference);
    fgSetTabController.setFgSetResult(result);
    fgSetTabController.calculateSumRow();

    CostGroupCompare costGroupComparer = new CostGroupCompare(costGroups);
    CostGroupCompareResult costGroupCompareResult = costGroupComparer.compare(vehicleConfigs, reference);
    costGroupTabController.setCostGroupResult(costGroupCompareResult);
    costGroupTabController.calculateSumRow();

    Map<String, PartGroupDTO> partGroups = new HashMap<>();
    List<PartGroupVMO> partGroupVMOs = PartGroupVMO.toVMOs(CacheManager.getPartGroups());
    PartGroupUtil.sortByParent(partGroupVMOs);
    CacheManager.getPartGroups().forEach(partGroup -> {
      if (partGroup.getMgrEnd() != null && partGroup.getCategory() < 100) {
        for (int index = partGroup.getMgr(); index < partGroup.getMgrEnd(); index++) {
          partGroups.put(PartGroupUtil.getKeyForPartGroupWithMgrEnd(partGroup, index), partGroup);
        }
      } else {
        partGroups.put(PartGroupUtil.getKeyForPartGroup(partGroup), partGroup);
      }
    });
    PartGroupCompare partGroupComparer = new PartGroupCompare(partGroups);
    PartGroupCompareResult partGroupCompareResult = partGroupComparer.compare(vehicleConfigs, reference);
    partGroupTabController.setPartGroups(partGroups);
    partGroupTabController.setPartGroupResult(partGroupCompareResult);
    partGroupTabController.calculateSumRow();
  }

  public Double getGewicht() {
    return vehicleConfig == null || vehicleConfig.getVehiclePartList() == null ? 0.0
      : vehicleConfig.getVehiclePartList().getWeight();
  }

  public List<VehicleConfigDTO> getComparedVehicleConfigs() {
    return comparedVehicleConfigs;
  }

}
