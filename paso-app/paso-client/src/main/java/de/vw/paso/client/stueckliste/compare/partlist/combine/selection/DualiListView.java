package de.vw.paso.client.stueckliste.compare.partlist.combine.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DualiListView<T> extends HBox {

    private Button moveRight;
    private Button moveAllRight;
    private Button moveLeft;
    private Button moveAllLeft;

    private Button moveTop;
    private Button moveUp;

    private Button moveDown;
    private Button moveBottom;

    private ObservableList<T> sourceList;
    private ObservableList<T> targetList;

    private FilteredList<T> sourceFilteredList;
    private FilteredList<T> targetFilteredList;

    private ListView<T> sourceListView;
    private ListView<T> targetListView;

    public DualiListView(List<T> items) {
        createView();

        sourceList = FXCollections.observableArrayList(items);
        sourceFilteredList = new FilteredList<>(sourceList);
        sourceListView.setItems(sourceFilteredList);

        targetList = FXCollections.observableArrayList();
        targetFilteredList = new FilteredList<>(targetList);
        targetListView.setItems(targetFilteredList);

        updateButtons();
    }

    private void createView() {
        sourceListView = new ListView<>();
        sourceListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            updateButtons();
            if (event.getClickCount() == 2) {
                moveRight();
            }
        });

        targetListView = new ListView<>();
        targetListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            updateButtons();
            if (event.getClickCount() == 2) {
                moveLeft();
            }
        });

        VBox targetListButtons = new VBox();
        moveTop = new Button("^^");
        moveTop.setOnAction(e -> moveTop());
        moveUp = new Button("^");
        moveUp.setOnAction(e -> moveUp());

        moveDown = new Button("v");
        moveDown.setOnAction(e -> moveDown());
        moveBottom = new Button("vv");
        moveBottom.setOnAction(e -> moveBottom());

        targetListButtons.getChildren().addAll(moveTop, moveUp, moveDown, moveBottom);

        VBox moveButtonBox = new VBox();
        moveButtonBox.setAlignment(Pos.TOP_CENTER);
        moveButtonBox.setFillWidth(true);
        moveButtonBox.setSpacing(3);

        moveRight = new Button("->");
        moveRight.setOnAction(e -> moveRight());

        moveAllRight = new Button("=>");
        moveAllRight.setOnAction(e -> moveAllRight());

        moveLeft = new Button("<-");
        moveLeft.setOnAction(e -> moveLeft());

        moveAllLeft = new Button("<=");
        moveAllLeft.setOnAction(e -> moveAllLeft());

        moveButtonBox.getChildren().addAll(moveRight, moveAllRight, moveLeft, moveAllLeft);

        getChildren().addAll(sourceListView, moveButtonBox, targetListView, targetListButtons);
    }

    private void updateButtons() {
        moveRight.disableProperty().set(sourceListView.getSelectionModel().getSelectedItem() == null);
        moveAllRight.disableProperty().set(sourceListView.getItems().isEmpty());

        moveLeft.disableProperty().set(targetListView.getSelectionModel().getSelectedItem() == null);
        moveAllLeft.disableProperty().set(targetListView.getItems().isEmpty());

        int selectedIndex = targetListView.getSelectionModel().getSelectedIndex();
        moveTop.disableProperty().set(selectedIndex == 0);
        moveUp.disableProperty().set(selectedIndex == 0);

        moveBottom.disableProperty().set(selectedIndex == targetListView.getItems().size() - 1);
        moveDown.disableProperty().set(selectedIndex == targetListView.getItems().size() - 1);

        sourceListView.refresh();
        targetListView.refresh();
    }

    private void moveTop() {
        int selectedFilteredIndex = targetListView.getSelectionModel().getSelectedIndex();
        int selectedIndex = targetFilteredList.getSourceIndex(selectedFilteredIndex);
        T toMove = targetList.remove(selectedIndex);
        targetList.add(0, toMove);
        targetListView.getSelectionModel().select(toMove);

        updateButtons();
    }

    private void moveUp() {
        int selectedFilteredIndex = targetListView.getSelectionModel().getSelectedIndex();
        int selectedIndex = targetFilteredList.getSourceIndex(selectedFilteredIndex);
        int targetPosIndex = targetFilteredList.getSourceIndex(selectedFilteredIndex - 1);

        T toMove = targetList.remove(selectedIndex);
        targetList.add(targetPosIndex, toMove);
        targetListView.getSelectionModel().select(toMove);

        updateButtons();
    }

    private void moveBottom() {
        int selectedFilteredIndex = targetListView.getSelectionModel().getSelectedIndex();
        int selectedIndex = targetFilteredList.getSourceIndex(selectedFilteredIndex);
        T toMove = targetList.remove(selectedIndex);
        targetList.add(toMove);
        targetListView.getSelectionModel().select(toMove);

        updateButtons();
    }

    private void moveDown() {
        int selectedFilteredIndex = targetListView.getSelectionModel().getSelectedIndex();
        int selectedIndex = targetFilteredList.getSourceIndex(selectedFilteredIndex);
        int targetPosIndex = targetFilteredList.getSourceIndex(selectedFilteredIndex + 1);

        T toMove = targetList.remove(selectedIndex);
        targetList.add(targetPosIndex, toMove);
        targetListView.getSelectionModel().select(toMove);

        updateButtons();
    }

    private void moveRight() {
        List<Integer> selectedIndicies = sourceListView.getSelectionModel().getSelectedIndices().stream()
                .map(index -> sourceFilteredList.getSourceIndex(index)).collect(Collectors.toList());
        move(selectedIndicies, sourceList, targetList);
    }

    private void moveAllRight() {
        moveAll(sourceList, sourceFilteredList, targetList);
    }

    private void moveLeft() {
        List<Integer> selectedIndicies = targetListView.getSelectionModel().getSelectedIndices().stream()
                .map(index -> targetFilteredList.getSourceIndex(index)).collect(Collectors.toList());
        move(selectedIndicies, targetList, sourceList);
    }

    private void moveAllLeft() {
        moveAll(targetList, targetFilteredList, sourceList);
    }

    private void move(List<Integer> selectedIndices, List<T> from, List<T> to) {
        selectedIndices.forEach(index -> to.add(from.remove(index.intValue())));

        updateButtons();
        sortSourceList();
    }

    private void moveAll(List<T> fromList, FilteredList<T> fromFilteredList, List<T> toList) {
        toList.addAll(fromFilteredList);
        fromList.removeAll(fromFilteredList);

        updateButtons();
        sortSourceList();
    }

    public List<T> getSelectedItems() {
        return new ArrayList<>(targetList);
    }

    public void setSelectedItems(List<T> items) {
        sourceList.removeAll(items);
        targetList.addAll(items);
    }

    private void sortSourceList() {
        sourceFilteredList.sort(Comparator.comparing(Object::toString));
        sourceList.sort(Comparator.comparing(Object::toString));
        sourceListView.setItems(sourceFilteredList);
    }

    public void setFilter(Predicate<T> predicate) {
        sourceFilteredList.setPredicate(predicate);
        targetFilteredList.setPredicate(predicate);
    }
}
