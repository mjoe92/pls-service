package de.vw.paso.client.control.tablebase.tableconfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoAlert;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.control.listview.DualListView;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItemPropertyNames;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.delegate.stammdaten.tableconfig.TableConfigRestClientHolder;
import de.vw.paso.service.tableconfig.TableConfigDTO;
import lombok.Getter;

public class ColumnSelectionDialog extends BaseDialogController<ColumnSelectionResult> {

  private static final int MAX_PRIVATE_CONFIG_PER_USER = 10;
  private static final int MAX_PUBLIC_CONFIG_PER_USER = 5;
  private static Long lastSelectedConfig = null;

  private final List<? extends TableColumnBase<?, ?>> columns;

  @Getter
  private final List<TableConfigDTO> configs;
  private ComboBox<TableConfigDTO> configurationCombobox;
  private DualListView<ColumnInfo> dualList;
  private List<ColumnInfo> allColumns;
  private List<ColumnInfo> necessaryColumn;
  private Button createConfigBtn;
  private Button deleteConfigBtn;
  private Button editBtn;
  private Button createPublicCopyBtn;
  private Button createPrivateCopyBtn;

  private RadioButton publicRadioButton;
  private RadioButton privateRadioButton;

  private RadioButton ownedButton;
  private RadioButton notOwnedButton;

  public ColumnSelectionDialog(List<? extends TableColumnBase<?, ?>> columns, List<TableConfigDTO> configs) {
    this.columns = columns;
    this.configs = configs;

    initialize(I18N.getString("column.selection"), () -> {
      necessaryColumn = columns.stream().filter(column -> column.getId() != null)
        .filter(column -> Objects.equals(column.getId(), EfsElementTreeItemPropertyNames.PART_NUMBER))
        .map(column -> new ColumnInfo(column.getId(), column.getText())).toList();
      initContent(configs);
    });
  }

  private void initContent(List<TableConfigDTO> configs) {
    allColumns = new ArrayList<>();
    List<ColumnInfo> available = new ArrayList<>();
    List<ColumnInfo> selected = new ArrayList<>();
    columns.forEach(e -> {
      allColumns.add(new ColumnInfo(e.getId(), e.getText()));
      if (e.isVisible()) {
        selected.add(new ColumnInfo(e.getId(), e.getText()));
      } else {
        available.add(new ColumnInfo(e.getId(), e.getText()));
      }
    });

    //part number remain selected every time
    columns.stream()
      .filter(treeTableColumn -> treeTableColumn.getText().equals(EfsElementTreeItemPropertyNames.PART_NUMBER))
      .findFirst().map(partNumber -> new ColumnInfo(partNumber.getId(), partNumber.getText()))
      .ifPresent(partNumberColumnInfo -> {
        if (!available.contains(partNumberColumnInfo)) {
          available.addFirst(partNumberColumnInfo);
        }
      });

    dualList = new DualListView<>(available, selected, true, necessaryColumn);
    dualList.setCellValueConverter(ColumnInfo::name);

    dualList.addChangeListener(change -> {
      commitButton.setDisable(
        dualList.getSelectedItems().isEmpty() || !new HashSet<>(dualList.getSelectedItems()).containsAll(
          necessaryColumn));

      if (configurationCombobox.getSelectionModel().getSelectedItem() != null) {
        configurationCombobox.getSelectionModel().getSelectedItem().setSelectedColumns(getSelectedColumnNamesToSave());
        configurationCombobox.getSelectionModel().getSelectedItem().setSelectedColumnIds(getSelectedColumnIdsToSave());
      }
    });

    Node topPanel = createTopPanel(configs);

    VBox content = new VBox(5);
    VBox.setVgrow(dualList, Priority.ALWAYS);
    content.setPrefWidth(1200);
    content.setPrefHeight(650);
    content.getChildren().addAll(topPanel, dualList);
    grid.add(content, 0, 0);
    getDialogPane().setContent(content);

    updateButtonState(configurationCombobox.getValue());
  }

