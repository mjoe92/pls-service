package de.vw.paso.client.stueckliste.util;

import java.util.Arrays;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import de.vw.paso.client.control.textfield.PasoCustomTextField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartNumberUtil {

    private static final char PART_NUMBER_SEPARATOR_CHAR = '.';
    private static final String PART_NUMBER_SEPARATOR_STRING = String.valueOf(PART_NUMBER_SEPARATOR_CHAR);
    private static final String PART_NUMBER_SEPARATOR_REGEX = "\\" + PART_NUMBER_SEPARATOR_CHAR;

    private static final String TAB_CHARACTER = "\t";

    private static final int MAX_BLOCK_SIZE = 4;
    private static final int MAX_BLOCK_INDEX = MAX_BLOCK_SIZE - 1;
    private static final int BLOCK_TEXT_SIZE = 3;
    private static final int LAST_BLOCK_TEXT_SIZE = 2;

    public static void keyEventFilter(final KeyEvent event, final PasoCustomTextField<?> textField) {
        String text = textField.getText();

        int caretPosition = textField.getCaretPosition();

        final String textBeforeCaret = text.substring(0, caretPosition);

        if (KeyEvent.KEY_PRESSED.equals(event.getEventType())) {
            if (KeyCode.TAB.equals(event.getCode())) {
                event.consume();
            } else if (KeyCode.BACK_SPACE.equals(event.getCode())) {
                while ((caretPosition > 0) && (text.charAt(--caretPosition) == PART_NUMBER_SEPARATOR_CHAR)) {
                    textField.positionCaret(caretPosition);
                }
            } else if (KeyCode.DELETE.equals(event.getCode())) {
                while ((caretPosition < text.length()) && (text.charAt(caretPosition) == PART_NUMBER_SEPARATOR_CHAR)) {
                    textField.positionCaret(++caretPosition);
                }
            }
        } else if (KeyEvent.KEY_TYPED.equals(event.getEventType())) {
            final int blockCount = StringUtils.countMatches(text, PART_NUMBER_SEPARATOR_CHAR) + 1;
            final int blockIndex = StringUtils.countMatches(textBeforeCaret, PART_NUMBER_SEPARATOR_CHAR);
            final int desiredBlockSize = (blockIndex < MAX_BLOCK_INDEX) ? BLOCK_TEXT_SIZE : LAST_BLOCK_TEXT_SIZE;

            if ((blockCount == 0) || ((blockCount - 1) < blockIndex)) {
                return;
            }

            final String[] blocks = Arrays.copyOf(text.split(PART_NUMBER_SEPARATOR_REGEX), blockCount);

            if (event.getCharacter().equals(PART_NUMBER_SEPARATOR_STRING) || event.getCharacter()
                    .equals(TAB_CHARACTER)) {
                if (blocks[blockIndex] == null) {
                    blocks[blockIndex] = StringUtils.EMPTY;
                }

                final int originalSize = blocks[blockIndex].length();

                for (int index = originalSize; index < desiredBlockSize; index++) {
                    blocks[blockIndex] += StringUtils.SPACE;

                    caretPosition++;
                }

                if (originalSize <= blocks[blockIndex].length()) {
                    if (blockCount != MAX_BLOCK_SIZE) {
                        blocks[blockIndex] += PART_NUMBER_SEPARATOR_STRING;

                        caretPosition++;
                    }

                    textField.setText(StringUtils.join(blocks, PART_NUMBER_SEPARATOR_STRING));
                } else {
                    caretPosition = caretPosition - (caretPosition % MAX_BLOCK_SIZE) + MAX_BLOCK_SIZE;
                }

                textField.positionCaret(caretPosition);
            } else if ((blocks[blockIndex] != null) && (blocks[blockIndex].length() >= desiredBlockSize)) {
                event.consume();

                textField.positionCaret(caretPosition - (caretPosition % MAX_BLOCK_SIZE) + MAX_BLOCK_SIZE);
            }
        } else if (KeyEvent.KEY_RELEASED.equals(event.getEventType())) {
            final int blockCaretPosition = caretPosition - textBeforeCaret.lastIndexOf(PART_NUMBER_SEPARATOR_CHAR) - 1;

            if (blockCaretPosition == BLOCK_TEXT_SIZE) {
                if (!((caretPosition < text.length()) && (text.charAt(caretPosition) == PART_NUMBER_SEPARATOR_CHAR))) {
                    textField.fireEvent(
                            new KeyEvent(KeyEvent.KEY_TYPED, PART_NUMBER_SEPARATOR_STRING, null, null, false, false,
                                    false, false));
                } else if (!event.getCode().isArrowKey()) {
                    textField.positionCaret(caretPosition + 1);
                }
            } else {
                event.consume();
            }
        } else {
            event.consume();
        }
    }

}
