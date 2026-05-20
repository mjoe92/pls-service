package de.vw.paso.pls;

public enum Status {
    /** Vehicle configuration setup is incomplete. */
    INCOMPLETE,
    /** Part list request or processing is running. */
    PENDING,
    /** We successfully got a response and processed the part list. */
    READY,
    /** The part list request and creation are finished. */
    COMPLETE,
    /** Something went wrong while requesting or processing the part list. */
    ERROR;

    public boolean canRequestPartList() {
        return this == Status.INCOMPLETE || this == Status.ERROR;
    }

    public static Status ofPartList(PartListStatus status) {
        return switch (status) {
            case PENDING -> PENDING;
            case READY -> READY;
            case ERROR, UNKNOWN, TIMEOUT -> ERROR;
        };
    }
}
