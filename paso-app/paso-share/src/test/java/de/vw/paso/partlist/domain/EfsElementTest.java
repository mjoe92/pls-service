package de.vw.paso.partlist.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EfsElementTest {

    @Test
    void testIsMotor() {
        EfsElementMara elementMara = new EfsElementMara();
        elementMara.setDescription1De("MOTOR123");
        elementMara.setPartNumber("123100456");
        EfsElement efsElement = new EfsElement();
        efsElement.setEfsElementMara(elementMara);
        efsElement.setAggregate("ABC");

        assertTrue(efsElement.isMotor());
    }

    @Test
    void testIsMotorWithoutElementMara() {
        EfsElement efsElement = new EfsElement();
        efsElement.setAggregate("ABC");

        assertFalse(efsElement.isMotor());
    }

    @Test
    void testIsMotorWithoutDescription() {
        EfsElementMara elementMara = new EfsElementMara();
        elementMara.setDescription1De(null);
        elementMara.setPartNumber("123100456");
        EfsElement efsElement = new EfsElement();
        efsElement.setEfsElementMara(elementMara);
        efsElement.setAggregate("ABC");

        assertFalse(efsElement.isMotor());
    }

    @Test
    void testIsMotorWithoutPartNumber() {
        EfsElementMara elementMara = new EfsElementMara();
        elementMara.setDescription1De("MOTOR123");
        elementMara.setPartNumber(null);
        EfsElement efsElement = new EfsElement();
        efsElement.setEfsElementMara(elementMara);
        efsElement.setAggregate("ABC");

        assertFalse(efsElement.isMotor());
    }

    @Test
    void testIsMotorWithoutAggregate() {
        EfsElementMara elementMara = new EfsElementMara();
        elementMara.setDescription1De("MOTOR123");
        elementMara.setPartNumber("123100456");
        EfsElement efsElement = new EfsElement();
        efsElement.setEfsElementMara(elementMara);
        efsElement.setAggregate(null);

        assertFalse(efsElement.isMotor());
    }

    @Test
    void testIsMotorWithEmptyAggregate() {
        EfsElementMara elementMara = new EfsElementMara();
        elementMara.setDescription1De("MOTOR123");
        elementMara.setPartNumber("123100456");
        EfsElement efsElement = new EfsElement();
        efsElement.setEfsElementMara(elementMara);
        efsElement.setAggregate("");

        assertFalse(efsElement.isMotor());
    }

    @Test
    void testIsMotorWithWhitespaceAggregate() {
        EfsElementMara elementMara = new EfsElementMara();
        elementMara.setDescription1De("MOTOR123");
        elementMara.setPartNumber("123100456");
        EfsElement efsElement = new EfsElement();
        efsElement.setEfsElementMara(elementMara);
        efsElement.setAggregate("    ");

        assertTrue(efsElement.isMotor());
    }

    @Test
    void testIsMotorWithMiddleGroupNot100() {
        EfsElementMara elementMara = new EfsElementMara();
        elementMara.setDescription1De("MOTOR123");
        elementMara.setPartNumber("123xxx456");
        EfsElement efsElement = new EfsElement();
        efsElement.setEfsElementMara(elementMara);
        efsElement.setAggregate("ABC");

        assertFalse(efsElement.isMotor());
    }

    @Test
    void testIsGearbox() {
        EfsElementMara elementMara = new EfsElementMara();
        elementMara.setDescription1De("GETRIEBE");
        EfsElement efsElement = new EfsElement();
        efsElement.setEfsElementMara(elementMara);

        assertTrue(efsElement.isGetriebe());
    }

    @Test
    void testIsGearboxWithoutElementMara() {
        EfsElement efsElement = new EfsElement();

        assertFalse(efsElement.isGetriebe());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   ", "GETRIEBE123", "getriebe" })
    void testIsGearboxWithInvalidDescription(String description) {
        EfsElementMara elementMara = new EfsElementMara();
        elementMara.setDescription1De(description);
        EfsElement efsElement = new EfsElement();
        efsElement.setEfsElementMara(elementMara);

        assertFalse(efsElement.isGetriebe());
    }
}
