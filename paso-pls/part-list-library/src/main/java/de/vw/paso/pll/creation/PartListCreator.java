package de.vw.paso.pll.creation;

import static de.vw.paso.pll.creation.PartListCreatorUtil.getPartData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.paso.pll.PPFUtil;
import de.vw.paso.pll.creation.filter.PartFilterResult;
import de.vw.paso.pll.model.FilterType;
import de.vw.paso.pll.model.FilteredOutPart;
import de.vw.paso.pll.model.PlsEfsElement;
import de.vw.paso.pll.model.WeightControlFlag;
import de.vw.paso.pll.preprocessing.formats.ppf.PPF;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbkVsdFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbomFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.NodeFields;

public class PartListCreator {

    private static final Logger LOG = LoggerFactory.getLogger(PartListCreator.class);

    private Set<String> risseNodeIds;
    private Map<String, PlsEfsElement> nodeElementMap;

    private PartListCreationConfiguration config;
    private PartChecker partChecker;
    private String[] lastNodeData;
    private List<PlsEfsElement> possibleElementsForLastNode;
    private PlsEfsElement lastCreatedPart;
    private Map<String, PlsEfsElement> baukastenMap;
    private PartListCreationResult result;

    private PrNumberRuleReader ruleChecker;

    private long sortingSequence;

    public PartListCreationResult createPartList(PartListCreationConfiguration cfg) {
        checkConfig(cfg);

        Iterator<String> lines = cfg.getLinesIterator();

        skipPackedFileHeader(lines);

        ruleChecker = new PrNumberRuleReader();
        ruleChecker.readRules(cfg, lines);
        partChecker = new PartChecker(cfg, ruleChecker);

        return readData(lines, partChecker);
    }

    private void checkConfig(PartListCreationConfiguration config) {
        if (config == null) {
            throw new PartListCreationException("Part list configuration cannot be null");
        }

        if (config.getValidDate() == null) {
            throw new PartListCreationException("Valid date cannot be null");
        }

        if (config.getLinesIterator() == null) {
            throw new PartListCreationException("Part list can not be empty");
        }

        this.config = config;
    }

    private void skipPackedFileHeader(Iterator<String> lines) {
        while (lines.hasNext()) {
            String line = lines.next();
            if (PPFUtil.SECTION_SEPARATOR.equals(line)) {
                break;
            }
        }
    }

    private PartListCreationResult readData(Iterator<String> lines, PartChecker ruleChecker) {
        lastNodeData = null;
        lastCreatedPart = null;
        possibleElementsForLastNode = new ArrayList<>();
        baukastenMap = new HashMap<>();
        risseNodeIds = new HashSet<>();
        nodeElementMap = new HashMap<>();
        result = new PartListCreationResult();

        while (lines.hasNext()) {
            String line = lines.next();

            PPF lineType = PartListCreatorUtil.getLineType(line);
            String[] data = PartListCreatorUtil.splitData(line, lineType);
            if (PPF.NODE.equals(lineType)) {
                processNode(data);
            } else if (PPF.PART.equals(lineType)) {
                processPart(ruleChecker, data);
            } else if (PPF.EBK.equals(lineType)) {
                processEBK(ruleChecker, data);
            }
        }

        if (result.getRootElement() != null) {
            cleanupPartList(result);
        }

        cleanupFilteredOutParts(result);

        return result;
    }

    private void processNode(String[] data) {
        //finish processing of last node before we start a new one.
        if (!possibleElementsForLastNode.isEmpty()) {
            WahlweiseUtil.handleWahlweiseFall(possibleElementsForLastNode);
            String parentId = getPartData(lastNodeData, NodeFields.NODE_PARENT_ID);
            if (StringUtils.isNotEmpty(parentId)) {
                PlsEfsElement parentElement = getParent(parentId);
                if (parentElement != null) {
                    for (PlsEfsElement possibleElement : possibleElementsForLastNode) {
                        parentElement.getChildren().add(possibleElement);
                        result.addEfsElement(possibleElement.getOriginNodeId(), possibleElement);
                    }
                } else {
                    throw new RuntimeException("No parent PLS element found for id: " + parentId);
                }
            } else {
                setRoot(possibleElementsForLastNode);
            }
        }

        possibleElementsForLastNode.clear();
        lastCreatedPart = null;
        baukastenMap.clear();

        lastNodeData = data;
        PlsEfsElement nodeElement = createElement(data);
        nodeElementMap.put(nodeElement.getOriginNodeId(), nodeElement);
    }

