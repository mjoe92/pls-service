package de.vw.paso.client.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import de.vw.paso.utility.PrNumberUtil;
import de.vw.paso.utility.StringConstant;
import org.junit.jupiter.api.Test;

class PrNumberUtilTest {

    @Test
    void testSplit() {
        Collection<String> splitPrNumbers = PrNumberUtil.split("ABC+DEF+GHI");

        assertEquals(3, splitPrNumbers.size());

        String[] expected = { "ABC", "DEF", "GHI" };
        Iterator<String> expectedPrNumberIterator = Arrays.stream(expected).iterator();
        for (String prNumber : splitPrNumbers) {
            assertEquals(expectedPrNumberIterator.next(), prNumber);
        }
    }

    @Test
    void testSplitNull() {
        Collection<String> splitPrNumbers = PrNumberUtil.split(null);

        assertEquals(0, splitPrNumbers.size());
    }

    @Test
    void testSplitEmpty() {
        Collection<String> splitPrNumbers = PrNumberUtil.split(StringConstant.EMPTY);

        assertEquals(0, splitPrNumbers.size());
    }

    @Test
    void testSplitLeadingPlus() {
        Collection<String> splitPrNumbers = PrNumberUtil.split("+ABC+DEF+GHI");

        String[] expected = { "ABC", "DEF", "GHI" };
        Iterator<String> expectedPrNumberIterator = Arrays.stream(expected).iterator();
        for (String prNumber : splitPrNumbers) {
            assertEquals(expectedPrNumberIterator.next(), prNumber);
        }
    }

}