  private VBox createTopPanel(List<TableConfigDTO> configs) {
    RadioButton ownedAndNotOwnedRadioButton;
    RadioButton privateAndPublicRadioButton;
    ToggleGroup toggleGroupRadioButtonPrivatePublic;
    Callback<ListView<TableConfigDTO>, ListCell<TableConfigDTO>> cellFactory = new ListViewListCellCallback();
    VBox panel = new VBox(2);
    HBox topPanel = new HBox(5);
    HBox middlePanel = new HBox(6);
    HBox bottomPanel = new HBox(6);

    configurationCombobox = new ComboBox<>();
    configurationCombobox.setMinWidth(200);
    configurationCombobox.setButtonCell(cellFactory.call(null));
    configurationCombobox.setCellFactory(cellFactory);

    createConfigBtn = new Button(I18N.getString("create"));
    createConfigBtn.setOnAction(actionEvent -> onCreateAction());

    deleteConfigBtn = new Button(I18N.getString("delete"));
    deleteConfigBtn.setOnAction(actionEvent -> onDeleteAction());

    editBtn = new Button(I18N.getString("edit"));
    editBtn.setOnAction(actionEvent -> onEditAction());

    createPublicCopyBtn = new Button(I18N.getString("public-copy.btn"));
    createPublicCopyBtn.setOnAction(
      actionEvent -> copyConfigToPublic(configurationCombobox.getSelectionModel().getSelectedItem()));

    createPrivateCopyBtn = new Button(I18N.getString("private-copy.btn"));
    createPrivateCopyBtn.setOnAction(
      actionEvent -> copyConfigToPrivate(configurationCombobox.getSelectionModel().getSelectedItem()));

    topPanel.getChildren()
      .addAll(configurationCombobox, createConfigBtn, deleteConfigBtn, editBtn, createPrivateCopyBtn,
        createPublicCopyBtn);

    EventHandler<ActionEvent> actionEventRefreshCurrentlySelectedConfigs = event -> {
      setConfigurations(configs);
      updateButtonState(configurationCombobox.getSelectionModel().getSelectedItem());
    };

    toggleGroupRadioButtonPrivatePublic = new ToggleGroup();

    publicRadioButton = new RadioButton(I18N.getString("radio.selectPublic"));
    publicRadioButton.setOnAction(actionEventRefreshCurrentlySelectedConfigs);

    privateRadioButton = new RadioButton(I18N.getString("radio.selectPrivate"));
    privateRadioButton.setOnAction(actionEventRefreshCurrentlySelectedConfigs);
    privateRadioButton.setSelected(true);

    privateAndPublicRadioButton = new RadioButton(
      I18N.getString("radio.selectPrivateAndPublic"));
    privateAndPublicRadioButton.setOnAction(actionEventRefreshCurrentlySelectedConfigs);

    toggleGroupRadioButtonPrivatePublic.getToggles()
      .addAll(publicRadioButton, privateRadioButton, privateAndPublicRadioButton);

    middlePanel.getChildren().addAll(publicRadioButton, privateRadioButton, privateAndPublicRadioButton);

    ToggleGroup toggleGroupRadioButtonOwnedOrNotOwned = new ToggleGroup();

    ownedButton = new RadioButton(I18N.getString("radio.selectOwned"));
    ownedButton.setOnAction(actionEventRefreshCurrentlySelectedConfigs);

    notOwnedButton = new RadioButton(I18N.getString("radio.selectNotOwned"));
    notOwnedButton.setOnAction(actionEventRefreshCurrentlySelectedConfigs);

    ownedAndNotOwnedRadioButton = new RadioButton(
      I18N.getString("radio.selectOwnedAndNotOwned"));
    ownedAndNotOwnedRadioButton.setOnAction(actionEventRefreshCurrentlySelectedConfigs);

    toggleGroupRadioButtonOwnedOrNotOwned.getToggles().addAll(ownedAndNotOwnedRadioButton, ownedButton, notOwnedButton);

    bottomPanel.getChildren().addAll(ownedAndNotOwnedRadioButton, ownedButton, notOwnedButton);

    configurationCombobox.getSelectionModel().selectedItemProperty().addListener((observableValue, tableConfig, t1) -> {
      updateButtonState(t1);
      selectConfig(t1);
    });

    setConfigurations(configs);

    panel.getChildren().addAll(topPanel, middlePanel, bottomPanel);

    return panel;
  }

  private void updateButtonState(TableConfigDTO selectedItem) {
    String currentUserId = UserProperties.getUserId();
    if (selectedItem == null) {
      deleteConfigBtn.setDisable(true);
      editBtn.setDisable(true);
    } else if (selectedItem.isDefault()) {
      deleteConfigBtn.setDisable(true);
      editBtn.setDisable(!selectedItem.getUserId().equals(currentUserId));
    } else {
      boolean isCurrentUserTheOwner = selectedItem.getUserId().equals(currentUserId);
      deleteConfigBtn.setDisable(!isCurrentUserTheOwner);
      editBtn.setDisable(!isCurrentUserTheOwner);
    }

    boolean isPrivateFull = isPrivateConfigsFull();
    boolean isPublicFull = isPublicConfigsFull();
    createPrivateCopyBtn.setDisable(isPrivateFull || selectedItem == null || !selectedItem.isPublic());
    createPublicCopyBtn.setDisable(isPublicFull || selectedItem == null || selectedItem.isPublic());
    createConfigBtn.setDisable(isPrivateFull && isPublicFull);
    //if the edit button is disabled at the end then we cannot edit the config, thus the dualList's edit
    // is disabled too
    dualList.setEditable(!editBtn.isDisabled());
  }

