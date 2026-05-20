package de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration;

import java.util.Date;

public class PrNumberValidator {

    /**
     * We consider a PR-Number valid if both dates exist and the startDate date is before the endDate date.
     *
     * @param startDate
     *     the start date
     * @param endDate
     *     the end date
     * @return <code>true</code>, if valid
     */
    static boolean isStartDateBeforeEndDate(Date startDate, Date endDate) {
        return startDate != null && endDate != null && startDate.before(endDate);
    }

    static boolean isPrNumberValid(Date startDate, Date endDate, Date validDate) {
        return startDate != null && endDate != null && validDate.before(endDate) && validDate.after(startDate);
    }
}
