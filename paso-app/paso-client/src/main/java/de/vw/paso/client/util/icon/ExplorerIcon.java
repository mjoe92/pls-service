package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

import de.vw.paso.utility.StringConstant;

public enum ExplorerIcon implements Icon {

    EXPLORER_16X16("explorer-16x16.png"),
    EXPLORER_32X32("explorer-32x32.png"),
    EXPLORER_FZGPROJEKT_16X16("fzgProjekt-16x16.png"),
    EXPLORER_STAR_16x16("star-16x16.png"),
    EXPLORER_STAR_32x32("star-32x32.png"),
    EXPLORER_STAR_EMPTY_16x16("starEmpty-16x16.png");

    private static final String FOLDER_NAME = "icons/explorer";

    private final String fileName;
    private final Image image;

    ExplorerIcon(String fileName) {
        this.fileName = fileName;
        image = IconUtil.loadImage(getPath());
    }

    public String getPath() {
        return FOLDER_NAME + StringConstant.SLASH + getFileName();
    }

    public String getFileName() {
        return fileName;
    }

    public Image getImage() {
        return image;
    }
}
