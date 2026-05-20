package de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
public class PasoStatusBar extends HBox {
  Label label1;
  Label label2;
  Hyperlink hyperlink;


  public PasoStatusBar(Label firstLabel, Label secondLabel, Hyperlink link){
    label1 = firstLabel;
    label2 = secondLabel;
    hyperlink = link;
  }

  public void setContent(Label firstLabel, Label secondLabel, @Nullable Hyperlink link){
    label1 = firstLabel;
    label2 = secondLabel;
    hyperlink = link;
  }

}
