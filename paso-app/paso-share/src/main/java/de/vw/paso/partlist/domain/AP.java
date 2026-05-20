package de.vw.paso.partlist.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AP {

    EMPTY(""), HUT("*H"), PLATFORM("*P"), SYSTEM("*S"), MODULE("*M"), X("*X");

    private static final Map<String, AP> apAbbreviationMap = new HashMap<>();

    private final String apAbbreviation;

    static {
        for (final AP ap : values()) {
            apAbbreviationMap.put(ap.getApAbbreviation(), ap);
        }
    }

    public static AP getApByAbbreviation(final String apAbbreviation) {
        return apAbbreviationMap.get(apAbbreviation);
    }

    @Override
    public String toString() {
        return apAbbreviation;
    }

    public static List<String> toStrList() {
        return Arrays.stream(values()).map(AP::getApAbbreviation).collect(Collectors.toList());
    }

    public boolean is(String apAbbreviation) {
        return this.apAbbreviation.equals(apAbbreviation);
    }
}
