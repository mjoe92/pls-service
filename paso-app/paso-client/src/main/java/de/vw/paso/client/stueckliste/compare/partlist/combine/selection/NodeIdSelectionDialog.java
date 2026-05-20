package de.vw.paso.client.stueckliste.compare.partlist.combine.selection;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import de.vw.paso.client.base.AbstractDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.util.ReflectionUtil;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.apache.commons.lang3.StringUtils;

public class NodeIdSelectionDialog extends AbstractDialogController<SelectionResult> {

  private final DualiListView<MethodWrapper> dualListView;

  public NodeIdSelectionDialog(SelectionResult preSelection) {
    setTitle(I18N.getString("choice"));

    setResult(new SelectionResult());
    setWidth(600);
    setHeight(600);

    VBox dialogConent = new VBox();
    dialogConent.setSpacing(5);

    HBox settingsBox = new HBox();
    settingsBox.setAlignment(Pos.CENTER);

    CheckBox pathCheckBox = new CheckBox(I18N.getString("node.path.used"));
    pathCheckBox.setTooltip(new Tooltip(I18N.getString("node.path.used.tooltip")));
    pathCheckBox.setSelected(preSelection == null || preSelection.isCheckPath());

    CheckBox combineAttributes = new CheckBox(I18N.getString("attribute.combine"));
    combineAttributes.setTooltip(new Tooltip(I18N.getString("attribute.combine.tooltip")));
    combineAttributes.setSelected(preSelection == null || preSelection.isCombineAll());

    settingsBox.getChildren().addAll(pathCheckBox, combineAttributes);

    HBox.setHgrow(pathCheckBox, Priority.ALWAYS);
    HBox.setHgrow(combineAttributes, Priority.ALWAYS);

    dialogConent.getChildren().add(settingsBox);

    Separator sep = new Separator();
    sep.setOrientation(Orientation.HORIZONTAL);
    dialogConent.getChildren().add(sep);

    TextField searchField = new TextField();
    searchField.textProperty().addListener(new ChangeListener<>() {
      @Override
      public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
        dualListView.setFilter(methodWrapper -> {
          if (StringUtils.isNotEmpty(searchField.getText())) {
            return methodWrapper.toString().toLowerCase().contains(searchField.getText().toLowerCase());
          } else {
            return true;
          }
        });
      }
    });
    dialogConent.getChildren().add(searchField);

    ObservableList<MethodWrapper> result = FXCollections.observableArrayList();
    ReflectionUtil.getProperties(EfsElementDTO.class).stream().filter(this::filterProperties)
      .sorted(Comparator.comparing(Method::getName)).forEach(e -> result.add(new MethodWrapper(e)));

    dualListView = new DualiListView<>(result);
    if (preSelection != null) {
      dualListView.setSelectedItems(preSelection.getSelectedProperties());
    }
    dialogConent.getChildren().add(dualListView);

    getDialogPane().setContent(dialogConent);
    getDialogPane().setHeaderText(I18N.getString("attribute.combine.choose"));

    getDialogPane().getButtonTypes().add(ButtonType.OK);
    getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

    setResultConverter(buttonType -> {
      if (ButtonType.OK.equals(buttonType)) {
        SelectionResult sr = new SelectionResult();
        sr.setCheckPath(pathCheckBox.isSelected());
        sr.setCombineAll(combineAttributes.isSelected());
        sr.setSelectedProperties(dualListView.getSelectedItems());
        return sr;
      }
      return null;
    });

    VBox.setMargin(pathCheckBox, new Insets(0, 0, 5, 0));
  }

  private boolean filterProperties(Method method) {
    Class<?> returnType = method.getReturnType();
    return !Collection.class.isAssignableFrom(returnType);
  }
}
