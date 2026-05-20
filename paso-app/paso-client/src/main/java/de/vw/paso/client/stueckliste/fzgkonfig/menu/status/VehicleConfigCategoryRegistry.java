package de.vw.paso.client.stueckliste.fzgkonfig.menu.status;

import java.util.HashMap;
import java.util.Map;

import de.vw.paso.client.stueckliste.fzgkonfig.content.AbstractContentController;
import de.vw.paso.client.stueckliste.fzgkonfig.content.fzgprojekt.FzgProjektController;
import de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration.ConfigurationController;
import de.vw.paso.client.stueckliste.fzgkonfig.content.modell.ModelController;
import de.vw.paso.client.stueckliste.fzgkonfig.content.zusammenfassung.ZusammenfassungController;
import de.vw.paso.vehicle.VehicleConfigCategory;

public final class VehicleConfigCategoryRegistry {

    private static final Map<VehicleConfigCategory, Class<? extends AbstractContentController>> mapper = new HashMap<>();

    static {
        mapper.put(VehicleConfigCategory.FZG_PROJEKT, FzgProjektController.class);
        mapper.put(VehicleConfigCategory.MODELL, ModelController.class);
        mapper.put(VehicleConfigCategory.KONFIGURATION, ConfigurationController.class);
        mapper.put(VehicleConfigCategory.ZUSAMMENFASSUNG, ZusammenfassungController.class);
    }

    private VehicleConfigCategoryRegistry() {
    }

    public static Class<? extends AbstractContentController> getControllerClass(
            VehicleConfigCategory vehicleConfigCategory) {
        return mapper.get(vehicleConfigCategory);
    }
}