    private PlsEfsElement getParent(String parentId) {
        List<PlsEfsElement> parentElements = result.getEfsElementByNodeId(parentId);
        if (isNotEmpty(parentElements)) {
            return parentElements.getLast();
        }

        PlsEfsElement parent = nodeElementMap.remove(parentId);
        if (parent != null) {
            result.addEfsElement(parentId, parent);
            parent.setGap(true);
            risseNodeIds.add(parent.getOriginNodeId());
            PlsEfsElement parentOfParent = getParent(parent.getOriginParentNodeId());
            if (parentOfParent != null) {
                parentOfParent.getChildren().add(parent);
            }

            return parent;
        }

        return null;
    }

    private void setRoot(List<PlsEfsElement> possibleElementsForLastNode) {
        if (possibleElementsForLastNode.isEmpty()) {
            throw new PartListCreationException("No possible element for root");
        } else if (possibleElementsForLastNode.size() > 1) {
            throw new PartListCreationException("Multiple Roots found");
        } else {
            PlsEfsElement newRoot = possibleElementsForLastNode.getFirst();
            result.setRootElement(newRoot);
            result.addEfsElement(newRoot.getOriginNodeId(), newRoot);
        }
    }

    private void processPart(PartChecker ruleChecker, String[] partData) {
        baukastenMap.clear();
        lastCreatedPart = null;

        // if there is no lastNodeData, it means the last node was filtered out. In that case, just skip this part
        if (lastNodeData == null) {
            return;
        }

        // we never want to filter out the root element
        if (result.getRootElement() == null) {
            if (StringUtils.isEmpty(getPartData(lastNodeData, NodeFields.NODE_PARENT_ID))) {
                lastCreatedPart = createElement(lastNodeData);
                fillEbom(lastCreatedPart, partData);
                possibleElementsForLastNode.add(lastCreatedPart);

                LOG.info("Root was originally filtered out, but is now set as root: {}",
                    lastCreatedPart.getNodeLabel());
                return;
            }
        }

        PartFilterResult partFilterResult = ruleChecker.checkEbomFilterAndDate(partData, lastNodeData);
        if (partFilterResult.isFilteredOut()) { //Check Filter rules
            PlsEfsElement filteredOutElement = createElement(lastNodeData);
            fillEbom(filteredOutElement, partData);

            saveFilteredOutParts(filteredOutElement, partFilterResult.getMessage(),
                partFilterResult.isRemoveChildren());

            return;
        }

        if (ruleChecker.isRuleActive(getPartData(partData, EbomFields.RULE_ID))) {
            lastCreatedPart = createElement(lastNodeData);
            fillEbom(lastCreatedPart, partData);
            possibleElementsForLastNode.add(lastCreatedPart);
        } else {
            PlsEfsElement filteredOutElement = createElement(lastNodeData);
            fillEbom(filteredOutElement, partData);

            saveFilteredOutParts(filteredOutElement, FilterType.FILTER_PR_NUMBER_RULE.getKey(), false);
        }
    }

    private PlsEfsElement createElement(String[] data) {
        PlsEfsElement element = new PlsEfsElement();
        for (NodeFields field : NodeFields.values()) {
            String value = getPartData(data, field);
            field.setValue(element, value);
        }

        return element;
    }

    private void fillEbom(PlsEfsElement element, String[] split) {
        for (EbomFields field : EbomFields.values()) {
            String value = getPartData(split, field);
            field.setValue(element, value);
        }

        String ruleId = getPartData(split, EbomFields.RULE_ID);
        element.setPrNumberRule(ruleChecker.getRuleForId(ruleId));
        element.setPartFound(true);
        element.setMaraSet(StringUtils.isNotEmpty(getPartData(split, EbomFields.PART_NUMBER)));
    }

