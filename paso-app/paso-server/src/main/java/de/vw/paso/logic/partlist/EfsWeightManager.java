package de.vw.paso.logic.partlist;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.IPartListChild;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.repository.partlist.EfsElementRepository;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.util.EfsEntityWeightCalculator;
import org.springframework.stereotype.Service;

@Service
public class EfsWeightManager {

    private final EfsElementRepository efsElementRepository;
    private final VehiclePartListRepository vehiclePartListRepository;

    public EfsWeightManager(EfsElementRepository efsElementRepository,
            VehiclePartListRepository vehiclePartListRepository) {
        this.efsElementRepository = efsElementRepository;
        this.vehiclePartListRepository = vehiclePartListRepository;
    }

    public Map<Long, Double> calculateWeight(Long vehiclePartListId) {
        VehiclePartList partList = vehiclePartListRepository.findById(vehiclePartListId).orElseThrow();
        return calculateWeight(partList);
    }

    public Map<Long, Double> calculateWeight(IPartListChild partList) {
        List<EfsElement> elementsInPartListLeft = efsElementRepository.findAllByVehiclePartListId(
                partList.getVehiclePartListId());

        Map<Long, EfsElement> idMap = new HashMap<>(elementsInPartListLeft.size());
        for (EfsElement element : elementsInPartListLeft) {
            idMap.put(element.getId(), element);
        }

        for (EfsElement element : elementsInPartListLeft) {
            EfsElement parent = idMap.get(element.getParentId());
            element.moveToParent(parent);
        }

        Collection<EfsElement> rootNodes;
        if (partList.asParent() == null) {
            rootNodes = elementsInPartListLeft.stream().filter(element -> element.getParent() == null).toList();
        } else {
            rootNodes = List.of(partList.asParent());
        }

        Collection<Long> elementsToRefresh = new HashSet<>();
        EfsEntityWeightCalculator calculator = new EfsEntityWeightCalculator();
        double rootValue = 0;
        for (EfsElement ele : rootNodes) {
            double weight = calculator.calculateWeight(ele, elementsToRefresh);
            if (!ele.isDeleted()) {
                rootValue += weight;
            }
        }

        Map<Long, Double> weightMap = calculator.getWeights();
        weightMap.put(Long.MIN_VALUE, rootValue);

        return weightMap;
    }
}
