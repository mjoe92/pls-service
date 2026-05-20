package de.vw.paso.client.control.breadcrumb;

import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class BreadCrumbButton extends Button {

  private final double arrowWidth = 5;
  private final double arrowHeight = 20;

  /**
   * Create a BreadCrumbButton
   *
   * @param text
   *   Buttons text
   */
  public BreadCrumbButton(String text) {
    this(text, null);
  }

  /**
   * Create a BreadCrumbButton
   *
   * @param text
   *   Buttons text
   * @param graphic
   *   graphic of the Button
   */
  public BreadCrumbButton(String text, Node graphic) {
    super(text, graphic);

    getStyleClass().addListener((InvalidationListener) inv -> updateShape());

    updateShape();
  }

  private void updateShape() {
    this.setShape(createButtonShape());
  }

  /**
   * Create an arrow path
   *
   * @return
   */
  private Path createButtonShape() {
    // build the following shape (or home without left arrow)

    //   --------
    //  \         \
    //  /         /
    //   --------
    Path path = new Path();

    // begin in the upper left corner
    MoveTo e1 = new MoveTo(0, 0);
    path.getElements().add(e1);

    // draw a horizontal line that defines the width of the shape
    HLineTo e2 = new HLineTo();
    // bind the width of the shape to the width of the button
    e2.xProperty().bind(this.widthProperty().subtract(arrowWidth));
    path.getElements().add(e2);

    // draw upper part of right arrow
    LineTo e3 = new LineTo();
    // the x endpoint of this line depends on the x property of line e2
    e3.xProperty().bind(e2.xProperty().add(arrowWidth));
    e3.setY(arrowHeight / 2.0);
    path.getElements().add(e3);

    // draw lower part of right arrow
    LineTo e4 = new LineTo();
    // the x endpoint of this line depends on the x property of line e2
    e4.xProperty().bind(e2.xProperty());
    e4.setY(arrowHeight);
    path.getElements().add(e4);

    // draw lower horizontal line
    HLineTo e5 = new HLineTo(0);
    path.getElements().add(e5);

    // draw lower part of left arrow
    // we simply can omit it for the first Button
    LineTo e6 = new LineTo(arrowWidth, arrowHeight / 2.0);
    path.getElements().add(e6);

    // close path
    ClosePath e7 = new ClosePath();
    path.getElements().add(e7);
    // this is a dummy color to fill the shape, it won't be visible
    path.setFill(Color.BLACK);

    return path;
  }
}