  private boolean isPrivateConfigsFull() {
    return
      configs.stream().filter(config -> UserProperties.getUserId().equals(config.getUserId()) && !config.isPublic())
        .toList().size() >= MAX_PRIVATE_CONFIG_PER_USER;
  }

  private boolean isPublicConfigsFull() {
    return configs.stream().filter(config -> UserProperties.getUserId().equals(config.getUserId()) && config.isPublic())
      .toList().size() >= MAX_PUBLIC_CONFIG_PER_USER;
  }

  private void setConfigurations(List<TableConfigDTO> configs) {
    configurationCombobox.setItems(FXCollections.observableArrayList(configs.stream()
      .filter(config -> getPredicateForVisibilityCheck().test(config) && getPredicateForOwnerCheck().test(config))
      .toList()));
    sortConfig();
    if (lastSelectedConfig != null) {
      configurationCombobox.getItems().stream().filter(e -> e.getId().equals(lastSelectedConfig)).findFirst()
        .ifPresent(configDTO -> {
          if (getPredicateForVisibilityCheck().test(configDTO)) {
            configurationCombobox.setValue(configDTO);
            selectConfig(configDTO);
          }
        });
    } else if (!configurationCombobox.getItems().isEmpty()) {
      TableConfigDTO configDTO = configurationCombobox.getItems().getFirst();
      configurationCombobox.setValue(configDTO);
      selectConfig(configDTO);
    }
  }

  private void sortConfig() {
    configurationCombobox.getItems().sort((conf1, conf2) -> {
      if (conf1.isPublic()) {
        return -1;
      } else if (conf2.isPublic()) {
        return 1;
      } else {
        return conf1.getName().compareToIgnoreCase(conf2.getName());
      }
    });
  }

  private Predicate<TableConfigDTO> getPredicateForVisibilityCheck() {
    if (privateRadioButton.isSelected()) {
      return config -> !config.isPublic();
    } else if (publicRadioButton.isSelected()) {
      return TableConfigDTO::isPublic;
    } else {
      return config -> true;
    }
  }

  private Predicate<TableConfigDTO> getPredicateForOwnerCheck() {
    String userId = UserProperties.getUserId();
    if (ownedButton.isSelected()) {
      return config -> userId.equals(config.getUserId());
    } else if (notOwnedButton.isSelected()) {
      return config -> !userId.equals(config.getUserId());
    } else {
      return config -> true;
    }
  }

  private void selectConfig(TableConfigDTO config) {
    if (config != null) {
      List<ColumnInfo> availableColumns = new ArrayList<>();
      List<ColumnInfo> selectedColumns = new ArrayList<>();
      allColumns.forEach(e -> {
        if (config.getSelectedColumnIds().contains(e.id())) {
          selectedColumns.add(e);
        } else {
          availableColumns.add(e);
        }
      });
      selectedColumns.sort((o1, o2) -> {
        int index1 = config.getSelectedColumns().indexOf(o1.id());
        int index2 = config.getSelectedColumns().indexOf(o2.id());
        return index1 - index2;
      });
      dualList.setItems(availableColumns, selectedColumns);
      lastSelectedConfig = config.getId();
    } else {
      lastSelectedConfig = null;
    }
  }

  @Override
  protected ChangeListener<?> getValidationListener() {
    return null;
  }

  @Override
  protected ListChangeListener<?> getValidationListenerForList() {
    return null;
  }

  @Override
  protected ColumnSelectionResult dialogResult() {
    TableConfigDTO tableConfigDTO = configurationCombobox.getValue();

    if (tableConfigDTO == null) {
      return new ColumnSelectionResult(dualList.getSelectedItems());
    }

    return new ColumnSelectionResult(dualList.getSelectedItems(), tableConfigDTO.getId(), tableConfigDTO.getName(),
      tableConfigDTO.isDefault(), tableConfigDTO.isPublic());
  }

  private List<String> getSelectedColumnIdsToSave() {
    return dualList.getSelectedItems().stream().map(ColumnInfo::id).collect(Collectors.toList());
  }

  private List<String> getSelectedColumnNamesToSave() {
    return dualList.getSelectedItems().stream().map(ColumnInfo::name).collect(Collectors.toList());
  }

