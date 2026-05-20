package de.vw.paso.client.stueckliste.compare.partlist;

import java.util.function.Function;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.utility.EfsWeightUtil;
import de.vw.paso.utility.PrioritizedWeight;

public class EfsMaraProperties<T> extends EfsProperty<T> {

    public static final EfsMaraProperties<String> PARTNUMBER = new EfsMaraProperties<>("efs.mara.property.partnumber",
        "partNumber", EfsElementMaraDTO::getPartNumber);
    public static final EfsMaraProperties<String> PARTNUMBER_FORMATTED = new EfsMaraProperties<>(
        "efs.mara.property.partnumberFormatted", "partNumberFormatted", EfsElementMaraDTO::getFormattedPartNumber);
    public static final EfsProperty<Double> WEIGHT_PRIO = new EfsMaraProperties<>("efs.mara.property.weightPrio",
        "weightPrio", EfsElementMaraDTO::getPrioritizedWeight);
    public static final EfsProperty<Integer> WEIGHT_QUALITY = new EfsMaraProperties<>("efs.mara.property.weightQuality",
        "weightQuality", EfsMaraProperties::getWeightQuality);

    public EfsMaraProperties(String propertyId, String columnId, Function<EfsElementMaraDTO, T> getter) {
        super(propertyId, columnId);
        this.getter = wrap(getter);
    }

    private Function<EfsElementDTO, T> wrap(Function<EfsElementMaraDTO, T> getter) {
        return efsElement -> {
            if (efsElement == null) {
                return null;
            }

            EfsElementMaraDTO efsElementMara = efsElement.getEfsElementMara();
            if (efsElementMara == null) {
                return null;
            }

            return getter.apply(efsElementMara);
        };
    }

    private static int getWeightQuality(EfsElementMaraDTO mara) {
        PrioritizedWeight mostPriorizedWeight = EfsWeightUtil.getMostPriorizedWeight(mara);
        return mostPriorizedWeight.getWeightOrigin().ordinal();
    }
}
