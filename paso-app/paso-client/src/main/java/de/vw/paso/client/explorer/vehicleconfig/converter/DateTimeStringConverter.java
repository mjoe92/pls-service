package de.vw.paso.client.explorer.vehicleconfig.converter;

import java.text.ParseException;
import java.util.Date;

import javafx.util.StringConverter;

import de.vw.paso.utility.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeStringConverter extends StringConverter<Date> {

    Logger logger = LoggerFactory.getLogger(DateTimeStringConverter.class);

    @Override
    public String toString(final Date value) {
        if (value == null) {
            return StringUtils.EMPTY;
        }
        return DateUtil.toDefaultDateString(value);
    }

    @Override
    public Date fromString(final String value) {
        Date parsedDate = null;
        try {
            parsedDate = DateUtil.parseDefaultString(value);
        } catch (ParseException e) {
            logger.warn("Could not parse date", e);
        }

        return parsedDate;
    }
}
