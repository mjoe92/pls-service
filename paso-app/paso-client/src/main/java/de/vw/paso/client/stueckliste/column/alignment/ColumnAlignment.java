package de.vw.paso.client.stueckliste.column.alignment;

import java.util.Arrays;

import de.vw.paso.client.util.FXStyleConstants;

public enum ColumnAlignment {
    AP("ap", FXStyleConstants.ALIGNMENT_CENTER),
    AP_NUM("apNum", FXStyleConstants.ALIGNMENT_RIGHT),
    AP_WEIGHT("apWeight", FXStyleConstants.ALIGNMENT_RIGHT),
    ASSEMBLY_INDICATOR("assemblyIndicator", FXStyleConstants.ALIGNMENT_CENTER),
    AVON_STATUS("avonStatus", FXStyleConstants.ALIGNMENT_RIGHT),
    BAUKASTEN_FLAG("baukastenFlag", FXStyleConstants.ALIGNMENT_CENTER),
    BAUKASTEN_NODE_ID("baukastenNodeId", FXStyleConstants.ALIGNMENT_LEFT),
    BAUKASTEN_STATUS("baukastenStatus", FXStyleConstants.ALIGNMENT_CENTER),
    BEGIN_DATE("beginDate", FXStyleConstants.ALIGNMENT_CENTER),
    BEGIN_DATE_KEY("beginDateKey", FXStyleConstants.ALIGNMENT_CENTER),
    BOM_NUMBER("bomNumber", FXStyleConstants.ALIGNMENT_CENTER),
    BUILD_SAMPLE_APPROVAL("buildSampleApproval", FXStyleConstants.ALIGNMENT_CENTER),
    BUILD_SAMPLE_APPROVAL_DATE("buildSampleApprovalDate", FXStyleConstants.ALIGNMENT_CENTER),
    COG("cog", FXStyleConstants.ALIGNMENT_CENTER),
    CONSTRUCTIONS_GROUP("constructionsGroup", FXStyleConstants.ALIGNMENT_CENTER),
    CONSTRUCTIONS_STATE("constructionsState", FXStyleConstants.ALIGNMENT_RIGHT),
    COST_GROUP_NAME("costGroupName", FXStyleConstants.ALIGNMENT_CENTER),
    COST_GROUP("costGroup", FXStyleConstants.ALIGNMENT_CENTER),
    COST_GROUP_TREE_COLUMN("costGroupTreeColumn", FXStyleConstants.ALIGNMENT_LEFT),
    DELETION_FLAG("deletionFlag", FXStyleConstants.ALIGNMENT_CENTER),
    DESCRIPTION("description", FXStyleConstants.ALIGNMENT_LEFT),
    DESCRIPTION_1("description1", FXStyleConstants.ALIGNMENT_LEFT),
    DESCRIPTION_2("description2", FXStyleConstants.ALIGNMENT_LEFT),
    DESIGNER_COST_GROUP("designerCostGroup", FXStyleConstants.ALIGNMENT_RIGHT),
    DESIGNER_NAME("designerName", FXStyleConstants.ALIGNMENT_LEFT),
    DESIGNER_PHONE_NUMBER("designerPhoneNumber", FXStyleConstants.ALIGNMENT_RIGHT),
    DIFFERENCE("difference", FXStyleConstants.ALIGNMENT_CENTER),
    DMU_RELEVANT("dmuRelevant", FXStyleConstants.ALIGNMENT_CENTER),
    DRAWING_DATE("drawingDate", FXStyleConstants.ALIGNMENT_CENTER),
    DRAWING_STATUS("drawingStatus", FXStyleConstants.ALIGNMENT_CENTER),
    EARLIEST_NS("earliestNs", FXStyleConstants.ALIGNMENT_CENTER),
    EARLIEST_PVS("earliestPvs", FXStyleConstants.ALIGNMENT_CENTER),
    EARLIEST_SOP("earliestSop", FXStyleConstants.ALIGNMENT_CENTER),
    END_DATE("endDate", FXStyleConstants.ALIGNMENT_CENTER),
    END_DATE_KEY("endDateKey", FXStyleConstants.ALIGNMENT_CENTER),
    HUT("hut", FXStyleConstants.ALIGNMENT_RIGHT),
    KONSTRUCTURE_DATE("konstructureDate", FXStyleConstants.ALIGNMENT_CENTER),
    KSE_KZ("kseKz", FXStyleConstants.ALIGNMENT_CENTER),
    K_STAND_REL_DATE("kStandRelDate", FXStyleConstants.ALIGNMENT_CENTER),
    MATERIAL_THICKNESS("materialThickness", FXStyleConstants.ALIGNMENT_RIGHT),
    MATERIAL_TYPE("materialType", FXStyleConstants.ALIGNMENT_CENTER),
    MFP_STATUS("mfpStatus", FXStyleConstants.ALIGNMENT_CENTER),
    MFP_THICKNESS("mfpThickness", FXStyleConstants.ALIGNMENT_RIGHT),
    NODE_ID("nodeId", FXStyleConstants.ALIGNMENT_LEFT),
    NODE_LABEL("nodeLabel", FXStyleConstants.ALIGNMENT_LEFT),
    NODE_LEVEL("nodeLevel", FXStyleConstants.ALIGNMENT_CENTER),
    NODE_TYPE("nodeType", FXStyleConstants.ALIGNMENT_CENTER),
    NODE_VALUE("nodeValue", FXStyleConstants.ALIGNMENT_LEFT),
    NODE_VALUE_PARENT("nodeValueParent", FXStyleConstants.ALIGNMENT_LEFT),
    NODE_WEIGHT("nodeWeight", FXStyleConstants.ALIGNMENT_LEFT),
    PART_GROUP("partGroup", FXStyleConstants.ALIGNMENT_LEFT),
    PART_GROUP_TREE_COLUMN("partGroupTreeColumn", FXStyleConstants.ALIGNMENT_LEFT),
    PART_NUMBER("partNumber", FXStyleConstants.ALIGNMENT_LEFT),
    PART_NUMBER_END_NUMBER("partNumberEndNumber", FXStyleConstants.ALIGNMENT_LEFT),
    PART_NUMBER_FORMATTED("partNumberFormatted", FXStyleConstants.ALIGNMENT_LEFT),
    PART_NUMBER_INDEX("partNumberIndex", FXStyleConstants.ALIGNMENT_LEFT),
    PART_NUMBER_MITTELGRUPPE("partNumberMittelgruppe", FXStyleConstants.ALIGNMENT_LEFT),
    PART_NUMBER_VORNUMMER("partNumberVornummer", FXStyleConstants.ALIGNMENT_LEFT),
    PART_TYPE("partType", FXStyleConstants.ALIGNMENT_CENTER),
    PLATFORM("platform", FXStyleConstants.ALIGNMENT_RIGHT),
    POSITION_VARIANT("positionVariant", FXStyleConstants.ALIGNMENT_CENTER),
    PROCESS_STATUS("processStatus", FXStyleConstants.ALIGNMENT_CENTER),
    PRODUCT("product", FXStyleConstants.ALIGNMENT_CENTER),
    PRODUCT_STRUCTURE("productStructure", FXStyleConstants.ALIGNMENT_CENTER),
    PR_NUMBER("prNumber", FXStyleConstants.ALIGNMENT_CENTER),
    PR_NUMBER_BEGIN_DATE("prNumberBeginDate", FXStyleConstants.ALIGNMENT_CENTER),
    PR_NUMBER_BEGIN_DATE_KEY("prNumberBeginDateKey", FXStyleConstants.ALIGNMENT_CENTER),
    PR_NUMBER_END_DATE("prNumberEndDate", FXStyleConstants.ALIGNMENT_CENTER),
    PR_NUMBER_END_DATE_KEY("prNumberEndDateKey", FXStyleConstants.ALIGNMENT_CENTER),
    PR_NUMBER_FAMILY("prNumberFamily", FXStyleConstants.ALIGNMENT_CENTER),
    PR_NUMBER_RULE("prNumberRule", FXStyleConstants.ALIGNMENT_LEFT),
    PR_NUMBER_STATUS("prNumberStatus", FXStyleConstants.ALIGNMENT_LEFT),
    P_ACTIVATION_DATE("pActivationDate", FXStyleConstants.ALIGNMENT_CENTER),
    QUALITY("quality", FXStyleConstants.ALIGNMENT_RIGHT),
    QUANTITY("quantity", FXStyleConstants.ALIGNMENT_RIGHT),
    QUANTITY_UNIT("quantityUnit", FXStyleConstants.ALIGNMENT_CENTER),
    QUANTITY_UNIT_EXTENDED("quantityUnitExtended", FXStyleConstants.ALIGNMENT_CENTER),
    REL_DATE_SOLL("relDateSoll", FXStyleConstants.ALIGNMENT_CENTER),
    RESP_CONSTR_1("respConstr1", FXStyleConstants.ALIGNMENT_CENTER),
    RESP_CONSTR_2("respConstr2", FXStyleConstants.ALIGNMENT_CENTER),
    REVISION("revision", FXStyleConstants.ALIGNMENT_RIGHT),
    SALES_SETTING("salesSetting", FXStyleConstants.ALIGNMENT_LEFT),
    SEE_DRAWING("seeDrawing", FXStyleConstants.ALIGNMENT_LEFT),
    SET_KEY("setKey", FXStyleConstants.ALIGNMENT_CENTER),
    SET_KEY_TREE_COLUMN("setKeyTreeColumn", FXStyleConstants.ALIGNMENT_LEFT),
    SUM("sum", FXStyleConstants.ALIGNMENT_RIGHT),
    SYSTEM("system", FXStyleConstants.ALIGNMENT_RIGHT),
    TECHNICALLY_OKAY("technicallyOkay", FXStyleConstants.ALIGNMENT_CENTER),
    TIMESTAMP_CHANGE("timestampChange", FXStyleConstants.ALIGNMENT_CENTER),
    TIO_FREI_REL_DATE("tioFreiRelDate", FXStyleConstants.ALIGNMENT_CENTER),
    TIS_SORT("tisSort", FXStyleConstants.ALIGNMENT_RIGHT),
    TYPE_CHANGE("typeChange", FXStyleConstants.ALIGNMENT_CENTER),
    USER_CHANGE("userChange", FXStyleConstants.ALIGNMENT_LEFT),
    WAHLWEISE_FALL("wahlweiseFall", FXStyleConstants.ALIGNMENT_RIGHT),
    WAHLWEISE_NR("wahlweiseNr", FXStyleConstants.ALIGNMENT_RIGHT),
    WEIGHT("weight", FXStyleConstants.ALIGNMENT_LEFT),
    WEIGHT_ACCEPTED_FROM_EPIS("weightAcceptedFromEpis", FXStyleConstants.ALIGNMENT_CENTER),
    WEIGHT_ALL("weightAll", FXStyleConstants.ALIGNMENT_RIGHT),
    WEIGHT_CALCULATED_TE("weightCalculatedTe", FXStyleConstants.ALIGNMENT_RIGHT),
    WEIGHT_CALCULATED_TE_DATE("weightCalculatedTeDate", FXStyleConstants.ALIGNMENT_CENTER),
    WEIGHT_CONTROL_FLAG("weightControlFlag", FXStyleConstants.ALIGNMENT_CENTER),
    WEIGHT_ESTIMATED_TE("weightEstimatedTe", FXStyleConstants.ALIGNMENT_RIGHT),
    WEIGHT_ESTIMATED_TE_DATE("weightEstimatedTeDate", FXStyleConstants.ALIGNMENT_CENTER),
    WEIGHT_NODE("weightNode", FXStyleConstants.ALIGNMENT_RIGHT),
    WEIGHT_PRIO("weightPrio", FXStyleConstants.ALIGNMENT_RIGHT),
    WEIGHT_QUALITY("weightQuality", FXStyleConstants.ALIGNMENT_RIGHT),
    WEIGHT_WEIGHTED_PROD("weightWeightedProd", FXStyleConstants.ALIGNMENT_RIGHT),
    WEIGHT_WEIGHTED_PROD_DATE("weightWeightedProdDate", FXStyleConstants.ALIGNMENT_CENTER),
    WEIGHT_WEIGHTED_TE("weightWeightedTe", FXStyleConstants.ALIGNMENT_RIGHT),
    WEIGHT_WEIGHTED_TE_DATE("weightWeightedTeDate", FXStyleConstants.ALIGNMENT_CENTER);

    private final String columnName;
    private final String alignment;

    ColumnAlignment(String columName, String alignment) {
        this.columnName = columName;
        this.alignment = alignment;
    }

    public static ColumnAlignment findByColumnName(String columnName) {
        return Arrays.stream(ColumnAlignment.values())
                .filter(columnAlignment -> columnAlignment.columnName.equals(columnName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(columnName));
    }

    public String getAlignment() {
        return alignment;
    }
}
