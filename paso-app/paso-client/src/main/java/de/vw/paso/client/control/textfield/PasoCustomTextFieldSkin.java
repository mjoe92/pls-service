package de.vw.paso.client.control.textfield;

import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.StackPane;
import javafx.scene.text.HitInfo;

public class PasoCustomTextFieldSkin<T> extends TextFieldSkin {

  private static final PseudoClass HAS_NO_SIDE_NODE = PseudoClass.getPseudoClass("no-side-nodes"); //$NON-NLS-1$
  private static final PseudoClass HAS_LEFT_NODE = PseudoClass.getPseudoClass("left-node-visible"); //$NON-NLS-1$
  private static final PseudoClass HAS_RIGHT_NODE = PseudoClass.getPseudoClass("right-node-visible"); //$NON-NLS-1$

  private StackPane leftPane;
  private StackPane rightPane;

  private final PasoCustomTextField<T> control;

  public PasoCustomTextFieldSkin(final PasoCustomTextField<T> control) {
    super(control);

    this.control = control;
    updateChildren();

    registerChangeListener(control.leftProperty(), e -> updateChildren());
    registerChangeListener(control.rightProperty(), e -> updateChildren());
  }

  private void updateChildren() {
    Node newLeft = control.leftProperty().get();
    getChildren().remove(leftPane);
    Node left;
    if (newLeft != null) {
      leftPane = new StackPane(newLeft);
      leftPane.setManaged(false);
      leftPane.setAlignment(Pos.CENTER_LEFT);
      leftPane.getStyleClass().add("left-pane");
      getChildren().add(leftPane);
      left = newLeft;
    } else {
      leftPane = null;
      left = null;
    }

    Node newRight = control.rightProperty().get();
    getChildren().remove(rightPane);
    Node right;
    if (newRight != null) {
      rightPane = new StackPane(newRight);
      rightPane.setManaged(false);
      rightPane.setAlignment(Pos.CENTER_RIGHT);
      rightPane.getStyleClass().add("right-pane");
      getChildren().add(rightPane);
      right = newRight;
    } else {
      rightPane = null;
      right = null;
    }

    control.pseudoClassStateChanged(HAS_LEFT_NODE, left != null);
    control.pseudoClassStateChanged(HAS_RIGHT_NODE, right != null);
    control.pseudoClassStateChanged(HAS_NO_SIDE_NODE, left == null && right == null);
  }

  @Override
  public void dispose() {
    super.dispose();

    if (getSkinnable() == null) {
      return;
    }

    getChildren().removeAll(leftPane, rightPane);
  }

  @Override
  protected void layoutChildren(double x, double y, double w, double h) {
    final double fullHeight = h + snappedTopInset() + snappedBottomInset();

    final double leftWidth = leftPane == null ? 0.0 : snapSizeX(leftPane.prefWidth(fullHeight));
    final double rightWidth = rightPane == null ? 0.0 : snapSizeX(rightPane.prefWidth(fullHeight));

    final double textFieldStartX = snapPositionX(x) + snapSizeX(leftWidth);
    final double textFieldWidth = w - snapSizeX(leftWidth) - snapSizeX(rightWidth);

    super.layoutChildren(textFieldStartX, 0, textFieldWidth, fullHeight);

    if (leftPane != null) {
      final double leftStartX = 0;
      leftPane.resizeRelocate(leftStartX, 0, leftWidth, fullHeight);
    }

    if (rightPane != null) {
      final double rightStartX = w - rightWidth + snappedLeftInset();
      rightPane.resizeRelocate(rightStartX, 0, rightWidth, fullHeight);
    }
  }

  @Override
  public HitInfo getIndex(double x, double y) {
    final double leftWidth = leftPane == null ? 0.0 : snapSizeX(leftPane.prefWidth(getSkinnable().getHeight()));
    return super.getIndex(x - leftWidth, y);
  }

  @Override
  protected double computePrefWidth(double h, double topInset, double rightInset, double bottomInset,
    double leftInset) {
    final double pw = super.computePrefWidth(h, topInset, rightInset, bottomInset, leftInset);
    final double leftWidth = leftPane == null ? 0.0 : snapSizeX(leftPane.prefWidth(h));
    final double rightWidth = rightPane == null ? 0.0 : snapSizeX(rightPane.prefWidth(h));

    return pw + leftWidth + rightWidth;
  }

  @Override
  protected double computePrefHeight(double w, double topInset, double rightInset, double bottomInset,
    double leftInset) {
    final double ph = super.computePrefHeight(w, topInset, rightInset, bottomInset, leftInset);
    final double leftHeight = leftPane == null ? 0.0 : snapSizeY(leftPane.prefHeight(-1));
    final double rightHeight = rightPane == null ? 0.0 : snapSizeY(rightPane.prefHeight(-1));

    return Math.max(ph, Math.max(leftHeight, rightHeight));
  }

  @Override
  protected double computeMinWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
    final double mw = super.computeMinWidth(h, topInset, rightInset, bottomInset, leftInset);
    final double leftWidth = leftPane == null ? 0.0 : snapSizeX(leftPane.minWidth(h));
    final double rightWidth = rightPane == null ? 0.0 : snapSizeX(rightPane.minWidth(h));

    return mw + leftWidth + rightWidth;
  }

  @Override
  protected double computeMinHeight(double w, double topInset, double rightInset, double bottomInset,
    double leftInset) {
    final double mh = super.computeMinHeight(w, topInset, rightInset, bottomInset, leftInset);
    final double leftHeight = leftPane == null ? 0.0 : snapSizeY(leftPane.minHeight(-1));
    final double rightHeight = rightPane == null ? 0.0 : snapSizeY(rightPane.minHeight(-1));

    return Math.max(mh, Math.max(leftHeight, rightHeight));
  }
}
