package de.vw.paso.utility;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.vw.paso.service.masterdata.prnumber.PrNumberNameProvider;
import org.apache.commons.lang3.StringUtils;

public class PrNumberUtil {

    private static final String SEPARATOR = StringConstant.PLUS;
    private static final String SEPARATOR_REGEX = "\\+";

    public static Collection<String> split(String prNumberString) {
        return prNumberString == null || prNumberString.isEmpty() ? List.of() :
                List.of(StringUtils.stripStart(prNumberString, SEPARATOR).split(SEPARATOR_REGEX));
    }

    public static String sortJoin(Collection<? extends PrNumberNameProvider> prNumbers) {
        return prNumbers.stream().map(PrNumberNameProvider::getName).sorted().collect(Collectors.joining(SEPARATOR));
    }

    public static String joinNames(Collection<String> names) {
        return String.join(SEPARATOR, names);
    }
}
