package de.vw.paso.client.control.listview;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MoveButtonPanel extends VBox {

  private final Button allRightButton;
  private final Button rightButton;
  private final Button leftButton;
  private final Button allLeftButton;
  private Button topButton;
  private Button upButton;
  private Button downButton;
  private Button bottomButton;

  public MoveButtonPanel(boolean isMovable) {
    setSpacing(5d);

    allRightButton = createButton(">>");
    allRightButton.setDisable(false);
    rightButton = createButton(">");
    leftButton = createButton("<");
    allLeftButton = createButton("<<");
    allLeftButton.setDisable(false);

    if (!isMovable) {
      getChildren().addAll(allRightButton, rightButton, leftButton, allLeftButton);
    } else {
      topButton = createButton("^^");
      upButton = createButton("^");
      downButton = createButton("v");
      bottomButton = createButton("vv");
      getChildren().addAll(topButton, upButton, allRightButton, rightButton, leftButton, allLeftButton, downButton,
        bottomButton);
    }
  }

  public Button getAllLeftButton() {
    return allLeftButton;
  }

  public Button getAllRightButton() {
    return allRightButton;
  }

  public Button getBottomButton() {
    return bottomButton;
  }

  public Button getDownButton() {
    return downButton;
  }

  public Button getLeftButton() {
    return leftButton;
  }

  public Button getRightButton() {
    return rightButton;
  }

  public Button getTopButton() {
    return topButton;
  }

  public Button getUpButton() {
    return upButton;
  }

  private Button createButton(String txt) {
    Button btn = new Button(txt);
    btn.setMinWidth(48);
    btn.setPrefWidth(48);
    btn.setDisable(true);
    return btn;
  }
}
