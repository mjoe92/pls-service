package de.vw.paso.client.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import de.vw.paso.client.util.preference.PreferenceHandler;
import de.vw.paso.client.util.preference.PreferenceKeys;
import de.vw.paso.utility.StringConstant;

public class FileUtil {

    private static final String EXCEL_EXTENSION_TEXT = "Excel file (*.xlsx)";
    private static final String EXCEL_EXTENSION = "*.xlsx";

    public static final FileChooser.ExtensionFilter EXCEL = new FileChooser.ExtensionFilter(EXCEL_EXTENSION_TEXT,
        EXCEL_EXTENSION);

    public static void openFileWithAssociatedProgram(File file) {
        Desktop desktop = Desktop.getDesktop();

        try {
            desktop.open(file);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to open the exported excel document", exception);
        }
    }

    public static File openSaveExcelDialog(String fileName, Window window) {
        String date = de.vw.paso.utility.DateUtil.formatDate(new Date(), "yyyy_MM_dd_hh_mm_ss");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(FileUtil.getLastSelectedDirectory());
        fileChooser.setInitialFileName(fileName + StringConstant.UNDERLINE + date + ".xlsx");
        fileChooser.getExtensionFilters().addAll(EXCEL);

        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            FileUtil.setLastSelectedDirectory(file.getParentFile());
        }

        return file;
    }

    private static File getLastSelectedDirectory() {
        PreferenceKeys<String> lastSelectedDirectoryKey = PreferenceKeys.LAST_SELECTED_DIRECTORY;
        String lastSelectedDirectory = PreferenceHandler.getInstance().get(lastSelectedDirectoryKey);
        File file = new File(lastSelectedDirectory);
        if (file.exists() && file.isDirectory()) {
            return file;
        }

        String userHome = System.getProperty("user.home");
        PreferenceHandler.getInstance().set(lastSelectedDirectoryKey, userHome);
        return new File(userHome);
    }

    private static void setLastSelectedDirectory(File file) {
        if (file.isFile()) {
            file = file.getParentFile();
        }

        PreferenceHandler.getInstance().set(PreferenceKeys.LAST_SELECTED_DIRECTORY, file.getAbsolutePath());
    }
}