    private void processEBK(PartChecker ruleChecker, String[] data) {
        //If the last create part is empty, then we don't have to process the ebk, because it was filtered out.
        if (lastCreatedPart != null && checkLtgsPart() && checkBaukastenKz()) {
            PlsEfsElement currentEBK = new PlsEfsElement();
            fillBaukasten(currentEBK, data);
            currentEBK.setPartFound(true);
            currentEBK.setEbk(true);
            PartFilterResult partFilterResult = ruleChecker.checkEbkPartRelevant(data);
            if (!partFilterResult.isFilteredOut()) {
                String parentPartNumber = getPartData(data, EbkVsdFields.BAUKASTEN_PARTNUMBER_PARENT);
                if (StringUtils.isEmpty(parentPartNumber)) {
                    lastCreatedPart.getChildren().add(currentEBK);
                } else if (parentPartNumber.equals(lastCreatedPart.getPartNumber())) {
                    lastCreatedPart.getChildren().add(currentEBK);
                } else {
                    PlsEfsElement parentEbk = baukastenMap.get(parentPartNumber);
                    if (parentEbk == null) {
                        throw new PartListCreationException("Parent ebk not found");
                    }
                    parentEbk.getChildren().add(currentEBK);
                }
            }

            baukastenMap.put(currentEBK.getPartNumber(), currentEBK);
        }
    }

    private void fillBaukasten(PlsEfsElement element, String[] split) {
        for (EbkVsdFields field : EbkVsdFields.values()) {
            String value = getPartData(split, field);
            field.setValue(element, value);
        }
        String ruleId = getPartData(split, EbkVsdFields.PR_NUMBER_RULE_ID);
        element.setPrNumberRule(ruleChecker.getRuleForId(ruleId));
    }

    private boolean checkLtgsPart() {
        if (!config.addLeitungsstraenge()) {
            return !lastCreatedPart.getNodeLabel().startsWith("LTGS");
        }
        return true;
    }

    private boolean checkBaukastenKz() {
        Set<String> validBaukasten = config.getValidBaukasten();
        String kz = lastCreatedPart.getBaukastenKz();
        if (kz != null) {
            return validBaukasten.contains(kz.trim().toUpperCase());
        }
        return false;
    }

    private void cleanupPartList(PartListCreationResult result) {
        /*
         * The root node is often not necessary for paso, so we check and move everything one level up
         */
        if (checkRemoveRoot(result)) {
            result.setRootElement(result.getRootElement().getChildren().getFirst());
        }

        result.getRootElement().setGlobalSort(nextSortValue());
        cleanupElements(result.getRootElement(), result);
    }

    private boolean checkRemoveRoot(PartListCreationResult result) {
        PlsEfsElement rootElement = result.getRootElement();
        if (rootElement != null) {
            return rootElement.hasChildren() && rootElement.getChildren().size() == 1;
        } else {
            return false;
        }
    }

