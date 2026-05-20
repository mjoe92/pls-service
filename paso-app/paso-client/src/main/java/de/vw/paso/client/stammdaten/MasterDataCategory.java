package de.vw.paso.client.stammdaten;

import de.vw.paso.client.base.I18N;

public enum MasterDataCategory {
    MARKE("treeitem.marken"),
    SALES_REGION("treeitem.sales.region"),
    FZG_PROJECT("treeitem.fzg.projects"),
    SET_VERSION("treeitem.set.versions"),
    SET_KEY("treeitem.set.keys"),
    COST_GROUP("treeitem.cost.groups"),
    PART_GROUP("treeitem.part.groups"),
    PRODUCT("treeitem.product"),
    PST("treeitem.pst");

    private final String name;

    MasterDataCategory(String key) {
        this.name = I18N.getString(key);
    }

    @Override
    public String toString() {
        return name;
    }
}