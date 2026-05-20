package de.vw.paso.client.util;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class AnimationUtil {

  private static AnimationUtil instance;

  public static AnimationUtil getInstance() {
    if (instance == null) {
      instance = new AnimationUtil();
    }
    return instance;
  }

  public void slideTopToBottom(Pane executionPane, Node slideInNode, Node slideOutNode, Duration duration) {
    Animation slideIn = new Transition() {
      {
        setCycleDuration(duration);
      }

      @Override
      protected void interpolate(double frac) {
        double height = executionPane.heightProperty().get();

        final double curHeight = height * frac;
        slideInNode.setTranslateY(-1 * height + curHeight);
      }
    };

    TranslateTransition slideOut = new TranslateTransition();
    slideOut.setDuration(duration);
    slideOut.setNode(slideOutNode);
    slideOut.setByY(executionPane.heightProperty().get());
    slideOut.setOnFinished(e -> executionPane.getChildren().remove(slideOutNode));

    slideIn.play();
    slideOut.play();
  }

  public void slideBottomToTop(Pane executionPane, Node slideInNode, Node slideOutNode, Duration duration) {
    Animation slideIn = new Transition() {
      {
        setCycleDuration(duration);
      }

      @Override
      protected void interpolate(double frac) {
        double height = executionPane.heightProperty().get();

        final double curHeight = height * frac;
        slideInNode.setTranslateY(height - curHeight);
      }
    };

    TranslateTransition slideOut = new TranslateTransition();
    slideOut.setDuration(duration);
    slideOut.setNode(slideOutNode);
    slideOut.setFromY(0);
    slideOut.setToY(-1 * executionPane.heightProperty().get());
    slideOut.setOnFinished(e -> executionPane.getChildren().remove(slideOutNode));

    slideIn.play();
    slideOut.play();
  }

}
