package de.vw.paso.partlist.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SetKeyTest {

    @Test
    void compareAlphabetically() {
        SetKey setKey1 = new SetKey("A", 1L);
        SetKey setKey2 = new SetKey("B", 1L);
        assertTrue(setKey2.compareTo(setKey1) > 0);
        assertTrue(setKey1.compareTo(setKey2) < 0);
    }

    @Test
    void compareWithItself() {
        SetKey setKey = new SetKey("A", 1L);
        assertEquals(0, setKey.compareTo(setKey));
    }

    @Test
    void compareActualSetKeyValues() {
        SetKey setKey = new SetKey("3F4", 1L);
        SetKey setKey2 = new SetKey("F01", 1L);
        assertTrue(setKey.compareTo(setKey2) < 0);
        assertTrue(setKey2.compareTo(setKey) > 0);
    }

    @Test
    void compareEmptyWithSetKeyStartingWithNumber() {
        SetKey setKey = new SetKey("3F4", 1L);
        SetKey setKey2 = new SetKey("<EMPTY>", 1L);
        assertTrue(setKey.compareTo(setKey2) < 0);
        assertTrue(setKey2.compareTo(setKey) > 0);
    }

    @Test
    void compareEmptyWithSetKeyStartingWithLetter() {
        SetKey setKey = new SetKey("F01", 1L);
        SetKey setKey2 = new SetKey("<EMPTY>", 1L);
        assertTrue(setKey.compareTo(setKey2) < 0);
        assertTrue(setKey2.compareTo(setKey) > 0);
    }

    @Test
    void compareEmptyWithSetKeyStartingWithPointyBraces() {
        SetKey setKey = new SetKey("<FOO>", 1L);
        SetKey setKey2 = new SetKey("<EMPTY>", 1L);
        assertTrue(setKey.compareTo(setKey2) > 0);
        assertTrue(setKey2.compareTo(setKey) < 0);
    }
}