  private void onCreateAction() {
    TableConfigCreationDialog dia = new TableConfigCreationDialog(
      configs.stream().map(TableConfigDTO::getName).collect(Collectors.toSet()),
      I18N.getString("edit.message"), necessaryColumn, isPrivateConfigsFull(),
      isPublicConfigsFull());
    Optional<TableConfigDTO> newTableConfig = dia.showAndWait();
    newTableConfig.ifPresent(this::saveConfig);
  }

  private void onDeleteAction() {
    Alert dia = new PasoAlert(Alert.AlertType.CONFIRMATION);
    TableConfigDTO selectedConfig = configurationCombobox.getSelectionModel().getSelectedItem();
    String message = I18N.getString("delete.message", selectedConfig.getName());
    dia.getDialogPane().setHeaderText(message);

    Optional<ButtonType> buttonType = dia.showAndWait();
    buttonType.ifPresent(button -> {
      if (button == ButtonType.OK) {
        deleteConfig(selectedConfig);
      }
    });
  }

  private void onEditAction() {
    TableConfigDTO selectedConfig = configurationCombobox.getSelectionModel().getSelectedItem();

    if (selectedConfig == null) {
      return;
    }

    TableConfigCreationDialog dia = new TableConfigCreationDialog(
      configs.stream().map(TableConfigDTO::getName).filter(name -> !name.equals(selectedConfig.getName()))
        .collect(Collectors.toSet()), I18N.getString("create.message"), selectedConfig,
      necessaryColumn, isPrivateConfigsFull(), isPublicConfigsFull());

    Optional<TableConfigDTO> tableConfigDTO = dia.showAndWait();
    tableConfigDTO.ifPresent(this::saveConfig);
  }

  private void copyConfigToPublic(TableConfigDTO config) {
    TableConfigDTO copiedConfig = new TableConfigDTO(config, UserProperties.getUserId(), true);
    saveConfig(copiedConfig);
  }

  private void copyConfigToPrivate(TableConfigDTO config) {
    TableConfigDTO copiedConfig = new TableConfigDTO(config, UserProperties.getUserId(), false);
    saveConfig(copiedConfig);
  }

  private void deleteConfig(TableConfigDTO config) {
    ServiceController<Void> service = new ServiceController<>();
    service.setOnSucceeded(result -> {
      configurationCombobox.getItems().removeIf(e -> e.getId().equals(config.getId()));
      TableConfigDTO configDTO = configurationCombobox.getItems().stream().findFirst().orElse(null);
      configurationCombobox.setValue(configDTO);
      configs.removeIf(e -> e.getId().equals(config.getId()));
      updateButtonState(configDTO);
    });
    service.setOnFailed(e -> ExceptionHandler.instance().handleException(service.getException()));
    service.start(
      () -> TableConfigRestClientHolder.getInstance().deleteConfiguration(config.getId()));
  }

  private void saveConfig(TableConfigDTO config) {
    ServiceController<TableConfigDTO> service = new ServiceController<>();
    service.setOnSucceeded(result -> {
      TableConfigDTO configDTO = service.getValue();
      configs.removeIf(e -> e.getId().equals(configDTO.getId()));
      configs.add(configDTO);
      updateConfig(configDTO);
    });
    service.setOnFailed(e -> ExceptionHandler.instance().handleException(service.getException()));
    service.start(() -> TableConfigRestClientHolder.getInstance().saveConfiguration(config));
  }

  private void updateConfig(TableConfigDTO config) {
    if (config.isDefault()) {
      configurationCombobox.getItems().forEach(tableConfig -> {
        if (tableConfig.isDefault() && !tableConfig.getId().equals(config.getId())) {
          tableConfig.setDefault(false);
        }
      });
    }

    updateButtonState(config);

    configurationCombobox.getItems().removeIf(e -> e.getId().equals(config.getId()));
    if (getPredicateForVisibilityCheck().test(config)) {
      configurationCombobox.getItems().add(config);
      sortConfig();
      configurationCombobox.getSelectionModel().select(config);
    }
  }

  private static class ListViewListCellCallback implements Callback<ListView<TableConfigDTO>, ListCell<TableConfigDTO>> {

    @Override
    public ListCell<TableConfigDTO> call(ListView<TableConfigDTO> l) {
      return new ListCell<>() {

        @Override
        protected void updateItem(TableConfigDTO item, boolean empty) {
          super.updateItem(item, empty);

          if (item == null || empty) {
            setText(null);
          } else {
            String userId = UserProperties.getUserId();
            String ownedText = I18N.getString("config.owned");
            setText(item + (item.getUserId().equals(userId) ? " " + ownedText : ""));
          }
        }
      };
    }
  }
}