    private void cleanupElements(PlsEfsElement rootElement, PartListCreationResult result) {
        if (!rootElement.hasChildren()) {
            return;
        }

        Map<String, PlsEfsElement> elementsByWahlweise = new HashMap<>();
        Map<String, List<PlsEfsElement>> elementsByNodeId = new HashMap<>();

        List<PlsEfsElement> children = rootElement.getChildren();
        Iterator<PlsEfsElement> itr = children.iterator();
        while (itr.hasNext()) {
            boolean removed = false;
            PlsEfsElement element = itr.next();
            element.setGlobalSort(nextSortValue());
            /*
             * If this element is a gab(riss), we need to check if the filter which caused this is set to 'removeChildren'.
             * In that case, we can remove this complete sub structure(this part and all children)
             */
            if (element.isGap()) {
                List<FilteredOutPart> filteredOutParts = result.getFilteredOutEfsElementsByNodeId(
                    element.getOriginNodeId());
                removed = filteredOutParts.stream().anyMatch(FilteredOutPart::isRemoveChildren);
                // Remove only if filter was set to removeChildren
                if (removed) {
                    itr.remove();
                    LOG.info("Remove complete subtree because of filter with 'removeChildren' active: {}",
                        element.getOriginNodeId());
                }
            }
            /* todo:
             * Consider to refactor this part if this gets bigger. It will be hard to see what's happening
             *
             * If it wasn't removed, we check the other filter:
             *  - Empty nodes
             *  - MIO elements
             */
            if (!removed) {
                if (element.hasChildren()) {
                    cleanupElements(element, result);
                } else if (isMio(element)) {
                    itr.remove();
                    removed = true;
                }
            }

            //Check special case 000890
            if (!element.hasChildren() && element.getPartNumber() != null && element.getPartNumber()
                .startsWith("000890") && !removed) {
                itr.remove();
                removed = true;
            }

            //Check wahlweise
            if (!removed && StringUtils.isNotEmpty(element.getWahlweiseFall()) && !element.isEbk()
                && !element.getWahlweiseFall().equals("0000")) {
                /*
                 * Remove node if there is already one for that WahlweiseFall and the WahlweiseNr is not 1.
                 * Empty WahlweiseNr is considered as 1 so we check for null and for 1 separately.
                 */
                if (elementsByWahlweise.containsKey(element.getWahlweiseFall()) && element.getWahlweiseNr() != null
                    && element.getWahlweiseNr() != 1) {
                    itr.remove();
                    removed = true;
                    LOG.info("Removed wahlweise node: {} ({}) it is a duplicate of {}", element.getWahlweiseFall(),
                        element.getOriginNodeId(),
                        elementsByWahlweise.get(element.getWahlweiseFall()).getOriginNodeId());
                } else {
                    elementsByWahlweise.put(element.getWahlweiseFall(), element);
                }
            }

            if (!removed && !element.isGap()) {
                elementsByNodeId.computeIfAbsent(element.getOriginNodeId(), e -> new ArrayList<>()).add(element);
            }
        }

        /*
         * Sometimes, there are successor nodes, but they are not marked correctly and multiple eboms apply.
         * We try to guess which one is the correct one by checking begin and end dates of the nodes.
         */
        for (Entry<String, List<PlsEfsElement>> entry : elementsByNodeId.entrySet()) {
            Collection<PlsEfsElement> nodes = entry.getValue();
            if (nodes.size() > 1) {
                PlsEfsElement lastElement = null;
                Collection<PlsEfsElement> toRemove = new HashSet<>(nodes.size());
                for (PlsEfsElement nextElement : nodes) {
                    if (lastElement == null) {
                        lastElement = nextElement;
                    } else {
                        String lastPartNumber = PartListCreatorUtil.removeVornummer(lastElement.getPartNumber());
                        String partNumber = PartListCreatorUtil.removeVornummer(nextElement.getPartNumber());
                        if (lastPartNumber.equals(partNumber) && lastElement.getEndDateKey() != null
                            && lastElement.getEndDateKey().equalsIgnoreCase(nextElement.getBeginDateKey())) {
                            toRemove.add(lastElement);
                            lastElement = nextElement;
                        }
                    }
                }

                children.removeAll(toRemove);
            }
        }
    }

    private void cleanupFilteredOutParts(PartListCreationResult result) {
        for (String id : new ArrayList<>(result.getFilteredOutEfsElementNodeIds())) {
            if (!risseNodeIds.contains(id)) {
                result.removeFilteredOutEfsElementsByNodeId(id);
            }
        }
    }

    private void saveFilteredOutParts(PlsEfsElement efsElement, String reason, boolean removeChildren) {
        FilteredOutPart filteredOutPart = new FilteredOutPart();
        filteredOutPart.setNodeId(efsElement.getOriginNodeId());
        filteredOutPart.setFilteredOutPart(efsElement);
        filteredOutPart.setProductId(efsElement.getProduct());
        filteredOutPart.setReason(reason);
        filteredOutPart.setRemoveChildren(removeChildren);

        result.addFilteredOutEfsElement(efsElement.getOriginNodeId(), filteredOutPart);
    }

    private boolean isMio(PlsEfsElement element) {
        return element != null && "Z_MIO".equals(element.getNodeType())
            && WeightControlFlag.YES != element.getWeightControlFlag();
    }

    private boolean isNotEmpty(Collection<?> col) {
        return col != null && !col.isEmpty();
    }

    public PartChecker getPartChecker() {
        return partChecker;
    }

    private long nextSortValue() {
        sortingSequence = sortingSequence + 20;
        return sortingSequence;
    }
}
