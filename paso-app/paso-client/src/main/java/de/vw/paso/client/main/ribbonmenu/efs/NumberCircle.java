package de.vw.paso.client.main.ribbonmenu.efs;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class NumberCircle extends StackPane {

  private Circle circle;

  NumberCircle(int nbr) {
    Text text;
    if (nbr > 99) {
      text = new Text("99+");
    } else {
      text = new Text(nbr + "");
    }
    text.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 10));
    circle = new Circle((text.getBoundsInLocal().getWidth() / 2) + 2);
    circle.setFill(Color.AQUAMARINE);
    circle.setStroke(Color.BLACK);
    circle.setStrokeWidth(1);
    getChildren().addAll(circle, text);
  }

  public void setCircleRadius(double radius) {
    circle.setRadius(radius);
  }

  @Override
  public boolean isResizable() {
    return false;
  }
}
