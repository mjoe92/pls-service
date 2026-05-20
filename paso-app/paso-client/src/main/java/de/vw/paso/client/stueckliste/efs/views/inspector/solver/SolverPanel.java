package de.vw.paso.client.stueckliste.efs.views.inspector.solver;

import java.util.Collection;
import java.util.List;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.util.GeneralNumberFormat;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;

public abstract class SolverPanel {

    private static final String WEIGHT_DIMENSION = " g";

    private AbstractSolver[] solvers;
    private VehicleConfigDTO vehicleConfig;
    private Collection<TreeItem<InspectorTreeItemObject>> entries;

    protected static AbstractSolver[] toArray(AbstractSolver... solvers) {
        return solvers;
    }

    public abstract String getDescription();

    public AbstractSolver[] getSolvers() {
        return solvers;
    }

    public void setSolvers(AbstractSolver... solvers) {
        this.solvers = solvers;
    }

    public void setVehicleConfig(VehicleConfigDTO vehicleConfig) {
        this.vehicleConfig = vehicleConfig;

        for (AbstractSolver solver : solvers) {
            solver.setVehicleConfig(vehicleConfig);
        }
    }

    public void setEntries(List<TreeItem<InspectorTreeItemObject>> items, boolean showIgnored) {
        entries = InspectorUtil.getEntries(items, showIgnored);

        for (AbstractSolver solver : solvers) {
            solver.setEntries(entries, showIgnored);
        }
    }

    protected String getInspectorTypeMessage(InspectorEntryType type) {
        return I18N.getString("inspector.type." + type.name().toLowerCase() + ".description");
    }

    protected String formatWeight(double weight) {
        if (weight == 0) {
            return StringConstant.DOUBLE_SPACE + weight + WEIGHT_DIMENSION;
        }

        Object prefix = weight < 0 ? StringConstant.SPACE_CHAR : StringConstant.SPACE_PLUS;
        return prefix + GeneralNumberFormat.format(weight, true, 0) + WEIGHT_DIMENSION;
    }

    public VehicleConfigDTO getVehicleConfig() {
        return vehicleConfig;
    }

    public Collection<TreeItem<InspectorTreeItemObject>> getEntries() {
        return entries;
    }
}
