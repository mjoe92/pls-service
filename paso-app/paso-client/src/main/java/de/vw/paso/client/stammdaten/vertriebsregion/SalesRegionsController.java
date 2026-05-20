package de.vw.paso.client.stammdaten.vertriebsregion;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoAlert;
import de.vw.paso.client.stammdaten.AbstractMasterDataTableViewController;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.converter.BooleanIntegerStringConverter;
import de.vw.paso.client.valueobject.SalesRegionVMO;
import de.vw.paso.delegate.stammdaten.salesregion.SalesRegionRestClientHolder;
import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;
import de.vw.paso.service.masterdata.salesregion.SalesRegionUpdateDTO;
import de.vw.paso.utility.StringConstant;

@FXController(name = "sales-regions-tab")
public class SalesRegionsController extends AbstractMasterDataTableViewController<SalesRegionVMO>
    implements Initializable {

    private static final String CONSTRAIN_ISSUE_DELETE_MESSAGE_START = "constrainIssue.delete.messageStart";
    private static final String CONSTRAIN_ISSUE_DELETE_MESSAGE_END = "constrainIssue.delete.messageEnd";
    private static final String ALERT_EDIT_TITLE = "alert.region.edit.title";
    private static final String ALERT_HEADER_TEXT = "alert.region.header.text";
    private static final String ALERT_CONTENT_TEXT_EDIT = "alert.region.content.text.edit";
    private static final String ALERT_DELETE_TITLE = "alert.region.delete.title";

    private static final String TOGGLE_RELEVANCE = "region.toggleRelevance";

    @FXML
    private Button toggleRelevance;
    @FXML
    private TableColumn<SalesRegionVMO, String> colSalesRegion;
    @FXML
    private TableColumn<SalesRegionVMO, String> colDescriptionDe;
    @FXML
    private TableColumn<SalesRegionVMO, String> colDescriptionEn;
    @FXML
    private TableColumn<SalesRegionVMO, Integer> colRelevant;

    @Override
    public void handleActionDelete() {
        doDelete(productTableView.getSelectionModel().getSelectedItem(), () -> doLoad(this::setItems));
    }

    @Override
    protected void openAddDialog(SalesRegionVMO selectedItem, Consumer<Optional<SalesRegionVMO>> callback) {
        SalesRegionDialog salesRegionDialog = new SalesRegionDialog(I18N.getString(DIALOG_TITLE_ADD),
            new SalesRegionVMO(0), getImmutableItems());

        callback.accept(salesRegionDialog.showAndWait());
    }

    @Override
    protected void openEditDialog(SalesRegionVMO selectedItem, Consumer<Optional<SalesRegionVMO>> callback) {
        SalesRegionDialog newSalesRegionDialog = new SalesRegionDialog(I18N.getString(DIALOG_TITLE_EDIT), selectedItem,
            getImmutableItems());

        callback.accept(newSalesRegionDialog.showAndWait());
    }

    @Override
    protected void doAdd(SalesRegionVMO newItem, Consumer<SalesRegionVMO> callback) {
        doAsync(() -> SalesRegionVMO.toVMO(
            SalesRegionRestClientHolder.getInstance().addSalesRegion(SalesRegionVMO.toSalesRegion(newItem))), callback);
    }

    @Override
    protected void doEdit(SalesRegionVMO selectedItem, SalesRegionVMO newItem, Consumer<SalesRegionVMO> callback) {
        SalesRegionDTO newItemDTO = SalesRegionVMO.toSalesRegion(newItem);
        if (selectedItem.getSalesRegion().equals(newItem.getSalesRegion())) {
            doAsync(() -> SalesRegionVMO.toVMO(SalesRegionRestClientHolder.getInstance()
                .updateSalesRegion(new SalesRegionUpdateDTO(newItemDTO, selectedItem.getSalesRegion()))), callback);
            return;
        }

        List<String> idList = List.of(selectedItem.getSalesRegion());
        doAsync(() -> SalesRegionRestClientHolder.getInstance().countConstrainIssues(idList).issues(), result -> {
            if (!result.isEmpty()) {
                Alert alert = new PasoAlert(Alert.AlertType.CONFIRMATION, I18N.getString(ALERT_CONTENT_TEXT_EDIT),
                    ButtonType.OK);

                alert.setTitle(I18N.getString(ALERT_EDIT_TITLE));
                alert.setHeaderText(I18N.getString(ALERT_HEADER_TEXT));
                alert.showAndWait();

            } else {
                doAsync(() -> SalesRegionVMO.toVMO(SalesRegionRestClientHolder.getInstance()
                    .updateSalesRegion(new SalesRegionUpdateDTO(newItemDTO, selectedItem.getSalesRegion()))), callback);
            }
        });
    }

    @Override
    protected void doDelete(SalesRegionVMO selectedItem, Runnable callback) {
        ObservableList<SalesRegionVMO> selectedItems = productTableView.getSelectionModel().getSelectedItems();
        List<String> idList = selectedItems.stream().map(SalesRegionVMO::getSalesRegion).collect(Collectors.toList());

        doAsync(() -> SalesRegionRestClientHolder.getInstance().countConstrainIssues(idList).issues(),
            constrainIssueMap -> {
                if (!constrainIssueMap.isEmpty()) {
                    Alert alert = new PasoAlert(Alert.AlertType.CONFIRMATION,
                        createConstrainIssueStringMessage(constrainIssueMap), ButtonType.YES, ButtonType.CANCEL);

                    alert.setTitle(I18N.getString(ALERT_DELETE_TITLE));
                    alert.setHeaderText(I18N.getString(ALERT_HEADER_TEXT));
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.YES) {
                        List<String> idsToDelete = getSalesRegionIdsSafeToDelete(selectedItems, constrainIssueMap);
                        if (!idsToDelete.isEmpty()) {
                            doAsync(() -> {
                                SalesRegionRestClientHolder.getInstance().deleteSalesRegions(idsToDelete);
                                productTableView.getSelectionModel().clearSelection();
                                disablePropertyEdit().set(true);
                            }, callback);
                        }
                    }
                } else {
                    List<String> idsToDelete = selectedItems.stream().map(SalesRegionVMO::getSalesRegion)
                        .collect(Collectors.toList());
                    doAsync(() -> {
                        SalesRegionRestClientHolder.getInstance().deleteSalesRegions(idsToDelete);
                        productTableView.getSelectionModel().clearSelection();
                        disablePropertyEdit().set(true);
                    }, callback);
                }
            });
    }

    @Override
    protected void setItems(List<SalesRegionVMO> items) {
        String[] prevSelectedItems = productTableView.getSelectionModel().getSelectedItems().stream()
            .map(SalesRegionVMO::getSalesRegion).toArray(String[]::new);

        super.setItems(items);

        selectTableItems(prevSelectedItems);
    }

    @Override
    protected void doLoad(Consumer<List<SalesRegionVMO>> callback) {
        doAsync(() -> SalesRegionVMO.toVMOs(
            SalesRegionRestClientHolder.getInstance().loadSalesRegions().salesRegionDTOList()), callback);
    }

    @Override
    protected Comparator<? super SalesRegionVMO> getItemComparator() {
        return Comparator.comparing(SalesRegionVMO::getSalesRegion);
    }

    @Override
    protected boolean getFilterCriteria(SalesRegionVMO item, PasoWildCardPattern pattern) {
        return pattern == null || (item.getDescriptionDe() != null && pattern.matches(item.getDescriptionDe()) != null)
            || (item.getDescriptionEn() != null && pattern.matches(item.getDescriptionEn()) != null)
            || pattern.matches(item.getSalesRegion()) != null;
    }

    @Override
    protected void initializeView() {
        initToggleRelevanceButton();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem toggleRelevance = new MenuItem(I18N.getString(TOGGLE_RELEVANCE));

        toggleRelevance.setOnAction(e -> toggleRelevanceState());
        contextMenu.getItems().add(toggleRelevance);

        productTableView.setEditable(true);
        productTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        productTableView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                contextMenu.hide();
                contextMenu.show(productTableView, event.getScreenX(), event.getScreenY());
            } else if (event.getButton().equals(MouseButton.PRIMARY)) {
                contextMenu.hide();
            }
        });
    }

    @Override
    protected void initTableColumns() {
        colSalesRegion.setCellValueFactory(cellData -> cellData.getValue().salesRegionProperty());
        colRelevant.setCellValueFactory(cellData -> cellData.getValue().relevantProperty());
        colDescriptionDe.setCellValueFactory(cellData -> cellData.getValue().descriptionDeProperty());
        colDescriptionEn.setCellValueFactory(cellData -> cellData.getValue().descriptionEnProperty());

        colRelevant.setCellFactory(column -> {
            TextFieldTableCell<SalesRegionVMO, Integer> cell = new TextFieldTableCell<>();

            cell.setConverter(new BooleanIntegerStringConverter());

            return cell;
        });
    }

    @Override
    protected boolean disableAdd() {
        return false;
    }

    @Override
    protected boolean disableRefresh() {
        return false;
    }

    @FXML
    private void toggleRelevanceState() {
        Collection<SalesRegionVMO> selectedRegions = productTableView.getSelectionModel().getSelectedItems();
        if (!selectedRegions.isEmpty()) {
            boolean isAllRelevant = selectedRegions.stream()
                .allMatch(selectedRegion -> selectedRegion.getRelevant() == 1);

            for (SalesRegionVMO selectedRegion : selectedRegions) {
                selectedRegion.setRelevant(isAllRelevant ? 0 : 1);
            }

            List<String> salesRegionIds = selectedRegions.stream().map(SalesRegionVMO::getSalesRegion).toList();

            doAsync(
                () -> SalesRegionRestClientHolder.getInstance().updateRelevance(salesRegionIds, isAllRelevant ? 0 : 1));
        }
    }

    private void selectTableItems(String[] prevSelectedItemIds) {
        if (prevSelectedItemIds.length == 0) {
            return;
        }

        int[] selectedIndices = new int[prevSelectedItemIds.length];
        List<String> regionIds = productTableView.getItems().stream().map(SalesRegionVMO::getSalesRegion).toList();
        for (int i = 0; i < prevSelectedItemIds.length; i++) {
            selectedIndices[i] = regionIds.indexOf(prevSelectedItemIds[i]);
        }

        int firstItemIndex = selectedIndices[0];
        productTableView.getSelectionModel().selectIndices(firstItemIndex, selectedIndices);
        productTableView.scrollTo(firstItemIndex);
        productTableView.requestFocus();
    }

    private void initToggleRelevanceButton() {
        toggleRelevance.setOnAction(e -> toggleRelevanceState());
    }

    private String createConstrainIssueStringMessage(Map<String, Integer> constrainIssueMap) {
        StringBuilder constrainIssuesString = new StringBuilder(StringConstant.EMPTY);
        for (String key : constrainIssueMap.keySet()) {
            constrainIssuesString.append("\n").append(StringConstant.SPACE).append(key);
        }

        return I18N.getString(CONSTRAIN_ISSUE_DELETE_MESSAGE_START) + constrainIssuesString + "\n" + I18N.getString(
            CONSTRAIN_ISSUE_DELETE_MESSAGE_END);
    }

    private List<String> getSalesRegionIdsSafeToDelete(ObservableList<SalesRegionVMO> selectedItems,
        Map<String, Integer> constrainIssueMap) {

        return selectedItems.stream().map(SalesRegionVMO::getSalesRegion)
            .filter(salesRegion -> !constrainIssueMap.containsKey(salesRegion)).toList();
    }
}
