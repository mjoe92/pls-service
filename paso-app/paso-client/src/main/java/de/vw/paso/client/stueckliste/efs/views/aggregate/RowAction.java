package de.vw.paso.client.stueckliste.efs.views.aggregate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.vw.paso.pls.ProductDataDTO;
import de.vw.paso.utility.DateUtil;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public class RowAction {

    private static final String SEPARATOR = StringConstant.SPACE_DASH_SPACE;

    private final String text;
    private boolean requestNew;
    private ProductDataDTO productData;
    private int days;
    private String description;

    public RowAction(String text, boolean requestNew) {
        this.text = text;
        this.requestNew = requestNew;
    }

    public RowAction(ProductDataDTO dto, String description) {
        this.productData = dto;
        text = dto.getImportDate() + SEPARATOR + dto.getStatus();
        this.description = description;

        long diffInMillies = Math.abs(dto.getImportDate().getTime() - new Date().getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        days = (int) diff;
    }

    public boolean isDoNothing() {
        return !requestNew && productData == null;
    }

    public boolean isRequestNew() {
        return requestNew && productData == null;
    }

    public boolean isUseExising() {
        return productData != null;
    }

    public ProductDataDTO getProductData() {
        return productData;
    }

    @Override
    public String toString() {
        if (isUseExising()) {

            if (DateUtils.isSameDay(productData.getImportDate(), new Date())) {
                return DateUtil.toDefaultDateString(productData.getImportDate());
            } else {
                return DateUtil.toDefaultDateString(productData.getImportDate()) + SEPARATOR + days + StringUtils.SPACE
                        + description;
            }

        } else {
            return text;
        }
    }
}
