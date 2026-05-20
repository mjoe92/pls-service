package de.vw.paso.client.control.tablebase.header;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.input.MouseEvent;

import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.control.tablebase.tableconfig.ColumnSelectionDialog;
import de.vw.paso.client.control.tablebase.tableconfig.ColumnSelectionResult;
import de.vw.paso.client.control.tablebase.tableconfig.TableColumnSelectionUtils;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.delegate.stammdaten.tableconfig.TableConfigRestClientHolder;
import de.vw.paso.service.tableconfig.TableConfigDTO;

public class CustomTableHeaderRow extends TableHeaderRow {

  private final TableViewSkinBase<?, ?, ?, ?, ?> skin;

  public CustomTableHeaderRow(TableViewSkinBase skin) {
    super(skin);
    this.skin = skin;

    switch (skin.getSkinnable()) {
      case TableView<?> tv -> new ResizeListener(tv.itemsProperty(), tv.getItems());
      case TreeTableView<?> tv ->
        new ResizeListener(tv.rootProperty(), tv.getRoot() != null ? tv.getRoot().getChildren() : null);
      default -> throw new IllegalArgumentException("Skin must be of type TableViewSkin or TreeTableViewSkin");
    }
  }

  @Override
  protected CustomRootHeader createRootHeader() {
    return new CustomRootHeader();
  }

  @Override
  protected void showColumnMenu(MouseEvent mouseEvent) {
    List<? extends TableColumnBase<?, ?>> columns = switch (skin.getSkinnable()) {
      case TableView<?> tv -> tv.getColumns();
      case TreeTableView<?> tv -> tv.getColumns();
      default -> throw new IllegalArgumentException("Skin must be of type TableViewSkin or TreeTableViewSkin");
    };

    ServiceController<List<TableConfigDTO>> service = new ServiceController<>();
    service.setOnSucceeded(event -> {
      ColumnSelectionDialog dia = new ColumnSelectionDialog(columns, service.getValue());
      dia.showAndWait().ifPresent(selectedColumns -> {
        TableColumnSelectionUtils.applyLayout(columns, selectedColumns);
        setSelectedColumns(selectedColumns);
      });
    });
    service.setOnFailed(event -> ExceptionHandler.instance().handleException(service.getException()));
    service.start(() -> TableConfigRestClientHolder.getInstance().getConfigurationsForUser().tableConfigDTOs());
  }

  private void setSelectedColumns(ColumnSelectionResult selectedColumns) {
    //if we didn't save this config before or the config has no name then we won't save it in the database
    if (selectedColumns.getId() == null || selectedColumns.getName() == null) {
      return;
    }

    TableConfigDTO tableConfigDTO = new TableConfigDTO();

    tableConfigDTO.setId(selectedColumns.getId());
    tableConfigDTO.setUserId(UserProperties.getUserId());
    tableConfigDTO.setName(selectedColumns.getName());
    tableConfigDTO.setSelectedColumnIds(selectedColumns.getSelectedIds());
    tableConfigDTO.setSelectedColumns(selectedColumns.getSelectedText());
    tableConfigDTO.setDefault(selectedColumns.isDefault());
    tableConfigDTO.setPublic(selectedColumns.isPublic());

    TableConfigDTO configDTO = TableConfigRestClientHolder.getInstance().saveConfiguration(tableConfigDTO);

    skin.getSkinnable().getProperties().put(TableColumnSelectionUtils.COLUMN_CONFIG, configDTO);
  }

  private class ResizeListener implements InvalidationListener {

    private final ObjectProperty<?> property;
    private final ObservableList<?> list;

    public ResizeListener(ObjectProperty<?> property, ObservableList<?> list) {
      this.property = property;
      this.list = list;

      property.addListener(this);
      if (list != null) {
        list.addListener(this);
      }
    }

    @Override
    public void invalidated(Observable observable) {
      disconnect();

      ((CustomRootHeader) getRootHeader()).resizeColumnToFitContent();
    }

    private void disconnect() {
      property.removeListener(this);
      if (list != null) {
        list.removeListener(this);
      }
    }
  }
}
