package de.vw.paso.client.stueckliste.efs.export.partlist;

import java.util.Date;
import java.util.function.Function;

import javafx.util.converter.DateStringConverter;

import de.vw.paso.client.base.I18N;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.StringConstant;

/**
 * Column definition of part list with ordered values for Excel export.
 */
public enum PartListExcelColumn {

    ZSB("property.assemblyindicator", 1000, element -> element.getEfsElementMara().getAssemblyIndicator(), null,
        createIndicatorHeaderComment()),
    PRODUCT("product", 3000, EfsElementDTO::getProduct, null, null),
    PART_NUMBER("property.teilenummer", 3500, EfsElementDTO::getPartNumber, null, null),
    NAMING("property.benennung", 10000, EfsElementDTO::getDescription1, null, null),
    ADDITIONAL_NAMING("property.zusatzbenennung", 4500, EfsElementDTO::getDescription2, null, null),
    AP("property.ap", 1800, EfsElementDTO::getAp, null, null),
    PR_NUMBER("property.prnummern", 20000, EfsElementDTO::getPrNumberRule, null, null),
    PARENT_NODE_NAME("node.value.parent", 6500, EfsElementDTO::getNodeValueParent, null, null),
    NODE_NAME("node.value", 6500, EfsElementDTO::getNodeValue, null, null),
    QUANTITY_UNIT("property.einheit", 3000, EfsElementDTO::getQuantityUnit, null, null),
    QUANTITY("property.menge", 3000, EfsElementDTO::getQuantity, null, null),
    NODE_WEIGHT("node.weight", 5000, EfsElementDTO::getNodeWeight, "#,##0", null),
    TOTAL_WEIGHT("efs.property.weight", 5000, EfsElementDTO::getWeight, "#,##0", null),
    PRIO_WEIGHT("efs.mara.property.weightPrio", 4000, element -> element.getEfsElementMara().getPrioritizedWeight(),
        "#,##0", null),
    CONSTANT_WEIGHTED("property.konstgewogen", 4000, element -> element.getEfsElementMara().getWeightWeightedTe(),
        "#,##0", null),
    CONSTANT_COMPUTED("property.konsterrechnet", 4000, element -> element.getEfsElementMara().getWeightCalculatedTe(),
        "#,##0", null),
    CONSTANT_ESTIMATED("property.konstgeschaetzt", 4000, element -> element.getEfsElementMara().getWeightEstimatedTe(),
        "#,##0", null),
    PROD_WEIGHTED("part.list.cell.prodweighted", 4000, element -> element.getEfsElementMara().getWeightWeightedProd(),
        "#,##0", null),
    WEIGHT_CONTROL_FLAG("part.list.cell.weightcontrolflag", 2000,
        element -> getWeightControlFlag(element.getWeightControlFlag()), null, null),
    SET_KEY("part.list.cell.setkey", 2000, EfsElementDTO::getSetKey, null, null),
    COST_GROUP("label.costgroup", 4000, EfsElementDTO::getCostGroup, null, null),
    START_KEY("property.einsatzschluessel", 4000, EfsElementDTO::getBeginDateKey, null, null),
    END_KEY("property.entfallschluessel", 4000, EfsElementDTO::getEndDateKey, null, null),
    EARLIEST_SOP("property.earliestSop", 4000, element -> fromDateToString(element.getEarliestSop()), null, null),
    END_SERIES("property.entfallserie", 3500, element -> fromDateToString(element.getEndDate()), null, null),
    POSITION_VARIANT("property.positionVariant", 4500, EfsElementDTO::getPositionVariant, null, null),
    NODE_TYPE("property.nodeType", 3000, EfsElementDTO::getNodeType, null, null),
    NODE_LABEL("efs.property.nodeLabel", 20000, EfsElementDTO::getNodeLabel, null, null),
    WAHLWEISE_FALL("property.wahlweiseFall", 4000, EfsElementDTO::getWahlweiseFall, null, null),
    BAUKASTEN_STATUS("property.baukastenStatus", 4500, EfsElementDTO::getBaukastenStatus, null, null),
    BAUKASTEN_ORDER("baukasten.order", 5000, element -> null, null, null),
    DRAWING_DATE("property.zeichnungdatum", 4000,
        element -> fromDateToString(element.getEfsElementMara().getDrawingDate()), null, null),
    AVON_STATUS("property.avonStatus", 3500, EfsElementDTO::getAvonStatus, null, null),
    OUTLINE_LEVEL("part.list.cell.outline.level", 4500, element -> getTreeDepth(element, 0), null, null),
    SORT("property.sortierung", 3500, EfsElementDTO::getTisSort, null, null),
    WAHLWEISE_NUMBER("tablecolumn.wahlweiseNr", 3500, EfsElementDTO::getWahlweiseNr, null, null),
    PRODUCT_STRUCTURE("property.productStructure", 4000, EfsElementDTO::getProductStructure, null, null);

    private static String createIndicatorHeaderComment() {
        String baseKey = "property.assemblyindicator";
        char[] indicators = { 'A', 'E', 'F', 'G', 'K', 'L', 'R', 'S', 'T', 'W', 'Y', 'Z' };

        StringBuilder comment = new StringBuilder(
            I18N.getString("empty") + StringConstant.SPACE_DASH_SPACE + I18N.getString(
                "property.assemblyindicatorEmpty"));
        for (char indicator : indicators) {
            comment.append("\n").append(indicator).append(StringConstant.SPACE_DASH_SPACE)
                .append(I18N.getString(baseKey + indicator));
        }

        return comment.toString();
    }

    private static final DateStringConverter DATE_CONVERTER = new DateStringConverter("dd.MM.yyyy");

    private static String fromDateToString(Date date) {
        return DATE_CONVERTER.toString(date);
    }

    private final String messageKey;
    private final int width;
    private final Function<EfsElementDTO, Object> dataGetter;
    private final String format;
    private final String comment;

    PartListExcelColumn(String messageKey, int width, Function<EfsElementDTO, Object> dataGetter, String format,
        String comment) {
        this.messageKey = messageKey;
        this.width = width;
        this.dataGetter = dataGetter;
        this.format = format;
        this.comment = comment;
    }

    private static String getWeightControlFlag(WeightControlFlag gws) {
        return gws == null ? null : gws.getValue();
    }

    private static int getTreeDepth(EfsElementDTO efsElement, int level) {
        EfsElementDTO parent = efsElement.getParent();
        return parent == null ? level : getTreeDepth(parent, ++level);
    }

    public String getMessage() {
        return I18N.getString(messageKey);
    }

    public int getWidth() {
        return width;
    }

    public Object apply(EfsElementDTO efsElement) {
        return dataGetter.apply(efsElement);
    }

    public String getFormat() {
        return format;
    }

    public String getComment() {
        return comment;
    }
}
