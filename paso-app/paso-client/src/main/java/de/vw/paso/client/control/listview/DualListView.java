package de.vw.paso.client.control.listview;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.CellUtils;

public class DualListView<T> extends HBox {

    private final ListWithHeader<T> availableItemListView;
    private final ListWithHeader<T> selectedItemListView;

    private MoveButtonPanel moveButtons;

    private final List<T> forcedItems;

    private boolean isEditable = true;

    public void setCellValueConverter(Function<T, String> converter) {
        availableItemListView.getListView().setCellFactory(e -> CellUtils.createListCell(converter));
        selectedItemListView.getListView().setCellFactory(e -> CellUtils.createListCell(converter));
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public DualListView(List<T> availableItems, List<T> selectedItems, boolean isMovable) {
        this(availableItems, selectedItems, isMovable, List.of());
    }

    public DualListView(List<T> availableItems, List<T> selectedItems, boolean isMovable, List<T> forcedItems) {
        super(5);
        this.forcedItems = forcedItems;
        setFillHeight(true);
        availableItemListView = new ListWithHeader<>(I18N.getString("column.available.title"));
        selectedItemListView = new ListWithHeader<>(I18N.getString("column.selected.title"));

        setItems(availableItems, selectedItems);

        configureListActions();
        configureButtonActions(isMovable);
        configureButtonEnablement(isMovable);
        HBox.setHgrow(availableItemListView, Priority.ALWAYS);
        HBox.setHgrow(selectedItemListView, Priority.ALWAYS);

        HBox.setMargin(moveButtons, new Insets(25, 0, 0, 0));
        getChildren().addAll(availableItemListView, moveButtons, selectedItemListView);
    }

    public void addChangeListener(ListChangeListener<T> listener) {
        selectedItemListView.getListView().getItems().addListener(listener);
    }

    public List<T> getSelectedItems() {
        return new ArrayList<>(selectedItemListView.getListView().getItems());
    }

    public void setAvailableItems(List<T> availableItems) {
        availableItemListView.getListView().getItems().setAll(availableItems);
        sort(availableItemListView.getListView());
    }

    public void setItems(List<T> availableItems, List<T> selectedItems) {
        selectedItemListView.getListView().getItems().setAll(selectedItems);
        setAvailableItems(availableItems);
    }

    private void configureButtonActions(boolean isMovable) {
        moveButtons = new MoveButtonPanel(isMovable);
        moveButtons.getRightButton().setOnAction(e -> moveRight());
        moveButtons.getAllRightButton().setOnAction(e -> moveAllRight());
        moveButtons.getLeftButton().setOnAction(e -> moveLeft());
        moveButtons.getAllLeftButton().setOnAction(e -> moveAllLeft());

        if (isMovable) {
            moveButtons.getTopButton().setOnAction(e -> moveTop());
            moveButtons.getUpButton().setOnAction(e -> moveUp());
            moveButtons.getDownButton().setOnAction(e -> moveDown());
            moveButtons.getBottomButton().setOnAction(e -> moveBottom());
        }
    }

    private void configureButtonEnablement(boolean isMovable) {
        //Move to right
        ListView<T> availableList = availableItemListView.getListView();
        availableList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldItem, newItem) -> moveButtons.getRightButton()
                        .setDisable(newItem == null || forcedItems.contains(newItem) || !isEditable));
        availableList.getItems().addListener((ListChangeListener<T>) change -> moveButtons.getAllRightButton()
                .setDisable(availableList.getItems().isEmpty() || !isEditable));

