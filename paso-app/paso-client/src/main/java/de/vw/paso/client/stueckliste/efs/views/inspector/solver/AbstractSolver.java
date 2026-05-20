package de.vw.paso.client.stueckliste.efs.views.inspector.solver;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public abstract class AbstractSolver {

    private VehicleConfigDTO vehicleConfig;
    private Collection<TreeItem<InspectorTreeItemObject>> entries;

    public abstract String getTitleKey();

    protected final Long getVehiclePartListId() {
        return vehicleConfig.getVehiclePartList().getId();
    }

    protected <T> void openDialog(AbstractSolutionDialog<T> dialog, Consumer<Optional<T>> callback) {
        callback.accept(dialog.showAndWait());
    }

    //todo: would be better to return the remaining unsolved elements -> easier to solve edge cases

    /** @return <code>true</code>, whether the solution was successful */
    public abstract boolean solve();

    public void handleException(Throwable throwable) {
        ExceptionHandler.instance().handleException(throwable);
    }

    protected Collection<TreeItem<InspectorTreeItemObject>> getEntries() {
        return entries;
    }

    /** @return <code>true</code>, when the solution must be disabled */
    public boolean disable() {
        return false;
    }

    public VehicleConfigDTO getVehicleConfig() {
        return vehicleConfig;
    }

    public void setVehicleConfig(VehicleConfigDTO vehicleConfig) {
        this.vehicleConfig = vehicleConfig;
    }

    public void setEntries(Collection<TreeItem<InspectorTreeItemObject>> items, boolean showIgnored) {
        this.entries = InspectorUtil.getEntries(items, showIgnored);
    }
}
