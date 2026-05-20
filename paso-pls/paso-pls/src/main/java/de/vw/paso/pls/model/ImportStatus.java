package de.vw.paso.pls.model;

public enum ImportStatus {

    /** Something went wrong while requesting or processing the part list. */
    ERROR,
    /** TI-WH did not respond in time. */
    TIMEOUT,
    /** The request is pending, so it is somewhere in the queue or requested/processing right now. */
    PENDING,
    /** We successfully got a response and processed the part list. */
    READY,
    /** There is no information about a part list for the requested product. */
    UNKNOWN

}