        //move to left
        ListView<T> selectedList = selectedItemListView.getListView();
        selectedList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldItem, newItem) -> moveButtons.getLeftButton()
                        .setDisable(newItem == null || forcedItems.contains(newItem) || !isEditable));
        selectedList.getItems().addListener((ListChangeListener<T>) change -> moveButtons.getAllLeftButton()
                .setDisable(selectedList.getItems().isEmpty() || !isEditable));

        //move up
        if (isMovable) {
            selectedList.getSelectionModel().selectedIndexProperty()
                    .addListener((observableValue, oldIndex, newIndex) -> {
                        boolean noSelection = newIndex.intValue() == -1;
                        moveButtons.getUpButton().setDisable(noSelection || newIndex.intValue() <= 0 || !isEditable);
                        moveButtons.getTopButton().setDisable(noSelection || newIndex.intValue() <= 0 || !isEditable);
                        moveButtons.getDownButton().setDisable(
                                noSelection || newIndex.intValue() >= selectedList.getItems().size() - 1
                                        || !isEditable);
                        moveButtons.getBottomButton().setDisable(
                                noSelection || newIndex.intValue() >= selectedList.getItems().size() - 1
                                        || !isEditable);
                    });
        }
    }

    private void configureListActions() {
        availableItemListView.setOnDoubleClick(mouseEvent -> moveRight());
        selectedItemListView.setOnDoubleClick(mouseEvent -> moveLeft());
    }

    private ArrayList<Integer> getSelectedIndices(ListView<T> listView) {
        return new ArrayList<>(listView.getSelectionModel().getSelectedIndices());
    }

    private void moveAllLeft() {
        if (!isEditable) {
            return;
        }
        ListView<T> availableList = availableItemListView.getListView();
        ListView<T> selectedList = selectedItemListView.getListView();

        availableList.getSelectionModel().clearSelection();

        List<T> selectedItems = new ArrayList<>(selectedList.getItems());
        selectedItems.removeAll(forcedItems);

        for (T selectedItem : selectedItems) {
            selectedList.getItems().remove(selectedItem);
            availableList.getItems().add(selectedItem);
        }
        sort(availableList);
    }

    private void moveAllRight() {
        if (!isEditable) {
            return;
        }
        ListView<T> availableList = availableItemListView.getListView();
        ListView<T> selectedList = selectedItemListView.getListView();

        selectedList.getSelectionModel().clearSelection();

        List<T> selectedItems = new ArrayList<>(availableList.getItems());
        selectedItems.removeAll(forcedItems);

        for (T selectedItem : selectedItems) {
            availableList.getItems().remove(selectedItem);
            selectedList.getItems().add(selectedItem);
        }
    }

    private void moveBottom() {
        if (!isEditable) {
            return;
        }
        ListView<T> listView = selectedItemListView.getListView();
        List<Integer> selectedIndices = getSelectedIndices(listView);

        listView.getSelectionModel().clearSelection();

        int shift = 0;
        for (int selectedIndex : selectedIndices) {
            if (selectedIndex == listView.getItems().size() - 1) {
                continue;
            }

            T selectedItem = listView.getItems().remove(selectedIndex - shift++);
            listView.getItems().addLast(selectedItem);
            selectAndScroll(listView, listView.getItems().size() - 1);
        }
    }

    private void moveDown() {
        if (!isEditable) {
            return;
        }

        ListView<T> listView = selectedItemListView.getListView();
        List<Integer> selectedIndices = getSelectedIndices(listView).reversed();

        listView.getSelectionModel().clearSelection();

        for (int selectedIndex : selectedIndices) {
            if (selectedIndex == listView.getItems().size() - 1) {
                continue;
            }

            T selectedItem = listView.getItems().remove(selectedIndex);
            listView.getItems().add(selectedIndex + 1, selectedItem);
            selectAndScroll(listView, selectedIndex + 1);
        }
    }

    private void moveLeft() {
        if (!isEditable) {
            return;
        }
        ListView<T> availableList = availableItemListView.getListView();
        ListView<T> selectedList = selectedItemListView.getListView();

        availableList.getSelectionModel().clearSelection();

        List<T> selectedItems = new ArrayList<>(selectedList.getSelectionModel().getSelectedItems());
        selectedItems.removeAll(forcedItems);

        for (T selectedItem : selectedItems) {
            selectedList.getItems().remove(selectedItem);
            availableList.getItems().add(selectedItem);
            selectAndScroll(availableList, selectedItem);
        }
        sort(availableList);
    }

    private void moveRight() {
        if (!isEditable) {
            return;
        }
        ListView<T> availableList = availableItemListView.getListView();
        ListView<T> selectedList = selectedItemListView.getListView();

        selectedList.getSelectionModel().clearSelection();

        List<T> selectedItems = new ArrayList<>(availableList.getSelectionModel().getSelectedItems());
        selectedItems.removeAll(forcedItems);

        for (T selectedItem : selectedItems) {
            availableList.getItems().remove(selectedItem);
            selectedList.getItems().add(selectedItem);
            selectAndScroll(selectedList, selectedItem);
        }
    }

    private void moveTop() {
        if (!isEditable) {
            return;
        }
        ListView<T> listView = selectedItemListView.getListView();
        List<Integer> selectedIndices = getSelectedIndices(listView).reversed();

        listView.getSelectionModel().clearSelection();

        int shift = 0;
        for (int selectedIndex : selectedIndices) {
            if (selectedIndex == 0) {
                continue;
            }

            T selectedItem = listView.getItems().remove(selectedIndex + shift++);
            listView.getItems().addFirst(selectedItem);
            selectAndScroll(listView, 0);
        }
    }

    private void moveUp() {
        if (!isEditable) {
            return;
        }
        ListView<T> listView = selectedItemListView.getListView();
        List<Integer> selectedIndices = getSelectedIndices(listView);

        listView.getSelectionModel().clearSelection();

        for (int selectedIndex : selectedIndices) {
            if (selectedIndex == 0) {
                continue;
            }

            T selectedItem = listView.getItems().remove(selectedIndex);
            listView.getItems().add(selectedIndex - 1, selectedItem);
            selectAndScroll(listView, selectedIndex - 1);
        }
    }

    private void selectAndScroll(ListView<T> list, int index) {
        list.getSelectionModel().select(index);
        list.scrollTo(index);
        list.requestFocus();
    }

    private void selectAndScroll(ListView<T> list, T item) {
        list.getSelectionModel().select(item);
        list.scrollTo(item);
        list.requestFocus();
    }

    private void sort(ListView<T> list) {
        list.getItems().sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
    }
}
