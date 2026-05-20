package de.vw.paso.client.control.menu;

import java.sql.Timestamp;

import javafx.scene.control.RadioMenuItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.tiwh.IDatenstand;
import de.vw.paso.utility.DateUtil;

public class DatenstandMenuItem extends RadioMenuItem {

    private final IDatenstand datenstand;

    public DatenstandMenuItem(IDatenstand datenstand) {
        this.datenstand = datenstand;
        this.setText(getImportStatus(datenstand));
    }

    public IDatenstand getDatenstand() {
        return datenstand;
    }

    public static String getImportErrorText() {
        return I18N.getString("import.fehler");
    }

    public static String getImportImportedText() {
        return I18N.getString("import.importiert");
    }

    public static String getImportNoDataText() {
        return I18N.getString("import.keine.daten");
    }

    public static String getImportRequestedText() {
        return I18N.getString("import.angefordert");
    }

    public String getImportStatus(IDatenstand datenstand) {
        String importStatusText = getImportStatusText(datenstand.getImportStatus());
        Timestamp timestampChange = datenstand.getTimestampChange();

        return DateUtil.formatDate(timestampChange, "dd.MM.yyyy HH:mm") + " - " + importStatusText;
    }

    private String getImportStatusText(ImportStatus importStatus) {
        return switch (importStatus) {
            case REQUESTED -> getImportRequestedText();
            case IMPORTED -> getImportImportedText();
            case NO_DATA -> getImportNoDataText();
            case ERROR -> getImportErrorText();
        };
    }
}
