package de.vw.paso.client.control.breadcrumb;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import de.vw.paso.partlist.domain.ICrumb;

public class PasoBreadCrumbBar<C extends ICrumb> extends HBox {

  private Consumer<C> crumbConsumer;
  private boolean isReadonly;

  public final void setSelectedCrumb(C selectedCrumb) {
    this.getChildren().clear();
    this.getChildren().addAll(getCrumbNodes(selectedCrumb));
  }

  private List<BreadCrumbButton> getCrumbNodes(C crumb) {
    List<BreadCrumbButton> crumbNodes = new ArrayList<>();
    List<ICrumb> crumbPath = getCrumbPath(crumb);
    for (ICrumb icrumb : crumbPath) {
      BreadCrumbButton button = new BreadCrumbButton(icrumb.getCrumbText() != null ? icrumb.getCrumbText() : "");
      button.setOnAction(event -> handleCrumbEvent((C) icrumb));

      setMinWidth(button);

      crumbNodes.add(button);
    }
    return crumbNodes;
  }

  private List<ICrumb> getCrumbPath(ICrumb crumb) {
    List<ICrumb> crumbList = new ArrayList<>();
    ICrumb currentCrumb = crumb;
    while (currentCrumb != null) {
      crumbList.addFirst(currentCrumb);
      currentCrumb = currentCrumb.getCrumbParent();
    }
    return crumbList;
  }

  public void setOnCrumbAction(Consumer<C> consumer) {
    crumbConsumer = consumer;
  }

  private void setMinWidth(Button button) {
    double stringWidth = computeTextWidth(button.getFont(), button.getText());
    stringWidth = stringWidth + 26;

    button.setMinWidth(stringWidth);
  }

  private double computeTextWidth(Font font, String text) {
    Text helper = new Text();
    helper.setFont(font);
    helper.setText(text);
    // Note that the wrapping width needs to be set to zero before
    // getting the text's real preferred width.
    helper.setWrappingWidth(0);
    helper.setLineSpacing(0);
    double w = Math.min(helper.prefWidth(-1), 0);
    helper.setWrappingWidth((int) Math.ceil(w));
    return Math.ceil(helper.getLayoutBounds().getWidth());
  }

  private void handleCrumbEvent(C crumb) {
    if (!isReadonly && (crumb != null)) {
      crumbConsumer.accept(crumb);
    }
  }

  public void setReadonly(final boolean isReadonly) {
    this.isReadonly = isReadonly;
  }
}
