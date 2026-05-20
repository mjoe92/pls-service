package de.vw.paso.partlist.domain;

public enum SpecialPartNumberType {

    NO_MARA("[NO MARA]"), GAP("[Riss]");

    private final String label;

    SpecialPartNumberType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}