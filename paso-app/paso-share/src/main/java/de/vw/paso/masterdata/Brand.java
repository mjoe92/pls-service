package de.vw.paso.masterdata;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Brand {

    AU("Audi"),
    BC("Jetta"),
    BG("Bugatti"),
    BY("Bentley"),
    CU("Cupra"),
    DK("Drittmarkt Komponenten"),
    DM("Drittmarkt"),
    DU("DUCATI"),
    FO("Ford"),
    LB("LAMBORGHINI"),
    MN("MAN"),
    PO("Porsche"),
    SE("Seat"),
    SK("Skoda"),
    SM("Scout"),
    SO("Sonder Aggregate Verkauf"),
    VC("Volkswagen Caminh\u00F5es & \u00F4nibus"),
    VN("Volkswagen Light Duty Vehicles"),
    VW("Volkswagen"),
    WU("SVW"),
    WV("FAW-VW");

    private static final Map<String, Brand> NAME_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(Brand::getBrandName, Function.identity()));

    private final String brandName;

    Brand(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandName() {
        return brandName;
    }

    public static Collection<Brand> getAllBrands() {
        return NAME_MAP.values();
    }

    public static Brand getBrandByName(String brandName) {
        return NAME_MAP.get(brandName);
    }

    @Override
    public String toString() {
        return this.brandName;
    }
}