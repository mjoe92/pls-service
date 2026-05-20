package de.vw.paso.partlist.domain;

/**
 * The weight control flag (GWS - Gewichtswertsystem).
 */
public enum WeightControlFlag {

    YES("J"), NO("N"), TEMP("V");

    private final String value;

    WeightControlFlag(String value) {
        this.value = value;
    }

    public static WeightControlFlag getType(String value) {
        if (value == null) {
            return null;
        }

        return switch (value) {
            case "J" -> WeightControlFlag.YES;
            case "N" -> WeightControlFlag.NO;
            case "V" -> WeightControlFlag.TEMP;
            default -> null;
        };
    }

    public String getValue() {
        return value;
    }
}
