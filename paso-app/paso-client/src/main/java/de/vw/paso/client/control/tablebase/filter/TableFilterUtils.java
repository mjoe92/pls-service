package de.vw.paso.client.control.tablebase.filter;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumnBase;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

import de.vw.paso.client.util.icon.FilterIcon;

public final class TableFilterUtils {

  private TableFilterUtils() {
    throw new IllegalArgumentException("Util class");
  }

  public static <S> void makeFilterable(TableColumnBase<S, ?> column, Consumer<TableColumnBase<S, ?>> consumer) {
    setTextAsGraphic(column, 24, false);

    column.getGraphic().setOnMouseReleased(event -> {
      if (MouseButton.SECONDARY.equals(event.getButton())) {
        event.consume();
        consumer.accept(column);
      }
    });
  }

  public static void makeHeaderWrappable(TableColumnBase<?, ?> column) {
    setTextAsGraphic(column, 48, true);
  }

  public static void removeFilter(TableColumnBase<?, ?> column) {
    if (column.getGraphic() instanceof StackPane stackPane) {
      ObservableList<Node> children = stackPane.getChildren();
      for (Node node : children) {
        if (node instanceof Label label) {
          Node graphic = label.getGraphic();
          if (graphic instanceof ImageView) {
            label.setGraphic(null);
            return;
          }
        }
      }
    }
  }

  public static void setFilter(TableColumnBase<?, ?> column) {
    if (column.getGraphic() instanceof StackPane stackPane) {
      ObservableList<Node> children = stackPane.getChildren();

      for (Node node : children) {
        if (node instanceof Label label) {
          Node graphic = label.getGraphic();
          if (graphic instanceof ImageView imageView) {
            imageView.setImage(FilterIcon.FILTER_16x16.getImage());
            return;
          } else {
            ImageView imageView = new ImageView(FilterIcon.FILTER_16x16.getImage());
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            label.setGraphic(imageView);
            return;
          }
        }
      }
    }
  }

  private static void setTextAsGraphic(TableColumnBase<?, ?> column, int size, boolean isWrappable) {
    if (column.getGraphic() != null) {
      return;
    }

    Label label = new Label();
    label.setMaxHeight(size);
    label.textProperty().bind(column.textProperty());
    label.setPadding(new Insets(4));
    label.setWrapText(isWrappable);
    label.setAlignment(Pos.CENTER);
    label.setTextAlignment(TextAlignment.CENTER);

    StackPane graphic = new StackPane(label);

    column.setGraphic(graphic);
    column.getStyleClass().add("wrappable-column");
  }

}
